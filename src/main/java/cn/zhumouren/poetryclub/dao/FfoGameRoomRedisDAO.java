package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.dto.FfoGameRoomDTO;
import cn.zhumouren.poetryclub.constants.RedisKey;
import cn.zhumouren.poetryclub.constants.TimeSecondConstants;
import cn.zhumouren.poetryclub.util.RedisUtil;
import org.springframework.stereotype.Repository;

@Repository
public class FfoGameRoomRedisDAO {

    private final RedisUtil redisUtil;

    public FfoGameRoomRedisDAO(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    public FfoGameRoomDTO getFfoGameRoomDTO(String roomId) {
        return (FfoGameRoomDTO) redisUtil.hget(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId);
    }

    public boolean saveFfoGameRoomDTO(FfoGameRoomDTO ffoGameRoomDTO) {
        return redisUtil.hset(RedisKey.FFO_GAME_ROOM_KEY.name(), ffoGameRoomDTO.getId(), ffoGameRoomDTO, TimeSecondConstants.HOUR_SECOND * 12);
    }

}
