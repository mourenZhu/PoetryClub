package cn.zhumouren.poetryclub.bean.mapper;

import cn.zhumouren.poetryclub.bean.entity.CommonWordEntity;
import cn.zhumouren.poetryclub.bean.vo.CommonWordResVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CommonWordMapper {
    CommonWordMapper INSTANCE = Mappers.getMapper(CommonWordMapper.class);

    CommonWordEntity commonWordResVOToCommonWordEntity(CommonWordResVO commonWordResVO);

    CommonWordResVO commonWordEntityToCommonWordResVO(CommonWordEntity commonWordEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CommonWordEntity updateCommonWordEntityFromCommonWordEntityResVO(CommonWordResVO commonWordResVO, @MappingTarget CommonWordEntity commonWordEntity);

    List<CommonWordResVO> toCommonWordResVOList(List<CommonWordEntity> commonWordEntityList);
}
