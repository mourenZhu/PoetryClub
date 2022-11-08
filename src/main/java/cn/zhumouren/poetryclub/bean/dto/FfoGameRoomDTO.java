package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.bean.mapper.FfoGameMapper;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomReqVO;
import cn.zhumouren.poetryclub.constants.games.FfoGamePoemType;
import cn.zhumouren.poetryclub.constants.games.FfoGameVerseType;
import cn.zhumouren.poetryclub.constants.games.FfoStateType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class FfoGameRoomDTO implements Serializable {
    private String id;
    private String name;
    private Integer maxPlayers;
    /**
     * 单位 秒
     */
    private Integer playerPreparationSecond;
    /**
     * 允许字出现在任意位置
     */
    private Boolean allowWordInAny;
    /**
     * 飞花令的关键字
     */
    private Character keyword;
    private Boolean display;
    private FfoGamePoemType ffoGamePoemType;
    private FfoGameVerseType ffoGameVerseType;
    private FfoStateType ffoStateType;
    private String homeowner;
    private List<String> users;

    public static FfoGameRoomDTO creatFfoGameRoomDTO(FfoGameRoomReqVO ffoGameRoomReqVO, String id, String homeowner, String... user) {
        FfoGameRoomDTO ffoGameRoomDTO = FfoGameMapper.INSTANCE.ffoGameRoomReqVOToFfoGameRoomDTO(ffoGameRoomReqVO);
        ffoGameRoomDTO.setId(id);
        ffoGameRoomDTO.setHomeowner(homeowner);
        ffoGameRoomDTO.setUsers(Arrays.asList(user));
        ffoGameRoomDTO.setFfoStateType(FfoStateType.WAITING);
        return ffoGameRoomDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FfoGameRoomDTO that = (FfoGameRoomDTO) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
