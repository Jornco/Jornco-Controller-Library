package com.jornco.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.jornco.demo.adapter.DataReceiverAdapter;

public class SendActivity extends AppCompatActivity {

    private ListView mListView;
    private DataReceiverAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        mListView = (ListView) findViewById(R.id.list_view);
        mAdapter = new DataReceiverAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.onStart();
    }
}
