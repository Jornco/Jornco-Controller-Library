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
            if (data.length > 0) {
                if (data.length > 255) {
                    // 每个单元的长度字节只占一位
                    throw new IllegalArgumentException("data 长度不能大于 255");
                }
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

    /**
     * 生成回应蓝牙的 成功 或失败
     *
     * @param cmd 指令
     * @param ok  0 或 1
     * @return 生成完整返回数据
     */
    public static byte[] createACKData(byte cmd, byte ok) {
        if (ok != 0x01 & ok != 0x00) {
            // 貌似也没必要抛出错误恩
            throw new IllegalArgumentException("ok 只能是0 或 1");
        }
        return createCMD(cmd, new byte[]{ok});
    }

    /**
     * 生成led灯指令
     *
     * @param repeat      重复次数(0x01~0xFE), 关灯 0x00, 永久 0xFF
     * @param onInterval  灯亮时长
     * @param offInterval 灭灯时长
     * @return 数据
     */
    public static byte[] createLEDData(byte repeat, byte onInterval, byte offInterval) {
        return createCMD(BLEConstant.CMD_LED, new byte[]{repeat}, new byte[]{onInterval}, new byte[]{offInterval});
    }

    /**
     * 无限次数
     *
     * @param onInterval  灯亮时长
     * @param offInterval 灭灯时长
     * @return 数据
     */
    public static byte[] createLEDData(byte onInterval, byte offInterval) {
        return createLEDData((byte) 0xFF, onInterval, offInterval);
    }

    public static byte[] offLed() {
        return createLEDData(((byte) 0x00), ((byte) 0x01), ((byte) 0x01));
    }

    // TODO: 又是角度 又是速度的?
    public static byte[] createServoData(byte num, byte angle, byte speed) {
        return createCMD(BLEConstant.CMD_SERVO, new byte[]{num}, new byte[]{angle}, new byte[]{speed});
    }

    /**
     * 查询主控信息
     *
     * @return 数据
     */
    public static byte[] queryControlMessage() {
        return createCMD(BLEConstant.CMD_CONTROL_MESSAGE);
    }

    /**
     * Todo: 写测试!!!!
     * 生成一个两位的byte[], 小端
     *
     * @param num
     * @return
     */
    public static byte[] generateTwo(int num) {
        return new byte[]{(byte) (num & 0xFF), (byte) (num >> 8)};
    }

    public static byte[] onlineScript(int index, byte[] script) {
        // TODO: 脚本的长度不会大于2000检查

        // step 1 分成 255长度的data
        byte[][] data = UpdateUtils.splitWithLen(script, 0xFF);
        byte[][] target = new byte[data.length + 2][];
        target[0] = new byte[]{(byte) index};
        target[1] = new byte[]{(byte) data.length};
        System.arraycopy(data, 0, target, 2, data.length);
        return createCMD(BLEConstant.CMD_DOWNLOAD_TMP_SCRIPT, target);
        // 合成
    }

    public static byte[] tmpScript(byte[] script) {
        byte[][] data = UpdateUtils.splitWithLen(script, 0xFF);
        byte[][] target = new byte[data.length + 1][];
        target[0] = new byte[]{(byte) data.length};
        System.arraycopy(data, 0, target, 1, data.length);
        return createCMD(BLEConstant.CMD_DOWNLOAD_TMP_SCRIPT, target);
    }

    public static byte[] clearScript(int index) {
        return createCMD(BLEConstant.CMD_CLEAR_SCRIPT, new byte[]{(byte) index});
    }

    /**
     * 行走电机
     * @param side 0/1 左右电机
     * @param action 0/1/2/3 前进/后退/停止/惯性滑动
     * @return 啦啦啦
     */
    public static byte[] createServoWalk(byte side, byte action) {
        return createCMD(BLEConstant.CMD_SERVO_WALK, new byte[]{ side}, new byte[]{action});
    }

    public static byte[] createExceScript(byte index) {
        return createCMD(BLEConstant.CMD_EXCE_SCRIPT, new byte[]{ index });
    }
}
