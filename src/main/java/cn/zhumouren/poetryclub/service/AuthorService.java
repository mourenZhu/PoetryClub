package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.AuthorEntity;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorService {

    ResponseResult<Page<AuthorEntity>> listAuthor(String name, String era, String description, Pageable pageable);
}
