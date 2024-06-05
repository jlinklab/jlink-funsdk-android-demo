package demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth.adapter;

import static com.constant.SDKLogConstant.APP_BLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.bluetooth.XMBleInfo;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.utils.LogUtils;
import com.xm.ui.widget.ListSelectItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth.DevBluetoothConnectActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth.listener.IDevBlueToothView;

public class DevBluetoothListAdapter extends RecyclerView.Adapter<DevBluetoothListAdapter.ViewHolder> {
    private List<XMBleInfo> xmBleInfoList = new ArrayList<>();
    private HashMap<String, Integer> rssiMap = new HashMap<>();
    private HashMap<String, Integer> progressMap = new HashMap<>();
    private IDevBlueToothView iDevBlueToothView;

    public DevBluetoothListAdapter(IDevBlueToothView iDevBlueToothView) {
        this.iDevBlueToothView = iDevBlueToothView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_dev_bluetooth_list, null));
    }

    @Override
    public void onBindViewHolder(@NonNull DevBluetoothListAdapter.ViewHolder viewHolder, int i) {
        XMBleInfo xmBleInfo = xmBleInfoList.get(i);
        viewHolder.lsiDevBluetooth.setTip(xmBleInfo.getMac() + " " + rssiMap.get(xmBleInfo.getMac()));
        viewHolder.lsiDevBluetooth.setTitle(xmBleInfo.getSn());
        viewHolder.pbWaiting.setTag("waiting:" + xmBleInfo.getMac());
        viewHolder.lsiDevBluetooth.setTag("completed:" + xmBleInfo.getMac());

        Integer progress = progressMap.get(xmBleInfo.getMac());
        if (progress != null) {
            if (progress != 100) {
                viewHolder.pbWaiting.setProgress(progress);
            } else {
                viewHolder.lsiDevBluetooth.setRightText(viewHolder.lsiDevBluetooth.getContext().getString(R.string.completed));
            }
        } else {
            viewHolder.lsiDevBluetooth.setRightText(viewHolder.lsiDevBluetooth.getContext().getString(R.string.not_paired));
        }
    }

    @Override
    public int getItemCount() {
        return xmBleInfoList == null ? 0 : xmBleInfoList.size();
    }

    /**
     * 添加数据
     *
     * @param xmBleInfo
     */
    public void setData(XMBleInfo xmBleInfo) {
        if (!rssiMap.containsKey(xmBleInfo.getMac())) {
            xmBleInfoList.add(xmBleInfo);
            notifyDataSetChanged();
        }

        rssiMap.put(xmBleInfo.getMac(), xmBleInfo.getRssi());
        updateRssi(xmBleInfo.getMac(), xmBleInfo.getRssi());
    }

    /**
     * 获取单个数据
     *
     * @param position
     * @return
     */
    public XMBleInfo getData(int position) {
        if (xmBleInfoList != null && position < xmBleInfoList.size()) {
            return xmBleInfoList.get(position);
        }

        return null;
    }

    /**
     * 根据MAC地址获取蓝牙信息
     *
     * @param mac
     * @return
     */
    public XMBleInfo getData(String mac) {
        if (mac != null) {
            for (XMBleInfo xmBleInfo : xmBleInfoList) {
                if (xmBleInfo != null && StringUtils.contrast(xmBleInfo.getMac(), mac)) {
                    return xmBleInfo;
                }
            }
        }

        return null;
    }

    /**
     * 删除数据
     *
     * @param xmDevInfo
     */
    public void removeData(XMDevInfo xmDevInfo) {
        if (xmBleInfoList != null && xmBleInfoList.contains(xmDevInfo)) {
            xmBleInfoList.remove(xmDevInfo);
            notifyDataSetChanged();
        }
    }

    /**
     * 清除所有数据
     */
    public void clearData() {
        if (progressMap != null) {
            progressMap.clear();
        }

        if (xmBleInfoList != null) {
            xmBleInfoList.clear();
        }

        if (rssiMap != null) {
            rssiMap.clear();
        }

        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ListSelectItem lsiDevBluetooth;
        ProgressBar pbWaiting;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lsiDevBluetooth = itemView.findViewById(R.id.lsi_dev_bluetooth_name);
            lsiDevBluetooth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    XMBleInfo xmBleInfo = xmBleInfoList.get(getAdapterPosition());
                    if (iDevBlueToothView != null) {
                        iDevBlueToothView.onDevBleItemSelected(xmBleInfo);
                    }
                }
            });

            pbWaiting = lsiDevBluetooth.getRightExtraView().findViewById(R.id.pb_waiting);
        }
    }

    /**
     * 显示配对等待
     */
    public void setProgress(String mac, int progress) {
        progressMap.put(mac, progress);
        RecyclerView recyclerView = iDevBlueToothView.getRecyclerView();
        if (recyclerView != null) {
            ListSelectItem listSelectItem = recyclerView.findViewWithTag("completed:" + mac);
            listSelectItem.setRightText("");
            ProgressBar progressBar = recyclerView.findViewWithTag("waiting:" + mac);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress);
            if (progress == 100) {
                progressBar.setVisibility(View.GONE);
                listSelectItem.setRightText(listSelectItem.getContext().getString(R.string.completed));
            }
        }
    }

    /**
     * 更新蓝牙接收信号的强度
     *
     * @param mac
     * @param rssi
     */
    private void updateRssi(String mac, int rssi) {
        RecyclerView recyclerView = iDevBlueToothView.getRecyclerView();
        if (recyclerView != null) {
            ListSelectItem listSelectItem = recyclerView.findViewWithTag("completed:" + mac);
            listSelectItem.setTip(mac + " " + rssi);
        }
    }
}
