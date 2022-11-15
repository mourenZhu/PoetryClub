package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.entity.RoleEntity;
import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constant.DBRoleType;
import cn.zhumouren.poetryclub.dao.RoleEntityRepository;
import cn.zhumouren.poetryclub.dao.UserEntityRepository;
import cn.zhumouren.poetryclub.property.AppWebImageProperty;
import cn.zhumouren.poetryclub.service.UserService;
import cn.zhumouren.poetryclub.util.FileUtil;
import cn.zhumouren.poetryclub.util.SecurityContextUtil;
import cn.zhumouren.poetryclub.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/14 19:27
 **/
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserEntityRepository userEntityRepository;

    private final AppWebImageProperty appWebImageProperty;

    private final RoleEntityRepository roleEntityRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.files-path}")
    private String appFilesPath;

    public UserServiceImpl(UserEntityRepository userEntityRepository, AppWebImageProperty appWebImageProperty, RoleEntityRepository roleEntityRepository, PasswordEncoder passwordEncoder) {
        this.userEntityRepository = userEntityRepository;
        this.appWebImageProperty = appWebImageProperty;
        this.roleEntityRepository = roleEntityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseResult<Boolean> isUsernameAvailable(String username) {
        UserEntity user = userEntityRepository.findByUsername(username);
        return ResponseResult.success(ObjectUtils.isEmpty(user));
    }

    @Override
    public ResponseResult<Boolean> saveUserAvatar(MultipartFile file) {
        UserEntity userEntity = SecurityContextUtil.getUserEntity();
        // 先删除原图片
        FileUtil.deleteFile(getUserAvatarSystemFilePath() + "/" + userEntity.getAvatarName());
        String fileName = FileUtil.saveFileGetFileName(file, getUserAvatarSystemFilePath(),
                UserUtil.getUserAvatarName(userEntity.getUsername()));
        userEntity.setAvatarName(fileName);
        userEntityRepository.save(userEntity);
        return ResponseResult.success();
    }

    @Override
    public ResponseResult<Boolean> createUser(UserEntity userEntity) {
        if (!isUsernameAvailable(userEntity.getUsername()).getData()) {
            return ResponseResult.failedWithMsg("用户名不可用");
        }
        if (ObjectUtils.isEmpty(userEntity.getRoles()) || userEntity.getRoles().size() == 0) {
            Set<RoleEntity> roles = new HashSet<>();
            roles.add(roleEntityRepository.findByRole(DBRoleType.ROLE_USER.getRole()));
            log.debug("roles = {}", roles);
            userEntity.setRoles(roles);
        }
        if (ObjectUtils.isEmpty(userEntity.getNickname())) {
            userEntity.setNickname(userEntity.getUsername());
        }
        log.debug("user = {}", userEntity);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        UserEntity save = userEntityRepository.save(userEntity);
        return ResponseResult.success(ObjectUtils.isNotEmpty(save));
    }

    @Override
    public ResponseResult<Boolean> createUser(UserEntity userEntity, DBRoleType... dbRoleType) {
        Set<RoleEntity> roles = new HashSet<>();
        for (DBRoleType roleType : dbRoleType) {
            roles.add(roleEntityRepository.findByRole(roleType.getRole()));
        }
        userEntity.setRoles(roles);
        return createUser(userEntity);
    }

    private String getUserAvatarSystemFilePath() {
        return appFilesPath + appWebImageProperty.getUserAvatarPath();
    }
}
