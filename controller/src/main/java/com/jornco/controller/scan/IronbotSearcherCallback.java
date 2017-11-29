package com.jornco.controller.scan;

import com.jornco.controller.ble.IronbotInfo;

/**
 * 扫描接口
 * Created by kkopite on 2017/10/25.
 */

public interface IronbotSearcherCallback {

    /**
     * 扫描得到的设备, 需要自己去重
     * @param info 蓝牙设备
     */
    void onIronbotFound(IronbotInfo info);
}
