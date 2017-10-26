package com.jornco.controller.scan;

import com.jornco.controller.BLEState;

/**
 * 连接状态变化
 * Created by kkopite on 2017/10/26.
 */

public interface OnBLEDeviceStatusChangeListener {

    /**
     * 状态变化接口
     * @param address 地址
     * @param state   状态(连接/连接中/断开)
     */
    void bleDeviceStateChange(String address, BLEState state);
}
