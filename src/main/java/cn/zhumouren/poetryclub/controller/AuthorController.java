package cn.zhumouren.poetryclub.controller;


import cn.zhumouren.poetryclub.bean.entity.AuthorEntity;
import cn.zhumouren.poetryclub.bean.vo.AuthorResVo;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.service.AuthorService;
import cn.zhumouren.poetryclub.util.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("permitAll()")
@RestController
@RequestMapping("/api/public/author")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/{id}")
    public ResponseResult<AuthorResVo> getAuthor(@PathVariable Long id) {
        return authorService.getAuthor(id);
    }

    @GetMapping("/")
    public ResponseResult<Page<AuthorResVo>> listAuthor(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String era,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize
    ) {
        return authorService.listAuthor(name, era, description, PageUtil.getPageable(pageNum, pageSize));
    }
}
