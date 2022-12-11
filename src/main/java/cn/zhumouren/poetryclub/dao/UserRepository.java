package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);

}