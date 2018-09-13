package com.jornco.controller.tuck.message;

import com.jornco.controller.BLEConstant;
import com.jornco.controller.receiver.BLEMessage;
import com.jornco.controller.receiver.BLEMessageFactory;
import com.jornco.controller.receiver.IBLEMessageFactory;
import com.jornco.controller.receiver.TuckBLEResolver;
import com.jornco.controller.util.TuckMessageUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 *
 * Created by kkopite on 2018/9/8.
 */

public class MessageUnitTest {

    private List<MsgBean> msgs = new ArrayList<>();

    @Before
    public void init() {
        msgs.add(
                new MsgBean(
                        (byte) 0x05,
                        new byte[][]{new byte[]{0x03}},
                        new byte[]{0x24, 0x2a, 0x24, 0x0C, 0x00, 0x05, 0x01, 0x03, 0x23, 0x2a, 0x23, (byte) 0xf7}
                ));

        msgs.add(
                new MsgBean(
                        (byte) 0x01,
                        new byte[][]{},
                        new byte[]{0x24, 0x2a, 0x24, 0x0a, 0x00, 0x01, 0x23, 0x2a, 0x23, (byte) 0xed}
                ));

        msgs.add(
                new MsgBean(
                        BLEConstant.CMD_SERVO,
                        new byte[][]{new byte[]{0x01}, new byte[] {0x50}, new byte[]{0x33}},
                        new byte[]{0x24, 0x2a, 0x24, 0x10, 0x00, 0x07, 0x01, 0x01, 0x01, 0x50, 0x01, 0x33, 0x23, 0x2a, 0x23, (byte) 0x80}
                )
        );
    }

    // 生成数据是对了
    @Test
    public void createMessage_isCorrect() {

        for (MsgBean msg : msgs) {
            byte[] data = TuckMessageUtils.createCMD(msg.cmd, msg.datas);
            assertArrayEquals(data, msg.res);
        }
    }

    // 解析是否正确
    @Test
    public void parseMessage_isCorrect() {
        IBLEMessageFactory factory = new BLEMessageFactory();

        byte[] data = TuckMessageUtils.createCMD(BLEConstant.CMD_SERVO, new byte[]{0x01}, new byte[] {0x50}, new byte[]{0x33});
        BLEMessage message = factory.createTuckBLEMessage("127", data);
        assertEquals(message.getCMDType(), BLEConstant.CMD_SERVO);
        // 没有错误信息
        assertEquals(message.getErrorMsg(), "");
        assertEquals(message.getPackageLen(), data.length);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            sb.append("a");
        }
        String version = sb.toString();
        // 测试接受返回的主控信息是否正确
        data = TuckMessageUtils.createCMD(BLEConstant.CMD_CONTROL_MESSAGE, version.getBytes(), new byte[]{ 0x00 });
        message = factory.createTuckBLEMessage("123", data);
        String[] recData = message.getRecData();
        assertEquals(recData[0], version);
        assertEquals(recData[1], "0");
        assertEquals(message.getPackageLen(), data.length);

        // 测试返回的距离信息是否正确
        data = TuckMessageUtils.createCMD(BLEConstant.CMD_DISTANCE, new byte[]{ 0x10, 0x20 });
        message = factory.createTuckBLEMessage("132", data);
        recData = message.getRecData();
        assertEquals(recData[0], "8208");
        assertEquals(message.getPackageLen(), data.length);
    }

    // 检测拼接是否正常
    @Test
    public void resolveBLEData_isCorrect() {
        TuckBLEResolver resolver = new TuckBLEResolver();
        assertEquals(false, resolver.resolve(new byte[]{0x24, 0x2a, 0x24, 0x10}));
        assertEquals(false, resolver.resolve(new byte[]{0x00, 0x07, 0x01, 0x01, 0x01}));
        assertEquals(false, resolver.resolve(new byte[]{0x50, 0x01, 0x33, 0x23}));
        assertEquals(false, resolver.resolve(new byte[]{0x2a, 0x23}));
        assertEquals(true, resolver.resolve(new byte[]{(byte) 0x80}));
        resolver.clear();


    }

    private class MsgBean {

        byte cmd;
        byte[][] datas;
        byte[] res;

        public MsgBean(byte cmd, byte[][] datas, byte[] res) {
            this.cmd = cmd;
            this.datas = datas;
            this.res = res;
        }
    }
}
