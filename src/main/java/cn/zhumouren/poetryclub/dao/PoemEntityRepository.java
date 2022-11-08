package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "poem")
public interface PoemEntityRepository extends JpaRepository<PoemEntity, Long> {
    // (^[\u4e00-\u9fa5]{5}红.*|^.*?[，。？]+[\u4e00-\u9fa5]{5}红.*)
}