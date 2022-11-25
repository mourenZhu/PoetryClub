package cn.zhumouren.poetryclub.controller.ffo;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomReqVO;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomResVO;
import cn.zhumouren.poetryclub.bean.vo.FfoSentenceInputVO;
import cn.zhumouren.poetryclub.bean.vo.FfoVoteInputVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.service.FfoPlayingService;
import cn.zhumouren.poetryclub.service.FfoService;
import cn.zhumouren.poetryclub.util.SecurityContextUtil;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/user/games/ffo")
public class FfoController {

    private final FfoService ffoService;

    private final FfoPlayingService ffoPlayingService;

    public FfoController(FfoService ffoService, FfoPlayingService ffoPlayingService) {
        this.ffoService = ffoService;
        this.ffoPlayingService = ffoPlayingService;
    }

    @PostMapping("/room")
    public ResponseResult<String> createGameRoom(@RequestBody @Validated FfoGameRoomReqVO roomReqVO) {
        return ffoService.userCreateGameRoom(
                SecurityContextUtil.getUserEntity(), roomReqVO);
    }

    @PostMapping("/room/{roomId}")
    public ResponseResult<Boolean> enterGameRoom(@PathVariable("roomId") String roomId) {
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

    @MessageMapping("/ffo/{roomId}/sentence")
    public void sendFfoSentence(FfoSentenceInputVO ffoSentenceInputVO, @DestinationVariable("roomId") String roomId) {
        UserEntity userEntity = SecurityContextUtil.getUserEntity();
        ffoSentenceInputVO.setCreateTime(LocalDateTime.now());
        ffoPlayingService.userSendFfoSentence(roomId, userEntity, ffoSentenceInputVO);
    }

    @MessageMapping("/ffo/{roomId}/vote")
    public void voteFfoSentence(FfoVoteInputVO ffoVoteInputVO, @DestinationVariable("roomId") String roomId) {
        UserEntity userEntity = SecurityContextUtil.getUserEntity();
        ffoVoteInputVO.setCreateTime(LocalDateTime.now());
        ffoPlayingService.userVoteFfoSentence(roomId, userEntity, ffoVoteInputVO);
    }

    @MessageMapping("/ffo/{roomId}/users")
    public void putUsersSequence(List<String> users, @DestinationVariable("roomId") String roomId)  {
        UserEntity userEntity = SecurityContextUtil.getUserEntity();
        ffoService.updateUsersSequence(roomId, userEntity, users);
    }


}
