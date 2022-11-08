package cn.zhumouren.poetryclub.controller.ffo;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomReqVO;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomResVO;
import cn.zhumouren.poetryclub.bean.vo.InputFfoSentenceVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.service.FfoService;
import cn.zhumouren.poetryclub.util.SecurityContextUtil;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/user/games/ffo")
public class FfoController {

    private final FfoService ffoService;

    public FfoController(FfoService ffoService) {
        this.ffoService = ffoService;
    }

    @PostMapping("/room")
    public ResponseResult<String> createGameRoom(@RequestBody @Validated FfoGameRoomReqVO roomReqVO) {
        return ffoService.userCreateGameRoom(
                SecurityContextUtil.getUserEntity(), roomReqVO);
    }

    @PostMapping("/room/{roomId}")
    public ResponseResult<Boolean> EnterGameRoom(@PathVariable("roomId") String roomId) {
        return ffoService.userEnterGameRoom(SecurityContextUtil.getUserEntity(), roomId);
    }

    @DeleteMapping("/room")
    public ResponseResult<Boolean> leaveGameRoom() {
        return ffoService.userLeaveGameRoom(SecurityContextUtil.getUserEntity());
    }

    @GetMapping("/")
    public ResponseResult<Set<FfoGameRoomResVO>> listFfoGameRoom() {
        return ffoService.listFfoGameRoom();
    }

    @PostMapping
    public ResponseResult<Boolean> startGame() {
        return null;
    }

    @MessageMapping("/ffo/{roomID}")
    public void sendFfoSentence(InputFfoSentenceVO inputFfoSentenceVO, @DestinationVariable("roomID") String roomID) {
        UserEntity userEntity = SecurityContextUtil.getUserEntity();
        ffoService.userSendFfoSentence(roomID, userEntity, inputFfoSentenceVO);
    }


}
