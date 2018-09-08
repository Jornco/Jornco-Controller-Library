package com.jornco.controller.receiver;

import com.jornco.controller.IronbotStatus;

/**
 * 每一种cmd命令对应一个接收
 * Created by kkopite on 2018/9/8.
 */

public abstract class BaseTuckReceiver extends IronbotStatus {

    public abstract byte getType();

    @Override
    public boolean onReceiveBLEMessage(BLEMessage message) {
        return message.getCMDType() == getType();
    }

    @Override
    public void handBLEMessage(BLEMessage message) {
        super.handBLEMessage(message);
    }
}
