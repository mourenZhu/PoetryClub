package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.dto.FfoGameRoomDTO;
import cn.zhumouren.poetryclub.bean.dto.OutputMessageDTO;
import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.mapper.FfoGameRoomMapper;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomReqVO;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomResVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constants.GamesType;
import cn.zhumouren.poetryclub.constants.MessageDestinations;
import cn.zhumouren.poetryclub.constants.RedisKey;
import cn.zhumouren.poetryclub.constants.games.FfoStateType;
import cn.zhumouren.poetryclub.service.FfoService;
import cn.zhumouren.poetryclub.service.RedisUserService;
import cn.zhumouren.poetryclub.util.RedisUtil;
import cn.zhumouren.poetryclub.util.RoomIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class FfoServiceImpl implements FfoService {

    private final RedisUtil redisUtil;
    private final RedisUserService redisUserService;
    private final SimpMessagingTemplate template;

    public FfoServiceImpl(RedisUtil redisUtil, RedisUserService redisUserService, SimpMessagingTemplate template) {
        this.redisUtil = redisUtil;
        this.redisUserService = redisUserService;
        this.template = template;
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
        ffoGameRoomDTO.getUsers().add(user.getUsername());
        redisUtil.hset(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId, ffoGameRoomDTO);
        redisUserService.userEnterGameRoom(user, GamesType.FFO, roomId);
        userGameRoomActionNotice(user, user.getNickname() + "进入了房间", ffoGameRoomDTO.getUsers());
        return ResponseResult.success();
    }

    /**
     * 用户在房间中的行为通知
     *
     * @param user        做出行为的用户
     * @param msg         要通知的信息
     * @param noticeUsers 被通知的用户
     */
    private void userGameRoomActionNotice(UserEntity user, String msg, Iterable<String> noticeUsers) {
        noticeUsers.forEach(username -> {
            if (!username.equals(user.getUsername())) {
                log.debug("发送给 = {}", username);
                template.convertAndSendToUser(username, MessageDestinations.USER_GAME_ROOM_MESSAGE_DESTINATION,
                        OutputMessageDTO.getOutputMessageDTO(user, msg));
            }
        });
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
        userGameRoomActionNotice(user, user.getNickname() + "离开了房间!", ffoGameRoomDTO.getUsers());
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
            ffoGameRoomResVOs.add(FfoGameRoomMapper.INSTANCE.ffoGameRoomDTOToFfoGameRoomResVO(ffoGameRoomDTO));
        });
        return ResponseResult.success(ffoGameRoomResVOs);
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
        return redisUtil.hset(RedisKey.FFO_GAME_ROOM_KEY.name(), ffoGameRoomDTO.getId(), ffoGameRoomDTO);
    }

    @Override
    public void sessionDisconnect(UserEntity user) {
        userLeaveGameRoom(user);
    }
}
