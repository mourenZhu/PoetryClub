package cn.zhumouren.poetryclub.bean.mapper;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.UserRegisterVO;
import cn.zhumouren.poetryclub.bean.vo.UserReqVO;
import cn.zhumouren.poetryclub.bean.vo.UserResVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/14 19:14
 **/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserEntity userRegisterVOToUserEntity(UserRegisterVO userRegisterVO);

    UserRegisterVO userEntityToUserRegisterVO(UserEntity userEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserEntity updateUserEntityFromUserRegisterVO(UserRegisterVO userRegisterVO, @MappingTarget UserEntity userEntity);


    UserResVO userEntityToUserResVO(UserEntity userEntity);

    UserEntity userReqVOToUserEntity(UserReqVO userReqVO);

    UserReqVO userEntityToUserReqVO(UserEntity userEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserEntity updateUserEntityFromUserReqVO(UserReqVO userReqVO, @MappingTarget UserEntity userEntity);
}
