package com.jornco.demo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.jornco.controller.BLEBinder;
import com.jornco.controller.BLEService;
import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.receiver.BLEMessage;
import com.jornco.controller.receiver.BLEReceiver;
import com.jornco.demo.adapter.TextAdapter;
import com.jornco.demo.view.ColorSendView;
import com.jornco.demo.view.ServoControllerView;

import java.util.ArrayList;
import java.util.List;

public class BLEServiceActivity extends AppCompatActivity implements BLEReceiver, IBLESend {

    private BLEBinder mBinder;

    private List<String> mData = new ArrayList<>();
    private TextAdapter mAdapter;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (BLEBinder) service;
            mBinder.registerBLEReceiver(BLEServiceActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleservice);
        bindService(new Intent(this, BLEService.class), connection, BIND_AUTO_CREATE);

        ColorSendView colorSendView = (ColorSendView) findViewById(R.id.color_view);
        colorSendView.setBLESend(this);

        ServoControllerView servoControllerView = (ServoControllerView) findViewById(R.id.servo_view);
        servoControllerView.setBLESend(this);

        ListView listView = (ListView) findViewById(R.id.list_view);
        mAdapter = new TextAdapter(mData);
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        if (mBinder != null) {
            mBinder.unRegisterBLEReceiver(this);
        }
        unbindService(connection);
        super.onDestroy();
    }

    @Override
    public boolean onReceiveBLEMessage(BLEMessage message) {
        return true;
    }

    @Override
    public void handBLEMessage(BLEMessage message) {
        mData.add(message.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void send(String msg) {
        if (mBinder == null) {
            return;
        }
        mBinder.sendMsg(IronbotCode.create(msg), null);
    }
}
