package com.jornco.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.jornco.controller.IronbotSearcher;
import com.jornco.controller.ble.IronbotInfo;
import com.jornco.controller.scan.IronbotSearcherCallback;
import com.jornco.demo.adapter.TextAdapter;

import java.util.ArrayList;
import java.util.List;

public class AISearchActivity extends AppCompatActivity implements View.OnClickListener, IronbotSearcherCallback {

    private static final String TAG = "AISearchActivity";
    private Button mBtnScan;
    private Button mBtnStop;
    private IronbotSearcher mSearcher;
    private IronbotInfo mDeviceInfo;
    private ListView mListView;
    private TextAdapter mAdapter;

    // 扫描到的蓝牙
    private List<String> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aisearch);
        mSearcher = new IronbotSearcher();
        mBtnScan = (Button) findViewById(R.id.btn_scan);
        mBtnStop = (Button) findViewById(R.id.btn_stop);
        mListView = (ListView) findViewById(R.id.list_view);
        mBtnScan.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);

        mAdapter = new TextAdapter(mData);
        mListView.setAdapter(mAdapter);
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
        String text = info.toString();
        Log.i(TAG, "onIronbotFound: " + text);
        mDeviceInfo = info;

        // 同一个设备会被多次扫描到, 需自己做过滤
        if (mData.contains(text)) {
            return;
        }

        mData.add(text);
        // 确保在ui线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSearcher.stopScan();
    }

}
