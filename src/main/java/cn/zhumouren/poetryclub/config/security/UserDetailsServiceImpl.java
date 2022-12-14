package cn.zhumouren.poetryclub.config.security;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.dao.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/12 16:15
 **/
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
//        log.info("username = {}, login", username);
        UserEntity userEntity = userRepository.findByUsername(username);
        if (ObjectUtils.isEmpty(userEntity)) {
            throw new UsernameNotFoundException("user " + username + " not found");
        }
        return userEntity;
    }

}
