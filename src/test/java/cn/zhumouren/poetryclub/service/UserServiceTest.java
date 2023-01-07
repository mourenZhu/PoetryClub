package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.mapper.UserMapper;
import cn.zhumouren.poetryclub.bean.vo.UserRegisterVO;
import cn.zhumouren.poetryclub.constant.DBRoleType;
import cn.zhumouren.poetryclub.dao.RoleRepository;
import cn.zhumouren.poetryclub.dao.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    //    @Rollback(value = false)
    @Transactional
    @Test
    public void createUserTest() {
        String pw = "123456";
        for (int i = 0; i < 100; i++) {
            UserRegisterVO userRegisterVO;
            if (i < 10) {
//                userRegisterVO = new UserRegisterVO("test0" + i, "test0" + i, pw);
            } else {
//                userRegisterVO = new UserRegisterVO("test" + i, "test" + i, pw);
            }
//            UserEntity userEntity = UserMapper.INSTANCE.userRegisterVOToUserEntity(userRegisterVO);
//            userService.createUser(userEntity);
        }
    }

    //    @Rollback(value = false)
    @Transactional
    @Test
    public void createAdminTest() {
        String pw = "123456";
        for (int i = 0; i < 10; i++) {
            UserRegisterVO userRegisterVO;
//            userRegisterVO = new UserRegisterVO("admin0" + i, "admin0" + i, pw);
//            UserEntity userEntity = UserMapper.INSTANCE.userRegisterVOToUserEntity(userRegisterVO);
//            userService.createUser(userEntity, DBRoleType.ROLE_ADMIN, DBRoleType.ROLE_USER);
        }
    }

    //    @Rollback(value = false)
    @Transactional
    @Test
    public void addRoleUserTest() {
        for (int i = 0; i < 10; i++) {
            UserEntity user = userRepository.findByUsername("admin0" + i);
            user.getRoles().add(roleRepository.findByRole(DBRoleType.ROLE_USER.getRole()));
//            System.out.println(Arrays.toString(user.getRoles().toArray()));
            userRepository.save(user);
            System.out.println(Arrays.toString(user.getRoles().toArray()));
            System.out.println(user);
        }

    }
}
