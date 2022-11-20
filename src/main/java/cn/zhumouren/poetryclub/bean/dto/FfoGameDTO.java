package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.bean.mapper.FfoGameMapper;
import cn.zhumouren.poetryclub.constant.games.FfoGamePoemType;
import com.google.common.collect.Iterables;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Data
public class FfoGameDTO implements Serializable {

    private String roomId;

    private List<String> users;

    /**
     * 正在玩游戏的玩家（即没有被淘汰的人）
     * 队首的就是当前回合要说飞花令的人
     */
    private Queue<String> playingUsers;

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

    private Deque<String> ranking;

    private LocalDateTime createTime;

    private LocalDateTime endTime;

    public static FfoGameDTO getFfoGameDTOByFfoGameRoomDTO(FfoGameRoomDTO ffoGameRoomDTO) {
        FfoGameDTO ffoGameDTO = FfoGameMapper.INSTANCE.ffoGameRoomDTOToFfoGameDTO(ffoGameRoomDTO);
        ffoGameDTO.setRoomId(ffoGameRoomDTO.getId());
        ffoGameDTO.setCreateTime(LocalDateTime.now());
        ffoGameDTO.setKeyword(ffoGameRoomDTO.getKeyword());
        if (ffoGameDTO.getAllowWordInAny()) {
            ffoGameDTO.setKeywordIndex(-1);
        } else {
            ffoGameDTO.setKeywordIndex(0);
        }
        ffoGameDTO.setPlayingUsers(new ConcurrentLinkedQueue<>(ffoGameRoomDTO.getUsers()));
        ffoGameDTO.setRanking(new ArrayDeque<>());
        ffoGameDTO.setUserSentences(new LinkedList<>());
        return ffoGameDTO;
    }

    public String getNextSpeaker() {
        return playingUsers.peek();
    }

    public FfoGameSentenceDTO getLastSentenceDTO() {
        return Iterables.getLast(userSentences);
    }
}


