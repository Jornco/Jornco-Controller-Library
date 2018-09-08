package com.jornco.controller.receiver;

import com.jornco.controller.ble.SensorType;

import java.util.Arrays;

/**
 * 接受到设备传出来的信息
 * Created by kkopite on 2017/10/26.
 */

public class BLEMessage {

    // 地址
    private final String address;
    // 接受的数据
    private final byte[] msg;
    // 塔克返回的值, 出现错误
    private String errorMsg = "";

    private SensorType mType;
    private int port;
    private String[] recData;

    public SensorType getType() {
        return mType;
    }

    public void setType(SensorType type) {
        mType = type;
    }


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

    public BLEMessage(String address, byte[] msg) {
        this.address = address;
        this.msg = msg;
    }

    public String getAddress() {

        return address;
    }

    public byte[] getMsg() {
        return msg;
    }

    // 获取cmd type
    public byte getCMDType() {
        if (msg != null && msg.length >= 10) {
            return msg[5];
        }
        return 0;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "BLEMessage{" +
                "address='" + address + '\'' +
                ", msg='" + Arrays.toString(msg) + '\'' +
                ", recData=" + Arrays.toString(recData) +
                '}';
    }
}
