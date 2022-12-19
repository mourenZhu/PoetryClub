package cn.zhumouren.poetryclub.bean.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.AnnouncementEntity} entity
 */
@Data
public class AnnouncementResVo implements Serializable {
    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createTime;
}