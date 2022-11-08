package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.dto.FfoGameDTO;
import cn.zhumouren.poetryclub.bean.dto.FfoGameRoomDTO;
import cn.zhumouren.poetryclub.bean.dto.FfoGameUserSentenceDTO;
import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.mapper.FfoGameMapper;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomReqVO;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomResVO;
import cn.zhumouren.poetryclub.bean.vo.InputFfoSentenceVO;
import cn.zhumouren.poetryclub.bean.vo.OutputFfoSpeakInfoVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constants.GamesType;
import cn.zhumouren.poetryclub.constants.RedisKey;
import cn.zhumouren.poetryclub.constants.TimeSecondConstants;
import cn.zhumouren.poetryclub.constants.games.FfoGameVerseType;
import cn.zhumouren.poetryclub.constants.games.FfoStateType;
import cn.zhumouren.poetryclub.notice.StompFfoGameNotice;
import cn.zhumouren.poetryclub.service.FfoService;
import cn.zhumouren.poetryclub.service.RedisUserService;
import cn.zhumouren.poetryclub.service.TaskSchedulingService;
import cn.zhumouren.poetryclub.util.RedisUtil;
import cn.zhumouren.poetryclub.util.RoomIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Service
public class FfoServiceImpl implements FfoService {

    private final RedisUtil redisUtil;
    private final RedisUserService redisUserService;
    private final StompFfoGameNotice ffoGameNotice;
    private final TaskSchedulingService taskSchedulingService;

    public FfoServiceImpl(RedisUtil redisUtil, RedisUserService redisUserService, StompFfoGameNotice ffoGameNotice, TaskSchedulingService taskSchedulingService) {
        this.redisUtil = redisUtil;
        this.redisUserService = redisUserService;
        this.ffoGameNotice = ffoGameNotice;
        this.taskSchedulingService = taskSchedulingService;
    }

    /**
     * 只能是单机同步
     *
     * @param user
     * @param roomId
     * @return
     */
    @Transactional
    @Override
    public synchronized ResponseResult<Boolean> userEnterGameRoom(UserEntity user, String roomId) {

        FfoGameRoomDTO ffoGameRoomDTO = getFfoGameRoomDTO(roomId);
        if (ffoGameRoomDTO.getFfoStateType().equals(FfoStateType.PLAYING)) {
            return ResponseResult.failedWithMsg("游戏进行中，不可加入");
        }
        if (ffoGameRoomDTO.getUsers().size() == ffoGameRoomDTO.getMaxPlayers()) {
            return ResponseResult.failedWithMsg("房间人数已满");
        }
        if (ffoGameRoomDTO.getUsers().contains(user.getUsername())) {
            return ResponseResult.failedWithMsg("用户已在房间中");
        }
        ffoGameRoomDTO.getUsers().add(user.getUsername());
        redisUtil.hset(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId, ffoGameRoomDTO);
        redisUserService.userEnterGameRoom(user, GamesType.FFO, roomId);
        ffoGameNotice.userGameRoomActionNotice(user, user.getNickname() + "进入了房间", ffoGameRoomDTO.getUsers());
        return ResponseResult.success();
    }


    /**
     * 1、判断房间是否存在
     * 2、用户退出后，房间用户是否为空，为空则删除房间
     * 3、用户退出后，房间用户不为空，随机分配房主
     *
     * @param user
     * @return
     */
    @Transactional
    @Override
    public synchronized ResponseResult<Boolean> userLeaveGameRoom(UserEntity user) {
        String roomId = redisUserService.getUserGameRoomId(user, GamesType.FFO);
        if (ObjectUtils.isEmpty(roomId)) {
            return ResponseResult.failedWithMsg("用户不在游戏中");
        }
        if (!redisUtil.hHasKey(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId)) {
            return ResponseResult.failedWithMsg("飞花令游戏房间不存在");
        }
        FfoGameRoomDTO ffoGameRoomDTO = getFfoGameRoomDTO(roomId);
        if (ffoGameRoomDTO.getFfoStateType().equals(FfoStateType.PLAYING)) {
            return ResponseResult.failedWithMsg("游戏进行中，不能离开游戏");
        }
        ffoGameRoomDTO.getUsers().remove(user.getUsername());
        redisUserService.userLeaveGameRoom(user);
        // 如果users 长度为0，则删除房间
        // 否则从新设置房主
        if (ffoGameRoomDTO.getUsers().size() == 0) {
            delFfoGameRoom(ffoGameRoomDTO);
            return ResponseResult.success();
        } else {
            setFfoGameRoomOwner(ffoGameRoomDTO);
        }
        boolean b = saveFfoGameRoomDTO(ffoGameRoomDTO);
        ffoGameNotice.userGameRoomActionNotice(user, user.getNickname() + "离开了房间!", ffoGameRoomDTO.getUsers());
        return ResponseResult.bool(b);
    }

    private void delFfoGameRoom(FfoGameRoomDTO ffoGameRoomDTO) {
        redisUtil.hdel(RedisKey.FFO_GAME_ROOM_KEY.name(), ffoGameRoomDTO.getId());
    }

    private void setFfoGameRoomOwner(FfoGameRoomDTO ffoGameRoomDTO) {
        String u = ffoGameRoomDTO.getUsers().iterator().next();
        ffoGameRoomDTO.setHomeowner(u);
    }

    @Override
    public ResponseResult<Set<FfoGameRoomResVO>> listFfoGameRoom() {
        HashMap<String, FfoGameRoomDTO> ffoGameRoomDTOHashMap = (HashMap<String, FfoGameRoomDTO>) redisUtil.hmget(RedisKey.FFO_GAME_ROOM_KEY.name());
        Set<FfoGameRoomResVO> ffoGameRoomResVOs = new HashSet<>();
        ffoGameRoomDTOHashMap.forEach((roomId, ffoGameRoomDTO) -> {
            ffoGameRoomResVOs.add(FfoGameMapper.INSTANCE.ffoGameRoomDTOToFfoGameRoomResVO(ffoGameRoomDTO));
        });
        return ResponseResult.success(ffoGameRoomResVOs);
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
        FfoGameRoomDTO ffoGameRoomDTO = getFfoGameRoomDTO(roomId);
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
        saveFfoGameRoomDTO(ffoGameRoomDTO);
        FfoGameDTO ffoGameDTO = createFfoGameDTO(ffoGameRoomDTO);
        saveFfoGameDTO(ffoGameDTO);
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
     * 5. 通过检查后，存入redis，通知用户，并开启下一轮
     *
     * @param roomId
     * @param user
     * @param inputFfoSentenceVO
     */
    @Override
    public void userSendFfoSentence(String roomId, UserEntity user, InputFfoSentenceVO inputFfoSentenceVO) {
        String sentence = inputFfoSentenceVO.getSentence();
        FfoGameDTO ffoGameDTO = getFfoGameDTO(roomId);
        // 1. 检测该用户是否满足发送句子的条件
        if (ObjectUtils.isEmpty(ffoGameDTO)) {
            log.debug("roomId = {} ，没有这个房间", roomId);
            return;
        }
        if (!ffoGameDTO.getNextSpeaker().equals(user.getUsername())) {
            log.debug("next speaker = {}, 但是 user = {}, 不满足条件", ffoGameDTO.getNextSpeaker(), user.getUsername());
            return;
        }
        if (LocalDateTime.now().isAfter(getFfoSpeakerNextEndTime(ffoGameDTO))) {
            log.debug("user = {}, 超时作答，等待超时任务自动处理", user.getUsername());
            return;
        }
        // 2. 取消该用户的超时任务
        taskSchedulingService.removeTask("ffo_" + roomId);
        // 3. 检测句子是否符合本局设置
        // 3.1 检测句子长度
        if (ffoGameDTO.getFfoGameVerseType().equals(FfoGameVerseType.FIVE_CHARACTER) && sentence.length() == 5) {
            log.debug("满足");
        } else if (ffoGameDTO.getFfoGameVerseType().equals(FfoGameVerseType.SEVEN_CHARACTER) && sentence.length() == 7) {
            log.debug("满足");
        } else if (ffoGameDTO.getFfoGameVerseType().equals(FfoGameVerseType.ALL)) {
            log.debug("满足");
        } else {
            log.debug("不满足");
        }
        // 3.2 检测句子是否在相应位置
        if (ffoGameDTO.getAllowWordInAny() && sentence.contains(ffoGameDTO.getKeyword().toString())) {
            log.debug("满足");
        } else if (!ffoGameDTO.getAllowWordInAny() && sentence.matches("")) {
            log.debug("满足");
        } else {
            log.debug("不满足");
        }


        FfoGameUserSentenceDTO ffoGameUserSentenceDTO = new FfoGameUserSentenceDTO();

        userFfoSpeakNotice(ffoGameDTO, ffoGameUserSentenceDTO);
    }

    private void addUserSendFfoSentenceTimeoutTask(FfoGameDTO ffoGameDTO) {
        LocalDateTime timeout = getFfoSpeakerNextEndTime(ffoGameDTO);
        taskSchedulingService.addTask("ffo_" + ffoGameDTO.getRoomId(), getFfoSentenceTimeoutCron(timeout), () -> {

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


    private FfoGameDTO createFfoGameDTO(FfoGameRoomDTO ffoGameRoomDTO) {
        FfoGameDTO ffoGameDTO = FfoGameMapper.INSTANCE.ffoGameRoomDTOToFfoGameDTO(ffoGameRoomDTO);
        ffoGameDTO.setRoomId(ffoGameRoomDTO.getId());
        ffoGameDTO.setCreateTime(LocalDateTime.now());
        ffoGameDTO.setKeyword(ffoGameRoomDTO.getKeyword());
        ffoGameDTO.setPlayingUsers(new ConcurrentLinkedQueue<>(ffoGameRoomDTO.getUsers()));
        ffoGameDTO.setRanking(new ArrayDeque<>());
        ffoGameDTO.setUserSentences(new LinkedList<>());
        ffoGameDTO.setNextSpeaker(ffoGameRoomDTO.getUsers().get(0));
        return ffoGameDTO;
    }

    @Transactional
    @Override
    public ResponseResult<String> userCreateGameRoom(UserEntity user, FfoGameRoomReqVO ffoGameRoomReqVO) {
        if (!redisUtil.hasKey(RedisKey.FFO_GAME_ROOM_KEY.name())) {
            redisUtil.hmset(RedisKey.FFO_GAME_ROOM_KEY.name(), new HashMap<>());
        }
        String roomId = RoomIdUtil.generateRoomId();
        while (redisUtil.hHasKey(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId)) {
            roomId = RoomIdUtil.generateRoomId();
        }
        FfoGameRoomDTO ffoGameRoomDTO = FfoGameRoomDTO.creatFfoGameRoomDTO(ffoGameRoomReqVO, roomId,
                user.getUsername(), user.getUsername());
        saveFfoGameRoomDTO(ffoGameRoomDTO);
        redisUserService.userEnterGameRoom(user, GamesType.FFO, roomId);
        return ResponseResult.success(roomId);
    }

    private FfoGameRoomDTO getFfoGameRoomDTO(String roomId) {
        return (FfoGameRoomDTO) redisUtil.hget(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId);
    }

    private boolean saveFfoGameRoomDTO(FfoGameRoomDTO ffoGameRoomDTO) {
        return redisUtil.hset(RedisKey.FFO_GAME_ROOM_KEY.name(), ffoGameRoomDTO.getId(), ffoGameRoomDTO, TimeSecondConstants.HOUR_SECOND * 12);
    }

    private boolean saveFfoGameDTO(FfoGameDTO ffoGameDTO) {
        return redisUtil.hset(RedisKey.FFO_GAME_KEY.name(), ffoGameDTO.getRoomId(), ffoGameDTO, TimeSecondConstants.HOUR_SECOND * 12);
    }

    private FfoGameDTO getFfoGameDTO(String roomId) {
        return (FfoGameDTO) redisUtil.hget(RedisKey.FFO_GAME_KEY.name(), roomId);
    }

    @Override
    public void sessionDisconnect(UserEntity user) {
        userLeaveGameRoom(user);
    }
}
