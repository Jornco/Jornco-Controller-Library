package com.jornco.controller.scan;

import com.jornco.controller.BLEState;

/**
 * 设备连接状态回调接口
 * Created by kkopite on 2017/10/25.
 */

public interface OnBLEDeviceStateChangeListener {

    void bleDeviceStateChange(String address, BLEState state);
}
