package cn.zhumouren.poetryclub.bean.mapper;

import cn.zhumouren.poetryclub.bean.entity.FfoGameUserSentenceEntity;
import cn.zhumouren.poetryclub.bean.vo.FfoGameUserSentenceResVo;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {UserMapper.class, FfoGameUserVoteMapper.class, AuthorMapper.class, PoemMapper.class})
public interface FfoGameUserSentenceMapper {
    @Mapping(source = "poem", target = "poemEntity")
    FfoGameUserSentenceEntity toEntity(FfoGameUserSentenceResVo ffoGameUserSentenceResVo);

    @Mapping(source = "poemEntity", target = "poem")
    FfoGameUserSentenceResVo toDto(FfoGameUserSentenceEntity ffoGameUserSentenceEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "poem", target = "poemEntity")
    FfoGameUserSentenceEntity partialUpdate(FfoGameUserSentenceResVo ffoGameUserSentenceResVo, @MappingTarget FfoGameUserSentenceEntity ffoGameUserSentenceEntity);
}