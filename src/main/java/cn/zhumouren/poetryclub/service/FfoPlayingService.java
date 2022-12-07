package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.FfoSentenceInputVO;
import cn.zhumouren.poetryclub.bean.vo.FfoVoteReqVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;

public interface FfoPlayingService {

    ResponseResult<Boolean> userStartGame(UserEntity userEntity);

    void userSendFfoSentence(String roomId, UserEntity user, FfoSentenceInputVO ffoSentenceInputVO);

    ResponseResult<Boolean> userVoteFfoSentence(String roomId, UserEntity userEntity, FfoVoteReqVO ffoVoteReqVO);
}
