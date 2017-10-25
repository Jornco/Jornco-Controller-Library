package com.jornco.controller;

import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.code.IronbotWriterCallback;
import com.jornco.controller.code.MulIronbotWriterCallback;
import com.jornco.controller.error.BLEWriterError;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 发送控制
 * Created by kkopite on 2017/10/25.
 */

public class IronbotController implements MulIronbotWriterCallback.OnSendListener{

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

    public void sendMsg(IronbotCode code, OnIronbotWriterCallback callback) {
        Set<String> address = BLEPool.getInstance().getConnectedBLE().keySet();
        String[] strings = address.toArray(new String[address.size()]);
        sendMsg(strings, code, callback);
    }

    public void sendMsg(String address, IronbotCode code, OnIronbotWriterCallback callback) {
        sendMsg(new String[]{address}, code, callback);
    }

    public void sendMsg(String[] address, final IronbotCode code, final OnIronbotWriterCallback callback) {
        List<String> codes = code.getCodes();
        int size = address.length;
        if (size == 0) {
            callback.writerFailure("", "", new BLEWriterError("", "", "当前没有连接的设备"));
            return;
        }
        for (int i = 0, length = codes.size(); i < length; i++) {
            WriteData data = new WriteData();
            data.address = address;
            data.data = codes.get(i);
            if (i == length - 1) {
                // 分割的最后一条指令才会去回调上面提供的回调
                data.callback = new MulIronbotWriterCallback(callback, this, size);
            } else {
                data.callback = new MulIronbotWriterCallback(null, this, size);
            }
            queueDatas.add(data);
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


    public void sendMsg(final String address, final String cmd) {
        BLEPool.getInstance().sendMsg(address, cmd, new IronbotWriterCallback() {
            @Override
            public void writerSuccess() {
                BLELog.log("发送完美成功哟: " + address + ": " + cmd);
            }

            @Override
            public void writerFailure(String address, String data, BLEWriterError error) {
                BLELog.log(error.getMessage());
            }

        });
    }

    /**
     * 携带传输数据的类
     */
    public static class WriteData {

        // 要发的数据
        private String data;

        // 要发的地址
        private String[] address;

        // 完成的回调
        private IronbotWriterCallback callback;

    }

    public interface OnIronbotWriterCallback extends IronbotWriterCallback {

        /**
         * 所有设备发送失败
         */
        void onAllDeviceFailure();
    }
}
