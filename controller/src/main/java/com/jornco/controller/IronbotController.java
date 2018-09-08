package com.jornco.controller;

import com.jornco.controller.ble.OnIronbotWriteCallback;
import com.jornco.controller.ble.SensorBean;
import com.jornco.controller.ble.SensorType;
import com.jornco.controller.code.IronbotCode;

import java.util.List;
import java.util.Map;

/**
 * 发送控制
 * Created by kkopite on 2017/10/25.
 */

public class IronbotController{

    public Map<SensorType, Integer> getSendSensorTypeIntegerMap() {
        return BLEPool.getInstance().getSendSensorTypeIntegerMap();
    }

    public void initSensor(List<SensorBean> list) {
        BLEPool.getInstance().initSensor(list);
    }
    /**
     * 给所有当前连接的设备发指令
     * @param code      指令
     * @param callback  回调
     */
    public void sendMsg(IronbotCode code, OnIronbotWriteCallback callback) {
        BLEPool.getInstance().sendMsg(code, callback);
    }

    /**
     * 该指定设备发送指令
     * @param address   地址
     * @param code      指令
     * @param callback  回调
     */
    public void sendMsg(String address, IronbotCode code, OnIronbotWriteCallback callback) {
        BLEPool.getInstance().sendMsg(address, code, callback);
    }

    /**
     * 给指定的设备发送指令
     * @param address   地址
     * @param code      指令
     * @param callback  回调
     */
    public void sendMsg(String[] address, final IronbotCode code, final OnIronbotWriteCallback callback) {
        BLEPool.getInstance().sendMsg(address, code, callback);
    }

}
