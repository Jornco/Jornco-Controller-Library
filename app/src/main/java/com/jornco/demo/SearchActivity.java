package com.jornco.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jornco.controller.BLELog;
import com.jornco.controller.IronbotInfo;
import com.jornco.controller.IronbotSearcher;
import com.jornco.controller.IronbotStatus;
import com.jornco.controller.receiver.BLEMessage;
import com.jornco.controller.scan.IronbotSearcherCallback;
import com.jornco.demo.adapter.RobotInfoAdapter;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, IronbotSearcherCallback {

    private static final String TAG = "SearchActivity";

    private RecyclerView mRecyclerView;
    private RobotInfoAdapter mAdapter;
    private List<IronbotInfo> items;
    private Button mBtnScan;
    private Button mBtnStop;

    private IronbotStatus status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        status = new MyIronbotStatus();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mBtnScan = (Button) findViewById(R.id.btn_scan);
        mBtnStop = (Button) findViewById(R.id.btn_stop);

        mBtnScan.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);

        items = IronbotSearcher.getInstance().getConnectedIronbot();

        mAdapter = new RobotInfoAdapter(this, items);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                if (IronbotSearcher.getInstance().isEnable()) {
                    IronbotSearcher.getInstance().searchIronbot(this);
                } else {
                    IronbotSearcher.getInstance().enable();
                }
                break;
            case R.id.btn_stop:
                IronbotSearcher.getInstance().stopScan();
                break;
        }
    }

    @Override
    public void onIronbotFound(IronbotInfo info) {
        Log.e(TAG, "onIronbotFound: " + info.toString());
        for (IronbotInfo item : items) {
            String address = item.getAddress();
            String a = info.getAddress();
            if (a.equals(address)) {
                return;
            }
        }
        items.add(info);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.onStart();
        status.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.onStop();
        status.onDestroy();
        IronbotSearcher.getInstance().stopScan();
    }

    class MyIronbotStatus extends IronbotStatus {

        @Override
        public boolean onReceiveMessage(BLEMessage message) {
            BLELog.log("接收到来自外围设备发来的信息: " + message.getMsg());
            return true;
        }
    }
}
