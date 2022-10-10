package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.constants.games.FfoStateType;
import cn.zhumouren.poetryclub.constants.games.FfoType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class FfoGameRoomDTO implements Serializable {
    private String id;
    private String name;
    private FfoType ffoType;
    private FfoStateType ffoStateType;
    private String homeowner;
    private Set<String> users;

    public FfoGameRoomDTO(String id, String name, FfoType ffoType, FfoStateType ffoStateType, String homeowner, String... user) {
        this.id = id;
        this.name = name;
        this.ffoType = ffoType;
        this.ffoStateType = ffoStateType;
        this.homeowner = homeowner;
        this.users = new HashSet<>(Arrays.asList(user));
    }
}