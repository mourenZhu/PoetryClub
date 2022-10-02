package cn.zhumouren.poetryclub.redis;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.dao.UserEntityRepository;
import cn.zhumouren.poetryclub.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Test
    public void test() {
        UserEntity zhumouren = userEntityRepository.findByUsername("zhumouren");
        redisUtil.set(zhumouren.getId().toString(), zhumouren);
    }
}
