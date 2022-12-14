package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.entity.AuthorEntity;
import cn.zhumouren.poetryclub.bean.mapper.AuthorMapper;
import cn.zhumouren.poetryclub.bean.vo.AuthorResVo;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.dao.AuthorRepository;
import cn.zhumouren.poetryclub.service.AuthorService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public ResponseResult<AuthorResVo> getAuthor(Long id) {
        Optional<AuthorEntity> optional = authorRepository.findById(id);
        return optional.map(authorEntity ->
                ResponseResult.success(AuthorMapper.INSTANCE.toDto(authorEntity)))
                .orElse(ResponseResult.failedWithMsg("该作者不存在"));
    }

    @Override
    public ResponseResult<Page<AuthorResVo>> listAuthor(
            String name, String era, String description, Pageable pageable) {
        Specification<AuthorEntity> specification = (root, query, cb) -> {
            List<Predicate> listAnd = new ArrayList<>();
            if (StringUtils.isNotBlank(name)) {
                listAnd.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            if (StringUtils.isNotBlank(era)) {
                listAnd.add(cb.like(root.get("era"), "%" + era + "%"));
            }
            if (StringUtils.isNotBlank(description)) {
                listAnd.add(cb.like(root.get("description"), "%" + description + "%"));
            }
            Predicate[] andPres = new Predicate[listAnd.size()];
            query.distinct(true);
            if (ArrayUtils.isNotEmpty(andPres)) {
                Predicate andPre = cb.and(listAnd.toArray(andPres));
                return query.where(andPre).getRestriction();
            }
            return query.where().getRestriction();
        };
        Page<AuthorEntity> page = authorRepository.findAll(specification, pageable);
        return ResponseResult.success(page.map(AuthorMapper.INSTANCE::toDto));
    }
}
