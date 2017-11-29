package com.jornco.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.jornco.controller.IronbotController;
import com.jornco.controller.ble.OnIronbotWriteCallback;
import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.error.BLEWriterError;
import com.jornco.demo.adapter.DataReceiverAdapter;

public class CodeActivity extends AppCompatActivity implements View.OnClickListener, OnIronbotWriteCallback{

    private EditText mEditCode;
    private ListView mResVal;
    private Button mBtnClear;
    private DataReceiverAdapter mAdapter;
    private Button mBtnSend;
    private EditText mEditShortCode;
    private Button mBtnSendTwo;
    private IronbotController ironbotController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        initView();
    }

    private void initView() {
        mEditCode = (EditText) findViewById(R.id.edit_code);
        mResVal = (ListView) findViewById(R.id.res_val);
        mBtnClear = (Button) findViewById(R.id.btn_clear);

        mBtnClear.setOnClickListener(this);

        mAdapter = new DataReceiverAdapter(this);
        mResVal.setAdapter(mAdapter);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this);
        mEditShortCode = (EditText) findViewById(R.id.edit_short_code);
        mEditShortCode.setOnClickListener(this);
        mBtnSendTwo = (Button) findViewById(R.id.btn_send_two);
        mBtnSendTwo.setOnClickListener(this);
        ironbotController = new IronbotController();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear:
                mAdapter.clearData();
                break;
            case R.id.btn_send:
                sendOne();
                break;
            case R.id.btn_send_two:
                sendTwo();
                break;
        }
    }

    private void sendTwo() {
        // validate
        String code = mEditShortCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "code不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        send(code);
    }

    private void sendOne() {
        // validate
        String code = mEditCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "code不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        send(code);
    }

    private void send(String data) {
        ironbotController.sendMsg(IronbotCode.create(data), this);
    }

    @Override
    public void onWriterSuccess(String address) {

    }

    @Override
    public void onWriterFailure(String address, final BLEWriterError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CodeActivity.this, "发送失败: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAllDeviceFailure() {

    }

    @Override
    public void onWriterEnd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CodeActivity.this, "发送完毕", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
