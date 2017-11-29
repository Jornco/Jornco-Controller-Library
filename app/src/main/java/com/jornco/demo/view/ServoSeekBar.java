package com.jornco.demo.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jornco.demo.R;

/**
 * Created by kkopite on 2017/11/29.
 */

public class ServoSeekBar extends FrameLayout implements SeekBar.OnSeekBarChangeListener{

    private TextView mTvNum;
    private SeekBar mSeekBar;

    private OnServoSeekBarChangeListener mOnServoSeekBarChangeListener;

    // 编号
    private int num;

    public ServoSeekBar(@NonNull Context context) {
        this(context, null);
    }

    public ServoSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ServoSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_seek_bar, this);
        this.mTvNum = (TextView) findViewById(R.id.tv_num);
        this.mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        this.mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setMax(2000);
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
        mTvNum.setText(String.valueOf(num));
    }

    public int getAngle() {
        return mSeekBar.getProgress() + 500;
    }

    public void setOnServoSeekBarChangeListener(OnServoSeekBarChangeListener mOnServoSeekBarChangeListener) {
        this.mOnServoSeekBarChangeListener = mOnServoSeekBarChangeListener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mOnServoSeekBarChangeListener != null) {
            mOnServoSeekBarChangeListener.onServoAngleChanged(num, progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    interface OnServoSeekBarChangeListener {

        void onServoAngleChanged(int num, int angle);
    }
}
