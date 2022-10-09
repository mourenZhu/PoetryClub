package cn.zhumouren.poetryclub.controller.ffo;

import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomReqVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.service.FfoService;
import cn.zhumouren.poetryclub.utils.SecurityContextUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class FfoController {

    private final FfoService ffoService;

    public FfoController(FfoService ffoService) {
        this.ffoService = ffoService;
    }

    @PostMapping("/games/ffo/room")
    public ResponseResult<String> createGameRoom(FfoGameRoomReqVO roomReqVO) {
        return ffoService.userCreateGameRoom(
                SecurityContextUtil.getUserEntity(), roomReqVO.getName(), roomReqVO.getFfoType());
    }

    @PostMapping("/games/ffo/room/enter")
    public ResponseResult<Boolean> EnterGameRoom(String roomId) {
        return ffoService.userEnterGameRoom(SecurityContextUtil.getUserEntity(), roomId);
    }


}
