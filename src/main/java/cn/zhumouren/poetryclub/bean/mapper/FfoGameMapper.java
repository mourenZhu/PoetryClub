package cn.zhumouren.poetryclub.bean.mapper;

import cn.zhumouren.poetryclub.bean.dto.FfoGameDTO;
import cn.zhumouren.poetryclub.bean.dto.FfoGameRoomDTO;
import cn.zhumouren.poetryclub.bean.entity.FfoGameEntity;
import cn.zhumouren.poetryclub.bean.vo.FfoGameResVo;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomReqVO;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomResVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {FfoGameUserInfoMapper.class, FfoGameUserSentenceMapper.class})
public interface FfoGameMapper {
    FfoGameMapper INSTANCE = Mappers.getMapper(FfoGameMapper.class);

    FfoGameRoomResVO ffoGameRoomDTOToFfoGameRoomResVO(FfoGameRoomDTO ffoGameRoomDTO);

    FfoGameRoomDTO ffoGameRoomReqVOToFfoGameRoomDTO(FfoGameRoomReqVO ffoGameRoomReqVO);

    FfoGameDTO ffoGameRoomDTOToFfoGameDTO(FfoGameRoomDTO ffoGameRoomDTO);

    FfoGameEntity toEntity(FfoGameResVo ffoGameResVo);

    FfoGameResVo toDto(FfoGameEntity ffoGameEntity);
    List<FfoGameResVo> toDtoList(List<FfoGameEntity> ffoGameEntities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    FfoGameEntity partialUpdate(FfoGameResVo ffoGameResVo, @MappingTarget FfoGameEntity ffoGameEntity);
}
