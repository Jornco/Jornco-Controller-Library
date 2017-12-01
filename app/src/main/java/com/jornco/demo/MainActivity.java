package com.jornco.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 0x1111;
    private Button mBtnSearch;
    private Button mBtnGoOld;
    private Button mBtnGoNew;
    private Button mBtnSetting;

    private ViewGroup container;

    private final TreeMap<String, Class<? extends Activity>> buttons = new TreeMap<String, Class<? extends Activity>>() {{
        put("蓝牙扫描", SearchActivity.class);
        put("旧版发送", BLEServiceActivity.class);
        put("新版发送", CodeActivity.class);
        put("设置", SettingsActivity.class);
    }};

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
        container = (ViewGroup) findViewById(R.id.list);
        for (final Map.Entry<String, Class<? extends Activity>> entry : buttons.entrySet()) {
            Button button = new Button(this);
            button.setText(entry.getKey());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, entry.getValue()));
                }
            });
            container.addView(button);
        }
    }

}
