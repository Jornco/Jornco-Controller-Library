package com.jornco.demo.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jornco.controller.IronbotController;
import com.jornco.controller.IronbotStatus;
import com.jornco.controller.ble.SensorBean;
import com.jornco.controller.receiver.BLEMessage;
import com.jornco.demo.R;

import java.util.ArrayList;
import java.util.List;

// 响应设备不同的回传, 并执行不同的命令
public class ListenActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences mPreferences;

    private IronbotStatus mStatus = new IronbotStatus() {

        @Override
        public void handBLEMessage(BLEMessage message) {
            super.handBLEMessage(message);
            // 处理数据

        }
    };

    // 限定距离
    private String mDistance;

    private List<SelectSensorPortView> mSelectSensorPortViews = new ArrayList<>();
    private LinearLayout mLlSelectSensorPort;
    private TextView mTvLimitDistance;
    private TextView mTvDistance;
    private Button mBtnInitSensor;

    private IronbotController mController = new IronbotController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mStatus.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mStatus.onDestroy();
    }

    private void handleAction() {

    }

    private void handleBee() {

    }

    private void handleDistance(final String distance) {

    }

    private void initView() {
        mDistance = mPreferences.getString("ultrasonic_distance", "200");
        mLlSelectSensorPort = (LinearLayout) findViewById(R.id.ll_select_sensor_port);
        mTvLimitDistance = (TextView) findViewById(R.id.tv_limit_distance);
        mTvDistance = (TextView) findViewById(R.id.tv_distance);

        for (int i = 0; i < 6; i++) {
            SelectSensorPortView view = new SelectSensorPortView(this);
            view.setPort(i);
            mLlSelectSensorPort.addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mSelectSensorPortViews.add(view);
        }
        mBtnInitSensor = (Button) findViewById(R.id.btn_init_sensor);
        mBtnInitSensor.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_init_sensor:
                List<SensorBean> list = new ArrayList<>();
                for (SelectSensorPortView view : mSelectSensorPortViews) {
                    list.add(new SensorBean(view.getPort(), view.getType()));
                }
                mController.initSensor(list);
                break;
        }
    }
}
