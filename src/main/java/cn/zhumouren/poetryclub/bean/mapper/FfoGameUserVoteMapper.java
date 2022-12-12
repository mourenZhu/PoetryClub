package cn.zhumouren.poetryclub.bean.mapper;

import cn.zhumouren.poetryclub.bean.entity.FfoGameUserVoteEntity;
import cn.zhumouren.poetryclub.bean.vo.FfoGameUserVoteResDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {UserMapper.class})
public interface FfoGameUserVoteMapper {
    FfoGameUserVoteEntity toEntity(FfoGameUserVoteResDto ffoGameUserVoteResDto);

    FfoGameUserVoteResDto toDto(FfoGameUserVoteEntity ffoGameUserVoteEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    FfoGameUserVoteEntity partialUpdate(FfoGameUserVoteResDto ffoGameUserVoteResDto, @MappingTarget FfoGameUserVoteEntity ffoGameUserVoteEntity);
}