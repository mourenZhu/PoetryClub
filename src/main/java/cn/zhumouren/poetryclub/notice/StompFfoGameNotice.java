package cn.zhumouren.poetryclub.notice;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.FfoSpeakInfoOutputVO;
import cn.zhumouren.poetryclub.bean.vo.MessageOutputVO;
import cn.zhumouren.poetryclub.constant.MessageDestinations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class StompFfoGameNotice {

    private final SimpMessagingTemplate messagingTemplate;

    public StompFfoGameNotice(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 用户在房间中的行为通知
     *
     * @param user        做出行为的用户
     * @param msg         要通知的信息
     * @param noticeUsers 被通知的用户
     */
    public void userGameRoomActionNotice(UserEntity user, String msg, Iterable<String> noticeUsers) {
        noticeUsers.forEach(username -> {
            if (!username.equals(user.getUsername())) {
                log.debug("发送给 = {}", username);
                messagingTemplate.convertAndSendToUser(username, MessageDestinations.USER_GAME_ROOM_MESSAGE_DESTINATION,
                        MessageOutputVO.getOutputMessageDTO(user, msg));
            }
        });
    }

    public void ffoSpeakNotice(List<String> usernames, FfoSpeakInfoOutputVO ffoSpeakInfoOutputVO) {
        usernames.forEach(username -> {
            messagingTemplate.convertAndSendToUser(username, MessageDestinations.USER_GAME_FFO_INFO_MESSAGE_DESTINATION,
                    ffoSpeakInfoOutputVO);
        });
    }
}
