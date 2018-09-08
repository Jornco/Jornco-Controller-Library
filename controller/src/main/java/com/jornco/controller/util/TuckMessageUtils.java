package com.jornco.controller.util;

import com.jornco.controller.BLEConstant;

/**
 * 关于塔克通信数据的生成, 解析
 * Created by kkopite on 2018/9/8.
 */

public class TuckMessageUtils {

    public static byte[] createCMD(byte cmd) {
        return createCMD(cmd, new byte[]{});
    }

    public static byte[] createCMD(byte cmd, byte[]... datas) {
        byte[] header = BLEConstant.HEADER.getBytes();
        byte[] tail = BLEConstant.TAIL.getBytes();
        // 包头 3
        // 全部长度 2
        // 命令    1
        // 数据区  datas每个元素长度, 加上长度字节1
        // 句尾 3
        // 和校验 1
        int len = header.length + 2 + 1 + tail.length + 1;
        for (byte[] data : datas) {
            if (data.length > 0)  {
                len += data.length + 1;
            }
        }
        byte[] t = new byte[len];
        int index = 0;

        // 包头
        System.arraycopy(header, 0, t, index, header.length);
        index += header.length;

        // 长度
        // 采用小端
        t[index] = (byte) (len & 0x00FF);
        t[index + 1] = (byte) (len >> 8);
        index += 2;

        // 命令
        t[index] = cmd;
        index += 1;

        // 数据区
        for (byte[] data : datas) {
            int l = data.length;
            // 长度大于0, 才会去做处理
            if (l > 0) {
                t[index] = (byte) data.length;
                System.arraycopy(data, 0, t, index + 1, data.length);
                index += data.length + 1;
            }
        }
        System.arraycopy(tail, 0, t, index, tail.length);
        index += tail.length;

        // 和校验
        // 取最低位
        // 此时index便是 len - 1
        t[index] = getPin(t, 0, len - 1);
        return t;
    }

    // 从start开始, 不包含end
    public static byte getPin(byte[] data, int start, int end) {
        int last = 0;
        for (int i = start; i < end; i++) {
            last = last + data[i];
        }
        return (byte) (last & 0xFF);
    }
}
