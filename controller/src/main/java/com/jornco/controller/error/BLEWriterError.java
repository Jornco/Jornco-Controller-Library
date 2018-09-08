package com.jornco.controller.error;

import java.util.Arrays;

/**
 * 蓝牙写入错误
 * Created by kkopite on 2017/10/25.
 */

public class BLEWriterError extends Exception {

    private final static String FORMAT = "[对地址 %s 发送 %s 失败, 原因: %s]";

    public BLEWriterError(String address, byte[] cmd, String message) {
        // 每次都重新实例, 好吗?
        super(String.format(FORMAT, address, Arrays.toString(cmd), message));
    }
}
