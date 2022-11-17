package cn.zhumouren.poetryclub.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FfoVoteOutputVO implements Serializable {

    private String currentSpeaker;

    private String currentSentence;

    private LocalDateTime speakingTime;

    private LocalDateTime endTime;
}
