package cn.zhumouren.poetryclub.bean.entity;

import cn.zhumouren.poetryclub.constant.games.FfoGamePoemType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
public class FfoGameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Character keyword;

    @NotNull
    @Min(15)
    private Integer playerPreparationSecond;

    @NotNull
    @Column(name = "is_allow_word_in_any")
    private Boolean allowWordInAny;

    /**
     * 飞花令句子的最大长度
     */
    @NotNull
    private Integer maxSentenceLength;
    /**
     * 本局游戏飞花令句子长度是否可变
     * 如果不可变，则每回合的句子长度 等于 句子的最大长度
     * 如果可变，则每回合的句子长度 小于等于 句子的最大长度
     */
    @NotNull
    @Column(name = "is_constant_sentence_length")
    private Boolean constantSentenceLength;

    @NotNull
    @Enumerated(EnumType.STRING)
    private FfoGamePoemType ffoGamePoemType;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<FfoGameUserInfoEntity> userInfoEntities = new java.util.LinkedHashSet<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<FfoGameUserSentenceEntity> userSentenceEntities = new java.util.LinkedHashSet<>();

    @NotNull
    private LocalDateTime createTime;

    @NotNull
    private LocalDateTime endTime;

    public boolean addUserSentence(FfoGameUserSentenceEntity ffoGameUserSentenceEntity) {
        return this.userSentenceEntities.add(ffoGameUserSentenceEntity);
    }

    public boolean addUserInfo(FfoGameUserInfoEntity ffoGameUserInfoEntity) {
        return this.userInfoEntities.add(ffoGameUserInfoEntity);
    }
}
