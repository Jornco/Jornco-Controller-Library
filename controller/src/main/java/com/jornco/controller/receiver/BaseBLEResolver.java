package com.jornco.controller.receiver;

/**
 * 处理蓝牙返回信息, 进行拼接
 * Created by kkopite on 2018/9/8.
 */

public abstract class BaseBLEResolver {

    public static final byte[] EMPTY = new byte[]{};

    byte[] mData = EMPTY;

    /**
     * 传入拼接数据, 如何拼接结束返回true, 否则false
     * @param data  收到的蓝牙数据
     * @return      拼接成功, 返回true, 否则false
     */
    public abstract boolean resolve(byte[] data);

    /**
     * 判断拼接成功后, 返回完整额拼接数据
     * @return 完成的数据
     */
    public byte[] getData() {
        // TODO: 需要做一个检验接口? 为拼接完就去获取, 直接报错?
        return mData;
    }

    public void clear() {
        mData = EMPTY;
    }
}
