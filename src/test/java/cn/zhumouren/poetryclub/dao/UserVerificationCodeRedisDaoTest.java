package cn.zhumouren.poetryclub.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserVerificationCodeRedisDaoTest {

    @Autowired
    private UserVerificationCodeRedisDao verificationCodeRedisDao;

    @Test
    public void saveVerificationCodeTest() {
        verificationCodeRedisDao.saveUserVerificationCode("zhumouren0623@qq.com", "fjo181");
    }

    @Test
    public void getVerificationCodeTest() {
        String verificationCode = verificationCodeRedisDao.getUserVerificationCode("zhumouren0623@qq.com");
        System.out.println("验证码为: " + verificationCode);
    }
}
