package demo.xm.com.xmfunsdkdemo.ui.device.record.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xm.ui.widget.XMRecordView;

import java.util.List;
import java.util.Map;

import demo.xm.com.xmfunsdkdemo.R;


/**
 * 
 * HorizontalListViewDemo MyAdapter.java
 * 
 * @author huangwanshui TODO 2015-1-13
 */
public class RecordTimeAxisAdapter extends RecyclerView.Adapter<RecordTimeAxisAdapter.ViewHolder>
		implements OnItemClickListener, OnItemLongClickListener {
	private LayoutInflater mInflater;
	private Context mContext;
	private List<Map<String, Object>> mList;
	private int mWidth;
	private int mnCount;
	private int mTimeUnit;// 单位 分钟

	public RecordTimeAxisAdapter(Context context, List<Map<String, Object>> list, int width, int count, int timeunit) {
		this.mContext = context;
		this.mList = list;
		this.mWidth = width;
		this.mnCount = count > 0 ? count : 4;
		this.mTimeUnit = timeunit;
		this.mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return null == mList ? 0 : mList.size();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int position) {
		View view = mInflater.inflate(R.layout.match_league_round_item, viewGroup, false);
		final ViewHolder holder = new ViewHolder(view, this, this);
		holder.recordLl = (LinearLayout) view.findViewById(R.id.match_league_roung_item_ll);
		holder.recordView = (XMRecordView) view.findViewById(R.id.imageView);
		holder.timeTv = (TextView) view.findViewById(R.id.textView);
		LayoutParams lp = (LayoutParams) holder.recordLl.getLayoutParams();
		lp.width = mWidth / mnCount;
		return holder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holderView, int position) {
		// TODO Auto-generated method stub
		if ((position < (mnCount / 2)) || (position >= (getItemCount() - (mnCount / 2)))) {
			holderView.recordView.setShow(false);
			holderView.recordView.requestLayout();
			return;
		}
		if (holderView.recordView.getTimeUnit() != mTimeUnit * 60) {
			holderView.recordView.setTimeUnit(mTimeUnit * 60);
		}
		holderView.recordView.setData((char[][]) mList.get(position).get("data"));
		holderView.recordView.setShowTime((String) mList.get(position).get("time"));
		holderView.recordView.requestLayout();
		holderView.recordView.setShow(true);
		holderView.recordView.setLastTime((position + 1 == (getItemCount() - (mnCount / 2))));
	}

	public static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener, OnLongClickListener {
		private LinearLayout recordLl;
		private XMRecordView recordView;
		private TextView timeTv;
		private OnItemClickListener mItemClickLs;
		private OnItemLongClickListener mItemLongClickLs;

		public ViewHolder(View itemView, OnItemClickListener itemClickls, OnItemLongClickListener itemLongClickls) {
			super(itemView);
			this.mItemClickLs = itemClickls;
			this.mItemLongClickLs = itemLongClickls;
			itemView.setOnClickListener(this);
			itemView.setOnLongClickListener(this);
		}

		@SuppressWarnings("deprecation")
		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			if (null != mItemLongClickLs) {
				mItemLongClickLs.onItemLongClick(null, v, getPosition(), getItemId());
			}
			return false;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (null != mItemClickLs) {
				mItemClickLs.onItemClick(null, v, getPosition(), getItemId());
			}
		}
	}

	public void setData(List<Map<String, Object>> list, int count, int timeUnit) {
		this.mnCount = count > 0 ? count : 4;
		this.mTimeUnit = timeUnit;
		this.mList = list;
		notifyDataSetChanged();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if (null != mOnItemLongClickLs) {
			mOnItemLongClickLs.onItemLongClick(parent, view, position, id);
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if (null != mOnItemClickLs) {
			mOnItemClickLs.onItemClick(parent, view, position, id);
		}
	}

	private OnItemLongClickListener mOnItemLongClickLs;

	public void setOnItmLongClickListener(OnItemLongClickListener onItemLongClickLs) {
		this.mOnItemLongClickLs = onItemLongClickLs;
	}

	private OnItemClickListener mOnItemClickLs;

	public void setOnItemClickListener(OnItemClickListener onItemClickLs) {
		this.mOnItemClickLs = onItemClickLs;
	}
}
