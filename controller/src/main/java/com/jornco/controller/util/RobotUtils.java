package com.jornco.controller.util;

/**
 * Created by kkopite on 2017/10/25.
 */

public class RobotUtils {

    /**
     *
     * @param min 最小值
     * @param max 最大值
     * @return    随机数, [min, max]
     */
    public static int getRandom(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min)) + min;
    }

    /**
     *
     * @return 0 - 255的随机数
     */
    private static int getRandom() {
        return getRandom(0, 255);
    }

    /**
     * 随机led指令
     * @return 指令
     */
    public static String getCmd() {
        return "#B" + getRandom() + "," + getRandom() + "," + getRandom() +",*";
    }
}
