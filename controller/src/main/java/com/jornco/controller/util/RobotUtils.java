package com.jornco.controller.util;

/**
 * Created by kkopite on 2017/10/25.
 */

public class RobotUtils {

    private static final String RS_BLE_BYTES = new String(new byte[]{0x07, 0x09, 0x52, 0x53, 0x2D, 0x42, 0x4c, 0x45});
    private static final String RS_BLE = "RS-BLE";

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

    /**
     * HACKER 从蓝牙广播中, 获取蓝牙名称
     * @param scanRecord 广播
     * @return 蓝牙名称
     */
    public static String getBLEName(byte[] scanRecord) {
        String t = new String(scanRecord);
        if (t.contains(RS_BLE_BYTES)) {
            return RS_BLE;
        }
        return "";
    }
}
