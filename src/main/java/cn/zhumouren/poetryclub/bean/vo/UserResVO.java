package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.bean.entity.RoleEntity;
import cn.zhumouren.poetryclub.utils.UserUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.UserEntity} entity
 */
@Data
public class UserResVO implements Serializable {
    @Length(message = "用户名不能为空", min = 6, max = 30)
    @NotEmpty
    private final String username;
    @Length(max = 20)
    private final String name;
    @Email
    private final String email;
    @JsonIgnore
    private final String avatarName;
    private Set<RoleEntity> roles;

    public Set<String> getRoles() {
        return roles.stream().map(roleEntity ->
                        roleEntity.getRole().toLowerCase().substring(5))
                .collect(Collectors.toSet());
    }

    /**
     * 这是实际的前端获取头像url的方法
     *
     * @return
     */
    public String getAvatar() {
        return UserUtil.getUserAvatarUrl(avatarName);
    }
}