package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.AnnouncementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AnnouncementRepository extends JpaRepository<AnnouncementEntity, Long>,
        JpaSpecificationExecutor<AnnouncementEntity> {
    Page<AnnouncementEntity> findAllByOrderByCreateTimeDesc(Pageable pageable);
}