package com.jornco.controller.scan;

import android.bluetooth.BluetoothDevice;

/**
 * 过滤接口
 * Created by kkopite on 2017/10/25.
 */

public interface IronbotFilter {

    /**
     *
     * @param info 扫描到的蓝牙设备
     * @return false 就过滤掉
     */
    boolean filter(BluetoothDevice info);
}
