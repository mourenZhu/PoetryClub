package cn.zhumouren.poetryclub.bean.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FfoVoteInputVO implements Serializable {
    private Boolean isPass;
    private LocalDateTime createTime;
}
