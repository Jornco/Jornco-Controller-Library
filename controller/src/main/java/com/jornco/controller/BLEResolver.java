package com.jornco.controller;

import com.jornco.controller.ble.OnIronbotWriteCallback;
import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.error.BLEWriterError;
import com.jornco.controller.receiver.BLEMessage;
import com.jornco.controller.util.BLELog;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 线程不安全,
 * 处理蓝牙返回信息, 比较返回信息与期望值是否一致
 * Created by kkopite on 2018/8/9.
 */

public class BLEResolver extends IronbotStatus implements OnIronbotWriteCallback {

    private String mExpect;
    private IronbotController mController = new IronbotController();
    private CountDownLatch mLatch;
    private boolean success = false;


    public void send(byte[] cmd, String expect) throws Exception {
        this.send(cmd, expect, true);
    }

    public void send(byte[] cmd) throws Exception {
        this.send(cmd, "", false);
    }

    private synchronized void send(byte[] cmd, String expect, final boolean needCompare) throws Exception {
        if (!needCompare) {
            mController.sendMsg(IronbotCode.create(cmd), null);
            return;
        }
        this.mExpect = expect;
        this.success = false;
        mController.sendMsg(IronbotCode.create(cmd), this);
        mLatch = new CountDownLatch(1);

        // 如何判断是超时, 不然可能永远不会停
        try {
            // 由于一个包2050个字节， 要分100多次蓝牙发送， 故需要挺久的
            mLatch.await(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mLatch = null;

        if (!this.success) {
            throw new Exception("蓝牙没有返回数据/返回数据与期望值不一致, mExpect = " + mExpect);
        }

    }

    @Override
    public void handBLEMessage(BLEMessage message) {
        super.handBLEMessage(message);
        BLELog.log("期望数据: " + mExpect + ", 返回数据: " + message.getMsg());
        if (mLatch != null && mExpect.equals(message.getMsg())) {
            this.success = true;
            mLatch.countDown();
        }
    }

    @Override
    public void onWriterSuccess(String address) {

    }

    @Override
    public void onWriterFailure(String address, BLEWriterError error) {

    }

    @Override
    public void onAllDeviceFailure() {

    }

    @Override
    public void onWriterEnd() {

    }

    public void stop() {
        if (mLatch != null) {
            onDestroy();
            mLatch.countDown();
        }
    }
}
