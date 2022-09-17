package cn.zhumouren.poetryclub.config.security;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.dao.UserEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/12 16:15
 **/
@Service
@Slf4j
public class UserDetailsManagerImpl implements UserDetailsManager {

    private final UserEntityRepository userEntityRepository;

    public UserDetailsManagerImpl(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public UserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("username = {}, login", username);
        return userEntityRepository.findByUsername(username);
    }

    @Override
    public void createUser(UserDetails user) {

    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        UserEntity user = userEntityRepository.findByUsername(username);
        return ObjectUtils.isNotEmpty(user);
    }
}
