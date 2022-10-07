package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.mapper.UserMapper;
import cn.zhumouren.poetryclub.bean.vo.UserRegisterVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Transactional
    @Rollback(false)
    @Test
    public void createUserTest() {
        UserRegisterVO userRegisterVO = new UserRegisterVO("test12", "123456");
        UserEntity userEntity = UserMapper.INSTANCE.userRegisterVOToUserEntity(userRegisterVO);
        userService.createUser(userEntity);
    }
}
