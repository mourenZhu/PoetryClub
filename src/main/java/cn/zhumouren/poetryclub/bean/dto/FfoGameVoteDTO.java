package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.constant.games.FfoVoteType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FfoGameVoteDTO implements Serializable {
    private UserDTO user;
    private FfoVoteType ffoVoteType;
    private LocalDateTime createTime;

    public FfoGameVoteDTO(UserDTO user, FfoVoteType ffoVoteType) {
        this.user = user;
        this.ffoVoteType = ffoVoteType;
        this.createTime = LocalDateTime.now();
    }
}
