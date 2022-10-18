package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.constants.GamesType;

import java.util.Set;

public interface RedisUserService {

    boolean userEnterGameRoom(UserEntity user, GamesType gamesType, String roomId);

    void userLeaveGameRoom(UserEntity user);

    String getUserGameRoomId(UserEntity user, GamesType gamesType);

    Set<String> listGameRoomUser(String roomId);
}
