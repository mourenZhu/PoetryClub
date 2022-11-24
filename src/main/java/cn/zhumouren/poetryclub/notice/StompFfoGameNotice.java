package cn.zhumouren.poetryclub.notice;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.FfoGameOverOutputVO;
import cn.zhumouren.poetryclub.bean.vo.FfoSpeakInfoOutputVO;
import cn.zhumouren.poetryclub.bean.vo.FfoVoteOutputVO;
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
            log.debug("发送给 = {}", username);
            messagingTemplate.convertAndSendToUser(username, MessageDestinations.USER_GAME_ROOM_MESSAGE_DESTINATION,
                    MessageOutputVO.getOutputMessageDTO(user, msg));
        });
    }

    public void ffoSpeakNotice(List<String> users, FfoSpeakInfoOutputVO ffoSpeakInfoOutputVO) {
        users.forEach(user -> messagingTemplate.convertAndSendToUser(
                user, MessageDestinations.USER_GAME_FFO_INFO_MESSAGE_DESTINATION, ffoSpeakInfoOutputVO));
    }

    public void ffoVoteNotice(List<String> users, FfoVoteOutputVO ffoVoteOutputVO) {
        users.forEach(user -> messagingTemplate.convertAndSendToUser(
                user, MessageDestinations.USER_GAME_FFO_USERS_VOTE_MESSAGE_DESTINATION, ffoVoteOutputVO));
    }

    public void ffoGameOverNotice(List<String> users, FfoGameOverOutputVO ffoGameOverOutputVO) {
        users.forEach(user -> messagingTemplate.convertAndSendToUser(
                user, MessageDestinations.USER_GAME_FFO_OVER_MESSAGE_DESTINATION, ffoGameOverOutputVO));
    }
}
