package com.jornco.controller;

import com.jornco.controller.ble.IronbotWriterCallback;
import com.jornco.controller.ble.OnIronbotWriteCallback;
import com.jornco.controller.error.BLEWriterError;

/**
 * 用于同步给多个设备发送的回调
 * Created by kkopite on 2017/10/25.
 */

class MultiIronbotWriterCallback implements IronbotWriterCallback {

    // 使用者的回调
    private final OnIronbotWriteCallback callback;

    // 主要用于确定全部设备都发送完毕的回调(无论成功失败), 通知IronbotController去消息队列取出下一个发送数据
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
        if (callback != null) {
            callback.onWriterSuccess(address);
        }
        synchronized (this) {
            num = num - 1;
            success++;
            sendListener.onUnSendDevices(total, num);
            if (num == 0) {
                if (callback != null) {
                    // 指令已经发给所有设备了, 这里和sendListener.onEndSend()其实是一样的
                    // callback.onWriterEnd() 是通知用户该消息已经发给所有的设备了
                    //
                    callback.onWriterEnd();
                }
                // 通知Controller该指令发送完毕了, 用于从队列中取出下一个指令
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
         * 给所有设备都发送完毕
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


