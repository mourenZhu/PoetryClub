package cn.zhumouren.poetryclub.bean.mapper;

import cn.zhumouren.poetryclub.bean.vo.AnnouncementResVo;
import cn.zhumouren.poetryclub.bean.entity.AnnouncementEntity;
import cn.zhumouren.poetryclub.bean.vo.AnnouncementTitleResVo;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface AnnouncementMapper {
    AnnouncementEntity toEntity(AnnouncementResVo announcementResVo);

    AnnouncementResVo toDto(AnnouncementEntity announcementEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AnnouncementEntity partialUpdate(AnnouncementResVo announcementResVo, @MappingTarget AnnouncementEntity announcementEntity);

    AnnouncementEntity toEntity1(AnnouncementTitleResVo announcementTitleResVo);

    AnnouncementTitleResVo toTitleResVo(AnnouncementEntity announcementEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AnnouncementEntity partialUpdate1(AnnouncementTitleResVo announcementTitleResVo, @MappingTarget AnnouncementEntity announcementEntity);
}