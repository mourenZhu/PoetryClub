package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.entity.AnnouncementEntity;
import cn.zhumouren.poetryclub.bean.entity.AuthorEntity;
import cn.zhumouren.poetryclub.bean.mapper.AnnouncementMapper;
import cn.zhumouren.poetryclub.bean.vo.AnnouncementPostVo;
import cn.zhumouren.poetryclub.bean.vo.AnnouncementPutVo;
import cn.zhumouren.poetryclub.bean.vo.AnnouncementResVo;
import cn.zhumouren.poetryclub.bean.vo.AnnouncementTitleResVo;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.dao.AnnouncementRepository;
import cn.zhumouren.poetryclub.service.AnnouncementService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final AnnouncementMapper announcementMapper;

    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository, AnnouncementMapper announcementMapper) {
        this.announcementRepository = announcementRepository;
        this.announcementMapper = announcementMapper;
    }

    @Override
    public ResponseResult<Boolean> add(AnnouncementPostVo announcementPostVo) {
        announcementRepository.save(new AnnouncementEntity(announcementPostVo));
        return ResponseResult.success();
    }

    @Override
    public ResponseResult<Boolean> del(Long id) {
        if (id.equals(1L)) {
            // 1 用做首页介绍
            return ResponseResult.failedWithMsg("不能删除该公告");
        }
        announcementRepository.deleteById(id);
        return ResponseResult.success();
    }

    @Override
    public ResponseResult<Boolean> update(AnnouncementPutVo announcementPutVo) {
        Optional<AnnouncementEntity> optional = announcementRepository.findById(announcementPutVo.getId());
        if (optional.isPresent()) {
            AnnouncementEntity announcementEntity = optional.get();
            announcementEntity.setTitle(announcementPutVo.getTitle());
            announcementEntity.setContent(announcementPutVo.getContent());
            announcementEntity.setUpdateTime(LocalDateTime.now());
            announcementRepository.save(announcementEntity);
            return ResponseResult.success();
        }
        return ResponseResult.failedWithMsg("该公告不存在");
    }

    @Override
    public ResponseResult<AnnouncementResVo> get(Long id) {
        Optional<AnnouncementEntity> optional = announcementRepository.findById(id);
        return optional.map(announcementEntity ->
                        ResponseResult.success(announcementMapper.toDto(announcementEntity)))
                .orElseGet(() -> ResponseResult.failedWithMsg("该公告不存在"));
    }

    @Override
    public ResponseResult<Page<AnnouncementTitleResVo>> pageTitleVo(String title, String content, Pageable pageable) {
        Specification<AnnouncementEntity> specification = (root, query, cb) -> {
            List<Predicate> listAnd = new ArrayList<>();
            if (StringUtils.isNotBlank(title)) {
                listAnd.add(cb.like(root.get("title"), "%" + title + "%"));
            }
            if (StringUtils.isNotBlank(content)) {
                listAnd.add(cb.like(root.get("content"), "%" + content + "%"));
            }
            Predicate[] andPres = new Predicate[listAnd.size()];
            query.distinct(true);

            if (ArrayUtils.isNotEmpty(andPres)) {
                Predicate andPre = cb.and(listAnd.toArray(andPres));
                query.where(andPre);
                query.orderBy(cb.desc(root.get("createTime")));
                return query.getRestriction();
            }
            query.orderBy(cb.desc(root.get("createTime")));
            return query.getRestriction();
        };
        Page<AnnouncementEntity> page = announcementRepository.findAll(specification, pageable);
        return ResponseResult.success(page.map(announcementMapper::toTitleResVo));
    }
}
