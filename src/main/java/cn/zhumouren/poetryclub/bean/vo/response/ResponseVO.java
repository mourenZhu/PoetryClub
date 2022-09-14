package cn.zhumouren.poetryclub.bean.vo.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/12 17:02
 **/
@Data
public class ResponseVO<T> implements Serializable {
    private int code;
    private T data;
    private String msg;

    public static <T> ResponseVO<T> success() {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.code = ResponseCode.SUCCESS.getCode();
        vo.msg = ResponseCode.SUCCESS.getMsg();
        return vo;
    }

    public static <T> ResponseVO<T> success(T data) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.code = ResponseCode.SUCCESS.getCode();
        vo.data = data;
        vo.msg = ResponseCode.SUCCESS.getMsg();
        return vo;
    }

    public static <T> ResponseVO<T> successWithMessage(String message) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.code = ResponseCode.SUCCESS.getCode();
        vo.msg = message;
        return vo;
    }

    public static <T> ResponseVO<T> success(ResponseCode code, T data) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.code = code.getCode();
        vo.data = data;
        vo.msg = code.getMsg();
        return vo;
    }

    public static <T> ResponseVO<T> failed() {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.code = ResponseCode.FAILED.getCode();
        vo.msg = ResponseCode.FAILED.getMsg();
        return vo;
    }

    public static <T> ResponseVO<T> failed(ResponseCode code) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.code = code.getCode();
        vo.msg = code.getMsg();
        return vo;
    }

    public static <T> ResponseVO<T> failed(String message) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.code = ResponseCode.FAILED.getCode();
        vo.msg = message;
        return vo;
    }

    public static <T> ResponseVO<T> failed(ResponseCode code, String message) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.code = code.getCode();
        vo.msg = message;
        return vo;
    }
}
