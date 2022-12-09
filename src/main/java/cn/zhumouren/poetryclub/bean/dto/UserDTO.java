package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.util.UserUtil;
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
    private String avatar;

    public UserDTO(UserEntity userEntity) {
        this.username = userEntity.getUsername();
        this.nickname = userEntity.getNickname();
        this.avatar = UserUtil.getUserAvatarUrl(userEntity.getAvatarName());
    }

    public UserDTO(String username) {
        this.username = username;
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
