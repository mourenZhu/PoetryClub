package cn.zhumouren.poetryclub.controller;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.mapper.UserMapper;
import cn.zhumouren.poetryclub.bean.vo.UserReqVO;
import cn.zhumouren.poetryclub.bean.vo.UserResVO;
import cn.zhumouren.poetryclub.dao.UserEntityRepository;
import cn.zhumouren.poetryclub.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/14 18:13
 **/
@RestController
@RequestMapping("/api/user")
public class UserController {


    private final UserEntityRepository userEntityRepository;

    private final UserService userService;

    public UserController(UserEntityRepository userEntityRepository, UserService userService) {
        this.userEntityRepository = userEntityRepository;
        this.userService = userService;
    }

    @PostMapping("/avatar")
    public Boolean postUserAvatar(MultipartFile file) {
        userService.saveUserAvatar(file);
        return true;
    }

    @GetMapping("/info")
    public UserResVO getInfo() {
        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UserMapper.INSTANCE.userEntityToUserResVO(userEntity);
    }

    @PostMapping("/info")
    public Boolean postUserInfo(@RequestBody @Validated UserReqVO userReqVO) {
        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userEntity.setEmail(userReqVO.getEmail());
        userEntity.setName(userReqVO.getName());
        userEntityRepository.save(userEntity);
        return true;
    }
}
