package com.jornco.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jornco.controller.BLEUpdater;
import com.jornco.controller.util.BLELog;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class UpdateActivity extends AppCompatActivity implements BLEUpdater.BLEUpdateListener {

    private BLEUpdater mUpdater = new BLEUpdater();
    private ProgressBar mBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdater.stop();
            }
        });

        mBar = (ProgressBar) findViewById(R.id.progress);
    }

    private void update() {
        try {
            String name = "jkcontorl.bin";
            byte[] data = readBinaryFile(name);
            mBar.setProgress(0);
            mUpdater.send(data, "--\n" +
                    "print(\"luxe\\r\\n\");" +
                    "\n--", this);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void onSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UpdateActivity.this, "成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onError(Error error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBar.setProgress(0);
                Toast.makeText(UpdateActivity.this, "失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onProgress(final int progress) {
        BLELog.log("进度值: " + progress);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBar.setProgress(progress);
            }
        });
    }
}
