package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.constants.games.FfoType;

public interface FfoService {

    String userCreateGameRoom(UserEntity user, FfoType ffoType);

    boolean userEnterGameRoom(UserEntity user, String roomId);
}
