package com.jornco.controller;

import android.os.Binder;

import com.jornco.controller.ble.OnIronbotWriteCallback;
import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.receiver.BLEReceiver;

/**
 * Created by kkopite on 2017/11/29.
 */

public class BLEBinder extends Binder{

    private BLEProxy mProxy = new BLEProxy();

    public void registerBLEReceiver(BLEReceiver receiver){
        mProxy.registerBLEReceiver(receiver);
    }

    public void unRegisterBLEReceiver(BLEReceiver receiver){
        mProxy.unRegisterBLEReceiver(receiver);
    }

    public void sendMsg(IronbotCode code, OnIronbotWriteCallback callback) {
        mProxy.sendMsg(code, callback);
    }

    /**
     * 该指定设备发送指令
     * @param address   地址
     * @param code      指令
     * @param callback  回调
     */
    public void sendMsg(String address, IronbotCode code, OnIronbotWriteCallback callback) {
        mProxy.sendMsg(address, code, callback);
    }

    /**
     * 给指定的设备发送指令
     * @param address   地址
     * @param code      指令
     * @param callback  回调
     */
    public void sendMsg(String[] address, final IronbotCode code, final OnIronbotWriteCallback callback) {
        mProxy.sendMsg(address, code, callback);
    }
}
