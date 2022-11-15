package cn.zhumouren.poetryclub.constants.games;

public enum FfoGameSentenceJudgeType {
    /**
     * 成功
     */
    SUCCESS,
    /**
     * 内容为空
     */
    NULL_CONTENT,
    /**
     * 长度不符合该轮游戏设置
     */
    LENGTH_NOT_MATCH,
    /**
     * 没有指定的关键字
     */
    NO_KEYWORD,
    /**
     * 关键字没有在相应的位置
     */
    KEYWORD_NOT_IN_CORRECT_POSITION,
    /**
     * 只允许说古诗，但没有在数据库中找到
     */
    ONLY_ANCIENTS_POEM_BUT_NOT_FIND,
    /**
     * 只允许自己创作的，但在数据库中找到了
     */
    ONLY_SELF_CREAT_BUT_FIND,
    /**
     * 玩家等待用户投票
     * 不出bug的话，这个类型不会存入持久层数据库
     */
    WAITING_USERS_VOTE,
    /**
     * 在程序不能确定的情况下，玩家投票认为这个句子不行
     */
    VOTE_FAILED;
}
