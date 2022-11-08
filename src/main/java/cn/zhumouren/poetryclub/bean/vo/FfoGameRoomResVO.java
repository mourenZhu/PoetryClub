package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.constants.games.FfoGamePoemType;
import cn.zhumouren.poetryclub.constants.games.FfoGameVerseType;
import cn.zhumouren.poetryclub.constants.games.FfoStateType;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Data
@ToString
public class FfoGameRoomResVO implements Serializable {
    private String id;
    private String name;
    private Integer maxPlayers;
    private Integer playerPreparationSecond;
    private Boolean display;
    private Boolean allowWordInAny;
    private FfoGamePoemType ffoGamePoemType;
    private FfoGameVerseType ffoGameVerseType;
    private FfoStateType ffoStateType;
    private String homeowner;
    private Set<String> users;

    public int getRemainingSeats() {
        return maxPlayers - users.size();
    }
}
