package cn.zhumouren.poetryclub.controller.ffo;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomReqVO;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomResVO;
import cn.zhumouren.poetryclub.bean.vo.FfoSentenceInputVO;
import cn.zhumouren.poetryclub.bean.vo.FfoVoteReqVO;
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
        UserEntity userEntity = SecurityContextUtil.getUserEntity();
        return ffoPlayingService.userStartGame(userEntity);
    }

    @MessageMapping("/ffo/{roomId}/sentence")
    public void sendFfoSentence(FfoSentenceInputVO ffoSentenceInputVO, @DestinationVariable("roomId") String roomId) {
        UserEntity userEntity = SecurityContextUtil.getUserEntity();
        ffoSentenceInputVO.setCreateTime(LocalDateTime.now());
        ffoPlayingService.userSendFfoSentence(roomId, userEntity, ffoSentenceInputVO);
    }

    @PostMapping("/{roomId}/vote")
    public ResponseResult<Boolean> postFfoVote(
            @PathVariable("roomId") String roomId, @RequestBody FfoVoteReqVO ffoVoteReqVO) {
        UserEntity userEntity = SecurityContextUtil.getUserEntity();
        ffoVoteReqVO.setCreateTime(LocalDateTime.now());
        return ffoPlayingService.userVoteFfoSentence(roomId, userEntity, ffoVoteReqVO);
    }

    @MessageMapping("/ffo/{roomId}/users")
    public void putUsersSequence(List<String> users, @DestinationVariable("roomId") String roomId) {
        UserEntity userEntity = SecurityContextUtil.getUserEntity();
        ffoService.updateUsersSequence(roomId, userEntity, users);
    }
    @MessageMapping("/ffo/{roomId}/kick_out/{kickOutUser}")
    public void kickOutUser(@DestinationVariable("roomId") String roomId,
                            @DestinationVariable("kickOutUser") String kickOutUser) {
        UserEntity userEntity = SecurityContextUtil.getUserEntity();
        ffoService.kickOutUser(roomId, userEntity, kickOutUser);
    }

    @MessageMapping("/ffo/{roomId}/update")
    public void updateGameRoom(@Validated FfoGameRoomReqVO ffoGameRoomReqVO,
                               @DestinationVariable("roomId") String roomId) {
        UserEntity userEntity = SecurityContextUtil.getUserEntity();
        ffoService.updateGameRoom(roomId, userEntity, ffoGameRoomReqVO);
    }

}
