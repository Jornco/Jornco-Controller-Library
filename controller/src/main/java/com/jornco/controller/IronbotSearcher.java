package com.jornco.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.jornco.controller.scan.IronbotFilter;
import com.jornco.controller.scan.IronbotSearcherCallback;
import com.jornco.controller.scan.OnBLEDeviceStatusChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 扫描蓝牙， 发现已连接蓝牙
 * 无需单例
 * Created by kkopite on 2017/10/25.
 */

public class IronbotSearcher implements BluetoothAdapter.LeScanCallback, OnBLEDeviceStatusChangeListener {

    private static class Holder {
        private static IronbotSearcher INSTANCE = new IronbotSearcher();
    }

    private IronbotSearcher() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static IronbotSearcher getInstance() {
        return Holder.INSTANCE;
    }

    // 过滤蓝牙扫描的规则
    private IronbotFilter mFilter = new IronbotFilter() {
        @Override
        public boolean filter(BluetoothDevice info) {
            String name = info.getName();
            return name != null && (name.equals("RS-BLE") || name.equals("PS-BLE") || name.startsWith("Tav"));
        }
    };

    private IronbotRule mRule = new IronbotRule() {

        final static String VERSION1_UUID_WRITER = "0000ffe9";
        final static String VERSION1_UUID_READ = "0000ffe1";

        final static String VERSION2_UUID_WRITER = "0000fff1";
        final static String VERSION2_UUID_READ = "0000fff4";

        @Override
        public boolean isRead(String uuid) {
            return uuid.startsWith(VERSION1_UUID_READ)
                    || uuid.startsWith(VERSION2_UUID_READ);
        }

        @Override
        public boolean isWrite(String uuid) {
            return uuid.startsWith(VERSION1_UUID_WRITER)
                    || uuid.startsWith(VERSION2_UUID_WRITER);
        }
    };

    // 蓝牙适配器
    private BluetoothAdapter mAdapter;

    // 扫描蓝牙回调
    private IronbotSearcherCallback mCallback;

    // 蓝牙设备连接状态回调
    private CopyOnWriteArraySet<OnBLEDeviceStatusChangeListener> mDeviceStatusChangeListeners = new CopyOnWriteArraySet<>();

    /**
     * 扫描设备
     * @param callback 扫描设备的回调
     */
    public void searchIronbot(IronbotSearcherCallback callback) {
        if (mAdapter == null || !mAdapter.isEnabled()) {
            BLELog.log("蓝牙不可用");
            return;
        }
        this.mCallback = callback;
        mAdapter.startLeScan(this);
    }

    /**
     * 停止扫描
     */
    public void stopScan(){
        mAdapter.stopLeScan(this);
        mCallback = null;
    }

    /**
     * 获取当前已连接的蓝牙设备
     * @return List<IronbotInfo>
     */
    public List<IronbotInfo> getConnectedIronbot(){
        Map<String, BLE> ble = BLEPool.getInstance().getConnectedBLE();
        List<IronbotInfo> infos = new ArrayList<>();
        for (BLE info : ble.values()) {
            String name = info.getName();
            String address = info.getAddress();
            infos.add(new IronbotInfo(name, address, BLEState.CONNECTED));
        }
        return infos;
    }

    /**
     * 蓝牙是否可用
     * @return true if bluetooth is enabled
     */
    public boolean isEnable(){
        return mAdapter != null && mAdapter.isEnabled();
    }

    /**
     * 打开蓝牙
     */
    public void enable() {
        if (mAdapter != null) {
            mAdapter.enable();
        }
    }

    /**
     * 连接设备
     * @param context 上下文
     * @param address 要连接设备的地址
     */
    public void connect(Context context, String address) {
        BluetoothDevice device = mAdapter.getRemoteDevice(address);
        BLE info = new BLE(address, device.getName(), device, mRule);
        BLEPool.getInstance().connect(context, info, this);
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
        mDeviceStatusChangeListeners.add(listener);
    }

    /**
     * 取消蓝牙设备连接断开状态的监听
     * @param listener 监听器
     */
    public void removeOnBLEDeviceStatusChangeListener(OnBLEDeviceStatusChangeListener listener) {
        mDeviceStatusChangeListeners.remove(listener);
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (mFilter.filter(device)) {
            String address = device.getAddress();
            String name = device.getName();
            mCallback.onIronbotFound(new IronbotInfo(name, address));
        }
    }

    @Override
    public void bleDeviceStateChange(String address, BLEState state) {
        for (OnBLEDeviceStatusChangeListener mDeviceStatusChangeListener : mDeviceStatusChangeListeners) {
            mDeviceStatusChangeListener.bleDeviceStateChange(address, state);
        }
    }
}
