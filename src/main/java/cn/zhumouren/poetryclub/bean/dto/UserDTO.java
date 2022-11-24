package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
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
    private String avatarName;

    public UserDTO(UserEntity userEntity) {
        this.username = userEntity.getUsername();
        this.nickname = userEntity.getNickname();
        this.avatarName = userEntity.getAvatarName();
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
