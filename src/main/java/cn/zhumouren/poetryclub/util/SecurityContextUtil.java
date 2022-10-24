package cn.zhumouren.poetryclub.util;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtil {

    public static UserEntity getUserEntity() {
        return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
