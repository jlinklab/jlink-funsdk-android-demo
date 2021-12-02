package demo.xm.com.xmfunsdkdemo.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.lib.sdk.bean.preset.ConfigGetPreset;
import com.xm.ui.widget.ListSelectItem;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;

/**
 * @author hws
 * @class
 * @time 2020/10/27 14:43
 */
public class PresetListAdapter extends RecyclerView.Adapter<PresetListAdapter.ViewHolder> {
    private List<ConfigGetPreset> presetNameList;

    public PresetListAdapter(PresetListAdapter.OnItemPresetClickListener ls) {
        this.onItemPresetClickListener = ls;
    }

    @Override
    public PresetListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_preset_list, parent, false);
        PresetListAdapter.ViewHolder viewHolder = new PresetListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PresetListAdapter.ViewHolder holder, int position) {
        String presetName = TextUtils.isEmpty(presetNameList.get(position).PresetName) ? "" : presetNameList.get(position).PresetName;
        holder.lsiPresetName.setTitle("PresetId:" + presetNameList.get(position).Id);
        holder.lsiPresetName.setTip(presetName);
    }

    @Override
    public int getItemCount() {
        return presetNameList != null ? presetNameList.size() : 0;
    }

    public void setData(List<ConfigGetPreset> presetNameList) {
        this.presetNameList = presetNameList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ListSelectItem lsiPresetName;

        public ViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemPresetClickListener != null) {
                        int index = getAdapterPosition();
                        ConfigGetPreset configGetPreset = presetNameList.get(index);
                        String presetName = configGetPreset.PresetName == null ? "" : configGetPreset.PresetName;
                        onItemPresetClickListener.onItemClick(index,configGetPreset.Id,presetName);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onItemPresetClickListener != null) {
                        int index = getAdapterPosition();
                        ConfigGetPreset configGetPreset = presetNameList.get(index);
                        return onItemPresetClickListener.onLongItemClick(index,configGetPreset.Id);
                    }
                    return false;
                }
            });
            lsiPresetName = itemView.findViewById(R.id.lsi_preset_name);
        }
    }

    private PresetListAdapter.OnItemPresetClickListener onItemPresetClickListener;

    public interface OnItemPresetClickListener {
        void onItemClick(int position, int presetId,String presetName);

        boolean onLongItemClick(int position, int presetId);
    }
}
