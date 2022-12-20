package cn.zhumouren.poetryclub.controller;

import cn.zhumouren.poetryclub.bean.vo.AnnouncementPostVo;
import cn.zhumouren.poetryclub.bean.vo.AnnouncementPutVo;
import cn.zhumouren.poetryclub.bean.vo.AnnouncementResVo;
import cn.zhumouren.poetryclub.bean.vo.AnnouncementTitleResVo;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.service.AnnouncementService;
import cn.zhumouren.poetryclub.util.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/announcement")
public class AnnouncementController {
    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping("/{id}")
    public ResponseResult<AnnouncementResVo> get(@PathVariable Long id) {
        return announcementService.get(id);
    }

    @GetMapping("/title/")
    public ResponseResult<Page<AnnouncementTitleResVo>> pageTitle(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize
    ) {
        return announcementService.pageTitleVo(title, content, PageUtil.getPageable(pageNum, pageSize));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping()
    public ResponseResult<Boolean> post(@RequestBody @Validated AnnouncementPostVo announcementPostVo) {
        return announcementService.add(announcementPostVo);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseResult<Boolean> put(@RequestBody @Validated AnnouncementPutVo announcementPutVo) {
        return announcementService.update(announcementPutVo);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseResult<Boolean> del(@PathVariable Long id) {
        return announcementService.del(id);
    }

}
