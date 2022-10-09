package cn.zhumouren.poetryclub.controller.ffo;

import cn.zhumouren.poetryclub.bean.dto.InputTextMessageDTO;
import cn.zhumouren.poetryclub.bean.dto.OutputMessageDTO;
import cn.zhumouren.poetryclub.utils.SecurityContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/8/24 18:47
 **/
@Controller
@Slf4j
public class ChatroomController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatroomController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/chatroom/{roomID}")
//    @SendTo("/topic/messages")
    public void send(InputTextMessageDTO inputTextMessageDTO, @DestinationVariable("roomID") String roomID) throws Exception {
        String name = SecurityContextUtil.getUserEntity().getName();
        log.debug("room id = {}", roomID);
        simpMessagingTemplate
                .convertAndSendToUser("zhumouren", "/messages",
                        new OutputMessageDTO(name, inputTextMessageDTO.getText(), LocalDateTime.now().toString()));
    }


}
