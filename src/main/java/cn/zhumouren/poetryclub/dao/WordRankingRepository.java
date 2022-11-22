package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.WordRankingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRankingRepository extends JpaRepository<WordRankingEntity, Long> {

    boolean existsByWord(Character word);

}