package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomReqVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constant.games.FfoGamePoemType;
import cn.zhumouren.poetryclub.dao.UserEntityRepository;
import cn.zhumouren.poetryclub.service.impl.FfoServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FfoPlayingServiceTest {

    @Autowired
    private FfoServiceImpl ffoService;
    @Autowired
    private FfoPlayingService ffoPlayingService;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Test
    public void userStartGameTest() {
        UserEntity user = userEntityRepository.findByUsername("test00");
        System.out.println(ffoPlayingService.userStartGame(user).getMsg());
    }

    @Test
    public void creatGameRoomAndEnterRoomAndStartGameTest() {
        UserEntity user = userEntityRepository.findByUsername("test00");
        FfoGameRoomReqVO ffoGameRoomReqVO = new FfoGameRoomReqVO("测试房间", 5, 30,
                true, true, FfoGamePoemType.ALL);
        String roomId = ffoService.userCreateGameRoom(user, ffoGameRoomReqVO).getData();

        for (int i = 1; i < 3; i++) {
            UserEntity u = userEntityRepository.findByUsername("test0" + i);
            ResponseResult<Boolean> booleanResponseResult = ffoService.userEnterGameRoom(u, roomId);
            System.out.println("user " + u.getUsername() + "  enter = " + booleanResponseResult.getData());
        }

        System.out.println(ffoPlayingService.userStartGame(user));
    }
}
