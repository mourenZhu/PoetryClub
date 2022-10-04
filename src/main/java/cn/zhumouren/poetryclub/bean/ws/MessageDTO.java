package cn.zhumouren.poetryclub.bean.ws;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageDTO implements Serializable {
    private String text;
}
