package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class PoemEntityRepositoryTest {

    @Autowired
    private PoemEntityRepository poemEntityRepository;

    @Test
    public void findByContentContainsTest() {
        PoemEntity poemEntity = poemEntityRepository.findByContentContains("xxxxxx");
        System.out.println(poemEntity);
    }

    @Test
    public void oneTest() {
        PoemEntity poem = poemEntityRepository.findById(2914L).get();
        System.out.println(poem);
    }

    @Test
    public void regexTest() {
        String regex = String.format("(^[\\u4e00-\\u9fa5]{%d}%c.*|^.*?[，。？]+[\\u4e00-\\u9fa5]{%d}%c.*)", 5, '花', 5, '花');
        System.out.println("regex = " + regex);
        List<PoemEntity> poemEntities = poemEntityRepository.findByRegex(regex);
        System.out.println("有诗: " + poemEntities.size() + " 首");
        poemEntities.forEach(System.out::println);
    }
}
