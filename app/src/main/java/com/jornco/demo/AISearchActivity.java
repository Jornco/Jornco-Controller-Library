package com.jornco.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jornco.controller.IronbotSearcher;
import com.jornco.controller.ble.IronbotInfo;
import com.jornco.controller.scan.IronbotSearcherCallback;

public class AISearchActivity extends AppCompatActivity implements View.OnClickListener, IronbotSearcherCallback {

    private static final String TAG = "AISearchActivity";
    private Button mBtnScan;
    private Button mBtnStop;
    private IronbotSearcher mSearcher;
    private IronbotInfo mDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aisearch);
        mSearcher = new IronbotSearcher();
        mBtnScan = (Button) findViewById(R.id.btn_scan);
        mBtnStop = (Button) findViewById(R.id.btn_stop);
        mBtnScan.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                if (mSearcher.isEnable()) {
                    mSearcher.searchIronbot(this);
                } else {
                    mSearcher.enable();
                }
                break;
            case R.id.btn_stop:
                mSearcher.stopScan();
                break;
        }
    }

    @Override
    public void onIronbotFound(IronbotInfo info) {
        Log.i(TAG, "onIronbotFound: " + info.toString());
        // 在该回调进行UI操作的话, 需要切换到UI线程进行处理
        // 如使用runOnUiThread(..);
        mDeviceInfo = info;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSearcher.stopScan();
    }
}
