package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.bean.vo.InfoPoemVo;
import cn.zhumouren.poetryclub.constant.games.FfoGameSentenceJudgeType;
import cn.zhumouren.poetryclub.constant.games.FfoVoteType;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private UserDTO user;
    private String sentence;
    private FfoGameSentenceJudgeType sentenceJudgeType;
    private InfoPoemVo poem;
    private List<FfoGameVoteDTO> userVotes;
    private LocalDateTime createTime;

    public FfoGameSentenceDTO(UserDTO user, String sentence, LocalDateTime createTime) {
        this.user = user;
        this.sentence = sentence;
        this.createTime = createTime;
        this.userVotes = new ArrayList<>();
    }

    public FfoGameSentenceDTO(UserDTO user, String sentence, FfoGameSentenceJudgeType sentenceJudgeType,
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
        if (isVoted(ffoGameVoteDTO.getUser())) {
            log.debug("用户 {} 已经投过票了", ffoGameVoteDTO.getUser());
            return false;
        }
        userVotes.add(ffoGameVoteDTO);
        return true;
    }

    /**
     * 投过票为true, 没有投过为false
     * @param userDTO
     * @return
     */
    public boolean isVoted(UserDTO userDTO) {
        for (FfoGameVoteDTO userVote : userVotes) {
            if (userVote.getUser().equals(userDTO)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public List<UserDTO> getVotedUsers() {
        List<UserDTO> votedUsers = new ArrayList<>();
        userVotes.forEach(voteDTO -> votedUsers.add(voteDTO.getUser()));
        return votedUsers;
    }

    @JsonIgnore
    public int getFavorNums() {
        return (int) userVotes.stream().mapToInt((userVote) -> {
         if (userVote.getFfoVoteType().equals(FfoVoteType.FAVOR)) {
             return 1;
         }
         return 0;
        }).summaryStatistics().getSum();
    }
}
