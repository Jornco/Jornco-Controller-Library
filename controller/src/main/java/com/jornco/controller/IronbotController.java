package com.jornco.controller;

import com.jornco.controller.code.IronbotWriterCallback;
import com.jornco.controller.error.BLEWriterError;

/**
 * 发送控制
 * Created by kkopite on 2017/10/25.
 */

public class IronbotController {

    private static class Holder {
        private static IronbotController INSTANCE = new IronbotController();
    }

    private IronbotController(){}

    public static IronbotController getInstance(){
        return Holder.INSTANCE;
    }

    public void sendMsg(final String address, final String cmd) {
        BLEPool.getInstance().sendMsg(address, cmd, new IronbotWriterCallback() {
            @Override
            public void writerSuccess() {
                BLELog.log("发送完美成功哟: " + address + ": " + cmd);
            }

            @Override
            public void writerFailure(BLEWriterError error) {
                BLELog.log(error.getMessage());
            }
        });
    }
}
