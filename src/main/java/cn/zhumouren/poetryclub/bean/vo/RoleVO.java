package cn.zhumouren.poetryclub.bean.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.RoleEntity} entity
 */
@Data
public class RoleVO implements Serializable {
    private final String role;
}