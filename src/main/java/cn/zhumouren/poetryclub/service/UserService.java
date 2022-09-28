package cn.zhumouren.poetryclub.service;

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
}
