package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.dto.FfoGameDTO;
import cn.zhumouren.poetryclub.bean.dto.FfoGameRoomDTO;
import cn.zhumouren.poetryclub.bean.dto.FfoGameUserSentenceDTO;
import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.InputFfoSentenceVO;
import cn.zhumouren.poetryclub.bean.vo.OutputFfoSpeakInfoVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constants.GamesType;
import cn.zhumouren.poetryclub.constants.games.FfoGamePoemType;
import cn.zhumouren.poetryclub.constants.games.FfoGameSentenceJudgeType;
import cn.zhumouren.poetryclub.constants.games.FfoStateType;
import cn.zhumouren.poetryclub.dao.FfoGameRedisDAO;
import cn.zhumouren.poetryclub.dao.FfoGameRoomRedisDAO;
import cn.zhumouren.poetryclub.dao.PoemEntityRepository;
import cn.zhumouren.poetryclub.notice.StompFfoGameNotice;
import cn.zhumouren.poetryclub.service.FfoPlayingService;
import cn.zhumouren.poetryclub.service.RedisUserService;
import cn.zhumouren.poetryclub.service.FfoTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class FfoPlayingServiceImpl implements FfoPlayingService {
    private final RedisUserService redisUserService;

    private final PoemEntityRepository poemEntityRepository;
    private final FfoGameRoomRedisDAO ffoGameRoomRedisDao;
    private final FfoGameRedisDAO ffoGameRedisDao;
    private final StompFfoGameNotice ffoGameNotice;
    private final FfoTaskService ffoTaskService;

    public FfoPlayingServiceImpl(RedisUserService redisUserService, PoemEntityRepository poemEntityRepository,
                                 FfoGameRoomRedisDAO ffoGameRoomRedisDao, FfoGameRedisDAO ffoGameRedisDao,
                                 StompFfoGameNotice ffoGameNotice, FfoTaskService ffoTaskService) {
        this.redisUserService = redisUserService;
        this.poemEntityRepository = poemEntityRepository;
        this.ffoGameRoomRedisDao = ffoGameRoomRedisDao;
        this.ffoGameRedisDao = ffoGameRedisDao;
        this.ffoGameNotice = ffoGameNotice;
        this.ffoTaskService = ffoTaskService;
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
        if (!ffoGameRoomDTO.getHomeowner().equals(user.getUsername())) {
            return ResponseResult.failedWithMsg("玩家不是房主，不能开启游戏");
        }
        if (ffoGameRoomDTO.getFfoStateType().equals(FfoStateType.PLAYING)) {
            return ResponseResult.failedWithMsg("已在游戏中，不能重复开启游戏");
        }
        if (ffoGameRoomDTO.getUsers().size() == 1) {
            return ResponseResult.failedWithMsg("至少需要两位玩家才能开始游戏!");
        }
        // 开启游戏
        ffoGameRoomDTO.setFfoStateType(FfoStateType.PLAYING);
        ffoGameRoomRedisDao.saveFfoGameRoomDTO(ffoGameRoomDTO);
        FfoGameDTO ffoGameDTO = FfoGameDTO.getFfoGameDTOByFfoGameRoomDTO(ffoGameRoomDTO);
        ffoGameRedisDao.saveFfoGameDTO(ffoGameDTO);
        // 开始通知用户游戏开始
        OutputFfoSpeakInfoVO outputFfoSpeakInfoVO = new OutputFfoSpeakInfoVO();
        outputFfoSpeakInfoVO.setNextSpeaker(ffoGameDTO.getNextSpeaker());
        ffoGameNotice.ffoSpeakNotice(ffoGameDTO.getUsers(), outputFfoSpeakInfoVO);
        // 添加玩家超时发言任务
        addUserSendFfoSentenceTimeoutTask(ffoGameDTO);
        return ResponseResult.success();
    }

    /**
     * 用户发送飞花令的句子
     * 1. 检测该用户是否满足发送句子的条件
     * 2. 取消该用户的超时任务
     * 3. 检测句子是否符合本局设置
     * 4. 如果允许用户自创作，并满足本局设置，然后通知玩家进行投票
     * 5. 把本回合数据存入ffoGameDTO中
     * 6. 如果只剩一个人，则游戏结束
     *
     * @param roomId
     * @param userEntity
     * @param inputFfoSentenceVO
     */
    @Override
    public void userSendFfoSentence(String roomId, UserEntity userEntity, InputFfoSentenceVO inputFfoSentenceVO) {
        String sentence = inputFfoSentenceVO.getSentence();
        FfoGameDTO ffoGameDTO = ffoGameRedisDao.getFfoGameDTO(roomId);
        // 1. 检测该用户是否满足发送句子的条件
        if (ObjectUtils.isEmpty(ffoGameDTO)) {
            log.debug("roomId = {} ，没有这个房间", roomId);
            return;
        }
        if (!ffoGameDTO.getNextSpeaker().equals(userEntity.getUsername())) {
            log.debug("next speaker = {}, 但是 user = {}, 不满足条件", ffoGameDTO.getNextSpeaker(), userEntity.getUsername());
            return;
        }
        if (LocalDateTime.now().isAfter(getFfoSpeakerNextEndTime(ffoGameDTO))) {
            log.debug("user = {}, 超时作答，等待超时任务自动处理", userEntity.getUsername());
            return;
        }
        // 2. 取消该用户的超时任务
        ffoTaskService.removeSpeakTimeOutTask(roomId);
        // 创建 FfoGameUserSentenceDTO，除了句子判断，其他的属性都一样
        FfoGameUserSentenceDTO ffoGameUserSentenceDTO = new FfoGameUserSentenceDTO(userEntity.getUsername(), sentence, LocalDateTime.now());
        // 3. 检测句子是否符合本局设置
        log.debug("{} 本轮发送的句子为 {}", userEntity.getUsername(), sentence);
        setSentenceJudgeType(ffoGameDTO, ffoGameUserSentenceDTO);
        FfoGameSentenceJudgeType sentenceJudgeType = ffoGameUserSentenceDTO.getSentenceJudgeType();
        // 4. 如果允许用户自创作，并满足本局设置，然后通知玩家进行投票
        if (sentenceJudgeType.equals(FfoGameSentenceJudgeType.WAITING_USERS_VOTE)) {

        }
        // 5. 把本回合数据存入ffoGameDTO中
        String user = ffoGameDTO.getPlayingUsers().poll();
        // 用户这句诗满足本局所有条件
        if (sentenceJudgeType.equals(FfoGameSentenceJudgeType.SUCCESS)) {
            ffoGameDTO.getPlayingUsers().offer(user);

        }
        // 用户这句诗不满足本局条件
        else {
            ffoGameDTO.getRanking().push(user);
        }
        ffoGameDTO.getUserSentences().add(ffoGameUserSentenceDTO);
        // 6. 如果只剩一个人，则游戏结束
        if (ffoGameDTO.getPlayingUsers().size() == 1) {
            String u = ffoGameDTO.getPlayingUsers().poll();
            ffoGameDTO.getRanking().push(u);
            ffoGameDTO.setEndTime(LocalDateTime.now());
        }


        userFfoSpeakNotice(ffoGameDTO, ffoGameUserSentenceDTO);
    }

    /**
     * 设置句子的状态
     * 先一步步判断不满足的情况，能走到最后就是成功
     *
     * @param ffoGameDTO
     * @param ffoGameUserSentenceDTO
     */
    private void setSentenceJudgeType(FfoGameDTO ffoGameDTO, FfoGameUserSentenceDTO ffoGameUserSentenceDTO) {
        String user = ffoGameUserSentenceDTO.getUser();
        String sentence = ffoGameUserSentenceDTO.getSentence();
        // 3.1 检测句子长度
        // 飞花令为空
        if (ObjectUtils.isEmpty(sentence)) {
            ffoGameUserSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.NULL_CONTENT);
            return;
        }
        // 飞花令长度与本句设置不匹配
        if (ffoGameDTO.getConstantSentenceLength()) {
            if (!ffoGameDTO.getMaxSentenceLength().equals(sentence.length())) {
                ffoGameUserSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.LENGTH_NOT_MATCH);
                return;
            }
        }
        // 3.2 检测句子是否在相应位置
        // 允许 令 在任意位置，但句子中不含 令
        if (!ffoGameDTO.getAllowWordInAny() && sentence.contains(ffoGameDTO.getKeyword().toString())) {
            log.debug("句子中不含 令， {} 本轮结束", user);
            ffoGameUserSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.NO_KEYWORD);
            return;
        }
        // 令 要处于指定位置，但不处于指定位置
        else if (!(!ffoGameDTO.getAllowWordInAny() &&
                sentence.matches(String.format("^[\\u4e00-\\u9fa5]{%d}%c.*",
                        ffoGameDTO.getKeywordIndex(), ffoGameDTO.getKeyword())))) {
            log.debug("令 要处于指定位置，但不处于指定位置，{} 本轮结束", user);
            ffoGameUserSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.KEYWORD_NOT_IN_CORRECT_POSITION);
            return;
        }
        // 3.3 检查允许说什么样类型的诗句
        FfoGamePoemType ffoGamePoemType = ffoGameDTO.getFfoGamePoemType();
        if (ffoGamePoemType.equals(FfoGamePoemType.ONLY_ANCIENTS_POEM)) {
            log.debug("只允许古诗，但没有找到古诗了，{} 本轮结束", user);
            PoemEntity poemEntity = poemEntityRepository.findByContentContains(sentence);
            if (ObjectUtils.isEmpty(poemEntity)) {
                ffoGameUserSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.KEYWORD_NOT_IN_CORRECT_POSITION);
                return;
            }
        } else if (ffoGamePoemType.equals(FfoGamePoemType.ONLY_SELF_CREAT)) {
            PoemEntity poemEntity = poemEntityRepository.findByContentContains(sentence);
            if (ObjectUtils.isNotEmpty(poemEntity)) {
                log.debug("只允许自创作，但找到了古诗，{} 本轮结束", user);
                ffoGameUserSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.ONLY_SELF_CREAT_BUT_FIND);
            } else {
                log.debug("只允许自创作，没有找到古诗，开启投票");
                ffoGameUserSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.WAITING_USERS_VOTE);
            }
            return;
        }
        // 先在数据库里找古诗，找到古诗直接成功，没有找到则开启投票
        else if (ffoGamePoemType.equals(FfoGamePoemType.ALL)) {
            log.debug("可以说古诗，也可以自由创作");
            PoemEntity poemEntity = poemEntityRepository.findByContentContains(sentence);
            if (ObjectUtils.isNotEmpty(poemEntity)) {
                log.debug("数据库中找到诗 {}", poemEntity);
                ffoGameUserSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.SUCCESS);
            } else {
                log.debug("没有在数据库中找到诗，开启投票");
                ffoGameUserSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.WAITING_USERS_VOTE);
            }
            return;
        }
        ffoGameUserSentenceDTO.setSentenceJudgeType(FfoGameSentenceJudgeType.SUCCESS);
    }

    private void addUserSendFfoSentenceTimeoutTask(FfoGameDTO ffoGameDTO) {
        LocalDateTime timeout = getFfoSpeakerNextEndTime(ffoGameDTO);
        ffoTaskService.addSpeakTimeOutTask(ffoGameDTO.getRoomId(), getFfoSentenceTimeoutCron(timeout), () -> {

        });
    }

    /**
     * 通过 LocalDateTime 来获取 cron 表达式
     *
     * @param timeout
     * @return
     */
    private String getFfoSentenceTimeoutCron(LocalDateTime timeout) {
        return timeout.getSecond() + " " + timeout.getMinute() + " " + timeout.getHour() + " "
                + " " + timeout.getDayOfMonth() + " " + timeout.getMonthValue() + " ? " + timeout.getYear();
    }

    /**
     * 用户发言通知
     * 发送当前用户发送的句子，并通知下一个用户要发言了
     *
     * @param ffoGameDTO
     * @param ffoGameUserSentenceDTO
     */
    private void userFfoSpeakNotice(FfoGameDTO ffoGameDTO, FfoGameUserSentenceDTO ffoGameUserSentenceDTO) {
        OutputFfoSpeakInfoVO outputFfoSpeakInfoVO = new OutputFfoSpeakInfoVO();
        outputFfoSpeakInfoVO.setNextSpeaker(ffoGameDTO.getNextSpeaker());
        outputFfoSpeakInfoVO.setCurrentSpeaker(ffoGameUserSentenceDTO.getUser());
        outputFfoSpeakInfoVO.setCurrentSentence(ffoGameUserSentenceDTO.getSentence());
        outputFfoSpeakInfoVO.setCurrentSentenceJudgeType(ffoGameUserSentenceDTO.getSentenceJudgeType());
        outputFfoSpeakInfoVO.setSpeakingTime(ffoGameUserSentenceDTO.getCreateTime());
        outputFfoSpeakInfoVO.setNextEndTime(getFfoSpeakerNextEndTime(ffoGameDTO));
        ffoGameNotice.ffoSpeakNotice(ffoGameDTO.getUsers(), outputFfoSpeakInfoVO);
    }

    /**
     * 获取飞花令下一个发言者超时发言的时间
     *
     * @param ffoGameDTO
     * @return
     */
    private LocalDateTime getFfoSpeakerNextEndTime(FfoGameDTO ffoGameDTO) {
        LocalDateTime timeout;
        if (ffoGameDTO.getUserSentences().size() == 0) {
            timeout = ffoGameDTO.getCreateTime().plusSeconds(ffoGameDTO.getPlayerPreparationSecond());
        } else {
            timeout = ffoGameDTO.getUserSentences()
                    .get(ffoGameDTO.getUserSentences().size() - 1)
                    .getCreateTime().plusSeconds(ffoGameDTO.getPlayerPreparationSecond());
        }
        return timeout;
    }
}
