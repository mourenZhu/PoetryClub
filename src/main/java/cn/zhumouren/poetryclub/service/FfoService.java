package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constants.games.FfoType;

public interface FfoService {

    ResponseResult<String> userCreateGameRoom(UserEntity user, String roomName, FfoType ffoType);

    ResponseResult<Boolean> userEnterGameRoom(UserEntity user, String roomId);
}
