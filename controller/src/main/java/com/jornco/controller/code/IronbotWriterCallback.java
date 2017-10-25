package com.jornco.controller.code;

import com.jornco.controller.error.BLEWriterError;

/**
 * 写入蓝牙设备成功/失败回调
 * Created by kkopite on 2017/10/25.
 */

public interface IronbotWriterCallback {

    /**
     * 发送成功
     */
    void writerSuccess();

    /**
     * 发送失败
     * @param address 发送成功的地址
     * @param data    发送失败的数据
     * @param error   详情
     */
    void writerFailure(String address, String data, BLEWriterError error);

}
