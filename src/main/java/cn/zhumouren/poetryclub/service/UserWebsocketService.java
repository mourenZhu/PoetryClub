package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;

public interface UserWebsocketService {

    void sessionDisconnect(UserEntity user);

}
