package com.jornco.controller.receiver;

/**
 * 接受到设备传出来的信息
 * Created by kkopite on 2017/10/26.
 */

public class BLEMessage {

    private final String address;
    private final String msg;

    public BLEMessage(String address, String msg) {
        this.address = address;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public String getAddress() {

        return address;
    }

    @Override
    public String toString() {
        return "BLEMessage{" +
                "address='" + address + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
