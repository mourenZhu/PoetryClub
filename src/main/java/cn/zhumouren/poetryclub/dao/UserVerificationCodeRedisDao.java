package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.constant.RedisKey;
import cn.zhumouren.poetryclub.util.RedisUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Repository;

@Repository
public class UserVerificationCodeRedisDao {
    private final RedisUtil redisUtil;

    public UserVerificationCodeRedisDao(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    public void saveUserVerificationCode(String key, String verificationCode) {
        redisUtil.hset(RedisKey.USER_VERIFICATION_CODE.name(), key, verificationCode, 180);
    }

    public void delUserVerificationCode(String key) {
        redisUtil.hdel(RedisKey.USER_VERIFICATION_CODE.name(), key);
    }

    public String getUserVerificationCode(String key) {
        return (String) redisUtil.hget(RedisKey.USER_VERIFICATION_CODE.name(), key);
    }
}
