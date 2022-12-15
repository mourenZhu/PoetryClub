package cn.zhumouren.poetryclub.controller;


import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.bean.vo.PoemResVo;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.service.PoemEntityService;
import cn.zhumouren.poetryclub.util.PageUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;

@PreAuthorize("permitAll()")
@RestController
@RequestMapping("/api/public/poem")
public class PoemController {

    private final PoemEntityService poemEntityService;

    public PoemController(PoemEntityService poemEntityService) {
        this.poemEntityService = poemEntityService;
    }

    @GetMapping("/{id}")
    public ResponseResult<PoemResVo> getPoem(@PathVariable("id") Long id) {
        return poemEntityService.getPoem(id);
    }

    @GetMapping("/")
    public ResponseResult<Page<PoemResVo>> listPoem(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String era,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String[] tags,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        if (ObjectUtils.isEmpty(tags)) {
            tags = new String[0];
        }
        Pageable pageable = PageUtil.getPageable(pageNum, pageSize);
        return poemEntityService.listPoem(author, era, title, content, new HashSet<>(Arrays.asList(tags)),
                pageable);
    }

    @GetMapping("/key-index/")
    public ResponseResult<Page<PoemResVo>> ffoListPoem(
            Character keyword, Integer index,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        return poemEntityService.listPoem(keyword, index, PageUtil.getPageable(pageNum, pageSize));
    }
}
