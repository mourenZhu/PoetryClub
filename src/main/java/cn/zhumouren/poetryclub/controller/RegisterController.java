package cn.zhumouren.poetryclub.controller;

import cn.zhumouren.poetryclub.bean.entity.RoleEntity;
import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.mapper.UserMapper;
import cn.zhumouren.poetryclub.bean.vo.UserRegisterVO;
import cn.zhumouren.poetryclub.constants.DBRoleType;
import cn.zhumouren.poetryclub.dao.RoleEntityRepository;
import cn.zhumouren.poetryclub.dao.UserEntityRepository;
import cn.zhumouren.poetryclub.exception.UsernameNotAvailableException;
import cn.zhumouren.poetryclub.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/14 18:15
 **/
@RestController
@RequestMapping("/api")
@Slf4j
public class RegisterController {

    private final UserEntityRepository userEntityRepository;
    private final UserService userService;
    private final RoleEntityRepository roleEntityRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(UserEntityRepository userEntityRepository,
                              UserService userService, RoleEntityRepository roleEntityRepository, PasswordEncoder passwordEncoder) {
        this.userEntityRepository = userEntityRepository;
        this.userService = userService;
        this.roleEntityRepository = roleEntityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public Boolean userRegister(@RequestBody UserRegisterVO userRegisterVO) {
        log.info("userRegisterVO = {}", userRegisterVO);
        UserEntity userEntity = UserMapper.INSTANCE.userRegisterVOToUserEntity(userRegisterVO);
        if (!userService.isUsernameAvailable(userEntity.getUsername())) {
            throw new UsernameNotAvailableException();
        }
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(roleEntityRepository.findByRole(DBRoleType.ROLE_USER.getRole()));
        userEntity.setRoles(roles);
        UserEntity save = userEntityRepository.save(userEntity);
        return ObjectUtils.isNotEmpty(save);
    }
}
