package com.jornco.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jornco.controller.util.BLELog;
import com.jornco.controller.IronbotController;
import com.jornco.controller.ble.OnIronbotWriteCallback;
import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.error.BLEWriterError;

import static com.jornco.demo.R.id.edit_delay_time;
import static com.jornco.demo.R.id.edit_humi_num;
import static com.jornco.demo.R.id.edit_key_num;
import static com.jornco.demo.R.id.edit_rec_data;
import static com.jornco.demo.R.id.edit_send_data;
import static com.jornco.demo.R.id.edit_temp_num;
import static com.jornco.demo.R.id.edit_time;
import static com.jornco.demo.R.id.edit_tube_num;
import static com.jornco.demo.R.id.edit_tube_show_value;
import static com.jornco.demo.R.id.edit_ultra_num;
import static com.jornco.demo.R.id.old_b;
import static com.jornco.demo.R.id.old_g;
import static com.jornco.demo.R.id.old_r;

public class NewActivity extends AppCompatActivity implements View.OnClickListener, OnIronbotWriteCallback {

    private IronbotController controller;
    private EditText mEditDelayTime;
    private Button mBtnAddDelay;
    private EditText mEditUltraNum;
    private Button mBtnAddUltra;
    private EditText mEditTempNum;
    private Button mBtnReadTemp;
    private EditText mEditHumiNum;
    private Button mBtnAddHumi;
    private EditText mEditTubeNum;
    private EditText mEditTubeShowValue;
    private Button mBtnAddTube;
    private EditText mOldR;
    private EditText mOldG;
    private EditText mOldB;
    private EditText mColorNum;
    private Button mBtnSendColor;
    private EditText mEditKeyNum;
    private Spinner mSpinner;
    private Button mBtnAddKey;
    private EditText mEditSendData;
    private Button mBtnSendData;
    private EditText mEditRecData;
    private Button mBtnRecData;
    private Spinner mServoNum;
    private EditText mEditTime;
    private Button mBtnServo;
    private Button mBtnInit;
    private Button mBtnGenerate;
    private Button mBtnSend;
    private TextView mTvCode;

    private IronbotCode.Builder builder = new IronbotCode.Builder();
    private IronbotCode code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        initView();
        controller = new IronbotController();
    }

    private void initView() {
        mColorNum = (EditText) findViewById(R.id.edit_color_num);
        mEditDelayTime = (EditText) findViewById(edit_delay_time);
        mBtnAddDelay = (Button) findViewById(R.id.btn_add_delay);
        mEditUltraNum = (EditText) findViewById(edit_ultra_num);
        mBtnAddUltra = (Button) findViewById(R.id.btn_add_ultra);
        mEditTempNum = (EditText) findViewById(edit_temp_num);
        mBtnReadTemp = (Button) findViewById(R.id.btn_read_temp);
        mEditHumiNum = (EditText) findViewById(edit_humi_num);
        mBtnAddHumi = (Button) findViewById(R.id.btn_add_humi);
        mEditTubeNum = (EditText) findViewById(edit_tube_num);
        mEditTubeShowValue = (EditText) findViewById(edit_tube_show_value);
        mBtnAddTube = (Button) findViewById(R.id.btn_add_tube);
        mOldR = (EditText) findViewById(old_r);
        mOldG = (EditText) findViewById(old_g);
        mOldB = (EditText) findViewById(old_b);
        mBtnSendColor = (Button) findViewById(R.id.btn_send_color);
        mEditKeyNum = (EditText) findViewById(edit_key_num);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mBtnAddKey = (Button) findViewById(R.id.btn_add_key);
        mEditSendData = (EditText) findViewById(edit_send_data);
        mBtnSendData = (Button) findViewById(R.id.btn_send_data);
        mEditRecData = (EditText) findViewById(edit_rec_data);
        mBtnRecData = (Button) findViewById(R.id.btn_rec_data);
        mServoNum = (Spinner) findViewById(R.id.servo_num);
        mEditTime = (EditText) findViewById(edit_time);
        mBtnServo = (Button) findViewById(R.id.btn_servo);
        mBtnInit = (Button) findViewById(R.id.btn_init);
        mBtnGenerate = (Button) findViewById(R.id.btn_generate);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mTvCode = (TextView) findViewById(R.id.tv_code);

        mBtnAddDelay.setOnClickListener(this);
        mBtnAddUltra.setOnClickListener(this);
        mBtnReadTemp.setOnClickListener(this);
        mBtnAddHumi.setOnClickListener(this);
        mBtnAddTube.setOnClickListener(this);
        mBtnSendColor.setOnClickListener(this);
        mBtnAddKey.setOnClickListener(this);
        mBtnSendData.setOnClickListener(this);
        mBtnRecData.setOnClickListener(this);
        mBtnServo.setOnClickListener(this);
        mBtnInit.setOnClickListener(this);
        mBtnGenerate.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String num = "1";
        String data = "";
        switch (v.getId()) {
            case R.id.btn_add_delay:
                String time = mEditDelayTime.getText().toString().trim();
                if (TextUtils.isEmpty(time)) {
                    Toast.makeText(this, "time不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                builder.delay(Integer.parseInt(time));
                showCode();
                break;
            case R.id.btn_add_ultra:
                num = mEditUltraNum.getText().toString().trim();
                if (TextUtils.isEmpty(num)) {
                    Toast.makeText(this, "num不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                builder.ultra(Integer.parseInt(num));
                showCode();

                break;
            case R.id.btn_read_temp:
                num = mEditTempNum.getText().toString().trim();
                if (TextUtils.isEmpty(num)) {
                    Toast.makeText(this, "num不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                builder.readTemp(Integer.parseInt(num));
                showCode();
                break;
            case R.id.btn_add_humi:
                num = mEditHumiNum.getText().toString().trim();
                if (TextUtils.isEmpty(num)) {
                    Toast.makeText(this, "num不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                builder.readHumi(Integer.parseInt(num));
                showCode();
                break;
            case R.id.btn_add_tube:
                num = mEditTubeNum.getText().toString().trim();
                if (TextUtils.isEmpty(num)) {
                    Toast.makeText(this, "num不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                String value = mEditTubeShowValue.getText().toString().trim();
                if (TextUtils.isEmpty(value)) {
                    Toast.makeText(this, "value不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                builder.tubeAll(Integer.parseInt(num), Integer.parseInt(value));
                break;
            case R.id.btn_send_color:
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

                num = mColorNum.getText().toString().trim();
                if (TextUtils.isEmpty(num)) {
                    Toast.makeText(this, "num不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                builder.Led(Integer.parseInt(num), Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b));
                showCode();
                break;
            case R.id.btn_add_key:
                num = mEditKeyNum.getText().toString().trim();
                if (TextUtils.isEmpty(num)) {
                    Toast.makeText(this, "num不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                value = (String) mSpinner.getSelectedItem();
                builder.Key(Integer.parseInt(num), Integer.parseInt(value));
                showCode();
                break;
            case R.id.btn_send_data:
                data = mEditSendData.getText().toString().trim();
                if (TextUtils.isEmpty(data)) {
                    Toast.makeText(this, "data不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "无效", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_rec_data:
                data = mEditRecData.getText().toString().trim();
                if (TextUtils.isEmpty(data)) {
                    Toast.makeText(this, "data不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "无效", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_servo:
                String t = mEditTime.getText().toString().trim();
                if (TextUtils.isEmpty(t)) {
                    Toast.makeText(this, "time不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                num = (String) mServoNum.getSelectedItem();
                builder.PWM(Integer.parseInt(num), Integer.parseInt(t));
                showCode();
                break;
            case R.id.btn_init:
                builder = new IronbotCode.Builder();
                showCode();
                break;
            case R.id.btn_generate:
                code = builder.create();
                showCode();
                break;
            case R.id.btn_send:
                send(code);
                break;
        }
    }

    private void send(IronbotCode code) {
        controller.sendMsg(code, this);
    }

    private void showCode() {
        String msg = builder.getMsg();
        mTvCode.setText(msg);
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
}
