package com.jornco.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.jornco.controller.IronbotController;
import com.jornco.controller.code.IronbotCode;
import com.jornco.demo.adapter.DataReceiverAdapter;
import com.jornco.demo.view.ColorSendView;
import com.jornco.demo.view.ServoControllerView;

public class SendActivity extends AppCompatActivity implements IBLESend {

    private ListView mListView;
    private DataReceiverAdapter mAdapter;
    private IronbotController mController = new IronbotController();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        mListView = (ListView) findViewById(R.id.list_view);
        mAdapter = new DataReceiverAdapter(this);
        mListView.setAdapter(mAdapter);

        ColorSendView colorSendView = (ColorSendView) findViewById(R.id.color_view);
        colorSendView.setBLESend(this);

        ServoControllerView servoControllerView = (ServoControllerView) findViewById(R.id.servo_view);
        servoControllerView.setBLESend(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.onStart();
    }

    @Override
    public void send(String msg) {
        mController.sendMsg(IronbotCode.create(msg), null);
    }

    @Override
    public void send(byte[] msg) {
        mController.sendMsg(IronbotCode.create(msg), null);
    }
}
