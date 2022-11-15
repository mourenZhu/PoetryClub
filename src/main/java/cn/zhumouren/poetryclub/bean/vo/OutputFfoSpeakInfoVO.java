package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.constant.games.FfoGameSentenceJudgeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputFfoSpeakInfoVO implements Serializable {

    private String currentSpeaker;

    private String currentSentence;

    private FfoGameSentenceJudgeType currentSentenceJudgeType;

    private LocalDateTime speakingTime;

    private String nextSpeaker;

    private LocalDateTime nextEndTime;



}
