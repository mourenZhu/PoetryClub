package cn.zhumouren.poetryclub.controller;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.mapper.UserMapper;
import cn.zhumouren.poetryclub.bean.vo.UserResVO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/14 18:13
 **/
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/info")
    public UserResVO getInfo() {
        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UserMapper.INSTANCE.userEntityToUserResVO(userEntity);
    }
}
