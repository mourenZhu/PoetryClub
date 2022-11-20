package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.constant.games.FfoGameSentenceJudgeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
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

    public boolean sentenceVote(FfoGameVoteDTO ffoGameVoteDTO) {
        if (!sentenceJudgeType.equals(FfoGameSentenceJudgeType.WAITING_USERS_VOTE)) {
            log.debug("不在等待投票的状态中");
            return false;
        }
        if (user.equals(ffoGameVoteDTO.getUser())) {
            log.debug("用户 {} 不能给自己投票", ffoGameVoteDTO.getUser());
            return false;
        }
        userVotes.add(ffoGameVoteDTO);
        return true;
    }

    public List<String> getVotedUsers() {
        List<String> votedUsers = new ArrayList<>();
        userVotes.forEach(voteDTO -> votedUsers.add(voteDTO.getUser()));
        return votedUsers;
    }
}