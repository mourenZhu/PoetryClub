package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.AdminChangePasswordVo;
import cn.zhumouren.poetryclub.bean.vo.ChangePasswordVo;
import cn.zhumouren.poetryclub.bean.vo.UserRegisterVO;
import cn.zhumouren.poetryclub.bean.vo.UserResVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constant.DBRoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/14 19:23
 **/
public interface UserService {
    ResponseResult<Boolean> isUsernameAvailable(String username);

    ResponseResult<Boolean> changePassword(UserEntity userEntity, ChangePasswordVo changePasswordVo);
    ResponseResult<Boolean> changePassword(String username, AdminChangePasswordVo adminChangePasswordVo);

    ResponseResult<Boolean> saveUserAvatar(UserEntity userEntity, MultipartFile file);

    ResponseResult<Boolean> createUser(UserEntity userEntity);

    ResponseResult<Boolean> userRegister(UserRegisterVO userRegisterVO);

    ResponseResult<Boolean> createUser(UserEntity userEntity, DBRoleType... dbRoleType);

    ResponseResult<UserResVO> getByUsername(String username);

    ResponseResult<Page<UserResVO>> listUser(String nickname, String username, String email, Pageable pageable);
}
