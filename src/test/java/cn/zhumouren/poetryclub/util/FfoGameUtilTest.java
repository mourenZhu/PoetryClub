package cn.zhumouren.poetryclub.util;

import cn.zhumouren.poetryclub.bean.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class FfoGameUtilTest {

    @Test
    public void getFfoVoteEndTimeTest() {
        LocalDateTime ffoVoteEndTime = FfoGameUtil.getFfoVoteEndTime(null);
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);
        System.out.println(ffoVoteEndTime);
    }

    @Test
    public void usersSequenceSortTest() {
        List<UserDTO> userDTOList = new ArrayList<>();
        userDTOList.add(new UserDTO("test00", "", ""));
        userDTOList.add(new UserDTO("test01", "", ""));
        userDTOList.add(new UserDTO("test02", "", ""));
        List<String> users = new ArrayList<>();
        users.add("test02");
        users.add("test00");
        users.add("test01");
        FfoGameUtil.usersSequenceSort(userDTOList, users);
        userDTOList.forEach(System.out::println);
    }
}
