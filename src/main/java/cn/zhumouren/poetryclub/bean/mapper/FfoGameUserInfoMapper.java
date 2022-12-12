package cn.zhumouren.poetryclub.bean.mapper;

import cn.zhumouren.poetryclub.bean.entity.FfoGameUserInfoEntity;
import cn.zhumouren.poetryclub.bean.vo.FfoGameUserInfoResVo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {UserMapper.class})
public interface FfoGameUserInfoMapper {

    FfoGameUserInfoMapper INSTANCE = Mappers.getMapper(FfoGameUserInfoMapper.class);
    FfoGameUserInfoEntity toEntity(FfoGameUserInfoResVo ffoGameUserInfoResVo);

    FfoGameUserInfoResVo toDto(FfoGameUserInfoEntity ffoGameUserInfoEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    FfoGameUserInfoEntity partialUpdate(FfoGameUserInfoResVo ffoGameUserInfoResVo, @MappingTarget FfoGameUserInfoEntity ffoGameUserInfoEntity);
}