package cn.zhumouren.poetryclub.constant;

import lombok.Getter;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/14 22:31
 **/
@Getter
public enum RoleType {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER");
    private final String role;

    RoleType(String role) {
        this.role = role;
    }
}
