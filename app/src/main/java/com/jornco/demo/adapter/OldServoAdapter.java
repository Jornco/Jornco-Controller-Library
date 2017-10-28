package com.jornco.demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jornco.demo.R;

import java.util.List;

/**
 * Created by kkopite on 2017/10/28.
 */

public class OldServoAdapter extends RecyclerView.Adapter<OldServoAdapter.VH> {

    private List<Info> items;

    public OldServoAdapter(List<Info> mItems) {
        init(mItems);
    }

    private void init(List<Info> mItems) {
        for (int i = 0; i < 9; i++) {
            Info info = new Info();
            info.number = i;
            info.angle = 1500;
            mItems.add(info);
        }
        items = mItems;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.old_servo_item, parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Info info = items.get(position);
        holder.bind(info);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        EditText angle;
        TextView number;

        public VH(View itemView) {
            super(itemView);
            angle = (EditText) itemView.findViewById(R.id.tv_angle);
            number = (TextView) itemView.findViewById(R.id.tv_number);
        }

        void bind(final Info info) {

            number.setText("编号: " + info.number);

            angle.setText(String.valueOf(info.angle));

            angle.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    info.angle = Integer.parseInt(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    public static class Info {

        private int number;
        private int angle;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getAngle() {
            return angle;
        }

        public void setAngle(int angle) {
            this.angle = angle;
        }
    }
}
