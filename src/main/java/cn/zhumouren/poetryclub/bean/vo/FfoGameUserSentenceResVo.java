package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.constant.games.FfoGameSentenceJudgeType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.FfoGameUserSentenceEntity} entity
 */
@Data
public class FfoGameUserSentenceResVo implements Serializable {
    private final Long id;
    private final UserPublicResVo userEntity;
    private final String sentence;
    private final PoemEntity poem;
    private final FfoGameSentenceJudgeType sentenceJudgeType;
    private final Set<FfoGameUserVoteResDto> userVoteEntities;
    private final LocalDateTime createTime;
}