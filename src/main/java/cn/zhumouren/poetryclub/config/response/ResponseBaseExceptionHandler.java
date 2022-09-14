package cn.zhumouren.poetryclub.config.response;

import cn.zhumouren.poetryclub.bean.vo.response.ResponseVO;
import cn.zhumouren.poetryclub.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/14 22:46
 **/
@RestControllerAdvice
public class ResponseBaseExceptionHandler {

    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseVO<String> exception(BaseException e) {
        return ResponseVO.failed(e.getResponseCode());
    }
}
