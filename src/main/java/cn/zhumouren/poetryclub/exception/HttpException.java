package cn.zhumouren.poetryclub.exception;

import cn.zhumouren.poetryclub.common.response.ResponseCode;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/18 16:12
 **/
public class HttpException extends BaseException{
    public HttpException(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }
}
