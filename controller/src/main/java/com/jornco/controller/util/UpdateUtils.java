package com.jornco.controller.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 蓝牙升级相关函数
 * Created by kkopite on 2018/8/9.
 */

public class UpdateUtils {

    private UpdateUtils() {}

    /**
     * 将二进制文件组合成2048一个byte[], 带crc验证
     * @param data
     * @return
     */
    public static List<byte[][]> splitDataWithCRC(byte[] data) {
        List<byte[]> list = split(data, 2048);
        List<byte[][]> res = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            byte[] bytes = list.get(i);
            res.add(createPackage(bytes, i));
        }
        return res;
    }

    private static byte[][] createPackage(byte[] bytes, int i) {
        byte[][] res = new byte[2][];
        // 加上2为crc验证
        int len = bytes.length + 2;

        byte[] r1 = new byte[]{0x40, 0x55, (byte) i, (byte) ((len & 0xff00) >> 8),
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

    private static byte[] crc16(byte[] data, int end) {
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

    public static List<byte[]> split(byte[] msg, int size) {
        List<byte[]> codes = new ArrayList<>();
        int length = msg.length;
        if (length <= size) {
            codes.add(msg);
        } else {
            int index = 0;
            while (index < length) {
                int end = index + size;
                if (index + size > length) {
                    end = length;
                }
                codes.add(Arrays.copyOfRange(msg, index, end));
                index += size;
            }
        }
        return codes;
    }

    public static byte[][] splitWithLen(byte[] data, int size) {
        int length = data.length;
        int len = (int) Math.ceil((double)length / size);
        byte[][] result = new byte[len][];
        for (int i = 0; i < len; i++) {
            int from = size * i;
            int to = Math.min(length, (i + 1) * size);
            result[i] = Arrays.copyOfRange(data, from, to);
        }
        return result;
    }
}
