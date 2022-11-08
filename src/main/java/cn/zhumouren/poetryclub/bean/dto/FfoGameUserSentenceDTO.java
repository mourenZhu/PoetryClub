package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.constants.games.FfoGameSentenceJudgeType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FfoGameUserSentenceDTO implements Serializable {

    private String user;

    private String sentence;

    private FfoGameSentenceJudgeType sentenceJudgeType;

    private LocalDateTime createTime;
}
