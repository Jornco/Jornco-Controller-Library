package com.jornco.controller.receiver;

/**
 * 接受处理塔克的返回数据
 * Created by kkopite on 2018/9/8.
 */

public class TuckBLEResolver extends BaseBLEResolver {

    @Override
    public boolean resolve(byte[] data) {
        int prevLen = mData.length;
        int curLen = data.length;
        int len = prevLen + curLen;
        if (prevLen == 0 && data[0] != 0x24) {
            // 普通的A B 返回
            mData = data;
            return true;
        } else {
            byte[] t = new byte[len];
            System.arraycopy(mData, 0, t, 0, prevLen);
            System.arraycopy(data, 0, t, prevLen, curLen);
            mData = t;
            // 兼容过去的的数据吗?
//            if (msg.endsWith("*")) {
//                return true;
//            }
            // 后四位分别是#*# 还有一个和检验
            if (t[len - 2] == 0x23 && t[len - 3] == 0x2a && t[len - 4] == 0x23) {
                return true;
            }
        }
        return false;
    }
}
