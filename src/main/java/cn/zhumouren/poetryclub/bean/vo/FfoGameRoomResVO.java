package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.bean.dto.UserDTO;
import cn.zhumouren.poetryclub.constant.games.FfoGamePoemType;
import cn.zhumouren.poetryclub.constant.games.FfoStateType;
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
    private FfoStateType ffoStateType;
    private UserDTO homeowner;
    private Set<UserDTO> users;

    public int getRemainingSeats() {
        return maxPlayers - users.size();
    }
}
