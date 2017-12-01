package com.jornco.demo.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jornco.demo.IBLESend;
import com.jornco.demo.R;

/**
 * Created by kkopite on 2017/11/29.
 */

public class ColorSendView extends FrameLayout implements SeekBar.OnSeekBarChangeListener, View.OnClickListener{
    private SeekBar mRedSeekBar;
    private SeekBar mGreenSeekBar;
    private SeekBar mBlueSeekBar;
    private CheckBox mEnableAutoSend;
    private Button mBtnSendColor;
    private TextView mTvColorCmd;

    private IBLESend mBLESend;
    private static final String COLOR_CMD = "#B%d,%d,%d,*";

    public ColorSendView(@NonNull Context context) {
        this(context, null);
    }

    public ColorSendView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorSendView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_color_send_view, this);
        initView();
    }

    private void initView() {
        View rootView = this;
        mRedSeekBar = (SeekBar) rootView.findViewById(R.id.red_seek_bar);
        mGreenSeekBar = (SeekBar) rootView.findViewById(R.id.green_seek_bar);
        mBlueSeekBar = (SeekBar) rootView.findViewById(R.id.blue_seek_bar);
        mEnableAutoSend = (CheckBox) rootView.findViewById(R.id.enable_auto_send);
        mBtnSendColor = (Button) rootView.findViewById(R.id.btn_send_color);
        mTvColorCmd = (TextView) rootView.findViewById(R.id.tv_color_cmd);

        mRedSeekBar.setOnSeekBarChangeListener(this);
        mGreenSeekBar.setOnSeekBarChangeListener(this);
        mBlueSeekBar.setOnSeekBarChangeListener(this);
        mBtnSendColor.setOnClickListener(this);

        mTvColorCmd.setText(generateCmd());

    }

    public void setBLESend(IBLESend send) {
        this.mBLESend = send;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_send_color:
                String cmd = generateCmd();
                send(cmd);
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String cmd = generateCmd();
        mTvColorCmd.setText(cmd);
        if (mEnableAutoSend.isChecked()) {
            send(cmd);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void send(String code) {
        if (mBLESend != null) {
            mBLESend.send(code);
        }
    }

    private String generateCmd() {
        int red = mRedSeekBar.getProgress();
        int green = mGreenSeekBar.getProgress();
        int blue = mBlueSeekBar.getProgress();

        return String.format(COLOR_CMD, red, green, blue);
    }

}
