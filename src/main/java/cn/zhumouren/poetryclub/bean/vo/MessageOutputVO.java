package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.bean.dto.UserDTO;
import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.util.UserUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageOutputVO implements Serializable {
    private String username;
    private String nickname;
    private String avatar;
    private String content;
    private LocalDateTime time;

    public MessageOutputVO(UserEntity user, String content) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.avatar = UserUtil.getUserAvatarUrl(user.getAvatarName());
        this.content = content;
        this.time = LocalDateTime.now();
    }

    public MessageOutputVO(UserDTO user, String content) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.avatar = user.getAvatar();
        this.content = content;
        this.time = LocalDateTime.now();
    }

    public String getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dtf.format(time);
    }
}
