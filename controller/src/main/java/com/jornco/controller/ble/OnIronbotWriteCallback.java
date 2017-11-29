package com.jornco.controller.ble;

import com.jornco.controller.error.BLEWriterError;

/**
 *
 * Created by kkopite on 2017/10/26.
 */

public interface OnIronbotWriteCallback {

    /**
     * 发送成功的回调
     * @param address 发送成功的地址
     */
    void onWriterSuccess(String address);

    /**
     * 发送失败的回调
     * @param address 发送失败的地址
     * @param error   错误
     */
    void onWriterFailure(String address, BLEWriterError error);

    /**
     * 该指令对当前所有地址发送都失败
     */
    void onAllDeviceFailure();

    /**
     * 对所有地址发送完毕, 至少有一个是发送成功的
     */
    void onWriterEnd();

}
