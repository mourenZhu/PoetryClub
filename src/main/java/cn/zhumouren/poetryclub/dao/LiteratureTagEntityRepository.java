package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.LiteratureTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiteratureTagEntityRepository extends JpaRepository<LiteratureTagEntity, Long> {
}