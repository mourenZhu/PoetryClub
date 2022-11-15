package cn.zhumouren.poetryclub.bean.entity;

import cn.zhumouren.poetryclub.constant.games.FfoGameSentenceJudgeType;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class FfoGameUserSentenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserEntity user;

    private String sentence;

    @Enumerated(EnumType.STRING)
    private FfoGameSentenceJudgeType sentenceJudgeType;

    private LocalDateTime createTime;

}
