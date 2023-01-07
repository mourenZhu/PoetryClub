package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.common.response.ResponseResult;

public interface EmailService {

    ResponseResult<Boolean> sendVerificationCode(String mail);
}
