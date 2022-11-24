package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.dto.UserGameStateDTO;
import cn.zhumouren.poetryclub.constant.RedisKey;
import cn.zhumouren.poetryclub.util.RedisUtil;
import org.springframework.stereotype.Repository;

@Repository
public class UserGameStateDAO {
    private final RedisUtil redisUtil;

    public UserGameStateDAO(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    public UserGameStateDTO getUserGameStateDTO(String user) {
        return (UserGameStateDTO) redisUtil.hget(RedisKey.USER_GAME_STATE.name(), user);
    }
}
