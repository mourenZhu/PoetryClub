package cn.zhumouren.poetryclub.constants.games;

/**
 * 飞花令可选的诗句类型
 * 比如选五字诗，就是每个玩家回答回答时，只能回答五个字的诗句
 */
public enum FfoGameVerseType {
    /**
     * 都可以
     */
    ALL,
    /**
     * 五字诗
     */
    FIVE_CHARACTER,
    /**
     * 七字诗
     */
    SEVEN_CHARACTER;
}
