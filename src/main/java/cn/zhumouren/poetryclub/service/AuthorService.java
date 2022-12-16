package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.vo.AuthorResVo;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorService {

    ResponseResult<AuthorResVo> getAuthor(Long id);
    ResponseResult<Page<AuthorResVo>> listAuthor(String name, String era, String description, Pageable pageable);
}
