package cn.zhumouren.poetryclub.util;

import com.google.common.base.Splitter;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UtilsTest {

    @Test
    public void roomIdUtilTest() {
        System.out.println("room id = " + RoomIdUtil.generateRoomId());
    }

    @Test
    public void poemContentTest() {
        String content = "舊穀未沒新穀登，家家擊壤含歡聲。";
        List<String> strings = Splitter.on("，").splitToList(content);
        strings.set(1, strings.get(1).substring(0, strings.get(1).length() - 1));
        strings.forEach(System.out::println);
    }
}
