package com.jornco.controller.error;

/**
 * 蓝牙写入错误
 * Created by kkopite on 2017/10/25.
 */

public class BLEWriterError extends Exception {

    private final static String FORMAT = "[对地址 %s 发送 %s 失败, 原因: %s]";

    public BLEWriterError(String address, String cmd, String message) {
        super(String.format(FORMAT, address, cmd, message));
    }
}
