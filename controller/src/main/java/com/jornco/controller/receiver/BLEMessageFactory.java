package com.jornco.controller.receiver;

import com.jornco.controller.BLEConstant;
import com.jornco.controller.ble.SensorTypeMap;
import com.jornco.controller.util.BLELog;
import com.jornco.controller.util.TuckMessageUtils;

import java.util.Arrays;

/**
 *
 * Created by kkopite on 2018/3/6.
 */

public class BLEMessageFactory implements IBLEMessageFactory {
    @Override
    public BLEMessage createBLEMessage(String address, byte[] data) {
        BLEMessage message = new BLEMessage(address, data);
        String msg = new String(data);
        if (msg.startsWith("#K")) {
            // #K101,1,12,13,*
            String replace = msg.replaceFirst("#K", "").replace(",*", "");
            String[] split = replace.split(",");
            if (split.length >= 3) {
                // 起码要三个, 第一个为101 表示typeID,  第二个表示端口
                message.setType(SensorTypeMap.getType(split[0]));
                message.setPort(Integer.parseInt(split[1]));
                String[] d = Arrays.copyOfRange(split,2, split.length - 1);
                message.setRecData(d);
            } else {
                BLELog.log("返回的传感器数据错误: " + msg);
            }
        }
        return message;
    }

    // 解析塔克传回的数据
    @Override
    public BLEMessage createTuckBLEMessage(String address, byte[] data) {
        BLEMessage message = new BLEMessage(address, data);
        int len = (data[3] & 0xFF) + ((data[4] & 0xFF) << 8);
        String error = "";
        if (data.length != len) {
            error = "返回信息的长度不正确: [真实长度: " + data.length + ", 应有长度: " + len + "]";
        }
        int sum = TuckMessageUtils.getPin(data, 0, data.length - 1);
        if (sum != data[data.length - 1]) {
            error += "返回信息的校验不正确: [真实校验: " + sum + ", 应有校验: " + data[data.length - 1] + "]";
        }
        BLELog.log(error);
        message.setErrorMsg(error);
        // 具体的数据, 应该根据具体属于哪一类cmd, 再进行处理

//        int startIdx = 6;
//        // 倒数第四个
//        int endIdx = data.length - 4;
//        List<String> recData = new ArrayList<>();
//        for (int i = startIdx; i < endIdx; ) {
//            int tmpLen = data[i];
//            byte[] tmp = new byte[tmpLen];
//            System.arraycopy(data, i + 1, tmp, 0, tmpLen);
//            recData.add(new String(tmp));
//            i += 1 + tmpLen;
//        }
//        message.setRecData((String[]) recData.toArray());

        message.setRecData(parseData(message.getCMDType(), Arrays.copyOfRange(data, 6, data.length - 4)));
        message.setPackageLen(len);
        return message;
    }

    /**
     * TODO: 无法知道处理的每个单元具体的值是字符串, 还是整型, 需要通过cmd来做判断, 没添加一个都要重新改方法实现
     * 将每个单元 len + 数据,  变成  len + type + 数据是不是更好 ?
     * 将塔克返回的data的数据取出来
     *
     * @param type 该数据的类型
     * @param data data
     * @return 数据
     */
    private String[] parseData(byte type, byte[] data) {
        // byte的值可能是负的, 故要用上  & 0xFF, 来将 -128~127 转成 0~255
        int d = 0;
        switch (type) {
            case BLEConstant.CMD_DISTANCE:
            case BLEConstant.CMD_CONTROL_UPGRADE:
                d = ((data[2] & 0xFF) << 8) + (data[1] & 0xFF);
                return new String[]{String.valueOf(d)};
            case BLEConstant.CMD_VOICE_LEVEL:
            case BLEConstant.CMD_KEY_INDEX:
            case BLEConstant.CMD_IR_KEY_INDEX:
            case BLEConstant.CMD_TRACK_SWITCH:
            case BLEConstant.CMD_LED:
            case BLEConstant.CMD_SERVO:
            case BLEConstant.CMD_CONTROL_MODE:
            case BLEConstant.CMD_DOWNLOAD_SCRIPT:
            case BLEConstant.CMD_CLEAR_SCRIPT:
            case BLEConstant.CMD_DOWNLOAD_TMP_SCRIPT:
                // 以上几个都是返回单个 一位长度的数据
                d = data[1];
                return new String[]{String.valueOf(d)};
            case BLEConstant.CMD_CONTROL_MESSAGE:
                // 如 3 a b c 1 0
                // 主控查询, 第一个单元是软件版本, 第二个是控制模式, 即在线(0), 离线(1)
                int len = data[0] & 0xFF;
                String version = new String(Arrays.copyOfRange(data, 1, len + 1));
                int mode = data[2 + len];
                return new String[]{version, String.valueOf(mode)};


        }
        return new String[]{};
    }
}
