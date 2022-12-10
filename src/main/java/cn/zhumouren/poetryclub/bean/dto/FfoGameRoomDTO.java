package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.bean.mapper.FfoGameMapper;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomReqVO;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomResVO;
import cn.zhumouren.poetryclub.constant.games.FfoGamePoemType;
import cn.zhumouren.poetryclub.constant.games.FfoStateType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class FfoGameRoomDTO implements Serializable {
    private String id;
    private String name;
    private Integer maxPlayers;
    /**
     * 飞花令的关键字
     */
    private Character keyword;
    /**
     * 允许字出现在任意位置
     */
    private Boolean allowWordInAny;
    /**
     * 单位 秒
     */
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
    private Boolean display;
    private FfoGamePoemType ffoGamePoemType;
    private FfoStateType ffoStateType;
    private UserDTO homeowner;
    private List<UserDTO> users;

    public static FfoGameRoomDTO creatFfoGameRoomDTO(FfoGameRoomReqVO ffoGameRoomReqVO, String id, UserDTO homeowner, UserDTO... user) {
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

    @JsonIgnore
    public List<String> getUsernames() {
        return users.stream().map(UserDTO::getUsername).collect(Collectors.toList());
    }

    public boolean removeUser(UserDTO userDTO) {
        return users.remove(userDTO);
    }

    @JsonIgnore
    public UserDTO getUserDTOByUsername(String username) {
        for (UserDTO userDTO : users) {
            if (userDTO.getUsername().equals(username)) {
                return userDTO;
            }
        }
        return null;
    }

    public boolean hasUser(String user) {
        return getUsernames().contains(user);
    }

    public void update(FfoGameRoomReqVO ffoGameRoomReqVO) {
        this.name = ffoGameRoomReqVO.getName();
        this.maxPlayers = ffoGameRoomReqVO.getMaxPlayers();
        this.keyword = ffoGameRoomReqVO.getKeyword();
        this.allowWordInAny = ffoGameRoomReqVO.getAllowWordInAny();
        this.playerPreparationSecond = ffoGameRoomReqVO.getPlayerPreparationSecond();
        this.maxSentenceLength = ffoGameRoomReqVO.getMaxSentenceLength();
        this.constantSentenceLength = ffoGameRoomReqVO.getConstantSentenceLength();
        this.display = ffoGameRoomReqVO.getDisplay();
        this.ffoGamePoemType = ffoGameRoomReqVO.getFfoGamePoemType();
    }
}
