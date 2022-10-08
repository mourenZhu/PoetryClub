package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.constants.games.FfoType;
import cn.zhumouren.poetryclub.dao.UserEntityRepository;
import cn.zhumouren.poetryclub.service.impl.FfoServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest
public class FfoServiceTest {

    @Autowired
    private FfoServiceImpl ffoService;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Test
    public void creteGameRoomTest() {
        UserEntity user = userEntityRepository.findByUsername("test00");
        ffoService.userCreateGameRoom(user, FfoType.FIVE_PLAYER_GAME);
    }

    @Test
    public void enterGameRoomTest() {
        String roomId = "BkzFXj";
        for (int i = 0; i < 10; i++) {
            UserEntity user = userEntityRepository.findByUsername("test0" + i);
            boolean b = ffoService.userEnterGameRoom(user, roomId);
            System.out.println("user " + user.getUsername() + "enter = " + b);
        }

    }

    @Test
    public void getGameRoomUserSetTest() {
        Set<String> usernameList = ffoService.getUsernameSetByRoomId("Th9fxI");
        usernameList.forEach(System.out::println);
    }
}
