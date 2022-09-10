package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoemEntityRepository extends JpaRepository<PoemEntity, Long> {
}