package com.jornco.controller;

import com.jornco.controller.ble.OnIronbotWriteCallback;
import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.receiver.BLEMessage;
import com.jornco.controller.receiver.BLEMessageFactory;
import com.jornco.controller.receiver.IBLEMessageFactory;
import com.jornco.controller.util.TuckMessageUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Taco更新
 * Created by kkopite on 2018/9/13.
 */

@RunWith(MockitoJUnitRunner.class)
public class BLETacoUpdateUnitTest {

    private byte[] updateData;
    private int size = 0xAA00;

    @Mock
    private IronbotController mController;

    @Mock
    private BLETacoUpdater mBLETacoUpdater;

    private IBLEMessageFactory mFactory = new BLEMessageFactory();

    @Before
    public void initMock() {
        initUpdateData();

        createMockController();
        createMockTacoUpdate();
    }

    private void createMockTacoUpdate() {
        mBLETacoUpdater = spy(BLETacoUpdater.class);
        doNothing().when(mBLETacoUpdater).onDestroy();
        doNothing().when(mBLETacoUpdater).onStart();
        mBLETacoUpdater.setController(mController);
    }

    private void createMockController() {
        mController = mock(IronbotController.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                IronbotCode code = invocation.getArgument(0);
                byte[] data = code.getCode();

                // 解析发送的包, 当前所属第几个
                byte[] idx = Arrays.copyOfRange(data, 10, 12);

                // 当前发的是第几个包
//                int currentIdx = (idx[1] << 8) + idx[0];

                // 模拟返回的接受到第几个包
                byte[] cmd = TuckMessageUtils.createCMD(BLEConstant.CMD_CONTROL_UPGRADE, idx);
                BLEMessage message = mFactory.createTuckBLEMessage("test", cmd);
                mBLETacoUpdater.handBLEMessage(message);
                return null;
            }
            // any(OnIronbotWriteCallback.class), 在发送的时候, OnIronbotWriteCallback不能传null, 否则无效
        }).when(mController).sendMsg(any(IronbotCode.class), any(OnIronbotWriteCallback.class));
    }

    private void initUpdateData() {
        updateData = new byte[size];
        for (int i = 0; i < size; i++) {
            updateData[i] = (byte) i;
        }
    }

    @Test
    public void update_isCorrect() {
        final int totalSize = (int) Math.ceil(updateData.length / BLETacoUpdater.MAX);
        final int[] tmp = {1};
        mBLETacoUpdater.startUpdate(this.updateData, new BLETacoUpdater.OnTacoUpdateListener() {
            @Override
            public void onTacoUpdateStart() {

            }

            @Override
            public void onTacoUpdateProgress(int index, int size) {
                System.out.println(index + ", " + size);
                assertEquals(tmp[0], index);
                if (tmp[0] < size) {
                    tmp[0]++;
                }
                assertEquals(size, totalSize);
            }

            @Override
            public void onTacoUpdateComplete() {

            }
        });

        assertEquals(tmp[0], totalSize);
    }

}
