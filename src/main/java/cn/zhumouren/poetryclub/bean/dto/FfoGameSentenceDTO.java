package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.constant.games.FfoGameSentenceJudgeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FfoGameSentenceDTO implements Serializable {

    private String user;
    private String sentence;
    private FfoGameSentenceJudgeType sentenceJudgeType;
    private List<FfoGameVoteDTO> userVotes;
    private LocalDateTime createTime;

    public FfoGameSentenceDTO(String user, String sentence, LocalDateTime createTime) {
        this.user = user;
        this.sentence = sentence;
        this.createTime = createTime;
        this.userVotes = new ArrayList<>();
    }

    public FfoGameSentenceDTO(String user, String sentence, FfoGameSentenceJudgeType sentenceJudgeType,
                              LocalDateTime createTime) {
        this.user = user;
        this.sentence = sentence;
        this.createTime = createTime;
        this.userVotes = new ArrayList<>();
        this.sentenceJudgeType = sentenceJudgeType;
    }
}
