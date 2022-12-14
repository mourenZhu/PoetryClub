package cn.zhumouren.poetryclub.controller;


import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.service.PoemEntityService;
import cn.zhumouren.poetryclub.util.PageUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;

@RestController
@RequestMapping("/api/public/poem")
public class PoemController {

    private final PoemEntityService poemEntityService;

    public PoemController(PoemEntityService poemEntityService) {
        this.poemEntityService = poemEntityService;
    }

    @GetMapping("/")
    public ResponseResult<Page<PoemEntity>> listPoem(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String[] tags,
            @RequestParam(required = false) Character keyword,
            @RequestParam(required = false) Integer keywordIndex,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        if (ObjectUtils.isEmpty(tags)) {
            tags = new String[0];
        }
        Pageable pageable = PageUtil.getPageable(pageNum, pageSize);
        return poemEntityService.listPoem(author, title, content, new HashSet<>(Arrays.asList(tags)),
                keyword, keywordIndex, pageable);
    }
}
