package com.jornco.controller.receiver;

import com.jornco.controller.ble.SensorType;

/**
 * 接受到设备传出来的信息
 * Created by kkopite on 2017/10/26.
 */

public class BLEMessage {

    private final String address;
    private final String msg;

    private SensorType mType;
    private int port;

    public SensorType getType() {
        return mType;
    }

    public void setType(SensorType type) {
        mType = type;
    }

    private String[] recData;


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String[] getRecData() {
        return recData;
    }

    public void setRecData(String[] recData) {
        this.recData = recData;
    }

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
