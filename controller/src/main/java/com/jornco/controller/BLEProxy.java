package com.jornco.controller;

import com.jornco.controller.ble.OnIronbotWriteCallback;
import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.receiver.BLEReceiver;

/**
 * Created by kkopite on 2017/11/29.
 */

class BLEProxy{

    private IronbotController controller = new IronbotController();

    void registerBLEReceiver(BLEReceiver receiver){
        BLEPool.getInstance().registerBLEReceiver(receiver);
    }

    void unRegisterBLEReceiver(BLEReceiver receiver){
        BLEPool.getInstance().unRegisterBLEReceiver(receiver);
    }

    /**
     * 给所有当前连接的设备发指令
     * @param code      指令
     * @param callback  回调
     */
    public void sendMsg(IronbotCode code, OnIronbotWriteCallback callback) {
        controller.sendMsg(code, callback);
    }

    /**
     * 该指定设备发送指令
     * @param address   地址
     * @param code      指令
     * @param callback  回调
     */
    public void sendMsg(String address, IronbotCode code, OnIronbotWriteCallback callback) {
        controller.sendMsg(address, code, callback);
    }

    /**
     * 给指定的设备发送指令
     * @param address   地址
     * @param code      指令
     * @param callback  回调
     */
    public void sendMsg(String[] address, final IronbotCode code, final OnIronbotWriteCallback callback) {
        controller.sendMsg(address, code, callback);
    }

}
