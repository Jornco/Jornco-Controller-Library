package com.jornco.controller;

import android.os.Binder;

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
}
