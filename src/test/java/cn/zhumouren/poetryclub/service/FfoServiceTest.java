package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomResVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
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
        ffoService.userCreateGameRoom(user, "测试房间", FfoType.FIVE_PLAYER_GAME);
    }

    @Test
    public void enterGameRoomTest() {
        String roomId = "16KgSK";
        for (int i = 0; i < 10; i++) {
            UserEntity user = userEntityRepository.findByUsername("test0" + i);
            ResponseResult<Boolean> booleanResponseResult = ffoService.userEnterGameRoom(user, roomId);
            System.out.println("user " + user.getUsername() + "enter = " + booleanResponseResult.getData());
        }
    }

    @Test
    public void multiplePeopleCreateAndEnterGameRoom() {
        for (int i = 0; i < 9; i++) {
            UserEntity user = userEntityRepository.findByUsername("test0" + i);
            String roomId = ffoService
                    .userCreateGameRoom(user, "test room " + i, FfoType.SEVEN_PLAYER_GAME).getData();
            System.out.println(user.getUsername() + "创建room " + roomId);
            for (int j = 0; j < 10; j++) {
                UserEntity u = userEntityRepository.findByUsername("test" + (i + 1) + j);
                Boolean b = ffoService.userEnterGameRoom(u, roomId).getData();
                System.out.println(u.getUsername() + "进入room " + roomId + ": " + b);
            }
        }
    }

    @Test
    public void listGameRoomTest() {
        Set<FfoGameRoomResVO> data = ffoService.listFfoGameRoom().getData();
        data.forEach(System.out::println);
    }

    @Test
    public void userLeaveGameRoomTest() {
        UserEntity test01 = userEntityRepository.findByUsername("test01");
        ResponseResult<Boolean> booleanResponseResult = ffoService.userLeaveGameRoom(test01);
        System.out.println(booleanResponseResult);
    }

    @Test
    public void usersLeavenGameRoomTest() {
        for (int i = 0; i < 10; i++) {
            UserEntity user = userEntityRepository.findByUsername("test2" + i);
            ResponseResult<Boolean> booleanResponseResult1 = ffoService.userLeaveGameRoom(user);
            System.out.println(booleanResponseResult1);
        }
    }
}
