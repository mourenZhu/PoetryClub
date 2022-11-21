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
    /**
     * 允许字出现在任意位置
     */
    private Boolean allowWordInAny;
    @Min(15)
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
}
