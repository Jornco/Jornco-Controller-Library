package com.jornco.controller;

import com.jornco.controller.receiver.BLEMessage;
import com.jornco.controller.receiver.BLEReceiver;

/**
 * 状态管理类
 * Created by kkopite on 2017/10/26.
 */

public class IronbotStatus implements BLEReceiver {

    /**
     * 使用前先调用该方法
     */
    public void onStart() {
        BLEPool.getInstance().registerReceiver(this);
    }

    /**
     * 使用后记得销毁
     */
    public void onDestroy() {
        BLEPool.getInstance().unRegisterReceiver(this);
    }

    @Override
    public boolean onReceiveMessage(BLEMessage message) {
        return true;
    }

    @Override
    public void handMessage(BLEMessage message) {
        BLELog.log("收到信息: " + message.getMsg());
    }
}
