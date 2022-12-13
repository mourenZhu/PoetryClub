package cn.zhumouren.poetryclub.bean.mapper;

import cn.zhumouren.poetryclub.bean.entity.FfoGameUserVoteEntity;
import cn.zhumouren.poetryclub.bean.vo.FfoGameUserVoteResVo;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {UserMapper.class})
public interface FfoGameUserVoteMapper {
    FfoGameUserVoteEntity toEntity(FfoGameUserVoteResVo ffoGameUserVoteResVo);

    FfoGameUserVoteResVo toDto(FfoGameUserVoteEntity ffoGameUserVoteEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    FfoGameUserVoteEntity partialUpdate(FfoGameUserVoteResVo ffoGameUserVoteResVo, @MappingTarget FfoGameUserVoteEntity ffoGameUserVoteEntity);
}