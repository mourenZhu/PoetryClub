package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.constants.games.FfoStateType;
import cn.zhumouren.poetryclub.constants.games.FfoType;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Data
@ToString
public class FfoGameRoomResVO implements Serializable {
    private String id;
    private String name;
    private FfoType ffoType;
    private FfoStateType ffoStateType;
    private String homeowner;
    private Set<String> users;

    public int getRemainingSeats() {
        return ffoType.getNum() - users.size();
    }
}
