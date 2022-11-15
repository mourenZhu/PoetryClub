package cn.zhumouren.poetryclub.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class FfoGameUtilTest {

    @Test
    public void getFfoVoteEndTimeTest() {
        LocalDateTime ffoVoteEndTime = FfoGameUtil.getFfoVoteEndTime(null);
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);
        System.out.println(ffoVoteEndTime);
    }
}
