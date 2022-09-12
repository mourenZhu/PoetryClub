package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false, collectionResourceRel = "role")
public interface RoleEntityRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByRole(String role);
}