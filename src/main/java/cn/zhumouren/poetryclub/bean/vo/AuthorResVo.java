package cn.zhumouren.poetryclub.bean.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.AuthorEntity} entity
 */
@Data
public class AuthorResVo implements Serializable {
    private final Long id;
    @NotEmpty
    private final String name;
    private final String era;
    private final String description;
}