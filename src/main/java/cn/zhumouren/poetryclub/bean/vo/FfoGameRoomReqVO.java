package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.constant.games.FfoGamePoemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FfoGameRoomReqVO implements Serializable {
    @Length(message = "检查游戏房间名长度", min = 1, max = 30)
    private String name;
    @Range(max = 10, min = 1)
    private Integer maxPlayers;
    @Min(15)
    private Integer playerPreparationSecond;
    private Boolean display;
    private Boolean allowWordInAny;
    private FfoGamePoemType ffoGamePoemType;
}
