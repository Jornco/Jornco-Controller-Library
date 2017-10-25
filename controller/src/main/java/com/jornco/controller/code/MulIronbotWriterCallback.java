package com.jornco.controller.code;

import com.jornco.controller.IronbotController;
import com.jornco.controller.error.BLEWriterError;

/**
 * 同步多个设备的发送
 * Created by kkopite on 2017/10/25.
 */

public class MulIronbotWriterCallback implements IronbotWriterCallback {

    private final IronbotController.OnIronbotWriterCallback callback;
    private final OnSendListener sendListener;
    private final int total;
    // 发送成功的设备
    private int success;
    // 剩余设备(发送未响应来的)
    private int num;

    public MulIronbotWriterCallback(IronbotController.OnIronbotWriterCallback callback, OnSendListener sendListener, int num) {
        this.callback = callback;
        this.sendListener = sendListener;
        this.total = num;
        this.num = num;
    }

    @Override
    public void writerSuccess() {
        synchronized (this) {
            num = num - 1;
            success++;
            sendListener.onUnSendDevices(total, num);
            if (num == 0) {
                if (callback != null) {
                    callback.writerSuccess();
                }
                sendListener.onEndSend();
            }
        }
    }

    @Override
    public void writerFailure(String address, String data, BLEWriterError error) {
        if (callback != null) {
            callback.writerFailure(address, data, error);
        }
        synchronized (this) {
            num = num - 1;
            sendListener.onUnSendDevices(total, num);
            if (num == 0) {
                sendListener.onEndSend();
                if (callback == null) {
                    return;
                }
                if (success == 0) {
                    // 没有一个成功的发送
                    callback.onAllDeviceFailure();
                } else {
                    // 至少有一个发送成功了
                    callback.writerSuccess();
                }
            }

        }
    }

    /**
     * 同步发送时各种回调
     */
    public interface OnSendListener {

        // 所有设备发送完毕
        void onEndSend();

        /**
         *
         * @param total 总共要发送的设备
         * @param device 还剩几个设备未发送
         */
        void onUnSendDevices(int total, int device);

    }
}


