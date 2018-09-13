package com.jornco.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jornco.controller.BLETacoUpdater;
import com.jornco.controller.IronbotController;
import com.jornco.controller.util.BLELog;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class TacoActivity extends AppCompatActivity implements View.OnClickListener, BLETacoUpdater.OnTacoUpdateListener{

    private Button mBtnUpdate;

    private IronbotController mController = new IronbotController();
    private BLETacoUpdater mBLETacoUpdater = new BLETacoUpdater();
    private Button mBtnStopUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taco);
        initView();


    }

    private void initView() {
        mBtnUpdate = findViewById(R.id.btn_update);

        mBtnUpdate.setOnClickListener(this);
        mBtnStopUpdate = (Button) findViewById(R.id.btn_stop_update);
        mBtnStopUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                update();
                break;
            case R.id.btn_stop_update:
                stopUpdate();
                break;
        }
    }

    private void update() {
        // 1 step 读取二进制文件
        try {
            String name = "a.bin";
            byte[] data = readBinaryFile(name);
            mBLETacoUpdater.startUpdate(data, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopUpdate() {
        mBLETacoUpdater.stopUpdate();
    }


    public byte[] readBinaryFile(String filePath) throws FileNotFoundException, IOException {

        InputStream in = null;
        BufferedInputStream buffer = null;
        DataInputStream dataIn = null;
        ByteArrayOutputStream bos = null;
        DataOutputStream dos = null;
        byte[] bArray;
        try {
            in = getAssets().open(filePath);
            buffer = new BufferedInputStream(in);
            dataIn = new DataInputStream(buffer);
            bos = new ByteArrayOutputStream();
            dos = new DataOutputStream(bos);
            byte[] buf = new byte[1024];
            while (true) {
                int len = dataIn.read(buf);
                if (len < 0)
                    break;
                dos.write(buf, 0, len);
            }
            bArray = bos.toByteArray();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        } finally {

            if (in != null)
                in.close();
            if (dataIn != null)
                dataIn.close();
            if (buffer != null)
                buffer.close();
            if (bos != null)
                bos.close();
            if (dos != null)
                dos.close();
        }

        return bArray;

    }

    @Override
    public void onTacoUpdateStart() {

    }

    @Override
    public void onTacoUpdateProgress(int index, int size) {
        BLELog.log("升级进度: [" + index + "/" + size + "]");
    }

    @Override
    public void onTacoUpdateComplete() {
        BLELog.log("升级完成");
    }
}
