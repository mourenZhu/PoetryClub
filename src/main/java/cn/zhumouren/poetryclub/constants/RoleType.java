package cn.zhumouren.poetryclub.constants;

import lombok.Getter;

/**
 * @author mourenZhu
 * @version 1.0
 * @description 这个是 spring security 直接读取的角色值
 * @date 2022/9/18 22:09
 **/
@Getter
public enum RoleType {
    USER("USER"),
    ADMIN("ADMIN");

    private final String str;

    RoleType(String str) {
        this.str = str;
    }
}
