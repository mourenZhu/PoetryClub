package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.bean.entity.RoleEntity;
import cn.zhumouren.poetryclub.properties.AppWebImageProperties;
import cn.zhumouren.poetryclub.utils.UserUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.context.ContextLoader;

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

    private Set<RoleEntity> roles;
    @Length(max = 20)
    private final String name;
    @Email
    private final String email;

    public Set<String> getRoles() {
        return roles.stream().map(roleEntity ->
                roleEntity.getRole().toLowerCase().substring(5))
                .collect(Collectors.toSet());
    }

    public String getAvatar() {
        return UserUtil.getUserAvatarUrl(username);
    }
}