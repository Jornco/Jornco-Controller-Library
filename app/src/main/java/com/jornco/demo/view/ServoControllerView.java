package com.jornco.demo.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jornco.demo.IBLESend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kkopite on 2017/11/29.
 */

public class ServoControllerView extends LinearLayout implements ServoSeekBar.OnServoSeekBarChangeListener {

    public static final String ANGLE_CMD = "#A%d,%d,50,*";

    private List<ServoSeekBar> mServoSeekBar = new ArrayList<>();

    private TextView mCmd;
    private Button mBtnSend;
    private EditText mTime;
    private CheckBox mEnableAutoSend;

    private IBLESend mBLESend;

    public ServoControllerView(@NonNull Context context) {
        this(context, null);
    }

    public ServoControllerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ServoControllerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);
        initView(context);
    }

    public void setBLESend(IBLESend send) {
        this.mBLESend = send;
    }

    private void initView(Context context) {
        for (int i = 0; i < 10; i++) {
            ServoSeekBar seekBar = new ServoSeekBar(context);
            seekBar.setNum(i);
            mServoSeekBar.add(seekBar);
            seekBar.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            addView(seekBar);
            seekBar.setOnServoSeekBarChangeListener(this);
        }

        LinearLayout ll = new LinearLayout(context);
        ll.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.setOrientation(HORIZONTAL);

        mTime = new EditText(context);
        mTime.setText(String.valueOf(1000));
        mTime.setInputType(InputType.TYPE_CLASS_NUMBER);
        mTime.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mEnableAutoSend = new CheckBox(context);
        mEnableAutoSend.setText("实时改变角度");
        mEnableAutoSend.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mBtnSend = new Button(context);
        mBtnSend.setText("发送");
        mBtnSend.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mBtnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                send(generateCmd());
            }
        });

        ll.addView(mTime);
        ll.addView(mBtnSend);
        ll.addView(mEnableAutoSend);

        addView(ll);

        mCmd = new TextView(context);
        mCmd.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mCmd);
    }

    private void send(String cmd) {
        mCmd.setText(cmd);
        mBLESend.send(cmd);
    }

    private String generateCmd() {
        String time = getTime();
        StringBuilder sb = new StringBuilder();
        sb.append("#A");
        for (ServoSeekBar seekBar : mServoSeekBar) {
            int angle = seekBar.getAngle();
            int num = seekBar.getNum();
            sb.append(num).append(",").append(angle).append(",").append(time).append(",");
        }
        sb.append("*");
        return sb.toString();
    }

    private String getTime() {
        String text = mTime.getText().toString();
        if (TextUtils.isEmpty(text)) {
            return "1000";
        }
        return text;
    }

    @Override
    public void onServoAngleChanged(int num, int angle) {
        if (!mEnableAutoSend.isChecked()) {
            return;
        }
        String cmd = String.format(ANGLE_CMD, num, angle);
        send(cmd);
    }
}
