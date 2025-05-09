package demo.xm.com.xmfunsdkdemo.ui.device.config.idr.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.lib.FunSDK;
import com.lib.SDKCONST;
import com.lib.sdk.bean.StringUtils;
import com.xm.activity.base.XMBasePresenter;

import java.util.ArrayList;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;

public class WeekSelectActivity extends DemoBaseActivity implements OnClickListener {
	private static String[] WEEKS = new String[] { FunSDK.TS("Sunday"), FunSDK.TS("Monday"), FunSDK.TS("Tuesday"),
			FunSDK.TS("Wednesday"), FunSDK.TS("Thursday"), FunSDK.TS("Friday"), FunSDK.TS("Saturday") };
	public static final int REQUEST_CODE = 0xff;
	public static final int REPEAT = 0;
	public static final int SINGLE = 1;
	public static final int AT_LEAST_ONE = 2;//至少选择一个
	private int mSelectType = REPEAT; // 0:重复；1一次
	private ListView mLvWeeks;
	private CommonAdapter mAdapter;
	private TextView mTvCancel;
	private TextView mTvSure;
	private RelativeLayout mButtomLayout;
	private RelativeLayout mLayout;
	private List<Bundle> mSelectedList;
	private int mWeekMask = 0;

	public static void actionStart(Activity from,int weekMask, int req) {
		Intent i = new Intent(from, WeekSelectActivity.class);
		i.putExtra("WeekMask", weekMask);
		i.putExtra("SelectType",REPEAT);
		from.startActivityForResult(i, req);
	}

	public static void actionStart(Activity from,int weekMask,int selectType, int req) {
		Intent i = new Intent(from, WeekSelectActivity.class);
		i.putExtra("WeekMask", weekMask);
		i.putExtra("SelectType",selectType);
		from.startActivityForResult(i, req);
	}
	/*
	 * (非 Javadoc) <p>Title: MyOnCreate</p> <p>Description: </p>
	 * 
	 * @param savedInstanceState
	 * 
	 * @see com.mobile.myeye.base.IBaseActivity#MyOnCreate(android.os.Bundle)
	 */

	@Override
	public XMBasePresenter getPresenter() {
		return null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_selectdate);
		initView();
		initData();
	}

	private void initView() {
		mButtomLayout = (RelativeLayout) findViewById(R.id.rl_week);
		Animation a = AnimationUtils.loadAnimation(this, R.anim.popshow_anim);
		mLayout = (RelativeLayout) findViewById(R.id.taskdate_rl);
		mLayout.setOnClickListener(this);
		mButtomLayout.setAnimation(a);
		mLvWeeks = (ListView) findViewById(R.id.lv_week);
		mTvCancel = (TextView) findViewById(R.id.tv_cancel);
		mTvSure = (TextView) findViewById(R.id.tv_sure);
		mTvCancel.setOnClickListener(this);
		mTvSure.setOnClickListener(this);
	}

	private void initData() {
		mWeekMask = getIntent().getIntExtra("WeekMask", 0);
		mSelectType = getIntent().getIntExtra("SelectType",REPEAT);
		mSelectedList = new ArrayList<Bundle>();
		for (int i = 0; i < WEEKS.length; ++i) {
			Bundle bundle = new Bundle();
			bundle.putString("name", WEEKS[i]);
			bundle.putBoolean("selected", ((mWeekMask >> i) & 0x01) == 1);
			mSelectedList.add(bundle);
		}
		mAdapter = new CommonAdapter<Bundle>(this, mSelectedList, R.layout.adapter_taskdateselect_mult_item) {

			@Override
			public void convert(ViewHolder holder, final Bundle data, final int position) {
				// TODO Auto-generated method stub
				boolean isSelected = data.getBoolean("selected", false);
				holder.setText(R.id.tv_week, data.getString("name"));
				holder.setImageResource(R.id.iv_switch, isSelected ? R.drawable.correct_sel : R.drawable.correct_nor);
				holder.setTag(R.id.iv_switch, "selected:" + position);
			}
		};
		mLvWeeks.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Bundle data = mSelectedList.get(position);
				if (mSelectType == AT_LEAST_ONE) {
					boolean isSelected = false;
					for (int i = 0; i < mSelectedList.size(); ++i) {
						isSelected = mSelectedList.get(i).getBoolean("selected");
						if (i != position && isSelected) {
							isSelected = mSelectedList.get(position).getBoolean("selected");
							ImageView iv = (ImageView) mLvWeeks.findViewWithTag("selected:" + position);
							if (null != iv) {
								iv.setImageResource(isSelected ? R.drawable.correct_nor : R.drawable.correct_sel);
							}
							data.putBoolean("selected", !isSelected);
							return;
						}
					}
					Toast.makeText(WeekSelectActivity.this, FunSDK.TS("AT_LEAST_ONE"), Toast.LENGTH_SHORT).show();
				}else if (mSelectType == SINGLE) {
					boolean isSelected = mSelectedList.get(position).getBoolean("selected");
					ImageView iv = (ImageView) mLvWeeks.findViewWithTag("selected:" + position);
					if (null != iv) {
						iv.setImageResource(isSelected ? R.drawable.correct_nor : R.drawable.correct_sel);
					}
					data.putBoolean("selected", !isSelected);
					for (int i = 0; i < mSelectedList.size(); i++) {
						if (i != position) {
							mSelectedList.get(i).putBoolean("selected", false);
						}
					}
					mAdapter.notifyDataSetChanged();
				}else {
					boolean isSelected = mSelectedList.get(position).getBoolean("selected");
					ImageView iv = (ImageView) mLvWeeks.findViewWithTag("selected:" + position);
					if (null != iv) {
						iv.setImageResource(isSelected ? R.drawable.correct_nor : R.drawable.correct_sel);
					}
					data.putBoolean("selected", !isSelected);
				}
			}
		});
		mLvWeeks.setAdapter(mAdapter);
	}

	/*
	 * (非 Javadoc) <p>Title: OnClicked</p> <p>Description: </p>
	 * 
	 * @param id
	 * 
	 * @see com.mobile.myeye.base.IBaseActivity#OnClicked(int)
	 */

	private void setDate() {
		mWeekMask = 0;
		for (int i = 0; i < mSelectedList.size(); i++) {
			if (mSelectedList.get(i).getBoolean("selected", false)) {
				mWeekMask |= (int) Math.pow(2, i);
				if (mSelectType == SINGLE) {
					break;
				}
			}
		}
		// if (mSelectType == SINGLE) {
		// mDate |= 128;
		// }
	}

	/*
	 * (非 Javadoc) <p>Title: OnFunSDKResult</p> <p>Description: </p>
	 * 
	 * @param msg
	 * 
	 * @param ex
	 * 
	 * @return
	 * 
	 * @see com.lib.IFunSDKResult#OnFunSDKResult(android.os.Message,
	 * com.lib.MsgContent)
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(Activity.RESULT_CANCELED);
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.taskdate_rl:
		case R.id.tv_cancel:
			setResult(Activity.RESULT_CANCELED);
			finish();
			break;
		case R.id.tv_sure:
			setDate();
			Intent intent = new Intent();
			intent.putExtra("WeekMask", mWeekMask);
			setResult(Activity.RESULT_OK, intent);
			finish();
			break;
		default:
			break;
		}
	}

	public static String getWeeks(int weekMask) {
		int i = 0;
		boolean isEveryDay = true;
		StringBuffer sb = new StringBuffer();
		do {
			if ((weekMask & 0x01) == 0x1) {
				sb.append(WEEKS[i]);
				sb.append(" ");
			} else {
				isEveryDay = false;
			}
			i++;
		} while ((weekMask = weekMask >> 1) != 0);
		if (isEveryDay && sb.length() > 0 && i == SDKCONST.NET_N_WEEKS) {
			return FunSDK.TS("every_day");
		}
		if (StringUtils.isStringNULL(sb.toString())) {
			return FunSDK.TS("Never");
		}
		return sb.toString();
	}
}
