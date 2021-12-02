package demo.xm.com.xmfunsdkdemo.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.xm.ui.widget.ListSelectItem;

import java.util.ArrayList;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;

/**
 * Created by hws on 2018-10-10.
 */

public class ItemSetAdapter extends RecyclerView.Adapter<ItemSetAdapter.ViewHolder> {
    private List<String> data;
    private int selItemPos = -1;
    private int selColorId = Color.BLACK;
    private RecyclerView recyclerView;
    public ItemSetAdapter(RecyclerView recyclerView,OnItemSetClickListener ls) {
        this.recyclerView = recyclerView;
        this.onItemSetClickListener = ls;
    }

    public ItemSetAdapter(OnItemSetClickListener ls) {
        this.onItemSetClickListener = ls;
    }

    public ItemSetAdapter(ArrayList<String> data, OnItemSetClickListener ls) {
        this.data = data;
        this.onItemSetClickListener = ls;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_base_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.listSelectItem.setTitle(data.get(position));
        holder.listSelectItem.setTag("itemSet:" + position);
        if (selItemPos == position) {
            holder.listSelectItem.setTitleColor(holder.listSelectItem.getResources().getColor(R.color.theme_color));
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public void setData(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ListSelectItem listSelectItem;

        public ViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemSetClickListener != null) {
                        if (selItemPos >= 0) {
                            if (recyclerView != null) {
                                ListSelectItem listSelectItem = recyclerView.findViewWithTag("itemSet:" + selItemPos);
                                if (listSelectItem != null) {
                                    listSelectItem.setTitleColor(listSelectItem.getResources().getColor(R.color.default_normal_text_color));
                                }
                            }
                        }

                        selItemPos = getAdapterPosition();
                        if (recyclerView != null) {
                            ListSelectItem listSelectItem = recyclerView.findViewWithTag("itemSet:" + selItemPos);
                            if (listSelectItem != null) {
                                listSelectItem.setTitleColor(selColorId);
                            }
                        }

                        onItemSetClickListener.onItem(selItemPos);
                    }
                }
            });
            listSelectItem = itemView.findViewById(R.id.item_set);
        }
    }

    public void initSelItemColor(int position, int colorId) {
        this.selColorId = colorId;
        if (recyclerView != null) {
            this.selItemPos = position;
            ListSelectItem listSelectItem = recyclerView.findViewWithTag("itemSet:" + position);
            if (listSelectItem != null) {
                listSelectItem.setTitleColor(colorId);
            }
        }
    }

    private OnItemSetClickListener onItemSetClickListener;

    public interface OnItemSetClickListener {
        void onItem(int position);
    }
}
