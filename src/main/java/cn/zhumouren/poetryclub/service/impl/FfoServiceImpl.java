package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.constants.RedisKey;
import cn.zhumouren.poetryclub.constants.games.FfoType;
import cn.zhumouren.poetryclub.service.FfoService;
import cn.zhumouren.poetryclub.utils.RedisUtil;
import cn.zhumouren.poetryclub.utils.RoomIdUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
    public synchronized boolean userEnterGameRoom(UserEntity user, String roomId) {
        String roomType = (String) redisUtil.hget(RedisKey.FFO_GAME_ROOM_TYPE_KEY.name(), roomId);
        Set<String> usernameSet = (Set<String>) redisUtil.hget(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId);
        if (roomType.equals(FfoType.FIVE_PLAYER_GAME.name())) {
            if (usernameSet.size() >= 5) {
                return false;
            }
        } else if (roomType.equals(FfoType.SEVEN_PLAYER_GAME.name())) {
            if (usernameSet.size() >= 7) {
                return false;
            }
        }
        usernameSet.add(user.getUsername());
        redisUtil.hset(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId, usernameSet);
        return true;
    }

    @Transactional
    @Override
    public String userCreateGameRoom(UserEntity user, FfoType ffoType) {
        if (!redisUtil.hasKey(RedisKey.FFO_GAME_ROOM_KEY.name())) {
            redisUtil.hmset(RedisKey.FFO_GAME_ROOM_KEY.name(), new HashMap<>());
        }
        String roomId = RoomIdUtil.generateRoomId();
        while (redisUtil.hHasKey(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId)) {
            roomId = RoomIdUtil.generateRoomId();
        }
        Set<String> usernameSet = new HashSet<>();
        usernameSet.add(user.getUsername());
        redisUtil.hset(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId, usernameSet);
        createFfoGameRoomType(roomId, ffoType);
        return roomId;
    }

    private void createFfoGameRoomType(String roomId, FfoType ffoType) {
        if (!redisUtil.hasKey(RedisKey.FFO_GAME_ROOM_TYPE_KEY.name())) {
            redisUtil.hmset(RedisKey.FFO_GAME_ROOM_TYPE_KEY.name(), new HashMap<>());
        }
        redisUtil.hset(RedisKey.FFO_GAME_ROOM_TYPE_KEY.name(), roomId, ffoType.name());
    }

    public Set<String> getUsernameSetByRoomId(String roomId) {
        Set<String> usernameSet = (Set<String>) redisUtil.hget(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId);
        return usernameSet;
    }
}
