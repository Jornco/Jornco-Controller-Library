package com.jornco.controller;

import android.content.Context;

import com.jornco.controller.error.BLEWriterError;
import com.jornco.controller.receiver.BLEMessage;
import com.jornco.controller.receiver.BLEReceiver;
import com.jornco.controller.scan.OnBLEDeviceStatusChangeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 保存连接设备的类
 * Created by kkopite on 2017/10/25.
 */

class BLEPool implements OnBLEDeviceChangeListener {

    private static class Holder {
        private static BLEPool INSTANCE = new BLEPool();
    }

    private BLEPool(){}

    static BLEPool getInstance() {
        return Holder.INSTANCE;
    }

    // 已连接设备
    private Map<String, BLE> mConnectedBLE = new HashMap<>();

    // 上层扫描组件需要的设备状态回调
    private OnBLEDeviceStatusChangeListener mListener;

    // 接受消息处理回调
    private List<BLEReceiver> mReceivers = new ArrayList<>();

    /**
     * 返回连接设备的map
     * @return  连接设备的map
     */
    Map<String, BLE> getConnectedBLE() {
        return mConnectedBLE;
    }

    /**
     * 连接
     * @param context   上下文
     * @param info      一个蓝牙设备
     * @param listener  监听器
     */
    void connect(Context context, BLE info, OnBLEDeviceStatusChangeListener listener) {
        mListener = listener;
        mConnectedBLE.put(info.getAddress(), info);
        info.connect(context, this);
    }

    /**
     * 断开连接
     * @param info 蓝牙设备
     */
    void disConnect(String info){
        BLE device = mConnectedBLE.remove(info);
        if (device == null) {
            return;
        }
        device.disconnect();
    }

    /**
     * 发送消息, 需要上层做同步
     * @param address   地址
     * @param cmd       指令
     * @param callback  回调
     */
    void sendMsg(String address, String cmd, IronbotWriterCallback callback) {
        BLE device = mConnectedBLE.get(address);
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

    @Override
    public void bleDeviceReceive(String address, String msg) {
        BLEMessage message = new BLEMessage(address, msg);
        // 在这里组合出接收到的信息, 发出去
        for (BLEReceiver mReceiver : mReceivers) {
            if (mReceiver.onReceiveMessage(message)) {
                break;
            }
        }
    }
}

/**
 * 设备连接变化, 接受信息接口
 */
interface OnBLEDeviceChangeListener {

    /**
     * 连接变化
     * @param address 地址
     * @param state   连接, 断开, 连接中
     */
    void bleDeviceStateChange(String address, BLEState state);

    /**
     * 接受到信息
     * @param address 地址
     * @param msg     信息
     */
    void bleDeviceReceive(String address, String msg);
}
