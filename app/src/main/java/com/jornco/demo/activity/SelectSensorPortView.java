package com.jornco.demo.activity;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jornco.controller.ble.SensorType;
import com.jornco.demo.R;
import com.jornco.demo.activity.adapter.SensorAdapter;
import com.jornco.demo.activity.adapter.SensorTypeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kkopite on 2018/3/6.
 */

public class SelectSensorPortView extends LinearLayout {
    private TextView mTvPort;
    private Spinner mSpinnerSensor;

    private int mPort;
    private SensorType mType;

    private List<SensorTypeBean> mList = new ArrayList<>();

    public SelectSensorPortView(Context context) {
        this(context, null);
    }

    public SelectSensorPortView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectSensorPortView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LinearLayout.inflate(context, R.layout.layout_sensor_port, this);
        initData();
        initView(context);
    }

    private void initData() {
        // TODO 改为语言适配  R.string.xx
        mList.add(new SensorTypeBean("没有传感器", SensorType.NONE));
        mList.add(new SensorTypeBean("颜色识别", SensorType.COLOR));
        mList.add(new SensorTypeBean("超声波", SensorType.ULTRASONIC));
        mList.add(new SensorTypeBean("寻迹", SensorType.TRACKING));
        mList.add(new SensorTypeBean("按键", SensorType.KEY));
        mList.add(new SensorTypeBean("温湿度", SensorType.HUMITURE));
        mList.add(new SensorTypeBean("光敏", SensorType.PHOTOSENSITIVE));
        mList.add(new SensorTypeBean("声音", SensorType.VOICE));
        mList.add(new SensorTypeBean("红外接收", SensorType.INFRARED_REC));
        mList.add(new SensorTypeBean("RGB LED", SensorType.RGB));
        mList.add(new SensorTypeBean("LED点阵显示面板", SensorType.LED));
        mList.add(new SensorTypeBean("蜂鸣器", SensorType.BEE));
        mList.add(new SensorTypeBean("红外发射", SensorType.INFRARED_SEND));
        mList.add(new SensorTypeBean("数码管", SensorType.SEGMENT_DISPLAYS));
    }

    private void initView(Context context){
        mTvPort = (TextView) findViewById(R.id.tv_port);
        mSpinnerSensor = (Spinner) findViewById(R.id.spinner_sensor);

        BaseAdapter adapter = new SensorAdapter(context, mList);
        mSpinnerSensor.setAdapter(adapter);
        mSpinnerSensor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mType = mList.get(position).getType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setPort(int port) {
        mPort = port;
        mTvPort.setText(String.valueOf(port));
    }

    public SensorType getType() {
        return mType;
    }

    public int getPort() {
        return mPort;
    }
}
