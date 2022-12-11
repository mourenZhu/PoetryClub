package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByRole(String role);
}