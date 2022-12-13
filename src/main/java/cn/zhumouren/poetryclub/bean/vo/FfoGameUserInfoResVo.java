package cn.zhumouren.poetryclub.bean.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.FfoGameUserInfoEntity} entity
 */
@Data
public class FfoGameUserInfoResVo implements Serializable {
    private final Long id;
    @JsonProperty("userVO")
    private final UserPublicResVo userEntity;
    @Range(min = 1, max = 10)
    private final Integer sequence;
    @Range(min = 1, max = 10)
    private final Integer ranking;
}