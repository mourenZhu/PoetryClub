package cn.zhumouren.poetryclub.config.security.constants;

import lombok.Getter;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/18 14:50
 **/
@Getter
public enum SecurityConstants {
    TOKEN_ROLE_CLAIM("ROLE");
    private String name;

    SecurityConstants(String name) {
        this.name = name;
    }
}
