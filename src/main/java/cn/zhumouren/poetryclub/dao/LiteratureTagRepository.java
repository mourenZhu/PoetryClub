package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.LiteratureTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "literature-tag")
public interface LiteratureTagRepository extends JpaRepository<LiteratureTagEntity, Long> {
}