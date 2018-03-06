package com.jornco.demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jornco.controller.IronbotStatus;
import com.jornco.controller.receiver.BLEMessage;

// 响应设备不同的回传, 并执行不同的命令
public class ListenActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;

    private IronbotStatus mStatus = new IronbotStatus() {

        @Override
        public void handBLEMessage(BLEMessage message) {
            super.handBLEMessage(message);
            // 处理数据

        }
    };
    private TextView mTvLimitDistance;
    private TextView mTvDistance;

    // 限定距离
    private String mDistance;

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvDistance.setText(distance);
            }
        });
    }

    private void initView() {
        mTvLimitDistance = (TextView) findViewById(R.id.tv_limit_distance);
        mTvDistance = (TextView) findViewById(R.id.tv_distance);

        mDistance = mPreferences.getString("ultrasonic_distance", "200");
        mTvLimitDistance.setText(mDistance);
    }
}
