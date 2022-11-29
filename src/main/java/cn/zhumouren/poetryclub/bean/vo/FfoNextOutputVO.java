package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.bean.dto.FfoGameDTO;
import cn.zhumouren.poetryclub.bean.dto.UserDTO;
import cn.zhumouren.poetryclub.util.FfoGameUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FfoNextOutputVO {
    private UserDTO nextUser;
    private LocalDateTime nextEndTime;

    public FfoNextOutputVO(FfoGameDTO ffoGameDTO) {
        this.nextEndTime = FfoGameUtil.getFfoSpeakerNextEndTime(ffoGameDTO);
        this.nextUser = ffoGameDTO.getNextSpeaker();
    }
}
