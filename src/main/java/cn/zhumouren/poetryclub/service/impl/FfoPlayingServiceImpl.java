package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.dto.*;
import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.*;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constant.GamesType;
import cn.zhumouren.poetryclub.constant.games.FfoGamePoemType;
import cn.zhumouren.poetryclub.constant.games.FfoGameSentenceJudgeType;
import cn.zhumouren.poetryclub.constant.games.FfoStateType;
import cn.zhumouren.poetryclub.constant.games.FfoVoteType;
import cn.zhumouren.poetryclub.dao.CommonWordRepository;
import cn.zhumouren.poetryclub.dao.FfoGameRedisDAO;
import cn.zhumouren.poetryclub.dao.FfoGameRoomRedisDAO;
import cn.zhumouren.poetryclub.dao.PoemRepository;
import cn.zhumouren.poetryclub.notice.StompFfoGameNotice;
import cn.zhumouren.poetryclub.service.FfoPlayingService;
import cn.zhumouren.poetryclub.service.FfoService;
import cn.zhumouren.poetryclub.service.FfoTaskService;
import cn.zhumouren.poetryclub.service.RedisUserService;
import cn.zhumouren.poetryclub.util.FfoGameUtil;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class FfoPlayingServiceImpl implements FfoPlayingService {
    private final RedisUserService redisUserService;

    private final PoemRepository poemRepository;
    private final FfoGameRoomRedisDAO ffoGameRoomRedisDao;
    private final FfoGameRedisDAO ffoGameRedisDao;
    private final StompFfoGameNotice ffoGameNotice;
    private final FfoTaskService ffoTaskService;
    private final FfoService ffoService;
    private final CommonWordRepository commonWordRepository;

    public FfoPlayingServiceImpl(RedisUserService redisUserService, PoemRepository poemRepository,
                                 FfoGameRoomRedisDAO ffoGameRoomRedisDao, FfoGameRedisDAO ffoGameRedisDao,
                                 StompFfoGameNotice ffoGameNotice, FfoTaskService ffoTaskService, FfoService ffoService, CommonWordRepository commonWordRepository) {
        this.redisUserService = redisUserService;
        this.poemRepository = poemRepository;
        this.ffoGameRoomRedisDao = ffoGameRoomRedisDao;
        this.ffoGameRedisDao = ffoGameRedisDao;
        this.ffoGameNotice = ffoGameNotice;
        this.ffoTaskService = ffoTaskService;
        this.ffoService = ffoService;
        this.commonWordRepository = commonWordRepository;
    }

    /**
     * 用户开启飞花令游戏
     * 1. 检测是否可以开启游戏
     * 2. 开启游戏（存入redis）
     * 3. 开启游戏通知
     * 4. 添加玩家超时发言task
     *
     * @param user
     * @return
     */
    @Override
    @Transactional
    public ResponseResult<Boolean> userStartGame(UserEntity user) {
        String roomId = redisUserService.getUserGameRoomId(user, GamesType.FFO);
        if (ObjectUtils.isEmpty(roomId)) {
            return ResponseResult.failedWithMsg("玩家未在游戏中");
        }
        FfoGameRoomDTO ffoGameRoomDTO = ffoGameRoomRedisDao.getFfoGameRoomDTO(roomId);
        UserDTO userDTO = new UserDTO(user);
        if (!ffoGameRoomDTO.getHomeowner().equals(userDTO)) {
            return ResponseResult.failedWithMsg("玩家不是房主，不能开启游戏");
        }
        if (ffoGameRoomDTO.getFfoStateType().equals(FfoStateType.PLAYING)) {
            return ResponseResult.failedWithMsg("已在游戏中，不能重复开启游戏");
        }
        if (ffoGameRoomDTO.getUsers().size() == 1) {
            return ResponseResult.failedWithMsg("至少需要两位玩家才能开始游戏!");
        }
        if (!commonWordRepository.existsByWord(ffoGameRoomDTO.getKeyword())) {
            return ResponseResult.failedWithMsg("令: " + ffoGameRoomDTO.getKeyword() + "，不存在数据库中，请换一个字");
        }
        // 开启游戏
        ffoGameRoomDTO.setFfoStateType(FfoStateType.PLAYING);
        ffoGameRoomRedisDao.saveFfoGameRoomDTO(ffoGameRoomDTO);
        FfoGameDTO ffoGameDTO = new FfoGameDTO(ffoGameRoomDTO);
        ffoGameRedisDao.saveFfoGameDTO(ffoGameDTO);
        // 开始通知用户游戏开始
        ffoGameNotice.ffoNextNotice(ffoGameDTO.getUsernames(), new FfoNextOutputVO(ffoGameDTO));
        ffoGameNotice.ffoGameRoomNotice(ffoGameRoomDTO);
        ffoGameNotice.ffoGameNotice(ffoGameDTO);
        // 添加玩家超时发言任务
        addUserSendSentenceTimeoutTask(ffoGameDTO);
        return ResponseResult.success();
    }

    /**
     * 用户发送飞花令的句子
     * 1. 检测该用户是否满足发送句子的条件
     * 2. 取消该用户的超时任务
     * 3. 检测句子是否符合本局设置
     * 4. 如果允许用户自创作，并满足本局设置，存入redis，然后通知玩家进行投票，结束
     *
     * @param roomId
     * @param userEntity
     * @param ffoSentenceInputVO
     */
    @Override
    public void userSendFfoSentence(String roomId, UserEntity userEntity, FfoSentenceInputVO ffoSentenceInputVO) {
        log.info("roomId: {}, 用户: {}, 飞花令发送了: {}", roomId, userEntity, ffoSentenceInputVO);
        String sentence = ffoSentenceInputVO.getSentence();
        FfoGameDTO ffoGameDTO = ffoGameRedisDao.getFfoGameDTO(roomId);
        // 1. 检测该用户是否满足发送句子的条件
        if (ObjectUtils.isEmpty(ffoGameDTO)) {
            log.debug("roomId = {} ，没有这个房间", roomId);
            return;
        }
        UserDTO userDTO = new UserDTO(userEntity);
        if (!ffoGameDTO.getNextSpeaker().equals(userDTO)) {
            log.debug("next speaker = {}, 但是 user = {}, 不满足条件", ffoGameDTO.getNextSpeaker(), userEntity.getUsername());
            return;
        }
        if (ObjectUtils.isNotEmpty(ffoGameDTO.getUserSentences())) {
            if (ffoGameDTO.getLastSentenceDTO()
                    .getSentenceJudgeType().equals(FfoGameSentenceJudgeType.WAITING_USERS_VOTE)) {
                log.debug("用户在等待投票，不允许回答");
                return;
            }
        }
        if (LocalDateTime.now().isAfter(FfoGameUtil.getFfoSpeakerNextEndTime(ffoGameDTO))) {
            log.debug("user = {}, 超时作答，等待超时任务自动处理", userEntity.getUsername());
            return;
        }
        // 2. 取消该用户的超时任务
        ffoTaskService.removeSpeakTimeOutTask(roomId);
        // 创建 FfoGameUserSentenceDTO，除了句子判断，其他的属性都一样
        FfoGameSentenceDTO ffoGameSentenceDTO = new FfoGameSentenceDTO(userDTO, sentence,
                ffoSentenceInputVO.getCreateTime());
        // 3. 检测句子是否符合本局设置
        log.debug("{} 本轮发送的句子为 {}", userEntity.getUsername(), sentence);
        setSentenceJudgeType(ffoGameDTO, ffoGameSentenceDTO);
        FfoGameSentenceJudgeType sentenceJudgeType = ffoGameSentenceDTO.getSentenceJudgeType();
        // 将本轮的飞花令存入对象
        ffoGameDTO.getUserSentences().add(ffoGameSentenceDTO);
        // 4. 如果允许用户自创作，并满足本局设置，存入redis，然后通知玩家进行投票
        if (sentenceJudgeType.equals(FfoGameSentenceJudgeType.WAITING_USERS_VOTE)) {
            ffoGameRedisDao.saveFfoGameDTO(ffoGameDTO);
            userFfoVoteNotice(ffoGameDTO);
            LocalDateTime voteTimeout = FfoGameUtil.getFfoVoteEndTime(ffoGameDTO);
            addUserVoteTimeoutTask(ffoGameDTO.getRoomId(), voteTimeout);
            return;
        }
        // 如果不用投票，就进入通用流程
        ffoSentenceHandle(ffoGameDTO);
    }

    /**
     * 飞花令 通用的最后几步
     * 5. 把本回合数据存入ffoGameDTO中
     * 6. 通知用户的发言
     * 7. 如果只剩一个人，则游戏结束
     * 8. 添加发言超时任务
     * 9. 存入redis
     *
     * @param ffoGameDTO
     */
    private void ffoSentenceHandle(FfoGameDTO ffoGameDTO) {
        // 5. 把本回合数据存入ffoGameDTO中
        UserDTO userDTO = ffoGameDTO.getPlayingUsers().poll();
        // 用户这句诗满足本局所有条件
        FfoGameSentenceDTO ffoGameSentenceDTO = ffoGameDTO.getLastSentenceDTO();
        if (ffoGameSentenceDTO.getSentenceJudgeType().equals(FfoGameSentenceJudgeType.SUCCESS)) {
            ffoGameDTO.getPlayingUsers().offer(userDTO);
            // 如果只允许令在指定地方，则当前用户回答正确，令出现的位置到下一个
            if (!ffoGameDTO.getAllowWordInAny()) {
                ffoGameDTO.setKeywordIndex((ffoGameDTO.getKeywordIndex() + 1) % ffoGameDTO.getMaxSentenceLength());
            }
        }
        // 用户这句诗不满足本局条件
        else {
            ffoGameDTO.getRanking().offerFirst(userDTO);
        }
        // 6. 通知用户发言
        userFfoSpeakNotice(ffoGameDTO);
        // 7. 如果只剩一个人，则游戏结束
        if (ffoGameDTO.getPlayingUsers().size() == 1) {
            UserDTO pollUser = ffoGameDTO.getPlayingUsers().poll();
            ffoGameDTO.getRanking().offerFirst(pollUser);
            ffoGameDTO.setEndTime(LocalDateTime.now());
            ffoGameOver(ffoGameDTO);
            return;
        }
        // 8. 添加发言超时任务
        addUserSendSentenceTimeoutTask(ffoGameDTO);
        // 9. 存入redis
        ffoGameRedisDao.saveFfoGameDTO(ffoGameDTO);
    }

    /**
     * 玩家投票
     *
     * @param roomId
     * @param userEntity
     * @param ffoVoteInputVO
     */
    @Override
    public void userVoteFfoSentence(String roomId, UserEntity userEntity, FfoVoteInputVO ffoVoteInputVO) {
        FfoGameDTO ffoGameDTO = ffoGameRedisDao.getFfoGameDTO(roomId);
        UserDTO userDTO = new UserDTO(userEntity);
        if (ObjectUtils.isEmpty(ffoGameDTO)) {
            log.debug("游戏 {} 不存在", roomId);
            return;
        }
        if (!ffoGameDTO.getUsers().contains(userDTO)) {
            log.debug("用户 {} 不在 {} 房间中", userEntity.getUsername(), roomId);
            return;
        }
        if (ffoGameDTO.getRanking().contains(userDTO)) {
            log.debug("用户 {} 已被淘汰，不能投票", userEntity.getUsername());
            return;
        }
        FfoGameSentenceDTO ffoGameSentenceDTO = ffoGameDTO.getLastSentenceDTO();
        boolean voted = ffoGameSentenceDTO.sentenceVote(new FfoGameVoteDTO(userDTO,
                ffoVoteInputVO.getFfoVoteType(), ffoVoteInputVO.getCreateTime()));
        if (!voted) {
            return;
        }
        // 最后一个用户投票，进入最后投票处理阶段
        if (ffoGameSentenceDTO.getUserVotes().size() == ffoGameDTO.getUsers().size()) {
            ffoTaskService.removeVoteTimeOutTask(roomId);
            ffoVoteHandle(ffoGameDTO);
            return;
        }
        ffoGameRedisDao.saveFfoGameDTO(ffoGameDTO);
    }

    /**
     * 所有玩家完成投票后，要做的事
     * 有可能是全部用户都投票完了，也有可能是投票超时定时任务启动
     *
     * @param ffoGameDTO
     */
    private void ffoVoteHandle(FfoGameDTO ffoGameDTO) {
        FfoGameSentenceDTO ffoGameSentenceDTO = ffoGameDTO.getLastSentenceDTO();
        // 只要没被淘汰的人投票有一半及以上的人通过，则算成功
        if (ffoGameSentenceDTO.getUserVotes().size() >= ffoGameDTO.getPlayingUsers().size() / 2) {
            ffoGameSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.SUCCESS);
        } else {
            ffoGameSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.VOTE_FAILED);
        }
        ffoSentenceHandle(ffoGameDTO);
    }


    /**
     * 设置句子的状态
     * 先一步步判断不满足的情况，能走到最后就是成功
     *
     * @param ffoGameDTO
     * @param ffoGameSentenceDTO
     */
    private void setSentenceJudgeType(FfoGameDTO ffoGameDTO, FfoGameSentenceDTO ffoGameSentenceDTO) {
        UserDTO userDTO = ffoGameSentenceDTO.getUser();
        String sentence = ffoGameSentenceDTO.getSentence();
        // 3.1 检测句子长度
        // 飞花令为空
        if (ObjectUtils.isEmpty(sentence)) {
            ffoGameSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.NULL_CONTENT);
            return;
        }
        // 飞花令长度与本句设置不匹配
        if (ffoGameDTO.getConstantSentenceLength()) {
            if (!ffoGameDTO.getMaxSentenceLength().equals(sentence.length())) {
                ffoGameSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.LENGTH_NOT_MATCH);
                return;
            }
        }
        // 3.2 检测句子是否在相应位置
        // 允许 令 在任意位置，但句子中不含 令
        if (!ffoGameDTO.getAllowWordInAny() && sentence.contains(ffoGameDTO.getKeyword().toString())) {
            log.debug("句子中不含 令， {} 本轮结束", userDTO);
            ffoGameSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.NO_KEYWORD);
            return;
        }
        // 令 要处于指定位置，但不处于指定位置
        else if (!ffoGameDTO.getAllowWordInAny() &&
                sentence.matches(String.format("^[\\u4e00-\\u9fa5]{%d}%c.*",
                        ffoGameDTO.getKeywordIndex(), ffoGameDTO.getKeyword()))) {
            log.debug("令 要处于指定位置，但不处于指定位置，{} 本轮结束", userDTO);
            ffoGameSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.KEYWORD_NOT_IN_CORRECT_POSITION);
            return;
        }
        // 3.3 检查允许说什么样类型的诗句
        FfoGamePoemType ffoGamePoemType = ffoGameDTO.getFfoGamePoemType();
        PoemEntity poemEntity = poemRepository.findByContentContains(sentence);
        if (ffoGamePoemType.equals(FfoGamePoemType.ONLY_ANCIENTS_POEM)) {
            if (ObjectUtils.isEmpty(poemEntity)) {
                log.debug("只允许古诗，但没有找到古诗了，{} 本轮结束", userDTO);
                ffoGameSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.KEYWORD_NOT_IN_CORRECT_POSITION);
                return;
            }
        } else if (ffoGamePoemType.equals(FfoGamePoemType.ONLY_SELF_CREAT)) {
            if (ObjectUtils.isNotEmpty(poemEntity)) {
                log.debug("只允许自创作，但找到了古诗，{} 本轮结束", userDTO);
                ffoGameSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.ONLY_SELF_CREAT_BUT_FIND);
            } else {
                log.debug("只允许自创作，没有找到古诗，开启投票");
                ffoGameSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.WAITING_USERS_VOTE);
            }
            return;
        }
        // 先在数据库里找古诗，找到古诗直接成功，没有找到则开启投票
        else if (ffoGamePoemType.equals(FfoGamePoemType.ALL)) {
            log.debug("可以说古诗，也可以自由创作");
            if (ObjectUtils.isNotEmpty(poemEntity)) {
                log.debug("数据库中找到诗 {}", poemEntity);
                ffoGameSentenceDTO.setPoemId(poemEntity.getId());
                ffoGameSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.SUCCESS);
            } else {
                log.debug("没有在数据库中找到诗，开启投票");
                ffoGameSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.WAITING_USERS_VOTE);
            }
            return;
        }
        ffoGameSentenceDTO.setPoemId(poemEntity.getId());
        ffoGameSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.SUCCESS);
    }

    /**
     * 添加用户发送飞花令超时要做的任务
     *
     * @param ffoGameDTO
     */
    private void addUserSendSentenceTimeoutTask(FfoGameDTO ffoGameDTO) {
        var timeout = FfoGameUtil.getFfoSpeakerNextEndTime(ffoGameDTO);
        var roomId = ffoGameDTO.getRoomId();
        ffoTaskService.addSpeakTimeOutTask(roomId, FfoGameUtil.getTimeoutCron(timeout), () -> {
            FfoGameDTO fgd = ffoGameRedisDao.getFfoGameDTO(roomId);
            FfoGameSentenceDTO ffoGameSentenceDTO = new FfoGameSentenceDTO(
                    fgd.getNextSpeaker(), "", FfoGameSentenceJudgeType.NULL_CONTENT, timeout);
            fgd.getUserSentences().add(ffoGameSentenceDTO);
            ffoSentenceHandle(fgd);
            ffoTaskService.removeSpeakTimeOutTask(roomId);
        });
    }

    /**
     * 添加用户投票超时后要做的事
     *
     * @param roomId
     * @param timeout
     */
    private void addUserVoteTimeoutTask(String roomId, LocalDateTime timeout) {
        ffoTaskService.addVoteTimeOutTask(roomId, FfoGameUtil.getTimeoutCron(timeout), () -> {
            FfoGameDTO ffoGameDTO = ffoGameRedisDao.getFfoGameDTO(roomId);
            FfoGameSentenceDTO ffoGameSentenceDTO = ffoGameDTO.getLastSentenceDTO();
            ffoGameDTO.getPlayingUsers().forEach(user -> {
                // 不是当前发言的用户并且还没有投票的用户去添加弃权
                if (!user.equals(ffoGameSentenceDTO.getUser()) &&
                        !ffoGameSentenceDTO.getVotedUsers().contains(user)
                ) {
                    ffoGameSentenceDTO.sentenceVote(new FfoGameVoteDTO(user, FfoVoteType.ABSTAIN));
                }
            });
            ffoVoteHandle(ffoGameDTO);
            ffoTaskService.removeVoteTimeOutTask(roomId);
        });
    }

    /**
     * 用户发言通知
     * 发送当前用户发送的句子，并通知下一个用户要发言了
     *
     * @param ffoGameDTO
     */
    private void userFfoSpeakNotice(FfoGameDTO ffoGameDTO) {
        ffoGameNotice.ffoNextNotice(ffoGameDTO.getUsernames(), new FfoNextOutputVO(ffoGameDTO));
        ffoGameNotice.ffoSentenceNotice(ffoGameDTO.getUsernames(), Iterables.getLast(ffoGameDTO.getUserSentences()));
    }

    /**
     * 当前用户说的飞花令需要玩家投票，通知这句游戏的玩家投票
     *
     * @param ffoGameDTO
     */
    private void userFfoVoteNotice(FfoGameDTO ffoGameDTO) {
        FfoGameSentenceDTO ffoGameSentenceDTO = ffoGameDTO.getLastSentenceDTO();
        FfoVoteOutputVO ffoVoteOutputVO = new FfoVoteOutputVO(
                ffoGameSentenceDTO.getUser(), ffoGameSentenceDTO.getSentence(),
                ffoGameSentenceDTO.getCreateTime(), FfoGameUtil.getFfoVoteEndTime(ffoGameDTO));
        ffoGameNotice.ffoVoteNotice(ffoGameDTO.getUsernames(), ffoVoteOutputVO);
    }

    /**
     * 飞花令游戏结束
     * 1、将ffoGameDTO 转化为为Entity对象，存入持久层数据库
     * 2、通知玩家游戏结束，并发送本局游戏数据
     * 3、删除redis中的ffoGameDTO，修改游戏房间状态
     *
     * @param ffoGameDTO
     */
    private void ffoGameOver(FfoGameDTO ffoGameDTO) {
        log.debug("房间 {} 游戏结束, ffoGameDTO = {}", ffoGameDTO.getRoomId(), ffoGameDTO);
        ffoGameNotice.ffoGameOverNotice(ffoGameDTO.getUsernames(), new FfoGameOverOutputVO(ffoGameDTO));
        ffoService.saveFfoGame(ffoGameDTO);
        ffoGameRedisDao.delFfoGameDTO(ffoGameDTO.getRoomId());
        var ffoGameRoomDTO = ffoGameRoomRedisDao.getFfoGameRoomDTO(ffoGameDTO.getRoomId());
        ffoGameRoomDTO.setFfoStateType(FfoStateType.WAITING);
        ffoGameNotice.ffoGameRoomNotice(ffoGameRoomDTO);
        ffoGameRoomRedisDao.saveFfoGameRoomDTO(ffoGameRoomDTO);
    }

}
