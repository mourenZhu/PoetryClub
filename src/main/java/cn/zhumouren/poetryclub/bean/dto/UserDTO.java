package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.util.UserUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO implements Serializable {
    private String username;
    private String nickname;
    @JsonIgnore
    private String avatarName;

    public UserDTO(UserEntity userEntity) {
        this.username = userEntity.getUsername();
        this.nickname = userEntity.getNickname();
        this.avatarName = userEntity.getAvatarName();
    }

    /**
     * 这是实际的前端获取头像url的方法
     *
     * @return
     */
    public String getAvatar() {
        return UserUtil.getUserAvatarUrl(avatarName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return getUsername().equals(userDTO.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername());
    }
}
