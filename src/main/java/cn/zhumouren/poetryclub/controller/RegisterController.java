package cn.zhumouren.poetryclub.controller;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.mapper.UserMapper;
import cn.zhumouren.poetryclub.bean.vo.UserRegisterVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constant.DBRoleType;
import cn.zhumouren.poetryclub.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseResult<Boolean> userRegister(@RequestBody @Validated UserRegisterVO userRegisterVO) {
        log.debug("userRegisterVO = {}", userRegisterVO);
        return userService.userRegister(userRegisterVO);
    }
}
