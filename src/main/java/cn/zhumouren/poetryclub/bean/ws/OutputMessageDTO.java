package cn.zhumouren.poetryclub.bean.ws;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OutputMessageDTO {
    private String from;
    private String text;
    private String time;
}