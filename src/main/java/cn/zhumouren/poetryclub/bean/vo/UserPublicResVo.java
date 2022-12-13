package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.util.UserUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.UserEntity} entity
 */
@Data
public class UserPublicResVo implements Serializable {
    @Length(message = "用户名不能为空", min = 6, max = 30)
    @NotEmpty
    private final String username;

    @JsonIgnore
    private final String avatarName;
    @Length(max = 20)
    private final String nickname;

    /**
     * 这是实际的前端获取头像url的方法
     *
     * @return
     */
    public String getAvatar() {
        return UserUtil.getUserAvatarUrl(avatarName);
    }
}