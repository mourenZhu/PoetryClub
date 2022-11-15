package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.constant.GamesType;

import java.util.List;

public interface RedisUserService {

    boolean userEnterGameRoom(UserEntity user, GamesType gamesType, String roomId);

    void userLeaveGameRoom(UserEntity user);

    String getUserGameRoomId(UserEntity user, GamesType gamesType);

    List<String> listGameRoomUser(String roomId);
}
