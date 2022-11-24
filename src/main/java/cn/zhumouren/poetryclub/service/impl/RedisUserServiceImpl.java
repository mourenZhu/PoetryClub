package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.dto.FfoGameRoomDTO;
import cn.zhumouren.poetryclub.bean.dto.UserDTO;
import cn.zhumouren.poetryclub.bean.dto.UserGameStateDTO;
import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.constant.GamesType;
import cn.zhumouren.poetryclub.constant.RedisKey;
import cn.zhumouren.poetryclub.constant.TimeSecondConstant;
import cn.zhumouren.poetryclub.service.RedisUserService;
import cn.zhumouren.poetryclub.util.RedisUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisUserServiceImpl implements RedisUserService {

    private final RedisUtil redisUtil;

    public RedisUserServiceImpl(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    public boolean userEnterGameRoom(UserEntity user, GamesType gamesType, String roomId) {
        UserGameStateDTO userGameStateDTO = new UserGameStateDTO(roomId, gamesType);
        return redisUtil.hset(RedisKey.USER_GAME_STATE.name(), user.getUsername(), userGameStateDTO, TimeSecondConstant.HOUR_SECOND * 12);
    }

    @Override
    public void userLeaveGameRoom(UserEntity user) {
        redisUtil.hdel(RedisKey.USER_GAME_STATE.name(), user.getUsername());
    }

    @Override
    public String getUserGameRoomId(UserEntity user, GamesType gamesType) {
        UserGameStateDTO userGameStateDTO = (UserGameStateDTO) redisUtil
                .hget(RedisKey.USER_GAME_STATE.name(), user.getUsername());
        if (ObjectUtils.isEmpty(userGameStateDTO)) {
            return "";
        }
        return userGameStateDTO.getRoomId();
    }

    @Override
    public List<UserDTO> listGameRoomUser(String roomId) {
        FfoGameRoomDTO ffoGameRoomDTO = (FfoGameRoomDTO) redisUtil.hget(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId);
        return ffoGameRoomDTO.getUsers();
    }

}
