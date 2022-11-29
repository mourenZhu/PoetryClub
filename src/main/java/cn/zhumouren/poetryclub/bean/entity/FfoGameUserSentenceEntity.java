package cn.zhumouren.poetryclub.bean.entity;

import cn.zhumouren.poetryclub.constant.games.FfoGameSentenceJudgeType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@Data
@Entity
public class FfoGameUserSentenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private UserEntity userEntity;
    private String sentence;

    @ManyToOne
    private PoemEntity poemEntity;
    @Enumerated(EnumType.STRING)
    private FfoGameSentenceJudgeType sentenceJudgeType;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<FfoGameUserVoteEntity> userVoteEntities = new java.util.LinkedHashSet<>();
    private LocalDateTime createTime;

    public FfoGameUserSentenceEntity(UserEntity userEntity, String sentence,
                                     FfoGameSentenceJudgeType ffoGameSentenceJudgeType, LocalDateTime createTime) {
        this.userEntity = userEntity;
        this.sentence = sentence;
        this.sentenceJudgeType = ffoGameSentenceJudgeType;
        this.createTime = createTime;
    }

    public boolean addUserVote(FfoGameUserVoteEntity ffoGameUserVoteEntity) {
        return this.userVoteEntities.add(ffoGameUserVoteEntity);
    }

}
