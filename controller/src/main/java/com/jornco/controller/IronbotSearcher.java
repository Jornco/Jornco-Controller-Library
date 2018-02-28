package com.jornco.controller;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.jornco.controller.ble.IronbotInfo;
import com.jornco.controller.ble.IronbotRule;
import com.jornco.controller.scan.IronbotFilter;
import com.jornco.controller.scan.IronbotSearcherCallback;
import com.jornco.controller.scan.OnBLEDeviceStatusChangeListener;

import java.util.List;

/**
 * 扫描蓝牙， 发现已连接蓝牙
 * 无需单例
 * Created by kkopite on 2017/10/25.
 */

public class IronbotSearcher{

    private BLEScan mBLEScan = new BLEScan();

    // 过滤蓝牙扫描的规则
    private IronbotFilter mFilter = new IronbotFilter() {
        @Override
        public boolean filter(BluetoothDevice info) {
            String name = info.getName();
            return name != null && (name.equals("RS-BLE") || name.equals("PS-BLE") || name.startsWith("TAv") || name.startsWith("CC"));
        }
    };

    public void setRule(IronbotRule rule) {
        BLEPool.getInstance().setRule(rule);
    }

    /**
     * 扫描设备
     * @param callback 扫描设备的回调
     */
    public void searchIronbot(IronbotSearcherCallback callback) {
        mBLEScan.searchIronbot(callback, mFilter);
    }

    /**
     * 停止扫描
     */
    public void stopScan(){
        mBLEScan.stopScan();
    }

    /**
     * 获取当前已连接的蓝牙设备
     * @return List<IronbotInfo>
     */
    public List<IronbotInfo> getConnectedIronbot(){
        return BLEPool.getInstance().getConnectedIronbot();
    }

    /**
     * 蓝牙是否可用
     * @return true if bluetooth is enabled
     */
    public boolean isEnable(){
        return mBLEScan.isEnable();
    }

    /**
     * 打开蓝牙
     */
    public void enable() {
        mBLEScan.enable();
    }

    /**
     * 连接设备
     * @param context 上下文
     * @param address 要连接设备的地址
     */
    public void connect(Context context, String address) {
        BLEPool.getInstance().connect(context, address);
    }

    /**
     * 断开连接
     * @param address 要断开连接的设备的地址
     */
    public void disConnect(String address) {
        BLEPool.getInstance().disConnect(address);
    }

    /**
     * 添加蓝牙设备连接断开状态的监听
     * @param listener 监听器
     */
    public void addOnBLEDeviceStatusChangeListener(OnBLEDeviceStatusChangeListener listener) {
        BLEPool.getInstance().addOnBLEDeviceStatusChangeListener(listener);
    }

    /**
     * 取消蓝牙设备连接断开状态的监听
     * @param listener 监听器
     */
    public void removeOnBLEDeviceStatusChangeListener(OnBLEDeviceStatusChangeListener listener) {
        BLEPool.getInstance().removeOnBLEDeviceStatusChangeListener(listener);
    }
}
