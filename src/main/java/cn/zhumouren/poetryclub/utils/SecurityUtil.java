package cn.zhumouren.poetryclub.utils;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static UserEntity getUserEntity() {
        return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
