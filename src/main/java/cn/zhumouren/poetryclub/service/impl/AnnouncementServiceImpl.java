package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.entity.AnnouncementEntity;
import cn.zhumouren.poetryclub.bean.mapper.AnnouncementMapper;
import cn.zhumouren.poetryclub.bean.vo.AnnouncementPostVo;
import cn.zhumouren.poetryclub.bean.vo.AnnouncementPutVo;
import cn.zhumouren.poetryclub.bean.vo.AnnouncementResVo;
import cn.zhumouren.poetryclub.bean.vo.AnnouncementTitleResVo;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.dao.AnnouncementRepository;
import cn.zhumouren.poetryclub.service.AnnouncementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        if (id.equals(0L)) {
            // 0 用做首页介绍
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
            announcementEntity.setTitle(announcementEntity.getTitle());
            announcementEntity.setContent(announcementEntity.getContent());
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
    public ResponseResult<Page<AnnouncementTitleResVo>> pageTitleVo(Pageable pageable) {
        return ResponseResult.success(
                announcementRepository.findAllByOrderByCreateTimeDesc(pageable).map(announcementMapper::toTitleResVo));
    }
}
