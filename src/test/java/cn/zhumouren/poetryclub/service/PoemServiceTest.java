package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}
