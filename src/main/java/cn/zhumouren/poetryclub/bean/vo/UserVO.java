package cn.zhumouren.poetryclub.bean.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Set;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.UserEntity} entity
 */
@Data
public class UserVO implements Serializable {
    private final Long id;
    @Length(message = "用户名不能为空", min = 6, max = 30)
    @NotEmpty
    private final String username;
    @NotEmpty
    private final String password;
    private final Set<RoleVO> roles;
}