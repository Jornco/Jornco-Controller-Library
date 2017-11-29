package com.jornco.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 0x1111;
    private Button mBtnSearch;
    private Button mBtnGoOld;
    private Button mBtnGoNew;
    private Button mBtnSetting;

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
        mBtnGoOld = (Button) findViewById(R.id.btn_go_old);
        mBtnGoOld.setOnClickListener(this);
        mBtnGoNew = (Button) findViewById(R.id.btn_go_new);
        mBtnGoNew.setOnClickListener(this);
        mBtnSetting = (Button) findViewById(R.id.btn_setting);
        mBtnSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.btn_go_old:
                startActivity(new Intent(this, OldActivity.class));
                break;
            case R.id.btn_go_new:
                startActivity(new Intent(this, CodeActivity.class));
                break;
            case R.id.btn_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }
}
