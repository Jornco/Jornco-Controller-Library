package com.jornco.controller.ble;

/**
 * 设备连接变化, 接受信息接口
 * Created by kkopite on 2017/11/29.
 */

public interface OnBLEDeviceChangeListener {

    /**
     * 连接变化
     * @param address 地址
     * @param state   连接, 断开, 连接中
     */
    void bleDeviceStateChange(String address, BLEState state);

    /**
     * 接受到信息
     * @param address 地址
     * @param msg     信息
     */
    void bleDeviceReceive(String address, byte[] msg);
}
