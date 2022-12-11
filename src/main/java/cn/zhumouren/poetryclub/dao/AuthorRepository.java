package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.AuthorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;

public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {
    Page<AuthorEntity> findByNameLikeOrEraLike(@Nullable String name, @Nullable String era, Pageable pageable);
}