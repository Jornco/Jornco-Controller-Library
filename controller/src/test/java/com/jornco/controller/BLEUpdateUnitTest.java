package com.jornco.controller;

import com.jornco.controller.ble.OnIronbotWriteCallback;
import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.receiver.BLEMessage;
import com.jornco.controller.receiver.BLEMessageFactory;
import com.jornco.controller.receiver.IBLEMessageFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

/**
 * 无法mock静态方法
 * Created by kkopite on 2018/8/10.
 */

@RunWith(MockitoJUnitRunner.class)
public class BLEUpdateUnitTest {

    public static final byte[] START_DOWNLOAD = "@S**".getBytes();
    public static final String READY = "$ReadyOk*";
    public static final String OK = "$RecOk*";
    public static final byte[] COMPLETE = "@V**".getBytes();
    public static final String COMPLETE_OK = "$AcceptOk*";

    private IBLEMessageFactory mFactory = new BLEMessageFactory();

    @Mock
    private BLEResolver mResolver;

    @Mock
    private IronbotController mController;

    @Mock
    private IronbotController mErrorController;

    @Before
    public void initMock() {
        try {
            mResolver = createMockResolve();
            mController = createMockController();
            mErrorController = createMockErrorConroller();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBLEUpdate () {
        BLEUpdater updater = new BLEUpdater();
        mResolver.setController(mController);
        updater.setBLEResolver(mResolver);

        final int[] t = {0};
        updater.test(createTestBin(), "----", new BLEUpdater.BLEUpdateListener() {
            @Override
            public void onSuccess() {
                t[0] = 1;
            }

            @Override
            public void onError(Error error) {
            }

            @Override
            public void onProgress(int progress) {
                System.out.println("进度: " + progress);
            }
        });

        assertEquals(1, t[0]);

        mResolver.setController(mErrorController);
        updater.test(createTestBin(), "----", new BLEUpdater.BLEUpdateListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Error error) {
                t[0] = 0;
            }

            @Override
            public void onProgress(int progress) {

            }
        });

        assertEquals(0, t[0]);

    }

    private IronbotController createMockErrorConroller () {
        mErrorController = mock(IronbotController.class);
        // 注意这里调用sendMsg()立马输入蓝牙模拟返回
        // 故mLatch的实例必须在 sendMsg()之前, 否则会造成错误
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                IronbotCode code = invocation.getArgument(0);
                String data = code.getData();
                byte[] first = code.getCodes().get(0);
                if (data.equals(new String(START_DOWNLOAD))) {
                    mResolver.handBLEMessage(createMessage(READY + "error"));
                } else if (data.equals(new String(COMPLETE))) {
                    mResolver.handBLEMessage(createMessage(COMPLETE_OK));
                } else if (first[0] == 0x40 && first[1] == 0x55 && data.length() < 20) {
                    int i = first[3];
                    int size = first[4] << 8 + first[5];
                    mResolver.handBLEMessage(createMessage("i = " + i + ", count = " + size));
                } else if (!data.startsWith("--") && !data.equals("#A")) {
                    // 这一部分就是那啥的
                    mResolver.handBLEMessage(createMessage(OK));
                }
                return null;
            }
        }).when(mErrorController).sendMsg(any(IronbotCode.class), any(OnIronbotWriteCallback.class));
        return mErrorController;
    }

    private IronbotController createMockController() {
        mController = mock(IronbotController.class);
        // 注意这里调用sendMsg()立马输入蓝牙模拟返回
        // 故mLatch的实例必须在 sendMsg()之前, 否则会造成错误
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                IronbotCode code = invocation.getArgument(0);
                String data = code.getData();
                byte[] first = code.getCodes().get(0);
                if (data.equals(new String(START_DOWNLOAD))) {
                    mResolver.handBLEMessage(createMessage(READY));
                } else if (data.equals(new String(COMPLETE))) {
                    mResolver.handBLEMessage(createMessage(COMPLETE_OK));
                } else if (first[0] == 0x40 && first[1] == 0x55 && data.length() < 20) {
                    int i = first[3];
                    int size = first[4] << 8 + first[5];
                    mResolver.handBLEMessage(createMessage("i = " + i + ", count = " + size));
                } else if (!data.startsWith("--") && !data.equals("#A")) {
                    // 这一部分就是那啥的
                    mResolver.handBLEMessage(createMessage(OK));
                }
                return null;
            }
        }).when(mController).sendMsg(any(IronbotCode.class), any(OnIronbotWriteCallback.class));
        return mController;
    }

    private BLEResolver createMockResolve() throws Exception {
        mResolver = mock(BLEResolver.class);
        doNothing().when(mResolver).onStart();
        doNothing().when(mResolver).onDestroy();
        doAnswer(Answers.CALLS_REAL_METHODS).when(mResolver).send(any(byte[].class));
        doAnswer(Answers.CALLS_REAL_METHODS).when(mResolver).handBLEMessage(any(BLEMessage.class));
        doAnswer(Answers.CALLS_REAL_METHODS).when(mResolver).send(any(byte[].class), anyString());
        doAnswer(Answers.CALLS_REAL_METHODS).when(mResolver).setController(any(IronbotController.class));

        return mResolver;
    }

    private BLEMessage createMessage(String msg) {
        return mFactory.createBLEMessage("test", msg, null);
    }

    private byte[] createTestBin() {
        byte[] data = new byte[10000];
        for (int i = 0; i < 10000; i++) {
              data[i] = (byte) i;
        }
        return data;
    }


}
