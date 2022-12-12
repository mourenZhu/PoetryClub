package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.constant.games.FfoGamePoemType;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.FfoGameEntity} entity
 */
@Data
public class FfoGameResVo implements Serializable {
    private final Long id;
    @NotNull
    private final Character keyword;
    @NotNull
    @Min(15)
    private final Integer playerPreparationSecond;
    @NotNull
    private final Boolean allowWordInAny;
    @NotNull
    private final Integer maxSentenceLength;
    @NotNull
    private final Boolean constantSentenceLength;
    @NotNull
    private final FfoGamePoemType ffoGamePoemType;
    private final Set<FfoGameUserInfoResVo> userInfoEntities;
    private final Set<FfoGameUserSentenceResVo> userSentenceEntities;
    @NotNull
    private final LocalDateTime createTime;
    @NotNull
    private final LocalDateTime endTime;
}