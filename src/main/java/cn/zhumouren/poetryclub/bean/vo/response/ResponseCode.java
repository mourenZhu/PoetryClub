package cn.zhumouren.poetryclub.bean.vo.response;

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
    FAILED(50000, "失败"),
    USERNAME_NOT_AVAILABLE(50001, "用户名不可用"),
    ;
    private final int code;
    private final String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
