package cn.zhumouren.poetryclub.exception;

import cn.zhumouren.poetryclub.common.response.ResponseCode;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/14 19:38
 **/
public class UsernameNotAvailableException extends BaseException {

    public UsernameNotAvailableException() {
        responseCode = ResponseCode.USERNAME_NOT_AVAILABLE;
    }

    public UsernameNotAvailableException(String message) {
        super(message);
    }
}
