package com.jornco.controller;

import com.jornco.controller.util.BLELog;
import com.jornco.controller.util.UpdateUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kkopite on 2018/8/9.
 */

public class BLEUpdater implements Runnable {

    public static final byte[] START_DOWNLOAD = "@S**".getBytes();
    public static final String READY = "$ReadyOk*";
    public static final String OK = "$RecOk*";
    public static final byte[] COMPLETE = "@V**".getBytes();
    public static final String COMPLETE_OK = "$AcceptOk*";

    private static ExecutorService service =
            Executors.newSingleThreadExecutor();

    // 二进制文件
    private List<byte[][]> mBinData = null;
    // 脚本文件
    private String script = "";

    public void setBLEResolver(BLEResolver BLEResolver) {
        mBLEResolver = BLEResolver;
    }

    private BLEResolver mBLEResolver = new BLEResolver();
    private BLEUpdateListener mBLEUpdateListener = null;
    private boolean isUpdating = false;

    public synchronized void send(byte[] data, String script, BLEUpdateListener listener) {
        if (isUpdating) {
            BLELog.log("当前已经有再更新的了");
            return;
        }
        isUpdating = true;
        this.script = script;
        mBLEUpdateListener = listener;
        mBinData = UpdateUtils.splitDataWithCRC(data);
        service.submit(this);
    }

    // 直接在该线程执行, 只是为了说方便同步测试
    public void test(byte[] data, String script, BLEUpdateListener listener) {
        if (isUpdating) {
            BLELog.log("当前已经有再更新的了");
            return;
        }
        isUpdating = true;
        this.script = script;
        mBLEUpdateListener = listener;
        mBinData = UpdateUtils.splitDataWithCRC(data);
        this.run();
    }

    @Override
    public void run() {
        try {
            mBLEResolver.onStart();
            mBLEResolver.send(START_DOWNLOAD, READY);
            int len = mBinData.size();
            for (int i = 0; i < len; i++) {
                byte[][] datas = mBinData.get(i);
                mBLEResolver.send(datas[0]);

                Thread.sleep(100);

                mBLEResolver.send(datas[1], OK);

                Thread.sleep(100);

                mBLEUpdateListener.onProgress(i * 100 / len);

                if (!isUpdating) {
                    BLELog.log("取消");
                    return;
                }
            }

            mBLEResolver.send(COMPLETE, COMPLETE_OK);

            Thread.sleep(10000);

            mBLEResolver.send(script.getBytes());

            Thread.sleep(10000);

            mBLEResolver.send("#A".getBytes());

            mBLEUpdateListener.onProgress(100);

        } catch (Exception e) {
            mBLEUpdateListener.onError(new Error(e));
            e.printStackTrace();
            return;
        } finally {
            mBLEResolver.onDestroy();
            isUpdating =  false;
        }
        mBLEUpdateListener.onSuccess();
    }

    public void stop() {
        isUpdating = false;
        mBLEResolver.stop();
    }

    public interface BLEUpdateListener {
        void onSuccess();
        void onError(Error error);
        void onProgress(int progress);
    }

}
