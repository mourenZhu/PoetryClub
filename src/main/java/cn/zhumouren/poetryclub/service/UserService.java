package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constants.DBRoleType;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/14 19:23
 **/
public interface UserService {
    ResponseResult<Boolean> isUsernameAvailable(String username);

    ResponseResult<Boolean> saveUserAvatar(MultipartFile file);

    ResponseResult<Boolean> createUser(UserEntity userEntity);

    ResponseResult<Boolean> createUser(UserEntity userEntity, DBRoleType ...dbRoleType);
}
