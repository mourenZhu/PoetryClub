package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.FfoGameEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FfoGameRepository extends JpaRepository<FfoGameEntity, Long> {
    Page<FfoGameEntity> findByUserInfoEntities_UserEntity_UsernameOrderByEndTimeDesc(String username, Pageable pageable);



}