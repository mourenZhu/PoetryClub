package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.utils.UserUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class OutputMessageDTO implements Serializable {
    private String username;
    private String nickname;
    private String avatar;
    private String content;
    private LocalDateTime time;

    @JsonIgnore
    public static OutputMessageDTO getOutputMessageDTO(UserEntity user, String content) {
        return new OutputMessageDTO(user.getUsername(), user.getNickname(),
                UserUtil.getUserAvatarUrl(user.getAvatarName()), content, LocalDateTime.now());
    }

    public String getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dtf.format(time);
    }
}
