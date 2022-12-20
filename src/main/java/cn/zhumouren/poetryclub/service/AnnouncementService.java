package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.vo.AnnouncementPostVo;
import cn.zhumouren.poetryclub.bean.vo.AnnouncementPutVo;
import cn.zhumouren.poetryclub.bean.vo.AnnouncementResVo;
import cn.zhumouren.poetryclub.bean.vo.AnnouncementTitleResVo;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnnouncementService {
    ResponseResult<Boolean> add(AnnouncementPostVo announcementPostVo);
    ResponseResult<Boolean> del(Long id);
    ResponseResult<Boolean> update(AnnouncementPutVo announcementPutVo);
    ResponseResult<AnnouncementResVo> get(Long id);
    ResponseResult<Page<AnnouncementTitleResVo>> pageTitleVo(String title, String content, Pageable pageable);
}
