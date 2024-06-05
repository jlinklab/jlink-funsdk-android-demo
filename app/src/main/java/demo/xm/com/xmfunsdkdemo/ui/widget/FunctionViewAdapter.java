package demo.xm.com.xmfunsdkdemo.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.model.FunctionViewItemElement;

/**
 * Created by zhangyongyong on 2017-05-08-11:14.
 */

public class FunctionViewAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private LayoutInflater mInflater;
    private List<FunctionViewItemElement> mList;
    private Context mContext;
    private OnItemClickListener mItemClickListener;


    public FunctionViewAdapter(Context context, List<FunctionViewItemElement> list) {
        this.mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup group, int i) {
        View view = mInflater.inflate(R.layout.function_item, null);
        view.setOnClickListener(this);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        holder.itemView.setTag(i);
        if (mList.get(i).isSelected()) {
            ((ViewHolder) holder).mImageView.setImageResource(mList.get(i).getSelectedResourceId());
            ((ViewHolder) holder).mTextView.setTextColor(mContext.getResources().getColor(R.color.theme_color));
        } else {
            ((ViewHolder) holder).mImageView.setImageResource(mList.get(i).getNormalResourceId());
            ((ViewHolder) holder).mTextView.setTextColor(mContext.getResources().getColor(R.color.bg_gray));
        }
        ((ViewHolder) holder).mTextView.setText(mList.get(i).getName());
        if (i < getItemCount()) {
            holder.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.smart_analyze_right_line));
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void setData(List<FunctionViewItemElement> functionList) {
        this.mList = functionList;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(v, (Integer) v.getTag(), mList.get((Integer) v.getTag()).getName());
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mTextView;


        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.function_view_ico);
            mTextView = (TextView) itemView.findViewById(R.id.function_view_name);
        }
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, String label);
    }

    public List<FunctionViewItemElement> getDataList() {
        return mList;
    }

    public void setItemSelected(int position) {
        if (mList != null && position >= 0 && position < mList.size()) {
            resetState();
            mList.get(position).setSelected(true);
            notifyDataSetChanged();
        }
    }

    public void setItemUnSelected() {
        if (mList != null) {
            resetState();
            notifyDataSetChanged();
        }
    }

    private void resetState() {
        for (FunctionViewItemElement element : mList) {
            element.setSelected(false);
        }

    }

}
