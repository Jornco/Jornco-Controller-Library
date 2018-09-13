package com.jornco.controller;

import com.jornco.controller.ble.OnIronbotWriteCallback;
import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.error.BLEWriterError;
import com.jornco.controller.receiver.BLEMessage;
import com.jornco.controller.util.BLELog;
import com.jornco.controller.util.TuckMessageUtils;

import java.util.Arrays;

/**
 * 塔克升级
 * Created by kkopite on 2018/9/12.
 */

public class BLETacoUpdater extends IronbotStatus implements OnIronbotWriteCallback {

    private byte[] data;

    private volatile boolean isUpdating = false;
    private int size = 0;
    public static final int MAX = 0xFF;

    private OnTacoUpdateListener mOnTacoUpdateListener;

    private IronbotController mController = new IronbotController();

    public void setController(IronbotController controller) {
        mController = controller;
    }

    public void startUpdate(byte[] data, OnTacoUpdateListener onTacoUpdateListener) {
        this.data = data;
        // 分成几个包
        size = (int) Math.ceil(data.length / MAX);
        mOnTacoUpdateListener = onTacoUpdateListener;

        isUpdating = true;
        onStart();
        if (mOnTacoUpdateListener != null) {
            mOnTacoUpdateListener.onTacoUpdateStart();
        }
        this.update(0);
    }

    public void stopUpdate() {
        isUpdating = false;
        onDestroy();
    }

    public void update(int index) {
        if (!isUpdating) {
            return;
        }
        if (index == size) {
            onDestroy();
            isUpdating = false;
            // 结束了
            if (mOnTacoUpdateListener != null) {
                mOnTacoUpdateListener.onTacoUpdateComplete();
            }
        } else {
            if (mOnTacoUpdateListener != null) {
                mOnTacoUpdateListener.onTacoUpdateProgress(index + 1, size);
            }
            int from = index * MAX;
            int to = from + MAX;
            if (to > data.length) {
                to = data.length;
            }
            // 数据包
            byte[] tmpData = Arrays.copyOfRange(data, from, to);
            // 总包数
            byte[] totalSize = TuckMessageUtils.generateTwo(size);
            // 第几个包
            byte[] currentIndex = TuckMessageUtils.generateTwo(index);
            // 组合起来要发的包
            byte[] sendPackage = TuckMessageUtils.createCMD(BLEConstant.CMD_CONTROL_UPGRADE,
                    totalSize, currentIndex, tmpData);

            // 发送升级
            mController.sendMsg(IronbotCode.create(sendPackage), this);

            // TODO: 设置超时模式, 等待多少秒没返回即失败
        }
    }

    @Override
    public boolean onReceiveBLEMessage(BLEMessage message) {
        return message.getCMDType() == BLEConstant.CMD_CONTROL_UPGRADE;
    }

    @Override
    public void handBLEMessage(BLEMessage message) {
        super.handBLEMessage(message);
        String[] recData = message.getRecData();
        if (recData.length == 0) {
            BLELog.log("出错了, 主控软件升级返回包没有接受到第几个包");
            return;
        }
        int index = Integer.parseInt(recData[0]);
        BLELog.log("第" + index + "个发送完毕, 总共有" + size + "个包");
        this.update(index + 1);
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

    public interface OnTacoUpdateListener {
        void onTacoUpdateStart();
        void onTacoUpdateProgress(int index, int size);
        void onTacoUpdateComplete();
    }

}
