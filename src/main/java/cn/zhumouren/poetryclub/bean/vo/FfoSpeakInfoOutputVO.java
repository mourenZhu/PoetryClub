package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.bean.dto.FfoGameDTO;
import cn.zhumouren.poetryclub.bean.dto.FfoGameSentenceDTO;
import cn.zhumouren.poetryclub.constant.games.FfoGameSentenceJudgeType;
import cn.zhumouren.poetryclub.util.FfoGameUtil;
import com.google.common.collect.Iterables;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FfoSpeakInfoOutputVO implements Serializable {

    private String currentSpeaker;

    private String currentSentence;

    private FfoGameSentenceJudgeType currentSentenceJudgeType;

    private LocalDateTime speakingTime;

    private String nextSpeaker;

    private LocalDateTime nextEndTime;

    public FfoSpeakInfoOutputVO(FfoGameDTO ffoGameDTO) {
        FfoGameSentenceDTO ffoGameSentenceDTO = Iterables.getLast(ffoGameDTO.getUserSentences());
        this.nextSpeaker = ffoGameDTO.getNextSpeaker();
        this.currentSpeaker = ffoGameSentenceDTO.getUser();
        this.currentSentence = ffoGameSentenceDTO.getSentence();
        this.currentSentenceJudgeType = ffoGameSentenceDTO.getSentenceJudgeType();
        this.speakingTime = ffoGameSentenceDTO.getCreateTime();
        this.nextEndTime = FfoGameUtil.getFfoSpeakerNextEndTime(ffoGameDTO);
    }


}
