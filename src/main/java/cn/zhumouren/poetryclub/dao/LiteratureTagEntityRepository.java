package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.LiteratureTagEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LiteratureTagEntityRepository extends PagingAndSortingRepository<LiteratureTagEntity, Long> {
}