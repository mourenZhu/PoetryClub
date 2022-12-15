package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.bean.vo.PoemResVo;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.util.PageUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@SpringBootTest
public class PoemServiceTest {

    @Autowired
    private PoemEntityService poemEntityService;

    @Test
    public void listBySentenceTest() {
        List<PoemEntity> list = poemEntityService.listBySentence("迎日之至");
        list.forEach(System.out::println);
    }

    @Test
    public void tagsTest() {
        var page = poemEntityService.listPoem("", "", "",
                new HashSet<>(Arrays.asList("春", "花")), PageUtil.getPageable());
        page.getData().getContent().forEach(System.out::println);
    }
}
