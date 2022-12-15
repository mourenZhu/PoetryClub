package cn.zhumouren.poetryclub.bean.mapper;

import cn.zhumouren.poetryclub.bean.entity.LiteratureTagEntity;
import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.bean.vo.PoemResVo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {AuthorMapper.class})
public interface PoemMapper {

    PoemMapper INSTANCE = Mappers.getMapper(PoemMapper.class);

    PoemEntity toEntity(PoemResVo poemResVo);

    @Mapping(target = "tagTags", expression = "java(tagsToTagTags(poemEntity.getTags()))")
    PoemResVo toDto(PoemEntity poemEntity);

    default Set<String> tagsToTagTags(Set<LiteratureTagEntity> tags) {
        return tags.stream().map(tag -> tag.getTag()).collect(Collectors.toSet());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PoemEntity partialUpdate(PoemResVo poemResVo, @MappingTarget PoemEntity poemEntity);
}