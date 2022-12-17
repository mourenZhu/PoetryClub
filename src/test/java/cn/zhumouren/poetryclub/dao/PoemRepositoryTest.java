package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.util.PageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class PoemRepositoryTest {

    @Autowired
    private PoemRepository poemRepository;

    @Test
    public void findByContentContainsTest() {
        var poems = poemRepository.findByContentContains("xxxxxx");
        System.out.println(poems);
    }

    @Test
    public void oneTest() {
        PoemEntity poem = poemRepository.findById(2914L).get();
        System.out.println(poem);
    }

    @Test
    public void regexTest() {
        String regex = String.format("(^[\\u4e00-\\u9fa5]{%d}%c.*|^.*?[，。？]+[\\u4e00-\\u9fa5]{%d}%c.*)", 5, '花', 5, '花');
        System.out.println("regex = " + regex);
        List<PoemEntity> poemEntities = poemRepository.findByRegex(regex);
        System.out.println("有诗: " + poemEntities.size() + " 首");
        poemEntities.forEach(poem -> {
            System.out.println(poem.getContent());
        });
    }

    @Test
    public void regexPageTest() {
        String regex = String.format("(^[\\u4e00-\\u9fa5]{%d}%c.*|^.*?[，。？]+[\\u4e00-\\u9fa5]{%d}%c.*)", 5, '花', 5, '花');
        System.out.println("regex = " + regex);
        var page = poemRepository.findByRegex(regex, PageUtil.getPageable());
        System.out.println("有诗: " + page.getContent().size() + " 首");
        page.getContent().forEach(System.out::println);
    }
}
