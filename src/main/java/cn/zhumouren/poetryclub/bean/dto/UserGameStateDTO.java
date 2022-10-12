package cn.zhumouren.poetryclub.bean.dto;

import cn.zhumouren.poetryclub.constants.GamesType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户游戏状态
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGameStateDTO implements Serializable {
    private String roomId;
    private GamesType gamesType;
}
