package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.entity.RoleEntity;
import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.mapper.UserMapper;
import cn.zhumouren.poetryclub.bean.vo.AdminChangePasswordVo;
import cn.zhumouren.poetryclub.bean.vo.ChangePasswordVo;
import cn.zhumouren.poetryclub.bean.vo.UserResVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constant.DBRoleType;
import cn.zhumouren.poetryclub.dao.RoleRepository;
import cn.zhumouren.poetryclub.dao.UserRepository;
import cn.zhumouren.poetryclub.property.AppWebImageProperty;
import cn.zhumouren.poetryclub.service.UserService;
import cn.zhumouren.poetryclub.util.FileUtil;
import cn.zhumouren.poetryclub.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    private final UserRepository userRepository;

    private final AppWebImageProperty appWebImageProperty;

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.files-path}")
    private String appFilesPath;

    public UserServiceImpl(UserRepository userRepository, AppWebImageProperty appWebImageProperty, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.appWebImageProperty = appWebImageProperty;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseResult<Boolean> isUsernameAvailable(String username) {
        UserEntity user = userRepository.findByUsername(username);
        return ResponseResult.success(ObjectUtils.isEmpty(user));
    }

    @Override
    public ResponseResult<Boolean> changePassword(UserEntity userEntity, ChangePasswordVo changePasswordVo) {
        if (!passwordEncoder.matches(changePasswordVo.getOldPassword(), userEntity.getPassword())) {
            return ResponseResult.failedWithMsg("原密码错误，请重试");
        }
        userEntity.setPassword(passwordEncoder.encode(changePasswordVo.getNewPassword()));
        userEntity.setUpdateTime(LocalDateTime.now());
        userRepository.save(userEntity);
        return ResponseResult.success();
    }

    @Override
    public ResponseResult<Boolean> changePassword(String username, AdminChangePasswordVo adminChangePasswordVo) {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (ObjectUtils.isEmpty(userEntity)) {
            return ResponseResult.failedWithMsg("用户不存在");
        }
        userEntity.setPassword(passwordEncoder.encode(adminChangePasswordVo.getNewPassword()));
        userEntity.setUpdateTime(LocalDateTime.now());
        userRepository.save(userEntity);
        return ResponseResult.success();
    }

    @Override
    public ResponseResult<Boolean> saveUserAvatar(UserEntity userEntity, MultipartFile file) {
        // 先删除原图片
        FileUtil.deleteFile(getUserAvatarSystemFilePath() + "/" + userEntity.getAvatarName());
        String fileName = FileUtil.saveFileGetFileName(file, getUserAvatarSystemFilePath(),
                UserUtil.getUserAvatarName(userEntity.getUsername()));
        userEntity.setAvatarName(fileName);
        userEntity.setUpdateTime(LocalDateTime.now());
        userRepository.save(userEntity);
        return ResponseResult.success();
    }

    @Override
    public ResponseResult<Boolean> createUser(UserEntity userEntity) {
        if (!isUsernameAvailable(userEntity.getUsername()).getData()) {
            return ResponseResult.failedWithMsg("用户名不可用,请换一个用户名");
        }
        if (ObjectUtils.isEmpty(userEntity.getRoles()) || userEntity.getRoles().size() == 0) {
            Set<RoleEntity> roles = new HashSet<>();
            roles.add(roleRepository.findByRole(DBRoleType.ROLE_USER.getRole()));
            log.debug("roles = {}", roles);
            userEntity.setRoles(roles);
        }
        if (ObjectUtils.isEmpty(userEntity.getNickname())) {
            userEntity.setNickname(userEntity.getUsername());
        }
        log.debug("user = {}", userEntity);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setCreateTime(LocalDateTime.now());
        UserEntity save = userRepository.save(userEntity);
        return ResponseResult.success(ObjectUtils.isNotEmpty(save));
    }

    @Override
    public ResponseResult<Boolean> createUser(UserEntity userEntity, DBRoleType... dbRoleType) {
        Set<RoleEntity> roles = new HashSet<>();
        for (DBRoleType roleType : dbRoleType) {
            roles.add(roleRepository.findByRole(roleType.getRole()));
        }
        userEntity.setRoles(roles);
        return createUser(userEntity);
    }

    @Override
    public ResponseResult<UserResVO> getByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (ObjectUtils.isNotEmpty(userEntity)) {
            return ResponseResult.success(UserMapper.INSTANCE.userEntityToUserResVO(userEntity));
        }
        return ResponseResult.failedWithMsg("用户不存在");
    }

    @Override
    public ResponseResult<Page<UserResVO>> listUser(
            String nickname, String username, String email, Pageable pageable) {
        Specification<UserEntity> specification = (root, query, cb) -> {
            List<Predicate> listAnd = new ArrayList<>();
            if (StringUtils.isNotBlank(nickname)) {
                listAnd.add(cb.like(root.get("nickname"), "%" + nickname + "%"));
            }
            if (StringUtils.isNotBlank(username)) {
                listAnd.add(cb.like(root.get("username"), "%" + username + "%"));
            }
            if (StringUtils.isNotBlank(email)) {
                listAnd.add(cb.like(root.get("email"), "%" + email + "%"));
            }
            Predicate[] andPres = new Predicate[listAnd.size()];
            query.distinct(true);
            if (ArrayUtils.isNotEmpty(andPres)) {
                Predicate andPre = cb.and(listAnd.toArray(andPres));
                return query.where(andPre).getRestriction();
            }
            return query.where().getRestriction();
        };
        Page<UserEntity> page = userRepository.findAll(specification, pageable);
        return ResponseResult.success(page.map(UserMapper.INSTANCE::userEntityToUserResVO));
    }

    private String getUserAvatarSystemFilePath() {
        return appFilesPath + appWebImageProperty.getUserAvatarPath();
    }
}
