package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.dto.FfoGameRoomDTO;
import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.mapper.FfoGameMapper;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomReqVO;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomResVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constants.GamesType;
import cn.zhumouren.poetryclub.constants.RedisKey;
import cn.zhumouren.poetryclub.constants.games.FfoStateType;
import cn.zhumouren.poetryclub.dao.FfoGameRoomRedisDAO;
import cn.zhumouren.poetryclub.notice.StompFfoGameNotice;
import cn.zhumouren.poetryclub.service.FfoService;
import cn.zhumouren.poetryclub.service.RedisUserService;
import cn.zhumouren.poetryclub.util.RedisUtil;
import cn.zhumouren.poetryclub.util.RoomIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class FfoServiceImpl implements FfoService {

    private final RedisUtil redisUtil;
    private final RedisUserService redisUserService;

    private final FfoGameRoomRedisDAO ffoGameRoomRedisDao;
    private final StompFfoGameNotice ffoGameNotice;

    public FfoServiceImpl(RedisUtil redisUtil, RedisUserService redisUserService,
                          FfoGameRoomRedisDAO ffoGameRoomRedisDao, StompFfoGameNotice ffoGameNotice) {
        this.redisUtil = redisUtil;
        this.redisUserService = redisUserService;
        this.ffoGameRoomRedisDao = ffoGameRoomRedisDao;
        this.ffoGameNotice = ffoGameNotice;
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
    public synchronized ResponseResult<Boolean> userEnterGameRoom(UserEntity user, String roomId) {

        FfoGameRoomDTO ffoGameRoomDTO = ffoGameRoomRedisDao.getFfoGameRoomDTO(roomId);
        if (ffoGameRoomDTO.getFfoStateType().equals(FfoStateType.PLAYING)) {
            return ResponseResult.failedWithMsg("游戏进行中，不可加入");
        }
        if (ffoGameRoomDTO.getUsers().size() == ffoGameRoomDTO.getMaxPlayers()) {
            return ResponseResult.failedWithMsg("房间人数已满");
        }
        if (ffoGameRoomDTO.getUsers().contains(user.getUsername())) {
            return ResponseResult.failedWithMsg("用户已在房间中");
        }
        ffoGameRoomDTO.getUsers().add(user.getUsername());
        redisUtil.hset(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId, ffoGameRoomDTO);
        redisUserService.userEnterGameRoom(user, GamesType.FFO, roomId);
        ffoGameNotice.userGameRoomActionNotice(user, user.getNickname() + "进入了房间", ffoGameRoomDTO.getUsers());
        return ResponseResult.success();
    }


    /**
     * 1、判断房间是否存在
     * 2、用户退出后，房间用户是否为空，为空则删除房间
     * 3、用户退出后，房间用户不为空，随机分配房主
     *
     * @param user
     * @return
     */
    @Transactional
    @Override
    public synchronized ResponseResult<Boolean> userLeaveGameRoom(UserEntity user) {
        String roomId = redisUserService.getUserGameRoomId(user, GamesType.FFO);
        if (ObjectUtils.isEmpty(roomId)) {
            return ResponseResult.failedWithMsg("用户不在游戏中");
        }
        if (!redisUtil.hHasKey(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId)) {
            return ResponseResult.failedWithMsg("飞花令游戏房间不存在");
        }
        FfoGameRoomDTO ffoGameRoomDTO = ffoGameRoomRedisDao.getFfoGameRoomDTO(roomId);
        if (ffoGameRoomDTO.getFfoStateType().equals(FfoStateType.PLAYING)) {
            return ResponseResult.failedWithMsg("游戏进行中，不能离开游戏");
        }
        ffoGameRoomDTO.getUsers().remove(user.getUsername());
        redisUserService.userLeaveGameRoom(user);
        // 如果users 长度为0，则删除房间
        // 否则从新设置房主
        if (ffoGameRoomDTO.getUsers().size() == 0) {
            delFfoGameRoom(ffoGameRoomDTO);
            return ResponseResult.success();
        } else {
            setFfoGameRoomOwner(ffoGameRoomDTO);
        }
        boolean b = ffoGameRoomRedisDao.saveFfoGameRoomDTO(ffoGameRoomDTO);
        ffoGameNotice.userGameRoomActionNotice(user, user.getNickname() + "离开了房间!", ffoGameRoomDTO.getUsers());
        return ResponseResult.bool(b);
    }

    private void delFfoGameRoom(FfoGameRoomDTO ffoGameRoomDTO) {
        redisUtil.hdel(RedisKey.FFO_GAME_ROOM_KEY.name(), ffoGameRoomDTO.getId());
    }

    private void setFfoGameRoomOwner(FfoGameRoomDTO ffoGameRoomDTO) {
        String u = ffoGameRoomDTO.getUsers().iterator().next();
        ffoGameRoomDTO.setHomeowner(u);
    }

    @Override
    public ResponseResult<Set<FfoGameRoomResVO>> listFfoGameRoom() {
        HashMap<String, FfoGameRoomDTO> ffoGameRoomDTOHashMap = (HashMap<String, FfoGameRoomDTO>) redisUtil.hmget(RedisKey.FFO_GAME_ROOM_KEY.name());
        Set<FfoGameRoomResVO> ffoGameRoomResVOs = new HashSet<>();
        ffoGameRoomDTOHashMap.forEach((roomId, ffoGameRoomDTO) -> {
            ffoGameRoomResVOs.add(FfoGameMapper.INSTANCE.ffoGameRoomDTOToFfoGameRoomResVO(ffoGameRoomDTO));
        });
        return ResponseResult.success(ffoGameRoomResVOs);
    }


    @Transactional
    @Override
    public ResponseResult<String> userCreateGameRoom(UserEntity user, FfoGameRoomReqVO ffoGameRoomReqVO) {
        if (!redisUtil.hasKey(RedisKey.FFO_GAME_ROOM_KEY.name())) {
            redisUtil.hmset(RedisKey.FFO_GAME_ROOM_KEY.name(), new HashMap<>());
        }
        String roomId = RoomIdUtil.generateRoomId();
        while (redisUtil.hHasKey(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId)) {
            roomId = RoomIdUtil.generateRoomId();
        }
        FfoGameRoomDTO ffoGameRoomDTO = FfoGameRoomDTO.creatFfoGameRoomDTO(ffoGameRoomReqVO, roomId,
                user.getUsername(), user.getUsername());
        ffoGameRoomRedisDao.saveFfoGameRoomDTO(ffoGameRoomDTO);
        redisUserService.userEnterGameRoom(user, GamesType.FFO, roomId);
        return ResponseResult.success(roomId);
    }

    @Override
    public void sessionDisconnect(UserEntity user) {
        userLeaveGameRoom(user);
    }
}
