package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.constants.games.FfoGamePoemType;
import cn.zhumouren.poetryclub.constants.games.FfoGameVerseType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

@Data
public class FfoGameDTO implements Serializable {

    private String roomId;

    private List<String> users;

    private Queue<String> playingUsers;

    private Integer playerPreparationSecond;

    private Character keyword;

    private Boolean allowWordInAny;

    private FfoGamePoemType ffoGamePoemType;

    private FfoGameVerseType ffoGameVerseType;

    private List<FfoGameUserSentenceDTO> userSentences;

    private String nextSpeaker;

    private Deque<String> ranking;

    private LocalDateTime createTime;

    private LocalDateTime endTime;

}


