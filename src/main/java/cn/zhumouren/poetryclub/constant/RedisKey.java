package cn.zhumouren.poetryclub.constant;

import lombok.Getter;

@Getter
public enum RedisKey {
    FFO_GAME_ROOM_KEY,

    FFO_GAME_KEY,
    USER_GAME_STATE,
    // 用户验证码
    USER_VERIFICATION_CODE
    ;
}
