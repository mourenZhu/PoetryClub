package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.CommonWordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommonWordRepository extends JpaRepository<CommonWordEntity, Long> {

    boolean existsByWord(Character word);

    /**
     * 获取最常用某个字（使用量最大的 index = 0）
     *
     * @param index
     * @return
     */
    @Query(value = "select id, word, usage_count from word_ranking_entity order by usage_count desc limit 1 offset :index ",
            nativeQuery = true)
    CommonWordEntity findCommonlyUsedWord(@Param("index") int index);
}