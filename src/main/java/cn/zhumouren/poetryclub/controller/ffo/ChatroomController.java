package cn.zhumouren.poetryclub.controller.ffo;

import cn.zhumouren.poetryclub.bean.dto.InputTextMessageDTO;
import cn.zhumouren.poetryclub.bean.dto.OutputMessageDTO;
import cn.zhumouren.poetryclub.service.RedisUserService;
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

    private final RedisUserService redisUserService;

    public ChatroomController(SimpMessagingTemplate simpMessagingTemplate, RedisUserService redisUserService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.redisUserService = redisUserService;
    }

    @MessageMapping("/chatroom/{roomID}")
//    @SendTo("/topic/messages")
    public void send(InputTextMessageDTO inputTextMessageDTO, @DestinationVariable("roomID") String roomID) throws Exception {
        String name = SecurityContextUtil.getUserEntity().getName();
        log.debug("room id = {}", roomID);
        redisUserService.listGameRoomUser(roomID).forEach(username -> {
            simpMessagingTemplate
                    .convertAndSendToUser(username, "/messages",
                            new OutputMessageDTO(name, inputTextMessageDTO.getText(), LocalDateTime.now()));
        });
    }


}
