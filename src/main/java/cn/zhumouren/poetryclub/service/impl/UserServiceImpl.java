package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.dao.UserEntityRepository;
import cn.zhumouren.poetryclub.properties.AppWebImageProperties;
import cn.zhumouren.poetryclub.service.UserService;
import cn.zhumouren.poetryclub.utils.FileUtil;
import cn.zhumouren.poetryclub.utils.SecurityUtil;
import cn.zhumouren.poetryclub.utils.UserUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/14 19:27
 **/
@Service
public class UserServiceImpl implements UserService {

    private final UserEntityRepository userEntityRepository;

    private final AppWebImageProperties appWebImageProperties;

    @Value("${app.files-path}")
    private String appFilesPath;

    public UserServiceImpl(UserEntityRepository userEntityRepository, AppWebImageProperties appWebImageProperties) {
        this.userEntityRepository = userEntityRepository;
        this.appWebImageProperties = appWebImageProperties;
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        UserEntity user = userEntityRepository.findByUsername(username);
        return ObjectUtils.isEmpty(user);
    }

    @Override
    public boolean saveUserAvatar(MultipartFile file) {
        UserEntity userEntity = SecurityUtil.getUserEntity();
        // 先删除原图片
        FileUtil.deleteFile(getUserAvatarSystemFilePath() + "/" + userEntity.getAvatarName());
        String fileName = FileUtil.saveFileGetFileName(file, getUserAvatarSystemFilePath(),
                UserUtil.getUserAvatarName(userEntity.getUsername()));
        userEntity.setAvatarName(fileName);
        userEntityRepository.save(userEntity);
        return true;
    }

    private String getUserAvatarSystemFilePath() {
        return appFilesPath + appWebImageProperties.getUserAvatarPath();
    }
}
