package cn.zhumouren.poetryclub.bean.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.AnnouncementEntity} entity
 */
@Data
public class AnnouncementTitleResVo implements Serializable {
    private final Long id;
    private final String title;
}