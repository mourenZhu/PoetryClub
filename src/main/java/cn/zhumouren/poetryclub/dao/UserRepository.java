package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<UserEntity, Long>,
        JpaSpecificationExecutor<UserEntity> {
    UserEntity findByUsername(String username);

}