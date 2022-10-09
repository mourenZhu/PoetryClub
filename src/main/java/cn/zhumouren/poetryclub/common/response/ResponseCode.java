package cn.zhumouren.poetryclub.common.response;

import lombok.Getter;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/12 17:10
 **/
@Getter
public enum ResponseCode {
    SUCCESS(20000, "成功"),
    FORBIDDEN(40300, "参数错误"),
    FAILED(50000, "失败"),
    ;
    private final int code;
    private final String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
