package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.dto.FfoGameDTO;
import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.FfoGameResVo;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomReqVO;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomResVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constant.games.FfoGamePoemType;
import cn.zhumouren.poetryclub.constant.games.FfoStateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FfoService extends UserWebsocketService {

    ResponseResult userCreateGameRoom(UserEntity user, FfoGameRoomReqVO ffoGameRoomReqVO);

    ResponseResult<Boolean> userEnterGameRoom(UserEntity user, String roomId);

    ResponseResult<Boolean> userLeaveGameRoom(UserEntity user);

    ResponseResult<List<FfoGameRoomResVO>> listFfoGameRoom();

    ResponseResult<List<FfoGameRoomResVO>> listFfoGameRoom(
            String roomId, String keyword, Boolean allowWordInAny, FfoGamePoemType ffoGamePoemType, FfoStateType ffoStateType);

    /**
     * 把用户踢出房间
     *
     * @param roomId
     * @param homeowner
     * @param kickOutUser
     * @return
     */
    void kickOutUser(String roomId, UserEntity homeowner, String kickOutUser);

    void saveFfoGame(FfoGameDTO ffoGameDTO);

    void updateUsersSequence(String roomId, UserEntity homeowner, List<String> users);

    /**
     * 用户更新房间数据
     *
     * @param roomId
     * @param homeowner
     * @param ffoGameRoomReqVO
     */
    void updateGameRoom(String roomId, UserEntity homeowner, FfoGameRoomReqVO ffoGameRoomReqVO);

    ResponseResult<Page<FfoGameResVo>> listFfoGame(Pageable pageable);

    ResponseResult<Page<FfoGameResVo>> listUserFfoGame(String username, Pageable pageable);

    ResponseResult<Page<FfoGameResVo>> listUserFfoGame(String username, Character keyword, Pageable pageable);

    /**
     * 复杂查询，如果参数为空则不加入查询
     *
     * @param username
     * @param keyword
     * @param pageable
     * @return
     */
    ResponseResult<Page<FfoGameResVo>> specificationListFfoGame(
            String username, Character keyword, Pageable pageable);

    /**
     * 获取飞花令的游戏记录
     *
     * @param id
     * @return
     */
    ResponseResult<FfoGameResVo> getFfoGame(Long id);

}
