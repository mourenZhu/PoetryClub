package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;

public interface UserWebsocketService {

    void disconnect(UserEntity user);

    void subscribe(String destination,UserEntity userEntity);

}
