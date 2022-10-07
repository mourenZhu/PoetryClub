package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.UserRegisterVO;
import cn.zhumouren.poetryclub.constants.DBRoleType;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/14 19:23
 **/
public interface UserService {
    boolean isUsernameAvailable(String username);

    boolean saveUserAvatar(MultipartFile file);

    boolean createUser(UserEntity userEntity);

    boolean createUser(UserEntity userEntity, DBRoleType ...dbRoleType);
}
