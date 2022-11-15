package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.constants.games.FfoGameSentenceJudgeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FfoGameUserSentenceDTO implements Serializable {

    private String user;

    private String sentence;

    private FfoGameSentenceJudgeType sentenceJudgeType;

    private LocalDateTime createTime;

    public FfoGameUserSentenceDTO(String user, String sentence, LocalDateTime createTime) {
        this.user = user;
        this.sentence = sentence;
        this.createTime = createTime;
    }
}
