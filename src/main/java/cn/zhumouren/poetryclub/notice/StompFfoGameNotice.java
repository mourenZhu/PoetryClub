package cn.zhumouren.poetryclub.notice;

import cn.zhumouren.poetryclub.bean.dto.FfoGameDTO;
import cn.zhumouren.poetryclub.bean.dto.FfoGameRoomDTO;
import cn.zhumouren.poetryclub.bean.dto.FfoGameSentenceDTO;
import cn.zhumouren.poetryclub.bean.dto.UserDTO;
import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.FfoGameOverOutputVO;
import cn.zhumouren.poetryclub.bean.vo.FfoNextOutputVO;
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
                    new MessageOutputVO(user, msg));
        });
    }

    /**
     * 用户在房间中的行为通知
     *
     * @param user        做出行为的用户
     * @param msg         要通知的信息
     * @param noticeUsers 被通知的用户
     */
    public void userGameRoomActionNotice(UserDTO user, String msg, Iterable<String> noticeUsers) {
        noticeUsers.forEach(username -> {
            log.debug("发送给 = {}", username);
            messagingTemplate.convertAndSendToUser(username, MessageDestinations.USER_GAME_ROOM_MESSAGE_DESTINATION,
                    new MessageOutputVO(user, msg));
        });
    }


    public void ffoGameRoomUserKickOutNotice(String kickOutUser) {
        messagingTemplate.convertAndSendToUser(kickOutUser,
                MessageDestinations.USER_GAME_FFO_ROOM_KICK_OUT_DESTINATION,
                kickOutUser + "被踢出房间了");
    }

    /**
     * 游戏数据通知
     */
    public void ffoGameNotice(FfoGameDTO ffoGameDTO) {
        log.debug("游戏数据通知 {}", ffoGameDTO);
        ffoGameDTO.getUsernames().forEach(user -> messagingTemplate
                .convertAndSendToUser(user, MessageDestinations.USER_GAME_FFO_DESTINATION, ffoGameDTO));
    }

    public void ffoSentenceNotice(Iterable<String> users, FfoGameSentenceDTO ffoGameSentenceDTO) {
        log.debug("通知, 飞花令为: {}", ffoGameSentenceDTO);
        users.forEach(user -> messagingTemplate.convertAndSendToUser(
                user, MessageDestinations.USER_GAME_FFO_SENTENCE_DESTINATION, ffoGameSentenceDTO));
    }

    public void ffoNextNotice(Iterable<String> users, FfoNextOutputVO ffoNextOutputVO) {
        log.debug("通知, 下一个回答的用户");
        users.forEach(user -> messagingTemplate.convertAndSendToUser(
                user, MessageDestinations.USER_GAME_FFO_NEXT_DESTINATION, ffoNextOutputVO));
    }

    public void ffoKeywordIndexNotice(Iterable<String> users, Integer keywordIndex) {
        users.forEach(user -> messagingTemplate.convertAndSendToUser(
                user, MessageDestinations.USER_GAME_FFO_KEYWORD_INDEX_DESTINATION, keywordIndex));
    }

    public void ffoVoteNotice(Iterable<String> users, FfoVoteOutputVO ffoVoteOutputVO) {
        users.forEach(user -> messagingTemplate.convertAndSendToUser(
                user, MessageDestinations.USER_GAME_FFO_USERS_VOTE_DESTINATION, ffoVoteOutputVO));
    }

    public void ffoGameOverNotice(Iterable<String> users, FfoGameOverOutputVO ffoGameOverOutputVO) {
        users.forEach(user -> messagingTemplate.convertAndSendToUser(
                user, MessageDestinations.USER_GAME_FFO_OVER_DESTINATION, ffoGameOverOutputVO));
    }

    public void ffoGameRoomUsersNotice(List<UserDTO> users) {
        log.debug("users = {}", users);
        users.forEach(user -> messagingTemplate.convertAndSendToUser(user.getUsername(),
                MessageDestinations.USER_GAME_FFO_USERS_DESTINATION, users));
    }

    public void ffoGameRoomNotice(FfoGameRoomDTO ffoGameRoomDTO) {
        ffoGameRoomDTO.getUsernames().forEach(user ->
                messagingTemplate.convertAndSendToUser(user,
                        MessageDestinations.USER_GAME_FFO_ROOM_DESTINATION, ffoGameRoomDTO));
    }
}
