package cn.zhumouren.poetryclub.controller;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.mapper.UserMapper;
import cn.zhumouren.poetryclub.bean.vo.UserReqVO;
import cn.zhumouren.poetryclub.bean.vo.UserResVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.dao.UserRepository;
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


    private final UserRepository userRepository;

    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/avatar")
    public ResponseResult<Boolean> postUserAvatar(MultipartFile file) {
        return userService.saveUserAvatar(file);
    }

    @GetMapping("/info")
    public ResponseResult<UserResVO> getInfo() {
        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseResult.success(UserMapper.INSTANCE.userEntityToUserResVO(userEntity));
    }

    @PostMapping("/info")
    public ResponseResult<Boolean> postUserInfo(@RequestBody @Validated UserReqVO userReqVO) {
        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userEntity.setEmail(userReqVO.getEmail());
        userEntity.setNickname(userReqVO.getNickname());
        userRepository.save(userEntity);
        return ResponseResult.success();
    }
}
