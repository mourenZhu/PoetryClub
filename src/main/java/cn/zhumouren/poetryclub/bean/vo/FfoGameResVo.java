package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.constant.games.FfoGamePoemType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.FfoGameEntity} entity
 */
@Data
public class FfoGameResVo implements Serializable {
    private Long id;
    @NotNull
    private Character keyword;
    @NotNull
    @Min(15)
    private Integer playerPreparationSecond;
    @NotNull
    private Boolean allowWordInAny;
    @NotNull
    private Integer maxSentenceLength;
    @NotNull
    private Boolean constantSentenceLength;
    @NotNull
    private FfoGamePoemType ffoGamePoemType;
    @JsonProperty("userInfos")
    private Set<FfoGameUserInfoResVo> userInfoEntities;
    @JsonProperty("userSentences")
    private List<FfoGameUserSentenceResVo> userSentenceEntities;
    @NotNull
    private LocalDateTime createTime;
    @NotNull
    private LocalDateTime endTime;
}