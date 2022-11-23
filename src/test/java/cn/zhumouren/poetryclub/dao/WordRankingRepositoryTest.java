package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.WordRankingEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WordRankingRepositoryTest {

    @Autowired
    private WordRankingRepository wordRankingRepository;

    @Test
    public void findCommonlyUsedWordTest() {
        WordRankingEntity commonlyUsedWord = wordRankingRepository.findCommonlyUsedWord(2);
        System.out.println(commonlyUsedWord);
    }
}
