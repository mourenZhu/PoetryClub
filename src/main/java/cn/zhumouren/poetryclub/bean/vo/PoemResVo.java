package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.core.PoemType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.PoemEntity} entity
 */
@Data
public class PoemResVo implements Serializable {
    private final Long id;
    private final AuthorResVo author;
    @NotEmpty
    private final String title;
    @NotEmpty
    private final String content;
    private final PoemType poemType;
    @JsonProperty("tags")
    private final Set<String> tagTags;
}