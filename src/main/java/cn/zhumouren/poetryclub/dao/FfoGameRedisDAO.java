package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.dto.FfoGameDTO;
import cn.zhumouren.poetryclub.constants.RedisKey;
import cn.zhumouren.poetryclub.constants.TimeSecondConstants;
import cn.zhumouren.poetryclub.util.RedisUtil;
import org.springframework.stereotype.Repository;

@Repository
public class FfoGameRedisDAO {

    private final RedisUtil redisUtil;

    public FfoGameRedisDAO(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }


    public boolean saveFfoGameDTO(FfoGameDTO ffoGameDTO) {
        return redisUtil.hset(RedisKey.FFO_GAME_KEY.name(), ffoGameDTO.getRoomId(), ffoGameDTO, TimeSecondConstants.HOUR_SECOND * 12);
    }

    public FfoGameDTO getFfoGameDTO(String roomId) {
        return (FfoGameDTO) redisUtil.hget(RedisKey.FFO_GAME_KEY.name(), roomId);
    }
}
