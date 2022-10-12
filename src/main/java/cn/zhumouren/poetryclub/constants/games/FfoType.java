package cn.zhumouren.poetryclub.constants.games;

public enum FfoType {
    // 五人游戏
    FIVE_PLAYER_GAME,
    // 七人游戏
    SEVEN_PLAYER_GAME;

    public int getNum() {
        if (this.equals(FfoType.FIVE_PLAYER_GAME)) {
            return 5;
        } else if (this.equals(FfoType.SEVEN_PLAYER_GAME)) {
            return 7;
        }
        return 0;
    }
}
