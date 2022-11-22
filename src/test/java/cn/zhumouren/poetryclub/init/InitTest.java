package cn.zhumouren.poetryclub.init;

import cn.zhumouren.poetryclub.init.db.InitWordRanking;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

//@ActiveProfiles("dev")
@SpringBootTest
public class InitTest {

    @Autowired
    private InitWordRanking initWordRanking;

//    @Rollback(value = false)
    @Transactional
    @Test
    public void initWordRankingTest() {
        System.out.println("开始");
        initWordRanking.init();
    }
}
