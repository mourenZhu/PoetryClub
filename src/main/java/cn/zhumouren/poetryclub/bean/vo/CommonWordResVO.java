package cn.zhumouren.poetryclub.bean.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.CommonWordEntity} entity
 */
@Data
public class CommonWordResVO implements Serializable {
    @NotNull
    private final Character word;
    @Min(0)
    private final Integer usageCount;
}