package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.bean.mapper.FfoGameMapper;
import cn.zhumouren.poetryclub.constant.games.FfoGamePoemType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Iterables;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class FfoGameDTO implements Serializable {

    private String roomId;

    private List<UserDTO> users;

    /**
     * 正在玩游戏的玩家（即没有被淘汰的人）
     * 队首的就是当前回合要说飞花令的人
     */
    private Queue<UserDTO> playingUsers;

    private Character keyword;

    /**
     * 飞花令的字现在到到哪个位置了
     * 从0开始
     */
    private Integer keywordIndex;

    private Boolean allowWordInAny;

    private Integer playerPreparationSecond;

    /**
     * 飞花令句子的最大长度
     */
    private Integer maxSentenceLength;
    /**
     * 本局游戏飞花令句子长度是否可变
     * 如果不可变，则每回合的句子长度 等于 句子的最大长度
     * 如果可变，则每回合的句子长度 小于等于 句子的最大长度
     */
    private Boolean constantSentenceLength;

    private FfoGamePoemType ffoGamePoemType;

    private List<FfoGameSentenceDTO> userSentences;

    private Deque<UserDTO> ranking;

    private LocalDateTime createTime;

    private LocalDateTime endTime;

    public FfoGameDTO(FfoGameRoomDTO ffoGameRoomDTO) {
        roomId = ffoGameRoomDTO.getId();
        users = ffoGameRoomDTO.getUsers();
        playingUsers = new ConcurrentLinkedQueue<>(ffoGameRoomDTO.getUsers());
        keyword = ffoGameRoomDTO.getKeyword();
        allowWordInAny = ffoGameRoomDTO.getAllowWordInAny();
        if (allowWordInAny) {
            keywordIndex = -1;
        } else {
            keywordIndex = 0;
        }
        playerPreparationSecond = ffoGameRoomDTO.getPlayerPreparationSecond();
        maxSentenceLength = ffoGameRoomDTO.getMaxSentenceLength();
        constantSentenceLength = ffoGameRoomDTO.getConstantSentenceLength();
        ffoGamePoemType = ffoGameRoomDTO.getFfoGamePoemType();
        userSentences = new ArrayList<>();
        ranking = new ArrayDeque<>(ffoGameRoomDTO.getUsers().size());
        createTime = LocalDateTime.now();
    }

    @JsonIgnore
    public UserDTO getNextSpeaker() {
        return playingUsers.peek();
    }

    @JsonIgnore
    public FfoGameSentenceDTO getLastSentenceDTO() {
        return Iterables.getLast(userSentences);
    }

    @JsonIgnore
    public int getUserSequence(UserDTO userDTO) {
        return this.users.indexOf(userDTO) + 1;
    }

    @JsonIgnore
    public List<String> getUsernames() {
        return users.stream().map(UserDTO::getUsername).collect(Collectors.toList());
    }
}


