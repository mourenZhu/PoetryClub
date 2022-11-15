package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.InputFfoSentenceVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;

public interface FfoPlayingService {

    ResponseResult<Boolean> userStartGame(UserEntity userEntity);

    void userSendFfoSentence(String roomId, UserEntity user, InputFfoSentenceVO inputFfoSentenceVO);
}
