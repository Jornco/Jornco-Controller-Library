package com.jornco.controller;

import com.jornco.controller.receiver.BLEMessage;
import com.jornco.controller.receiver.BLEReceiver;
import com.jornco.controller.util.BLELog;

/**
 * 状态管理类
 * Created by kkopite on 2017/10/26.
 */

public class IronbotStatus implements BLEReceiver {

    /**
     * 使用前先调用该方法
     */
    public void onStart() {
        BLEPool.getInstance().registerBLEReceiver(this);
    }

    /**
     * 使用后记得销毁
     */
    public void onDestroy() {
        BLEPool.getInstance().unRegisterBLEReceiver(this);
    }

    @Override
    public boolean onReceiveBLEMessage(BLEMessage message) {
        return true;
    }

    @Override
    public void handBLEMessage(BLEMessage message) {
        BLELog.log("收到信息: " + message.getMsg());
    }
}
