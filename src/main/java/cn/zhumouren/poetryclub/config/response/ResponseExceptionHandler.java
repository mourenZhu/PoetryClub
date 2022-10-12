package cn.zhumouren.poetryclub.config.response;

import cn.zhumouren.poetryclub.common.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/12 17:39
 **/
@Slf4j
@RestControllerAdvice
public class ResponseExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult<Boolean> exception(Exception e) {
        log.error("全局异常信息 ex={}", e.getMessage());
        return ResponseResult.failedWithMsg(e.getMessage());
    }

}
