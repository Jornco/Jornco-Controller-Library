package com.jornco.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jornco.controller.BLEConstant;
import com.jornco.controller.BLETacoUpdater;
import com.jornco.controller.IronbotController;
import com.jornco.controller.IronbotStatus;
import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.receiver.BLEMessage;
import com.jornco.controller.util.BLELog;
import com.jornco.controller.util.TuckMessageUtils;
import com.jornco.demo.util.FileUtils;

import java.io.IOException;
import java.util.Arrays;

public class TacoActivity extends AppCompatActivity implements View.OnClickListener, BLETacoUpdater.OnTacoUpdateListener {

    private Button mBtnUpdate;

    private IronbotController mController = new IronbotController();
    private BLETacoUpdater mBLETacoUpdater = new BLETacoUpdater();
    private Button mBtnStopUpdate;
    private TextView mTvDistance;
    private TextView mTvVoice;
    private TextView mTvKey;
    private TextView mTvIrKey;
    private TextView mTvTraceSwitch;
    private TextView mTvCtrlMode;
    private TextView mTvVersionMode;
    private Button mBtnCtrl;
    private EditText mTvScriptIndex;
    private Button mBtnOnlineScript = (Button) findViewById(R.id.btn_online_script);
    private Button mBtnClearScript;
    private Button mBtnTmpScript;
    private EditText mTvLedCount;
    private EditText mTvLedOnInterval;
    private EditText mTvLedOffInterval;
    private Button mBtnLed;
    private EditText mTvServoIndex;
    private EditText mTvServoAngle;
    private EditText mTvServoSpeed;
    private Button mBtnServo;

    private byte[] script;

    private TacoReceiver mReceiver = new TacoReceiver();
    private ProgressBar mProgressBarUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taco);
        initView();

        try {
            script = FileUtils.readBinaryFile(this, "script.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mReceiver.onDestroy();
    }

    private void initView() {
        mBtnUpdate = findViewById(R.id.btn_update);

        mBtnUpdate.setOnClickListener(this);
        mBtnStopUpdate = findViewById(R.id.btn_stop_update);
        mBtnStopUpdate.setOnClickListener(this);
        mTvDistance = findViewById(R.id.tv_distance);
        mTvDistance.setOnClickListener(this);
        mTvVoice = findViewById(R.id.tv_voice);
        mTvVoice.setOnClickListener(this);
        mTvKey = findViewById(R.id.tv_key);
        mTvKey.setOnClickListener(this);
        mTvIrKey = findViewById(R.id.tv_ir_key);
        mTvIrKey.setOnClickListener(this);
        mTvTraceSwitch = findViewById(R.id.tv_trace_switch);
        mTvTraceSwitch.setOnClickListener(this);
        mTvCtrlMode = findViewById(R.id.tv_ctrl_mode);
        mTvCtrlMode.setOnClickListener(this);
        mTvVersionMode = findViewById(R.id.tv_version_mode);
        mTvVersionMode.setOnClickListener(this);
        mBtnCtrl = findViewById(R.id.btn_ctrl);
        mBtnCtrl.setOnClickListener(this);
        mTvScriptIndex = findViewById(R.id.tv_script_index);
        mTvScriptIndex.setOnClickListener(this);
        mBtnOnlineScript.setOnClickListener(this);
        mBtnClearScript = findViewById(R.id.btn_clear_script);
        mBtnClearScript.setOnClickListener(this);
        mBtnTmpScript = findViewById(R.id.btn_tmp_script);
        mBtnTmpScript.setOnClickListener(this);
        mTvLedCount = findViewById(R.id.tv_led_count);
        mTvLedCount.setOnClickListener(this);
        mTvLedOnInterval = findViewById(R.id.tv_led_on_interval);
        mTvLedOnInterval.setOnClickListener(this);
        mTvLedOffInterval = findViewById(R.id.tv_led_off_interval);
        mTvLedOffInterval.setOnClickListener(this);
        mBtnLed = findViewById(R.id.btn_led);
        mBtnLed.setOnClickListener(this);
        mTvServoIndex = findViewById(R.id.tv_servo_index);
        mTvServoIndex.setOnClickListener(this);
        mTvServoAngle = findViewById(R.id.tv_servo_angle);
        mTvServoAngle.setOnClickListener(this);
        mTvServoSpeed = findViewById(R.id.tv_servo_speed);
        mTvServoSpeed.setOnClickListener(this);
        mBtnServo = findViewById(R.id.btn_servo);
        mBtnServo.setOnClickListener(this);
        mProgressBarUpdate = findViewById(R.id.progress_bar_update);
        mProgressBarUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        byte[] data = null;
        switch (v.getId()) {
            case R.id.btn_update:
                update();
                break;
            case R.id.btn_stop_update:
                stopUpdate();
                break;
            case R.id.btn_ctrl:
                data = TuckMessageUtils.queryControlMessage();
                break;
            case R.id.btn_online_script:
                data = TuckMessageUtils.onlineScript(getScriptIndex(), script);
                break;
            case R.id.btn_clear_script:
                data = TuckMessageUtils.clearScript(getScriptIndex());
                break;
            case R.id.btn_tmp_script:
                data = TuckMessageUtils.tmpScript(script);
                break;
            case R.id.btn_led:
                data = TuckMessageUtils.createLEDData(getLedCount(), getLedOnInterval(), getLedOffInterval());
                break;
            case R.id.btn_servo:
                data = TuckMessageUtils.createServoData(getServoIndex(), getServoAngle(), getServoSpeed());
                break;
        }
        if (data != null) {
            mController.sendMsg(IronbotCode.create(data), null);
        }

    }

    private void update() {
        // 1 step 读取二进制文件
        try {
            String name = "a.bin";
            byte[] data = FileUtils.readBinaryFile(this, name);
            mBLETacoUpdater.startUpdate(data, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopUpdate() {
        mBLETacoUpdater.stopUpdate();
    }

    @Override
    public void onTacoUpdateStart() {
    }

    @Override
    public void onTacoUpdateProgress(final int index, final int size) {
        BLELog.log("升级进度: [" + index + "/" + size + "]");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBarUpdate.setMax(size);
                mProgressBarUpdate.setProgress(index);
            }
        });
    }

    @Override
    public void onTacoUpdateComplete() {
        BLELog.log("升级完成");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TacoActivity.this, "升级结束", Toast.LENGTH_SHORT).show();
                mProgressBarUpdate.setProgress(0);
            }
        });
    }

    private int getScriptIndex() {
        String index = mTvScriptIndex.getText().toString().trim();
        if (TextUtils.isEmpty(index)) {
            Toast.makeText(this, "index不能为空", Toast.LENGTH_SHORT).show();
            return 1;
        }
        return Integer.parseInt(index);
    }

    private byte getLedCount() {
        String count = mTvLedCount.getText().toString().trim();
        if (TextUtils.isEmpty(count)) {
            Toast.makeText(this, "重复次数(0~255)", Toast.LENGTH_SHORT).show();
            return 1;
        }
        return (byte) Integer.parseInt(count);
    }

    private byte getLedOnInterval() {
        // 单位是100ms, 所以1000ms应该返回10
        String interval = mTvLedOnInterval.getText().toString().trim();
        if (TextUtils.isEmpty(interval)) {
            Toast.makeText(this, "灯亮时间(0~25500ms)", Toast.LENGTH_SHORT).show();
            return 10;
        }
        return (byte) (Integer.parseInt(interval) / 100);
    }

    private byte getLedOffInterval() {
        String interval = mTvLedOffInterval.getText().toString().trim();
        if (TextUtils.isEmpty(interval)) {
            Toast.makeText(this, "灯亮时间(0~25500ms)", Toast.LENGTH_SHORT).show();
            return 10;
        }
        return (byte) (Integer.parseInt(interval) / 100);
    }

    private byte getServoIndex() {
        String index = mTvServoIndex.getText().toString().trim();
        if (TextUtils.isEmpty(index)) {
            Toast.makeText(this, "舵机编号(1~4)", Toast.LENGTH_SHORT).show();
            return 1;
        }
        int idx = Integer.parseInt(index);
        if (idx > 4 || idx < 1) {
            Toast.makeText(this, "编号只能是1~4", Toast.LENGTH_SHORT).show();
            return 1;
        }
        return (byte) idx;
    }

    private byte getServoAngle() {
        String angle = mTvServoAngle.getText().toString().trim();
        if (TextUtils.isEmpty(angle)) {
            Toast.makeText(this, "指定角度(0~180)", Toast.LENGTH_SHORT).show();
            return 90;
        }
        int a = Integer.parseInt(angle);
        if (a < 0 || a > 180) {
            Toast.makeText(this, "角度必须是0~180", Toast.LENGTH_SHORT).show();
            return 90;
        }
        return (byte) a;
    }

    private byte getServoSpeed() {
        String speed = mTvServoSpeed.getText().toString().trim();
        if (TextUtils.isEmpty(speed)) {
            Toast.makeText(this, "转速百分比(0~100)", Toast.LENGTH_SHORT).show();
            return 50;
        }
        int s = Integer.parseInt(speed);
        if (s > 100 || s < 0) {
            Toast.makeText(this, "转速必须是0~100", Toast.LENGTH_SHORT).show();
            return 50;
        }
        return (byte) s;
    }

    private class TacoReceiver extends IronbotStatus {

        @Override
        public void handBLEMessage(final BLEMessage message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    byte cmd = message.getCMDType();
                    String[] data = message.getRecData();
                    switch (cmd) {
                        case BLEConstant.CMD_DISTANCE:
                            mTvDistance.setText(data[0]);
                            break;
                        case BLEConstant.CMD_VOICE_LEVEL:
                            mTvVoice.setText(data[0]);
                            break;
                        case BLEConstant.CMD_KEY_INDEX:
                            mTvKey.setText(data[0]);
                            break;
                        case BLEConstant.CMD_IR_KEY_INDEX:
                            mTvIrKey.setText(data[0]);
                            break;
                        case BLEConstant.CMD_TRACK_SWITCH:
                            mTvTraceSwitch.setText(data[0]);
                            break;
                        case BLEConstant.CMD_CONTROL_MODE:
                            mTvCtrlMode.setText(data[0]);
                            break;
                        case BLEConstant.CMD_CONTROL_MESSAGE:
                            mTvVersionMode.setText(Arrays.toString(data));
                            break;
                    }
                }
            });
            super.handBLEMessage(message);
        }
    }

}
