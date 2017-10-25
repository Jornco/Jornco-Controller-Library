package com.jornco.controller.util;

/**
 * Created by kkopite on 2017/10/25.
 */

public class RobotUtils {

    public static int getRandom(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min)) + min;
    }

    private static int getRandom() {
        return (int) Math.floor(Math.random() * 255);
    }

    public static String getCmd() {
        return "#B" + getRandom() + "," + getRandom() + "," + getRandom() +",*";
    }
}
