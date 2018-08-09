package com.jornco.controller;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kkopite on 2018/8/9.
 */

public class BLEUpdater implements Runnable {

    public static final byte[] START_DOWNLOAD = "@S**".getBytes();
    public static final String READY = "$ReadyOk";
    public static final String OK = "$RecOk";
    public static final byte[] COMPLETE = "@V**".getBytes();
    public static final String COMPLETE_OK = "$AcceptOk*";

    private static ExecutorService service =
            Executors.newSingleThreadExecutor();

    // 二进制文件
    private List<byte[][]> mBinData = null;
    // 脚本文件
    private String script = "";
    private BLEResolver mBLEResolver = new BLEResolver();
    private BLEUpdateListener mBLEUpdateListener = null;
    @Override
    public void run() {
        try {
            mBLEResolver.send(START_DOWNLOAD, READY);
            int len = mBinData.size();
            for (int i = 0; i < len; i++) {
                byte[][] datas = mBinData.get(i);
                mBLEResolver.send(datas[0]);

                Thread.sleep(50);

                mBLEResolver.send(datas[1], OK);

                Thread.sleep(50);

                mBLEUpdateListener.onProgress(i * 100 / len);
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
        }

        mBLEUpdateListener.onSuccess();
    }

    interface BLEUpdateListener {
        void onSuccess();
        void onError(Error error);
        void onProgress(int progress);
    }

}
