package cn.zhumouren.poetryclub.bean.ws;

import lombok.Data;

import java.io.Serializable;

@Data
public class Message implements Serializable {
    private String from;
    private String text;
}
