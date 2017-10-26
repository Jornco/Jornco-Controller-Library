package com.jornco.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jornco.controller.BLELog;
import com.jornco.controller.IronbotController;
import com.jornco.controller.OnIronbotWriteCallback;
import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.error.BLEWriterError;
import com.jornco.controller.util.RobotUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 0x1111;
    private Button mBtnSearch;
    private Button mBtnSendRandom;

    private ExecutorService service = Executors.newFixedThreadPool(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPermission();

    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    || (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }

    private void initView() {
        mBtnSearch = (Button) findViewById(R.id.btn_search);

        mBtnSearch.setOnClickListener(this);
        mBtnSendRandom = (Button) findViewById(R.id.btn_send_random);
        mBtnSendRandom.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.btn_send_random:
                send();
//                sendRandom();
                break;
        }
    }

    private void send(){
        for (int i = 0; i < 10; i++) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    sendRandom();
                }
            });
        }
    }

    private void sendRandom() {
        new IronbotController().sendMsg(createRandom(), new OnIronbotWriteCallback() {
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
        });
    }


    private IronbotCode createRandom() {
        IronbotCode.Builder builder = new IronbotCode.Builder();
        builder.addServoStart();
        for (int i = 0; i < 10; i++) {
            builder.addServo(i, RobotUtils.getRandom(500,2500), 1000);
        }
        builder.addServoEnd();
        return builder.build();
    }
}
