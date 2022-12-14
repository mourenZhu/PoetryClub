package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PoemRepository extends JpaRepository<PoemEntity, Long>,
        JpaSpecificationExecutor<PoemEntity> {
    List<PoemEntity> findByContentLike(String content);
    List<PoemEntity> findByContentContains(String content);

    List<PoemEntity> findByAuthor_NameContains(String name);

    /**
     * 通过正则表达式检索
     *
     * @param regex
     * @return
     */
    @Query(value = "select * from poem_entity where content ~ :regex", nativeQuery = true)
    List<PoemEntity> findByRegex(@Param("regex") String regex);

    @Query(value = "select * from poem_entity where content ~ :regex", nativeQuery = true)
    Page<PoemEntity> findByRegex(@Param("regex") String regex, Pageable pageable);
}