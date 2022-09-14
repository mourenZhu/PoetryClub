package cn.zhumouren.poetryclub.exception;

import cn.zhumouren.poetryclub.bean.vo.response.ResponseCode;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/14 19:40
 **/
public class BaseException extends RuntimeException {

    protected ResponseCode responseCode;

    public BaseException() {
        super();
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

}
