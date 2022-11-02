package cn.zhumouren.poetryclub.bean.mapper;

import cn.zhumouren.poetryclub.bean.dto.FfoGameDTO;
import cn.zhumouren.poetryclub.bean.dto.FfoGameRoomDTO;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomReqVO;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomResVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface FfoGameMapper {
    FfoGameMapper INSTANCE = Mappers.getMapper(FfoGameMapper.class);

    FfoGameRoomResVO ffoGameRoomDTOToFfoGameRoomResVO(FfoGameRoomDTO ffoGameRoomDTO);

    FfoGameRoomDTO ffoGameRoomReqVOToFfoGameRoomDTO(FfoGameRoomReqVO ffoGameRoomReqVO);

    FfoGameDTO ffoGameRoomDTOToFfoGameDTO(FfoGameRoomDTO ffoGameRoomDTO);
}
