package cn.zhumouren.poetryclub.utils;

import java.util.Random;

public class RoomIdUtil {

    private static final String[] GENERATE_SOURCE = new String[]{
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
            "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
    };

    public static String generateRoomId() {
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            stringBuffer.append(generateRandomChar());
        }
        return stringBuffer.toString();
    }

    private static String generateRandomChar() {
        Random random = new Random();
        return GENERATE_SOURCE[random.nextInt(GENERATE_SOURCE.length)];
    }

}
