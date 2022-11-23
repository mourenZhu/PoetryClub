package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.CommonWordEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CommonWordRepositoryTest {

    @Autowired
    private CommonWordRepository commonWordRepository;

    @Test
    public void findCommonlyUsedWordTest() {
        CommonWordEntity commonlyUsedWord = commonWordRepository.findCommonlyUsedWord(2);
        System.out.println(commonlyUsedWord);
    }
}
