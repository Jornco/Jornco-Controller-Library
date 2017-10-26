package com.jornco.controller;

import com.jornco.controller.error.BLEWriterError;

/**
 * 同步多个设备的发送
 * Created by kkopite on 2017/10/25.
 */

class MultiIronbotWriterCallback implements IronbotWriterCallback {

    private final OnIronbotWriteCallback callback;
    private final OnSendListener sendListener;
    // 总共要发送设备的数量
    private final int total;
    // 发送成功的设备
    private int success;
    // 剩余设备(发送未响应来的)
    private int num;

    MultiIronbotWriterCallback(OnIronbotWriteCallback callback, OnSendListener sendListener, int num) {
        this.callback = callback;
        this.sendListener = sendListener;
        this.total = num;
        this.num = num;
    }

    @Override
    public void writerSuccess(String address) {
        synchronized (this) {
            num = num - 1;
            success++;
            sendListener.onUnSendDevices(total, num);
            if (num == 0) {
                if (callback != null) {
                    callback.onWriterSuccess(address);
                    callback.onWriterEnd();
                }
                sendListener.onEndSend();
            }
        }
    }

    @Override
    public void writerFailure(String address, String data, BLEWriterError error) {
        if (callback != null) {
            callback.onWriterFailure(address, error);
        }
        synchronized (this) {
            num = num - 1;
            sendListener.onUnSendDevices(total, num);
            if (num == 0) {
                if (callback != null) {
                    if (success == 0) {
                        // 没有一个成功的发送
                        callback.onAllDeviceFailure();
                    }
                    callback.onWriterEnd();
                }
                sendListener.onEndSend();
            }

        }
    }

    /**
     * 同步发送时各种回调
     */
    interface OnSendListener {

        /**
         * 给所有设备都发送了
         */
        void onEndSend();

        /**
         *
         * @param total 总共要发送的设备
         * @param device 还剩几个设备未发送
         */
        void onUnSendDevices(int total, int device);
    }
}


