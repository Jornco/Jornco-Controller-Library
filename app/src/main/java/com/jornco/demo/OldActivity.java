package com.jornco.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jornco.controller.IronbotController;
import com.jornco.controller.IronbotStatus;
import com.jornco.controller.ble.OnIronbotWriteCallback;
import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.error.BLEWriterError;
import com.jornco.controller.receiver.BLEMessage;
import com.jornco.controller.util.BLELog;
import com.jornco.controller.util.RobotUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OldActivity extends AppCompatActivity implements View.OnClickListener, OnIronbotWriteCallback{

    private IronbotController controller;
    private EditText mOldR;
    private EditText mOldG;
    private EditText mOldB;
    private Button mBtnSendOldColor;
    private EditText mOldTime;
    private Button mBtnOldServo;
    private LinearLayout mOldServoList;
    private List<EditText> mOldServoAngle = new ArrayList<>();
    private TextView mSendData;
    private TextView mRecData;
    private Status status = new Status();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old);
        initView();
        controller = new IronbotController();
    }

    @Override
    protected void onStart() {
        super.onStart();
        status.onStart();
    }

    @Override
    protected void onStop() {
        status.onDestroy();
        super.onStop();
    }

    private void initView() {
        mSendData = (TextView) findViewById(R.id.tv_send_data);
        mOldR = (EditText) findViewById(R.id.old_r);
        mOldR.setOnClickListener(this);
        mOldG = (EditText) findViewById(R.id.old_g);
        mOldG.setOnClickListener(this);
        mOldB = (EditText) findViewById(R.id.old_b);
        mOldB.setOnClickListener(this);
        mBtnSendOldColor = (Button) findViewById(R.id.btn_send_old_color);
        mBtnSendOldColor.setOnClickListener(this);

        mOldTime = (EditText) findViewById(R.id.old_time);
        mOldTime.setOnClickListener(this);
        mBtnOldServo = (Button) findViewById(R.id.btn_old_servo);
        mBtnOldServo.setOnClickListener(this);

        mOldServoList = (LinearLayout) findViewById(R.id.old_servo_list);
        for (int i = 0; i < 10; i++) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            TextView tv = new TextView(this);
            tv.setText("编号: " + i + " 角度: ");
            EditText angle = new EditText(this);
            angle.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
            angle.setText("1500");
            mOldServoAngle.add(angle);
            linearLayout.addView(tv);
            linearLayout.addView(angle);
            mOldServoList.addView(linearLayout);
        }

        mRecData = (TextView) findViewById(R.id.tv_rec_data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_old_color:
                sendOldColor();
                break;
            case R.id.btn_old_servo:
                sendOldAction();
                break;
        }
    }

    private IronbotCode createRandom() {
        IronbotCode.Builder builder = new IronbotCode.Builder();
        builder.addServoStart();
        for (int i = 0; i < 10; i++) {
            builder.addServo(i, RobotUtils.getRandom(500, 2500), 1000);
        }
        builder.addServoEnd();
        return builder.build();
    }

    private void sendOldColor() {
        // validate
        String r = mOldR.getText().toString().trim();
        if (TextUtils.isEmpty(r)) {
            Toast.makeText(this, "r不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String g = mOldG.getText().toString().trim();
        if (TextUtils.isEmpty(g)) {
            Toast.makeText(this, "g不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String b = mOldB.getText().toString().trim();
        if (TextUtils.isEmpty(b)) {
            Toast.makeText(this, "b不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        IronbotCode ironbotCode = new IronbotCode.Builder()
                .addColor(Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b)).build();

        send(ironbotCode);
    }

    private void sendOldAction(){
        String time = mOldTime.getText().toString().trim();
        if (TextUtils.isEmpty(time)) {
            Toast.makeText(this, "time不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        IronbotCode.Builder builder = new IronbotCode.Builder()
                .addServoStart();

        int t = Integer.parseInt(time);

        for (int i = 0; i < mOldServoAngle.size(); i++) {
            EditText text = mOldServoAngle.get(i);
            String result = text.getText().toString().trim();
            if (TextUtils.isEmpty(result)) {
                Toast.makeText(this, "编号: " + i + "的角度不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            int angle = Integer.parseInt(result);
            builder.addServo(i, angle, t);
        }

        builder.addServoEnd();

        send(builder.build());
    }

    private void send(IronbotCode code) {
        mSendData.setText(code.getData());
        controller.sendMsg(code, this);
    }

    @Override
    public void onWriterSuccess(String address) {
        BLELog.log("发送成功: " + address);
    }

    @Override
    public void onWriterFailure(String address, BLEWriterError error) {
        BLELog.log("发送失败: " + error.getMessage());
    }

    @Override
    public void onAllDeviceFailure() {
        BLELog.log("都发送失败");
    }

    @Override
    public void onWriterEnd() {
        BLELog.log("指令已发给所有设备");
    }

    private class Status extends IronbotStatus {

        @Override
        public boolean onReceiveBLEMessage(final BLEMessage message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecData.setText(Arrays.toString(message.getMsg()));
                }
            });
            return true;
        }
    }
}
