package com.jornco.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.error.BLEWriterError;
import com.jornco.controller.receiver.BLEMessage;
import com.jornco.controller.receiver.BLEReceiver;
import com.jornco.controller.scan.IronbotFilter;
import com.jornco.controller.scan.IronbotSearcherCallback;
import com.jornco.controller.scan.OnBLEDeviceStatusChangeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 保存连接设备的类
 * Created by kkopite on 2017/10/25.
 */

class BLEPool implements OnBLEDeviceChangeListener, MultiIronbotWriterCallback.OnSendListener, BluetoothAdapter.LeScanCallback{

    private IronbotFilter mFilter;

    private static class Holder {
        private static BLEPool INSTANCE = new BLEPool();
    }

    private BLEPool(){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    static BLEPool getInstance() {
        return Holder.INSTANCE;
    }

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

    // 已连接设备
    private Map<String, BLE> mConnectedBLE = new HashMap<>();

    // 接受消息处理回调
    private CopyOnWriteArraySet<BLEReceiver> mReceivers = new CopyOnWriteArraySet<>();

    public void setRule(IronbotRule rule) {
        this.mRule = rule;
    }

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
     * @param address   地址
     */
    void connect(Context context, String address) {
        BluetoothDevice device = mAdapter.getRemoteDevice(address);
        BLE info = new BLE(address, device.getName(), device, mRule);
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
            for (OnBLEDeviceStatusChangeListener mDeviceStatusChangeListener : mDeviceStatusChangeListeners) {
                mDeviceStatusChangeListener.bleDeviceStateChange(info, BLEState.DISCONNECT);
            }
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

    /**
     * 注册接受器
     * @param receiver 接收器
     */
    void registerReceiver(BLEReceiver receiver) {
        mReceivers.add(receiver);
    }

    /**
     * 取消订阅
     * @param receiver 接收器
     */
    void unRegisterReceiver(BLEReceiver receiver) {
        mReceivers.remove(receiver);
    }

    @Override
    public void bleDeviceReceive(String address, String msg) {
        BLEMessage message = new BLEMessage(address, msg);
        // 在这里组合出接收到的信息, 发出去
        for (BLEReceiver mReceiver : mReceivers) {
            if (mReceiver.onReceiveMessage(message)) {
                mReceiver.handMessage(message);
            }
        }
    }

    /**
     * 当前是否在发送
     */
    private volatile boolean isWriting = false;

    /**
     * 待发送的消息队列
     */
    private ConcurrentLinkedQueue<WriteData> queueDatas = new ConcurrentLinkedQueue<>();

    /**
     * 给所有当前连接的设备发指令
     * @param code      指令
     * @param callback  回调
     */
    public void sendMsg(IronbotCode code, OnIronbotWriteCallback callback) {
        Set<String> address = BLEPool.getInstance().getConnectedBLE().keySet();
        String[] strings = address.toArray(new String[address.size()]);
        sendMsg(strings, code, callback);
    }

    /**
     * 该指定设备发送指令
     * @param address   地址
     * @param code      指令
     * @param callback  回调
     */
    public void sendMsg(String address, IronbotCode code, OnIronbotWriteCallback callback) {
        sendMsg(new String[]{address}, code, callback);
    }

    /**
     * 给指定的设备发送指令
     * @param address   地址
     * @param code      指令
     * @param callback  回调
     */
    public void sendMsg(String[] address, final IronbotCode code, final OnIronbotWriteCallback callback) {
        List<String> codes = code.getCodes();
        int size = address.length;
        if (size == 0 && callback != null) {
            callback.onWriterFailure("", new BLEWriterError("", "", "没有要发送的设备地址或者没有连接的设备"));
            callback.onAllDeviceFailure();
            callback.onWriterEnd();
            return;
        }
        // 确保一条完整的指令
        synchronized (this) {
            for (int i = 0, length = codes.size(); i < length; i++) {
                WriteData data = new WriteData();
                data.address = address;
                data.data = codes.get(i);
                if (i == length - 1) {
                    // 分割的最后一条指令发完才算这条指令发送完毕, 才会去执行用户传进来的回调
                    data.callback = new MultiIronbotWriterCallback(callback, this, size);
                } else {
                    data.callback = new MultiIronbotWriterCallback(null, this, size);
                }
                queueDatas.add(data);
            }
        }
        next();
    }

    @Override
    public void onEndSend() {
        isWriting = false;
        next();
    }

    @Override
    public void onUnSendDevices(int total, int device) {

    }

    /**
     * 尝试从发送队列拉出一个指令来发送
     */
    private void next() {
        synchronized (this) {
            if (isWriting) {
                BLELog.log("当前有任务还在执行, 队列中还有" + queueDatas.size() + "个任务");
                return;
            }
            isWriting = true;
        }
        WriteData data = queueDatas.poll();
        if (data != null) {
            String[] address = data.address;
            for (String a : address) {
                sendMsg(a, data.data, data.callback);
            }
        } else {
            isWriting = false;
            if (!queueDatas.isEmpty()) {
                next();
            }
        }
    }

    /**
     * 携带传输数据的类
     */
    private static class WriteData {

        // 要发的数据
        private String data;

        // 要发的地址
        private String[] address;

        // 完成的回调
        private IronbotWriterCallback callback;

    }

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
    public void searchIronbot(IronbotSearcherCallback callback, IronbotFilter filter) {
        if (mAdapter == null || !mAdapter.isEnabled()) {
            BLELog.log("蓝牙不可用");
            return;
        }
        mFilter = filter;
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
        List<IronbotInfo> infos = new ArrayList<>();
        for (BLE info : mConnectedBLE.values()) {
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
