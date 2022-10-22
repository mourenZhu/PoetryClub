package cn.zhumouren.poetryclub.bean.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class InputTextMessageDTO implements Serializable {
    private String content;
}
