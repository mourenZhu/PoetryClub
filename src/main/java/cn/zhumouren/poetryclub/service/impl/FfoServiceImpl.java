package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.dto.FfoGameRoomDTO;
import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constants.RedisKey;
import cn.zhumouren.poetryclub.constants.games.FfoType;
import cn.zhumouren.poetryclub.service.FfoService;
import cn.zhumouren.poetryclub.utils.RedisUtil;
import cn.zhumouren.poetryclub.utils.RoomIdUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
public class FfoServiceImpl implements FfoService {

    private final RedisUtil redisUtil;

    public FfoServiceImpl(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
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
        FfoGameRoomDTO ffoGameRoomDTO = (FfoGameRoomDTO) redisUtil.hget(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId);
        if (ffoGameRoomDTO.getFfoType().equals(FfoType.FIVE_PLAYER_GAME)) {
            if (ffoGameRoomDTO.getUsers().size() >= 5) {
                ResponseResult.failed("房间人数已满");
            }
        } else if (ffoGameRoomDTO.getFfoType().equals(FfoType.SEVEN_PLAYER_GAME)) {
            if (ffoGameRoomDTO.getUsers().size() >= 7) {
                ResponseResult.failed("房间人数已满");
            }
        }
        ffoGameRoomDTO.getUsers().add(user.getUsername());
        redisUtil.hset(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId, ffoGameRoomDTO);
        return ResponseResult.success();
    }

    @Transactional
    @Override
    public ResponseResult<String> userCreateGameRoom(UserEntity user, String roomName, FfoType ffoType) {
        if (!redisUtil.hasKey(RedisKey.FFO_GAME_ROOM_KEY.name())) {
            redisUtil.hmset(RedisKey.FFO_GAME_ROOM_KEY.name(), new HashMap<>());
        }
        String roomId = RoomIdUtil.generateRoomId();
        while (redisUtil.hHasKey(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId)) {
            roomId = RoomIdUtil.generateRoomId();
        }
        FfoGameRoomDTO ffoGameRoomDTO = new FfoGameRoomDTO(roomId, roomName, ffoType, user.getUsername(), user.getUsername());
        redisUtil.hset(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId, ffoGameRoomDTO);
        return ResponseResult.success(roomId);
    }

}
