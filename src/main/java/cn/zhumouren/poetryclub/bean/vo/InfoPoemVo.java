package cn.zhumouren.poetryclub.bean.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.PoemEntity} entity
 */
@Data
public class InfoPoemVo implements Serializable {
    private Long id;
    @NotEmpty
    private String authorName;
    @NotEmpty
    private String title;
}