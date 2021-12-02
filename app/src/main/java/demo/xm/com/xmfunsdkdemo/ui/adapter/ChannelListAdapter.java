package demo.xm.com.xmfunsdkdemo.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.xm.ui.widget.ListSelectItem;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;

/**
 * @author hws
 * @class
 * @time 2020/10/27 14:43
 */
public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.ViewHolder> {
    private List<String> channelInfo;
    public ChannelListAdapter(ChannelListAdapter.OnItemChannelClickListener ls) {
        this.onItemChannelClickListener = ls;
    }

    @Override
    public ChannelListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_channel_list, parent, false);
        ChannelListAdapter.ViewHolder viewHolder = new ChannelListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChannelListAdapter.ViewHolder holder, int position) {
        holder.lsiChannelName.setTitle(channelInfo.get(position));
    }

    @Override
    public int getItemCount() {
        return channelInfo != null ? channelInfo.size() : 0;
    }

    public void setData(List<String> channelInfo) {
        this.channelInfo = channelInfo;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ListSelectItem lsiChannelName;
        public ViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemChannelClickListener != null) {
                        int index = getAdapterPosition();
                        onItemChannelClickListener.onItemClick(index);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onItemChannelClickListener != null) {
                        int index = getAdapterPosition();
                        return onItemChannelClickListener.onLongItemClick(index);
                    }
                    return false;
                }
            });
            lsiChannelName = itemView.findViewById(R.id.lsi_channel_name);
        }
    }

    private ChannelListAdapter.OnItemChannelClickListener onItemChannelClickListener;

    public interface OnItemChannelClickListener {
        void onItemClick(int position);
        boolean onLongItemClick(int position);
    }
}
