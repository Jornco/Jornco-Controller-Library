package com.jornco.controller;

import android.content.Context;

import com.jornco.controller.error.BLEWriterError;
import com.jornco.controller.scan.OnBLEDeviceStateChangeListener;

import java.util.HashMap;
import java.util.Map;

/**
 * 保存连接设备的类
 * Created by kkopite on 2017/10/25.
 */

class BLEPool implements OnBLEDeviceStateChangeListener {

    private static class Holder {
        private static BLEPool INSTANCE = new BLEPool();
    }

    private BLEPool(){}

    static BLEPool getInstance() {
        return Holder.INSTANCE;
    }

    // 已连接设备
    private Map<String, BLE> connectedBLE = new HashMap<>();

    // 上层扫描组件需要的设备状态回调
    private OnBLEDeviceStateChangeListener mListener;

    /**
     * 返回连接设备的map
     * @return  连接设备的map
     */
    Map<String, BLE> getConnectedBLE() {
        return connectedBLE;
    }

    /**
     * 连接
     * @param context
     * @param info
     * @param listener
     */
    void connect(Context context, BLE info, OnBLEDeviceStateChangeListener listener) {
        mListener = listener;
        connectedBLE.put(info.getAddress(), info);
        info.connect(context, this);
    }

    void disConnect(String info){
        BLE device = connectedBLE.remove(info);
        if (device == null) {
            return;
        }
        device.disconnect();
    }

    void sendMsg(String address, String cmd, IronbotWriterCallback callback) {
        BLE device = connectedBLE.get(address);
        if (device == null) {
            callback.writerFailure(address, cmd, new BLEWriterError(address, cmd, "该设备未连接"));
            return;
        }
        device.writeData(cmd, callback);
    }

    @Override
    public void bleDeviceStateChange(String address, BLEState state) {
        mListener.bleDeviceStateChange(address, state);
    }

}
