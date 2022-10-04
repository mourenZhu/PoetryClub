package cn.zhumouren.poetryclub.controller;

import cn.zhumouren.poetryclub.bean.ws.MessageDTO;
import cn.zhumouren.poetryclub.bean.ws.OutputMessageDTO;
import cn.zhumouren.poetryclub.utils.SecurityUtil;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/8/24 18:47
 **/
@Controller
public class GameRoomController {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public OutputMessageDTO send(MessageDTO messageDTO) throws Exception {
        String name = SecurityUtil.getUserEntity().getName();
        return new OutputMessageDTO(name, messageDTO.getText(), LocalDateTime.now().toString());
    }


}
