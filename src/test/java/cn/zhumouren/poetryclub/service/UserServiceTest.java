package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.mapper.UserMapper;
import cn.zhumouren.poetryclub.bean.vo.UserRegisterVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

//    @Rollback(value = false)
    @Transactional
    @Test
    public void createUserTest() {
        String pw = "123456";
        for (int i = 0; i < 100; i++) {
            UserRegisterVO userRegisterVO;
            if (i < 10) {
                userRegisterVO = new UserRegisterVO("test0" + i, pw);
            } else {
                userRegisterVO = new UserRegisterVO("test" + i, pw);
            }
            UserEntity userEntity = UserMapper.INSTANCE.userRegisterVOToUserEntity(userRegisterVO);
            userService.createUser(userEntity);
        }

    }
}
