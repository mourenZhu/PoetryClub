package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.constants.games.FfoType;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class FfoGameRoomReqVO implements Serializable {
    @Length(message = "检查游戏房间名长度", min = 1, max = 30)
    private String name;
    @NotNull
    private FfoType ffoType;
}
