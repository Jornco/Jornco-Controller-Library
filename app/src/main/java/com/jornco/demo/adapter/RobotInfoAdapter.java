package com.jornco.demo.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jornco.controller.util.BLELog;
import com.jornco.controller.ble.BLEState;
import com.jornco.controller.IronbotController;
import com.jornco.controller.ble.IronbotInfo;
import com.jornco.controller.IronbotSearcher;
import com.jornco.controller.ble.OnIronbotWriteCallback;
import com.jornco.controller.code.IronbotCode;
import com.jornco.controller.error.BLEWriterError;
import com.jornco.controller.scan.OnBLEDeviceStatusChangeListener;
import com.jornco.controller.util.RobotUtils;
import com.jornco.demo.R;

import java.util.List;

/**
 * Created by kkopite on 2017/10/25.
 */

public class RobotInfoAdapter extends RecyclerView.Adapter<RobotInfoAdapter.VH> implements OnBLEDeviceStatusChangeListener {

    private Activity context;
    private List<IronbotInfo> mItems;
    private IronbotSearcher mSearcher;

    public RobotInfoAdapter(Activity context, List<IronbotInfo> mItems, IronbotSearcher searcher) {
        this.context = context;
        this.mItems = mItems;
        mSearcher = searcher;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ble, parent, false));
    }

    @Override
    public void onBindViewHolder(final VH holder, int position) {
        final IronbotInfo info = mItems.get(position);
        holder.bind(info);

    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public void onStart() {
        mSearcher.addOnBLEDeviceStatusChangeListener(this);
    }

    public void onStop() {
        mSearcher.removeOnBLEDeviceStatusChangeListener(this);
    }

    @Override
    public void bleDeviceStateChange(String address, BLEState state) {
        if (address == null) {
            return;
        }
        BLELog.log("接收到变化: " + address + ", " + state);
        for (int i = 0; i < mItems.size(); i++) {
            IronbotInfo info = mItems.get(i);
            if (address.equals(info.getAddress())) {
                info.setState(state);
                final int finalI = i;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyItemChanged(finalI);
                    }
                });
                break;
            }
        }
    }

    public class VH extends RecyclerView.ViewHolder {

        public TextView mTvName;
        public TextView mTvAddress;
        public Button mBtnConnect;

        public VH(View itemView) {
            super(itemView);
            this.mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            this.mTvAddress = (TextView) itemView.findViewById(R.id.tv_address);
            this.mBtnConnect = (Button) itemView.findViewById(R.id.btn_connect);
        }

        public void bind(final IronbotInfo info) {

            final String address = info.getAddress();
            mTvName.setText(info.getName());
            mTvAddress.setText(address);
//
            BLEState state = info.getState();
            String text = "连接";
            if (state == BLEState.CONNECTED) {
                text = "断开";
            } else if (state == BLEState.CONNECTING) {
                text = "连接中";
            }
            mBtnConnect.setText(text);

            mBtnConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BLEState state = info.getState();
                    if (state == BLEState.DISCONNECT) {
                        mSearcher.connect(itemView.getContext(), address, info.getName());
                    } else {
                        mSearcher.disConnect(address);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IronbotCode code = new IronbotCode.Builder()
                            .addColor(RobotUtils.getRandom(0, 255), RobotUtils.getRandom(0, 255), RobotUtils.getRandom(0, 255))
                            .build();
                    new IronbotController().sendMsg(address, code, new OnIronbotWriteCallback() {
                        @Override
                        public void onWriterSuccess(String address) {
                            BLELog.log("发送成功: " + address);
                        }

                        @Override
                        public void onWriterFailure(String address, BLEWriterError error) {
                            BLELog.log("发送失败: " + error.getMessage());
                        }

                        @Override
                        public void onAllDeviceFailure() {
                            BLELog.log("都发送失败");
                        }

                        @Override
                        public void onWriterEnd() {
                            BLELog.log("指令已发给所有设备");
                        }
                    });
                }
            });
        }
    }
}
