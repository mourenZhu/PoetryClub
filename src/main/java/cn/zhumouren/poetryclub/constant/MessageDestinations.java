package cn.zhumouren.poetryclub.constant;

public class MessageDestinations {

    /**
     * 用户接收聊天房间的路径
     */
    public static final String USER_GAME_ROOM_MESSAGE_DESTINATION = "/game_room/messages";

    /**
     * 飞花令游戏房间数据路径
     */
    public static final String USER_GAME_FFO_ROOM_DESTINATION = "/game/ffo/room";
    /**
     * 用户被踢出房间路径
     */
    public static final String USER_GAME_FFO_ROOM_KICK_OUT_DESTINATION = "/game/ffo/room/kick_out";
    /**
     * 飞花令游戏数据路径
     */
    public static final String USER_GAME_FFO_DESTINATION = "/game/ffo";
    /**
     * 飞花令游戏句子路径
     */
    public static final String USER_GAME_FFO_SENTENCE_DESTINATION = "/game/ffo/sentence";
    /**
     * 飞花令下一个用户发言
     */
    public static final String USER_GAME_FFO_NEXT_DESTINATION = "/game/ffo/next";
    /**
     * 飞花令的令应该出现的索引位置
     */
    public static final String USER_GAME_FFO_KEYWORD_INDEX_DESTINATION = "/game/ffo/keyword_index";
    /**
     * 飞花令房间所有用户信息路径
     */
    public static final String USER_GAME_FFO_USERS_DESTINATION = "/game/ffo/users";

    /**
     * 飞花令用户投票路径
     */
    public static final String USER_GAME_FFO_USERS_VOTE_DESTINATION = "/game/ffo/vote";

    /**
     * 飞花令游戏结束路径
     */
    public static final String USER_GAME_FFO_OVER_DESTINATION = "/game/ffo/end";
}
