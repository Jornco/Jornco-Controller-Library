package com.jornco.controller.util;

import com.jornco.controller.code.IronbotCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙升级相关函数
 * Created by kkopite on 2018/8/9.
 */

public class UpdateUtil {

    private UpdateUtil() {}

    /**
     * 将二进制文件组合成2048一个byte[], 带crc验证
     * @param data
     * @return
     */
    public List<byte[][]> splitDataWithCRC(byte[] data) {
        List<byte[]> list = IronbotCode.split(data);
        List<byte[][]> res = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            byte[] bytes = list.get(i);
            res.add(createPackage(bytes, i));
        }
        return res;
    }

    private byte[][] createPackage(byte[] bytes, int i) {
        byte[][] res = new byte[2][];
        // 加上2为crc验证
        int len = bytes.length + 2;

        byte[] r1 = new byte[]{0x44, 0x55, (byte) i, (byte) ((len & 0xff00) >> 8),
                (byte) (len & 0x00ff), 0x2a, 0x2a};

        byte[] r2 = new byte[len];
        System.arraycopy(bytes, 0, r2, 0, len - 2);
        // 处理crc
        byte[] crc = crc16(r2, len - 2);
        r2[len - 2] = crc[0];
        r2[len - 1] = crc[1];

        res[0] = r1;
        res[1] = r2;
        return res;
    }

    private byte[] crc16(byte[] data, int end) {
        int TOPBIT = 0x8000;
        int crc = 0x0000;

        for (int i = 0; i < end; i++) {
            crc = (crc ^ (data[i] << 8));
            for (int j = 8; j > 0; j--) {
                if ((crc & TOPBIT) > 0) {
                    crc = (crc << 1) ^ 0x8005;
                } else {
                    crc = (crc << 1);
                }
            }
        }

        return new byte[]{(byte) ((crc & 0xFF00) >> 8), (byte) (crc & 0x00FF)};
    }
}
