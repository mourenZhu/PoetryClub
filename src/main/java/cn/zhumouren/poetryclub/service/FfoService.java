package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomResVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constants.games.FfoType;

import java.util.Set;

public interface FfoService extends UserWebsocketService {

    ResponseResult<String> userCreateGameRoom(UserEntity user, String roomName, FfoType ffoType);

    ResponseResult<Boolean> userEnterGameRoom(UserEntity user, String roomId);

    ResponseResult<Boolean> userLeaveGameRoom(UserEntity user);

    ResponseResult<Set<FfoGameRoomResVO>> listFfoGameRoom();
}
