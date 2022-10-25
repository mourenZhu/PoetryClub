package cn.zhumouren.poetryclub.bean.mapper;

import cn.zhumouren.poetryclub.bean.dto.FfoGameRoomDTO;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomReqVO;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomResVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface FfoGameRoomMapper {
    FfoGameRoomMapper INSTANCE = Mappers.getMapper(FfoGameRoomMapper.class);

    FfoGameRoomResVO ffoGameRoomDTOToFfoGameRoomResVO(FfoGameRoomDTO ffoGameRoomDTO);

    FfoGameRoomDTO ffoGameRoomReqVOToFfoGameRoomDTO(FfoGameRoomReqVO ffoGameRoomReqVO);
}
