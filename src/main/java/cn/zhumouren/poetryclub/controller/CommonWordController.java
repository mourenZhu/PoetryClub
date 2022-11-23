package cn.zhumouren.poetryclub.controller;

import cn.zhumouren.poetryclub.bean.entity.CommonWordEntity;
import cn.zhumouren.poetryclub.bean.mapper.CommonWordMapper;
import cn.zhumouren.poetryclub.bean.vo.CommonWordResVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.service.CommonWordService;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/common_word")
public class CommonWordController {

    private final CommonWordService commonWordService;

    public CommonWordController(CommonWordService commonWordService) {
        this.commonWordService = commonWordService;
    }

    @GetMapping("/random")
    public ResponseResult<CommonWordResVO> getRandomCommonWord() {
        CommonWordResVO commonWordResVO = CommonWordMapper.INSTANCE
                .commonWordEntityToCommonWordResVO(commonWordService.getCommonWordRandom());
        return ResponseResult.success(commonWordResVO);
    }

    @GetMapping("/{top}")
    public ResponseResult getCommonWordTop(
            @PathVariable("top") int top) {
        if (top < 1 || top > 200) {
            return ResponseResult.failedWithMsg("只能查询 1 ~ 200");
        }
        List<CommonWordEntity> commonWordEntities = commonWordService.topCommonWords(top);
        List<CommonWordResVO> commonWordResVOS = CommonWordMapper.INSTANCE.toCommonWordResVOList(commonWordEntities);
        return ResponseResult.success(commonWordResVOS);
    }
}
