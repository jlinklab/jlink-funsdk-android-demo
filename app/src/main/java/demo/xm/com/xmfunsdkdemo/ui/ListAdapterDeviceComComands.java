package demo.xm.com.xmfunsdkdemo.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lib.sdk.bean.alarm.AlarmInfo;

import demo.xm.com.xmfunsdkdemo.R;


public class ListAdapterDeviceComComands extends BaseAdapter {
	
	public static class ComCommand {
		boolean isSend;	// 是发送的命令还是接收到的数据 (Determine whether the command is sent or the data is received)
		String content;	// 内容
		
		public ComCommand(boolean send, String cmd) {
			this.isSend = send;
			this.content = cmd;
		}
	}
	
	private Context mContext = null;
	private LayoutInflater mInflater;
	private List<ComCommand> mCommands = new ArrayList<ComCommand>();
	
	
	public ListAdapterDeviceComComands(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		
	}
	
	
	public AlarmInfo getAlarmInfo(int position) {
		return (AlarmInfo)getItem(position);
	}
	
	public void updateCommand(ComCommand cmd) {
		mCommands.add(cmd);
		this.notifyDataSetChanged();
	}
	
	@Override
	public Object getItem(int position) {
		if ( position >= 0 && position < mCommands.size() ) {
			return mCommands.get(position);
		}
		return null;
	}

	@Override
	public int getCount() {
		return mCommands.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position,
			View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_device_com_list_item,
					null);
			
			holder = new ViewHolder();
			holder.txtCommand = (TextView) convertView
					.findViewById(R.id.txtCommand);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		ComCommand cmd = mCommands.get(position);
		
		holder.txtCommand.setText("[demo]# " + cmd.content);
		
		return convertView;
	}
	
	private class ViewHolder {
		TextView txtCommand;
	}

}
