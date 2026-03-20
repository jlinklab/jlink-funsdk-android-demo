package demo.xm.com.xmfunsdkdemo.ui.device.config.networksetting.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import  demo.xm.com.xmfunsdkdemo.bean.wifi.WifiAP;
import com.utils.XUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;


public class WifiAPAdapter extends RecyclerView.Adapter<WifiAPAdapter.ViewHolder> {



    private List<WifiAP> mWifiApList;
    private Context mContext;
    private OnItemWifiListener mOnItemWifiListener;
    private boolean showBottomLine = false;

    public WifiAPAdapter(Context context) {
        mContext = context;
    }
    public void setWifiApList(List<WifiAP> mWifiApList) {
        this.mWifiApList = mWifiApList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(mContext).inflate(R.layout.config_network_item, null);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        WifiAP scanResult = mWifiApList.get(position);
        holder.name.setText(scanResult.getSSID());
        if (XUtils.getCapabilities(scanResult.getAuth()) == 0) {
            holder.lock.setBackground(null);
        } else {
            holder.lock.setBackgroundResource(R.drawable.ic_net_password);
        }
        if (position == 0) {
            holder.topLine.setVisibility(View.GONE);
        } else {
            holder.topLine.setVisibility(View.VISIBLE);
        }
        if (position == mWifiApList.size() - 1 && showBottomLine) {
            holder.bottomLine.setVisibility(View.VISIBLE);
        } else {
            holder.bottomLine.setVisibility(View.GONE);
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mWifiApList != null ? mWifiApList.size() : 0;
    }

    public void setShowBottomLine(boolean showBottomLine) {
        this.showBottomLine = showBottomLine;
    }

    public WifiAP getItem(int pos) {
        if (pos >= 0 && mWifiApList != null && mWifiApList.size() > pos) {
            return mWifiApList.get(pos);
        }
        return null;
    }

    public void updateData(List<WifiAP> scanResults) {
        this.mWifiApList = scanResults;
        notifyDataSetChanged();
    }

    public void setOnItemWifiListener(OnItemWifiListener onItemWifiListener) {
        mOnItemWifiListener = onItemWifiListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        View topLine, bottomLine;
        TextView name;
        ImageView single, lock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.rl_wifi);
            topLine = itemView.findViewById(R.id.top_line);
            bottomLine = itemView.findViewById(R.id.bottom_line);
            name = itemView.findViewById(R.id.wifi_SSID);
            single = itemView.findViewById(R.id.wifi_single);
            lock = itemView.findViewById(R.id.wifi_lock);
            linearLayout.setOnClickListener(v -> {
                if (mOnItemWifiListener != null) {
                    mOnItemWifiListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemWifiListener {
        void onItemClick(int pos);
    }
}
