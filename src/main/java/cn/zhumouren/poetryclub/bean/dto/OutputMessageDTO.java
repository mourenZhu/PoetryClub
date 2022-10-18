package cn.zhumouren.poetryclub.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class OutputMessageDTO implements Serializable {
    private String from;
    private String text;
    private LocalDateTime time;

    public String getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dtf.format(time);
    }
}
