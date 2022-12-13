package cn.zhumouren.poetryclub.controller.ffo;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.*;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constant.games.FfoGamePoemType;
import cn.zhumouren.poetryclub.constant.games.FfoStateType;
import cn.zhumouren.poetryclub.service.FfoPlayingService;
import cn.zhumouren.poetryclub.service.FfoService;
import cn.zhumouren.poetryclub.util.PageUtil;
import cn.zhumouren.poetryclub.util.SecurityContextUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/games/ffo")
@PreAuthorize("hasAnyRole('ROLE_USER')")
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
        if (ObjectUtils.isEmpty(roomId)) {
            return ResponseResult.failedWithMsg("房间号为空");
        }
        return ffoService.userEnterGameRoom(SecurityContextUtil.getUserEntity(), roomId);
    }

    @DeleteMapping("/room")
    public ResponseResult<Boolean> leaveGameRoom() {
        return ffoService.userLeaveGameRoom(SecurityContextUtil.getUserEntity());
    }

    @GetMapping("/room/")
    public ResponseResult<List<FfoGameRoomResVO>> listFfoGameRoom(
            @RequestParam(required = false) String roomId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean allowWordInAny,
            @RequestParam(required = false) FfoGamePoemType ffoGamePoemType,
            @RequestParam(required = false) FfoStateType ffoStateType) {
        return ffoService.listFfoGameRoom(roomId, keyword, allowWordInAny, ffoGamePoemType, ffoStateType);
    }

    @PostMapping
    public ResponseResult<Boolean> startGame() {
        UserEntity userEntity = SecurityContextUtil.getUserEntity();
        return ffoPlayingService.userStartGame(userEntity);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/")
    public ResponseResult<Page<FfoGameResVo>> listFfoGame(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        return ffoService.listFfoGame(PageUtil.getPageable(pageNum, pageSize));
    }

    @GetMapping("/{username}/")
    public ResponseResult<Page<FfoGameResVo>> listUserFfoGame(
            @PathVariable("username") String username,
            @RequestParam(required = false) Character keyword,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        UserEntity userEntity = SecurityContextUtil.getUserEntity();
        if (!userEntity.getUsername().equals(username)) {
            return ResponseResult.failedWithMsg("没有权限查看其他用户的比赛记录");
        }
        if (ObjectUtils.isNotEmpty(keyword)) {
            return ffoService.listUserFfoGame(username, keyword, PageUtil.getPageable(pageNum, pageSize));
        }
        return ffoService.listUserFfoGame(username, PageUtil.getPageable(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseResult<FfoGameResVo> getFfoGame(@PathVariable("id") Long id) {
        return ffoService.getFfoGame(id);
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
