package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.bean.dto.FfoGameDTO;
import cn.zhumouren.poetryclub.bean.dto.UserDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Deque;

@Data
@NoArgsConstructor
public class FfoGameOverOutputVO implements Serializable {

    private Deque<UserDTO> ranking;

    private LocalDateTime endTime;

    public FfoGameOverOutputVO(FfoGameDTO ffoGameDTO) {
        this.ranking = ffoGameDTO.getRanking();
        this.endTime = ffoGameDTO.getEndTime();
    }
}
