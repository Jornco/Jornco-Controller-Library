package com.jornco.controller.ble;

import com.jornco.controller.error.BLEWriterError;

/**
 * Created by kkopite on 2017/11/29.
 */

public interface IronbotWriterCallback {
    /**
     * 发送成功
     * @param address 地址
     */
    void writerSuccess(String address);

    /**
     * 发送失败
     * @param address 发送成功的地址
     * @param data    发送失败的数据
     * @param error   详情
     */
    void writerFailure(String address, String data, BLEWriterError error);
}
