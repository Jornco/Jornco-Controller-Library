package com.jornco.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.jornco.controller.ble.BLEState;
import com.jornco.controller.ble.DataType;
import com.jornco.controller.ble.IronbotInfo;
import com.jornco.controller.ble.IronbotRule;
import com.jornco.controller.ble.IronbotWriterCallback;
import com.jornco.controller.ble.OnBLEDeviceChangeListener;
import com.jornco.controller.ble.OnIronbotWriteCallback;
import com.jornco.controller.ble.SensorBean;
import com.jornco.controller.ble.SensorType;
import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.error.BLEWriterError;
import com.jornco.controller.receiver.BLEMessage;
import com.jornco.controller.receiver.BLEMessageFactory;
import com.jornco.controller.receiver.BLEReceiver;
import com.jornco.controller.receiver.IBLEMessageFactory;
import com.jornco.controller.scan.OnBLEDeviceStatusChangeListener;
import com.jornco.controller.util.BLELog;

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

class BLEPool implements OnBLEDeviceChangeListener, MultiIronbotWriterCallback.OnSendListener{

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

    // 端口对应传感器
    private SparseArray<SensorType> mSensorTypeMap = new SparseArray<>();

    // 输出传感器对应端口
    private Map<SensorType, Integer> mSendSensorTypeIntegerMap = new HashMap<>();

    // 接受消息处理回调
    private CopyOnWriteArraySet<BLEReceiver> mReceivers = new CopyOnWriteArraySet<>();

    private IBLEMessageFactory mFactory = new BLEMessageFactory();

    void setRule(IronbotRule rule) {
        this.mRule = rule;
    }

    /**
     * 返回连接设备的map
     * @return  连接设备的map
     */
    private Map<String, BLE> getConnectedBLE() {
        return mConnectedBLE;
    }

    /**
     * 连接
     * @param context   上下文
     * @param address   地址
     * @param name 设备名称
     */
    void connect(Context context, String address, String name) {
        BluetoothDevice device = mAdapter.getRemoteDevice(address);
        String tmpName = device.getName();
        if (TextUtils.isEmpty(tmpName)) {
            tmpName = name;
        }
        BLE info = new BLE(address, tmpName, device, mRule);
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

    Map<SensorType, Integer> getSendSensorTypeIntegerMap() {
        return mSendSensorTypeIntegerMap;
    }

    /**
     * 发送消息, 需要上层做同步
     * @param address   地址
     * @param cmd       指令
     * @param callback  回调
     */
    void sendMsg(String address, byte[] cmd, IronbotWriterCallback callback) {
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
    void registerBLEReceiver(BLEReceiver receiver) {
        mReceivers.add(receiver);
    }

    /**
     * 取消订阅
     * @param receiver 接收器
     */
    void unRegisterBLEReceiver(BLEReceiver receiver) {
        mReceivers.remove(receiver);
    }

    @Override
    public void bleDeviceReceive(String address, String msg) {
//        BLEMessage message = new BLEMessage(address, msg);
        BLEMessage message = mFactory.createBLEMessage(address, msg, mSensorTypeMap);
        // 在这里组合出接收到的信息, 发出去
        for (BLEReceiver mReceiver : mReceivers) {
            if (mReceiver.onReceiveBLEMessage(message)) {
                mReceiver.handBLEMessage(message);
            }
        }
    }

    void initSensor(List<SensorBean> list) {
        if (list == null) {
            return;
        }
        SparseArray<SensorBean> sparseArray = new SparseArray<>();
        for (SensorBean sensorBean : list) {
            sparseArray.put(sensorBean.getPort(), sensorBean);
        }

        // 清空之前的
        mSendSensorTypeIntegerMap.clear();
        mSensorTypeMap.clear();
        StringBuilder sb = new StringBuilder();
        sb.append("#E");
        // 设置好对应端口对应的传感器
        for (int i = 0; i < 6; i++) {
            SensorBean bean = sparseArray.get(i);
            String typeId = "0";
            if (bean != null) {
                SensorType type = bean.getType();
                int port = bean.getPort();
                typeId = type.getTypeId();

                // 记录那个端口对应哪个传感器
                if (type.getDataType() == DataType.REC) {
                    mSensorTypeMap.put(port, type);
                } else if (type.getDataType() == DataType.SEND){
                    mSendSensorTypeIntegerMap.put(type, port);
                }
            }
            sb.append(typeId).append(",");
        }
        sb.append("*");
        BLELog.log("初始化传感器: " + sb.toString());
        // 发送
        sendMsg(IronbotCode.create(sb.toString()), null);
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
        List<byte[]> codes = code.getCodes();
        int size = address.length;
        if (size == 0) {
            if (callback != null) {
                callback.onWriterFailure("", new BLEWriterError("", BLEConstants.EMPTY_DATA, "没有要发送的设备地址或者没有连接的设备"));
                callback.onAllDeviceFailure();
                callback.onWriterEnd();
            }
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
        private byte[] data;

        // 要发的地址
        private String[] address;

        // 完成的回调
        private IronbotWriterCallback callback;

    }

    // 蓝牙适配器
    private BluetoothAdapter mAdapter;

    // 蓝牙设备连接状态回调
    private CopyOnWriteArraySet<OnBLEDeviceStatusChangeListener> mDeviceStatusChangeListeners = new CopyOnWriteArraySet<>();

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
    public void bleDeviceStateChange(String address, BLEState state) {
        for (OnBLEDeviceStatusChangeListener mDeviceStatusChangeListener : mDeviceStatusChangeListeners) {
            mDeviceStatusChangeListener.bleDeviceStateChange(address, state);
        }
    }
}
