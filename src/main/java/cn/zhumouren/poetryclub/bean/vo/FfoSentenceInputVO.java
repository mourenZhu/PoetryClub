package cn.zhumouren.poetryclub.bean.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FfoSentenceInputVO implements Serializable {
    String sentence;
    LocalDateTime createTime;
}
