package cn.zhumouren.poetryclub.common.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/12 17:02
 **/
@Data
public class ResponseResult<T> implements Serializable {
    private int code;
    private T data;
    private String msg;

    public static <T> ResponseResult<T> success() {
        ResponseResult<T> vo = new ResponseResult<>();
        vo.code = ResponseCode.SUCCESS.getCode();
        vo.msg = ResponseCode.SUCCESS.getMsg();
        return vo;
    }

    public static <T> ResponseResult<T> success(T data) {
        ResponseResult<T> vo = new ResponseResult<>();
        vo.code = ResponseCode.SUCCESS.getCode();
        vo.data = data;
        vo.msg = ResponseCode.SUCCESS.getMsg();
        return vo;
    }

    public static <T> ResponseResult<T> successWithMessage(String message) {
        ResponseResult<T> vo = new ResponseResult<>();
        vo.code = ResponseCode.SUCCESS.getCode();
        vo.msg = message;
        return vo;
    }

    public static <T> ResponseResult<T> success(ResponseCode code, T data) {
        ResponseResult<T> vo = new ResponseResult<>();
        vo.code = code.getCode();
        vo.data = data;
        vo.msg = code.getMsg();
        return vo;
    }

    public static <T> ResponseResult<T> failed() {
        ResponseResult<T> vo = new ResponseResult<>();
        vo.code = ResponseCode.FAILED.getCode();
        vo.msg = ResponseCode.FAILED.getMsg();
        return vo;
    }

    public static <T> ResponseResult<T> failed(ResponseCode code) {
        ResponseResult<T> vo = new ResponseResult<>();
        vo.code = code.getCode();
        vo.msg = code.getMsg();
        return vo;
    }

    public static <T> ResponseResult<T> failed(String message) {
        ResponseResult<T> vo = new ResponseResult<>();
        vo.code = ResponseCode.FAILED.getCode();
        vo.msg = message;
        return vo;
    }

    public static <T> ResponseResult<T> failed(ResponseCode code, String message) {
        ResponseResult<T> vo = new ResponseResult<>();
        vo.code = code.getCode();
        vo.msg = message;
        return vo;
    }
}
