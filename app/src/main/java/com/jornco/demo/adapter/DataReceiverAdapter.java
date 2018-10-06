package com.jornco.demo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jornco.controller.IronbotStatus;
import com.jornco.controller.receiver.BLEMessage;
import com.jornco.controller.util.BLELog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kkopite on 2017/10/28.
 */

public class DataReceiverAdapter extends BaseAdapter {

    private Activity mContext;
    private List<String> data = new ArrayList<>();
    private MyStatus status = new MyStatus();

    public DataReceiverAdapter(Activity context) {
        mContext = context;
    }

    public void onStart(){
        status.onStart();
    }

    public void onStop(){
        status.onDestroy();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Holder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            holder = new Holder();
            holder.textView = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.textView.setText(data.get(position));
        return convertView;
    }

    public void clearData() {
        data.clear();
        notifyDataSetChanged();
    }

    private static class Holder {
        TextView textView;
    }

    private class MyStatus extends IronbotStatus {

        @Override
        public boolean onReceiveBLEMessage(BLEMessage message) {
            data.add(Arrays.toString(message.getMsg()));
            BLELog.log(message.getMsg() + "收到");
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
            return true;
        }
    }
}
