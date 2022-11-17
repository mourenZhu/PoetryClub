package cn.zhumouren.poetryclub.bean.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FfoGameVoteDTO implements Serializable {
    private String user;
    private Boolean admit;
    private LocalDateTime createTime;
}
