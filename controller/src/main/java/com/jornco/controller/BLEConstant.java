package com.jornco.controller;

/**
 * 塔克执行命令
 * Created by kkopite on 2018/9/8.
 */

public class BLEConstant {

    public static final String HEADER = "$*$";
    public static final String TAIL = "#*#";

    public static final byte CMD_DISTANCE = 0X01;
    public static final byte CMD_VOICE_LEVEL = 0X02;
    public static final byte CMD_KEY_INDEX = 0X03;
    public static final byte CMD_IR_KEY_INDEX = 0X04;
    public static final byte CMD_TRACK_SWITCH = 0X05;
    public static final byte CMD_LED = 0X06;
    public static final byte CMD_SERVO = 0X07;
    public static final byte CMD_CONTROL_MODE = 0X08;
    public static final byte CMD_CONTROL_MESSAGE = 0X09;
    // 这个文档有点问题呀
    public static final byte CMD_DOWNLOAD_SCRIPT = 0X0a;
    public static final byte CMD_CLEAR_SCRIPT = 0X0b;
    public static final byte CMD_DOWNLOAD_TMP_SCRIPT= 0X0c;
    public static final byte CMD_CONTROL_UPGRADE = 0X0d;
    public static final byte CMD_VOICE_PLAY = 0X0e;
    public static final byte CMD_SERVO_WALK = 0x0f;
}
