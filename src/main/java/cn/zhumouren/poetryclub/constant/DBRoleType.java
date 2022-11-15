package cn.zhumouren.poetryclub.constant;

import lombok.Getter;

/**
 * @author mourenZhu
 * @version 1.0
 * @description 这是数据库中存入的字段内容，会在角色前加入ROLE_
 * @date 2022/9/14 22:31
 **/
@Getter
public enum DBRoleType {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER");
    private final String role;

    DBRoleType(String role) {
        this.role = role;
    }
}
