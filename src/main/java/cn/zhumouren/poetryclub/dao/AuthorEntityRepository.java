package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.AuthorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.Nullable;

public interface AuthorEntityRepository extends PagingAndSortingRepository<AuthorEntity, Long> {
    Page<AuthorEntity> findByNameLikeOrEraLike(@Nullable String name, @Nullable String era, Pageable pageable);
}