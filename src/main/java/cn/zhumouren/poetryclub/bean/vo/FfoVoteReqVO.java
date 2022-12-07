package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.constant.games.FfoVoteType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FfoVoteReqVO implements Serializable {
    private FfoVoteType ffoVoteType;
    private LocalDateTime createTime;
}
