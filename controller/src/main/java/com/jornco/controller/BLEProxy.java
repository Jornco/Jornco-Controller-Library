package com.jornco.controller;

import com.jornco.controller.receiver.BLEReceiver;

/**
 * Created by kkopite on 2017/11/29.
 */

class BLEProxy{

    void registerBLEReceiver(BLEReceiver receiver){
        BLEPool.getInstance().registerBLEReceiver(receiver);
    }

    void unRegisterBLEReceiver(BLEReceiver receiver){
        BLEPool.getInstance().unRegisterBLEReceiver(receiver);
    }

}
