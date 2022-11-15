package cn.zhumouren.poetryclub.constant.games;

/**
 * 飞花令回答时，可选的诗类型
 */
public enum FfoGamePoemType {
    /**
     * 只允许说古诗
     */
    ONLY_ANCIENTS_POEM,
    /**
     * 只允许自己创作
     */
    ONLY_SELF_CREAT,
    /**
     * 可以说古诗，也可以自己创作。先检索古诗，没找到算自己创作的
     */
    ALL
}
