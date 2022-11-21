package cn.zhumouren.poetryclub.redis;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.dao.UserRepository;
import cn.zhumouren.poetryclub.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void test() {
        UserEntity zhumouren = userRepository.findByUsername("zhumouren");
        redisUtil.set(zhumouren.getId().toString(), zhumouren);
    }
}
