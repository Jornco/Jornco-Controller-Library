package com.jornco.controller;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;

import com.jornco.controller.ble.BLEState;
import com.jornco.controller.ble.IronbotRule;
import com.jornco.controller.ble.IronbotWriterCallback;
import com.jornco.controller.ble.OnBLEDeviceChangeListener;
import com.jornco.controller.receiver.BaseBLEResolver;
import com.jornco.controller.receiver.TuckBLEResolver;
import com.jornco.controller.util.BLELog;
import com.jornco.controller.util.Helper;

import java.util.Arrays;
import java.util.List;

/**
 * 代表一个蓝牙设备
 * Created by kkopite on 2017/10/25.
 */

class BLE extends BluetoothGattCallback {

    // 地址
    private final String address;

    // 名称
    private final String name;

    // 蓝牙设备
    private BluetoothDevice device;

    // 连接状态
    private BLEState mState;

    // 服务过滤
    private final IronbotRule mRule;

    private BluetoothGatt mGatt;

    private BluetoothGattCharacteristic mReadBGC;

    // 设备状态变化回调
    private OnBLEDeviceChangeListener mDeviceStateChangeListener;

    // 连接时读写处理
    private IWriterStrategy mConnectedStrategy;
    // 断开时读写处理
    private IWriterStrategy mDisconnectedStrategy;
    // 当前的读写处理
    private IWriterStrategy mCurrentStrategy;

    // 处理蓝牙返回的值, 拼接得到一个完整的值, 再进行处理
    private BaseBLEResolver mBaseBLEResolver = new TuckBLEResolver();

    BLE(String address, String name, BluetoothDevice device, IronbotRule rule) {
        this.address = address;
        this.name = name;
        this.device = device;
        mRule = rule;
        mConnectedStrategy = new ConnectedWriterStrategy(address);
        mDisconnectedStrategy = new DisconnectedWriterStrategy(address);
        mCurrentStrategy = mDisconnectedStrategy;
    }

    public BLEState getState() {
        return mState;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    /**
     * 连接
     * @param context   上下文
     * @param bleDeviceStateChangeListener 回调
     */
    void connect(Context context, OnBLEDeviceChangeListener bleDeviceStateChangeListener) {
        this.mDeviceStateChangeListener = bleDeviceStateChangeListener;
        if (device == null) {
            changeState(BLEState.DISCONNECT);
            return;
        }
        changeState(BLEState.CONNECTING);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mGatt = device.connectGatt(context, false, this, BluetoothDevice.TRANSPORT_LE);
        } else {
            mGatt = device.connectGatt(context, false, this);
        }
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        BLELog.log("之前状态: " + status + "连接状态: " + newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            BLELog.log(address + "连接成功");
            boolean res = gatt.discoverServices();
            BLELog.log("发现服务" + res);
            changeState(BLEState.CONNECTED);
            return;
        } else {
            BLELog.log(address + "连接断开");
            changeState(BLEState.DISCONNECT);
            mGatt.close();
            destroy();
        }
        switchWriterToDisconnect();
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            BLELog.log("写入成功");
            mCurrentStrategy.writeSuccess();
        } else {
            BLELog.log("错误代码: " + status);
            mCurrentStrategy.writeFailure();
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        // todo: 在这里做接受的传感器返回信息拼接, 因为可能是分段传回来的
        byte[] bytes = mReadBGC.getValue();
//        String msg = new String(bytes);
//        BLELog.log("收到设备传来的: " + msg);
        // 这里应该直接去处理字节数组了, 不能变成字符串再去处理
        byte[] tmp;
        synchronized (this) {
            if (mBaseBLEResolver.resolve(bytes)) {
                tmp = mBaseBLEResolver.getData();
                mBaseBLEResolver.clear();
            } else {
                return;
            }
        }
        mDeviceStateChangeListener.bleDeviceReceive(address, tmp);
        BLELog.log("接受到的信息拼接完成后: " + Arrays.toString(tmp));
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            List<BluetoothGattService> bgss = mGatt.getServices();
            for (BluetoothGattService bgs : bgss) {
                BLELog.log("find BluetoothGattService : " + bgs.getUuid().toString());
                List<BluetoothGattCharacteristic> bgcs = bgs.getCharacteristics();
                for (BluetoothGattCharacteristic bgc : bgcs) {
                    String uuid = bgc.getUuid().toString();
                    BLELog.log("find BluetoothGattCharacteristic : " + uuid + ", " + Helper.decodeProperties(bgc).toString());
                    if (mRule.isRead(uuid)) {
                        BLELog.log("getRead BluetoothGattCharacteristic : " + uuid);
                        mReadBGC = bgc;
                        boolean b = mGatt.setCharacteristicNotification(mReadBGC, true);
                        BLELog.log("监听: " + b);
                    }
                    if (mRule.isWrite(uuid)) {
                        BLELog.log("getWrite BluetoothGattCharacteristic : " + uuid);
                        switchWriterToConnect(mGatt, bgc);
                    }
                }
            }
        }
    }
    /**
     * 切换至连接的读写状态
     * @param gatt gatt
     * @param bgc  可读的一个属性
     */
    private void switchWriterToConnect(BluetoothGatt gatt, BluetoothGattCharacteristic bgc){
        synchronized (this){
            mCurrentStrategy.stop();
            boolean b = gatt.setCharacteristicNotification(bgc, true);
            BLELog.log("监听: " + b);
            mCurrentStrategy = mConnectedStrategy;
            mCurrentStrategy.start(gatt, bgc);
        }
    }

    /**
     * 切换至断开的读写状态
     */
    private void switchWriterToDisconnect(){
        synchronized (this){
            mCurrentStrategy.stop();
            mCurrentStrategy = mDisconnectedStrategy;
            mCurrentStrategy.start(null, null);
        }
    }

    /**
     * 对设备进行读写
     * @param data      指令
     * @param callback  回调
     */
    void writeData(byte[] data, IronbotWriterCallback callback) {
        mCurrentStrategy.write(data, callback);
    }

    /**
     * 断开连接
     */
    void disconnect() {
        if (mGatt == null) {
            return;
        }
        mGatt.disconnect();
    }

    /**
     * 销毁
     */
    private void destroy() {
        mReadBGC = null;
        mGatt = null;
        mDeviceStateChangeListener = null;
    }

    /**
     * 连接状态的改变
     * @param state state
     */
    private void changeState(BLEState state) {
        mState = state;
        mDeviceStateChangeListener.bleDeviceStateChange(address, state);
    }

}


