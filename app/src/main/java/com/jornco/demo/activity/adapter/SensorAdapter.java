package com.jornco.demo.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jornco.demo.R;

import java.util.List;

/**
 * Created by kkopite on 2018/3/6.
 */

public class SensorAdapter extends BaseAdapter {


    private final List<SensorTypeBean> mList;
    private final Context mContext;

    public SensorAdapter(Context context, List<SensorTypeBean> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.support_simple_spinner_dropdown_item, null);
            holder = new ViewHolder();
            holder.mTextView = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mTextView.setText(mList.get(position).getMsg());
        return convertView;
    }

    private class ViewHolder {
        TextView mTextView;
    }
}
