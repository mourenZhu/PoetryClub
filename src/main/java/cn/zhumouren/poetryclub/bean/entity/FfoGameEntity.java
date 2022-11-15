package cn.zhumouren.poetryclub.bean.entity;

import cn.zhumouren.poetryclub.constants.games.FfoGamePoemType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
public class FfoGameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(15)
    private Integer playerPreparationSecond;

    @Column(name = "is_allow_word_in_any")
    private Boolean allowWordInAny;

    @Enumerated(EnumType.STRING)
    private FfoGamePoemType ffoGamePoemType;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<FfoGameUserInfoEntity> userInfoEntities = new java.util.LinkedHashSet<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<FfoGameUserSentenceEntity> userSentenceEntities = new java.util.LinkedHashSet<>();

    private LocalDateTime createTime;

    private LocalDateTime endTime;
}
