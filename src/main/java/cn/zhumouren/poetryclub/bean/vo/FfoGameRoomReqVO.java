package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.constants.games.FfoGamePoemType;
import cn.zhumouren.poetryclub.constants.games.FfoGameVerseType;
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
    private FfoGamePoemType ffoGamePoemType;
    private FfoGameVerseType ffoGameVerseType;
}
