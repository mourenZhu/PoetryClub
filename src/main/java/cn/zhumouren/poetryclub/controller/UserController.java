package cn.zhumouren.poetryclub.controller;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.mapper.UserMapper;
import cn.zhumouren.poetryclub.bean.vo.AdminChangePasswordVo;
import cn.zhumouren.poetryclub.bean.vo.ChangePasswordVo;
import cn.zhumouren.poetryclub.bean.vo.UserReqVO;
import cn.zhumouren.poetryclub.bean.vo.UserResVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.dao.UserRepository;
import cn.zhumouren.poetryclub.service.UserService;
import cn.zhumouren.poetryclub.util.PageUtil;
import cn.zhumouren.poetryclub.util.SecurityContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Slf4j
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
        UserEntity userEntity = SecurityContextUtil.getUserEntity();
        return userService.saveUserAvatar(userEntity, file);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/{username}/avatar")
    public ResponseResult<Boolean> adminPostUserAvatar(MultipartFile file, @PathVariable String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (ObjectUtils.isEmpty(userEntity)) {
            return ResponseResult.failedWithMsg("改用户不存在");
        }
        return userService.saveUserAvatar(userEntity, file);
    }

    @PostMapping("/password")
    public ResponseResult<Boolean> changePassword(@RequestBody @Validated ChangePasswordVo changePasswordVo) {
        UserEntity userEntity = SecurityContextUtil.getUserEntity();
        return userService.changePassword(userEntity, changePasswordVo);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/{username}/password")
    public ResponseResult<Boolean> adminChangePassword(
            @PathVariable String username,
            @RequestBody @Validated AdminChangePasswordVo adminChangePasswordVo) {
        return userService.changePassword(username, adminChangePasswordVo);
    }

    @GetMapping("/info")
    public ResponseResult<UserResVO> getInfo() {
        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseResult.success(UserMapper.INSTANCE.userEntityToUserResVO(userEntity));
    }

    @PostMapping("/info")
    public ResponseResult<Boolean> postUserInfo(@RequestBody @Validated UserReqVO userReqVO) {
        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.debug("{} 修改用户信息", userEntity.getUsername());
        userEntity.setEmail(userReqVO.getEmail());
        userEntity.setNickname(userReqVO.getNickname());
        userRepository.save(userEntity);
        return ResponseResult.success();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("/{username}/info")
    public ResponseResult<Boolean> adminPutUserInfo(
            @PathVariable String username, @RequestBody @Validated UserReqVO userReqVO) {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (ObjectUtils.isEmpty(userEntity)) {
            return ResponseResult.failedWithMsg("该用户不存在");
        }
        userEntity.setEmail(userReqVO.getEmail());
        userEntity.setNickname(userReqVO.getNickname());
        userRepository.save(userEntity);
        return ResponseResult.success();
    }
    @GetMapping("/{username}")
    public ResponseResult<UserResVO> getUserByUsername(@PathVariable String username) {
        return userService.getByUsername(username);
    }

    @GetMapping("/")
    public ResponseResult<Page<UserResVO>> listUser(
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize
    ) {
        return userService.listUser(nickname, username, email, PageUtil.getPageable(pageNum, pageSize));
    }
}
