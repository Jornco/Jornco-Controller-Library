package com.jornco.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.jornco.controller.ble.IronbotInfo;
import com.jornco.controller.scan.IronbotFilter;
import com.jornco.controller.scan.IronbotSearcherCallback;
import com.jornco.controller.util.BLELog;

/**
 * Created by kkopite on 2017/12/21.
 */

class BLEScan implements BluetoothAdapter.LeScanCallback{

    // 蓝牙适配器
    private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

    // 扫描蓝牙回调
    private IronbotSearcherCallback mCallback;
    private IronbotFilter mFilter;

    /**
     * 扫描设备
     * @param callback 扫描设备的回调
     */
    void searchIronbot(IronbotSearcherCallback callback, IronbotFilter filter) {
        if (mAdapter == null || !mAdapter.isEnabled()) {
            BLELog.log("蓝牙不可用");
            return;
        }
        mFilter = filter;
        this.mCallback = callback;
        mAdapter.startLeScan(this);
    }


    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (mFilter.filter(device)) {
            String address = device.getAddress();
            String name = device.getName();
            if (mCallback != null) {
                mCallback.onIronbotFound(new IronbotInfo(name, address));
            }
        }
    }

    /**
     * 蓝牙是否可用
     * @return true if bluetooth is enabled
     */
    boolean isEnable(){
        return mAdapter != null && mAdapter.isEnabled();
    }

    /**
     * 打开蓝牙
     */
    void enable() {
        if (mAdapter != null) {
            mAdapter.enable();
        }
    }

    /**
     * 停止扫描
     */
    void stopScan(){
        mAdapter.stopLeScan(this);
        mCallback = null;
    }
}
