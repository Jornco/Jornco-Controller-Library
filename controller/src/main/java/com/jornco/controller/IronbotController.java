package com.jornco.controller;

import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.error.BLEWriterError;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 发送控制
 * Created by kkopite on 2017/10/25.
 */

public class IronbotController implements MultiIronbotWriterCallback.OnSendListener{

    private static class Holder {
        private static IronbotController INSTANCE = new IronbotController();
    }

    private IronbotController() {
    }

    public static IronbotController getInstance() {
        return Holder.INSTANCE;
    }

    private volatile boolean isWriting = false;

    private ConcurrentLinkedQueue<WriteData> queueDatas = new ConcurrentLinkedQueue<>();

    public void sendMsg(IronbotCode code, OnIronbotWriteCallback callback) {
        Set<String> address = BLEPool.getInstance().getConnectedBLE().keySet();
        String[] strings = address.toArray(new String[address.size()]);
        sendMsg(strings, code, callback);
    }

    public void sendMsg(String address, IronbotCode code, OnIronbotWriteCallback callback) {
        sendMsg(new String[]{address}, code, callback);
    }

    public void sendMsg(String[] address, final IronbotCode code, final OnIronbotWriteCallback callback) {
        List<String> codes = code.getCodes();
        int size = address.length;
        if (size == 0 && callback != null) {
            callback.onWriterFailure("", new BLEWriterError("", "", "当前没有连接的设备"));
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
                    // 分割的最后一条指令才会去回调上面提供的回调
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

    private void sendMsg(String address, String data, final IronbotWriterCallback callback) {
        BLEPool.getInstance().sendMsg(address, data, callback);
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

}
