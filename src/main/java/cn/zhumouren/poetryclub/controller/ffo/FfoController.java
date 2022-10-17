package cn.zhumouren.poetryclub.controller.ffo;

import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomReqVO;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomResVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.service.FfoService;
import cn.zhumouren.poetryclub.utils.SecurityContextUtil;
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
                SecurityContextUtil.getUserEntity(), roomReqVO.getName(), roomReqVO.getFfoType());
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


}
