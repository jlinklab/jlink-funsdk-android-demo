package demo.xm.com.xmfunsdkdemo.ui.device.alarm.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basic.G;
import com.lib.FunSDK;
import com.lib.SDKCONST;
import com.lib.sdk.bean.AlarmInfoBean;
import com.lib.sdk.bean.DevVolumeBean;
import com.lib.sdk.bean.IntelliAlertAlarmBean;
import com.lib.sdk.bean.LP4GLedParameterBean;
import com.lib.sdk.bean.VoiceTipBean;
import com.lib.sdk.bean.WhiteLightBean;
import com.manager.db.DevDataCenter;
import com.manager.device.idr.IdrDefine;
import com.utils.XUtils;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.listselectitem.extra.view.ExtraSpinner;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.listener.WhiteLightContract;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.presenter.WhiteLightPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.alarmconfig.view.DevAlarmSetActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.view.IntelligentVigilanceActivity;
import demo.xm.com.xmfunsdkdemo.ui.dialog.NumberPickDialog;
import demo.xm.com.xmfunsdkdemo.ui.entity.WorkModeBean;
import demo.xm.com.xmfunsdkdemo.utils.ParseTimeUtil;


/**
 * 声光报警:白光灯界面
 */
public class WhiteLightActivity extends BaseConfigActivity<WhiteLightPresenter> implements
		WhiteLightContract.IWhiteLightView,View.OnClickListener, SeekBar.OnSeekBarChangeListener, NumberPickDialog.TimeSettingListener {


	private XTitleBar titleBar;


	protected TextView mWhiteLightOpenTime;
	protected TextView mTvOpenTime;
	protected TextView mWhiteLightCloseTime;
	protected TextView mTvCloseTime;
	protected LinearLayout mTimeSettingLayout;
	protected LinearLayout mIntelligentModelLayout;
	private ListSelectItem mListIntelligentDuration;
	private ListSelectItem mListIntelligentSensitivity;
	private ListSelectItem mListColorWhiteLight;
	private ExtraSpinner<Integer> mSpIntelligentDuration;
	private ExtraSpinner<Integer> mSpIntelligentSensitivity;
	private ExtraSpinner<Integer> mSpSmartAlarmDuration;
	private ExtraSpinner<Long> mSpColorWhiteLight;
	private String[] durationArray;
	private String[] levelArray;
	private String[] mAlarmDurationArray;


	private String[] mWhiteLightSwitchArray;
	protected ListSelectItem mLisWhiteLightSwitch;
	protected ExtraSpinner mSpWhiteLightSwitch;
	protected ListSelectItem mLsiMusicSwitch;

	private ListSelectItem mLsiSmartAlarmSwitch;
	private ListSelectItem mLsiSmartAlarmDuration;
	private ListSelectItem mLsiSmartAlarmLight;
	private ListSelectItem mLsiSmartAlarmVoice;
	private ListSelectItem mLsiSmartAlarmRing;
	private ListSelectItem mLsiSmartAlarmBodyTrigger;
	private LinearLayout mLlSmartAlarm;
	private SeekBar mSeekBarVolume;

	private ListSelectItem mLsiDoubleLightSwitch;
	private ExtraSpinner<Integer> mSpDoubleLightSwitch;

	//庭院灯布局
	private ListSelectItem mLsiGardenLightSwitch;
	private LinearLayout mLlLightSeekbar;
	private SeekBar mLightSeekbar;
	private LinearLayout mLlSmartLayout;
	private ListSelectItem mLsiPirAlarm;
	private ListSelectItem mLsiSmartAlarm;


	private int mChnId = -1;

	private View mView;

	private NumberPickDialog mNumberPickDialog;

	@Override
	public WhiteLightPresenter getPresenter() {
		return new WhiteLightPresenter(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.white_light);
		initView();
		initData();
	}

	private void initView() {
		titleBar = findViewById(R.id.layoutTop);
		titleBar.setTitleText(getString(R.string.alarm_by_voice_light));
		titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
			@Override
			public void onLeftclick() {
				finish();
			}
		});
		titleBar.setBottomTip(getClass().getName());


		mView = findViewById(R.id.xm_root_layout);

		
		// 灯光控制开关
		mLisWhiteLightSwitch = findViewById(R.id.lsi_white_light_switch);
		mLisWhiteLightSwitch.setVisibility(View.VISIBLE);
		// 定时开始时间
		mWhiteLightOpenTime = findViewById(R.id.open_setting_text_white_light);
		mTvOpenTime = findViewById(R.id.open_setting_time);
		findViewById(R.id.setting_open_time_rl_white_light).setOnClickListener(this);
		findViewById(R.id.setting_close_time_rl_white_light).setOnClickListener(this);
		// 定时关闭时间
		mWhiteLightCloseTime = findViewById(R.id.close_setting_text_white_light);
		mTvCloseTime = findViewById(R.id.close_setting_time);
		mTimeSettingLayout = findViewById(R.id.time_setting);
		mIntelligentModelLayout = findViewById(R.id.intelligent_model_setting);
		mListIntelligentDuration = findViewById(R.id.lsi_intelligent_duration);
		mListIntelligentSensitivity = findViewById(R.id.lsi_intelligent_sensitivity);

		presenter.setSupportLightSwitch(getIntent().getBooleanExtra("supportLightSwitch",true));
		if (!presenter.isSupportLightSwitch()) {
			findViewById(R.id.ll_white_light).setVisibility(View.GONE);
		}

		mLlSmartAlarm = findViewById(R.id.ll_smart_alarm_set);
		mLsiSmartAlarmSwitch = findViewById(R.id.lsi_smart_alarm_switch);
		mLsiSmartAlarmDuration = findViewById(R.id.lsi_smart_alarm_duration);
		mLsiSmartAlarmLight = findViewById(R.id.lsi_smart_alarm_light);
		mLsiSmartAlarmVoice = findViewById(R.id.lsi_smart_alarm_voice);
		mLsiSmartAlarmRing = findViewById(R.id.lsi_smart_alarm_ring);
		mLsiSmartAlarmBodyTrigger = findViewById(R.id.lsi_body_trigger);

		mListColorWhiteLight = findViewById(R.id.lsi_expert_color_white_light);

		mLsiDoubleLightSwitch = findViewById(R.id.lsi_double_light_switch);

		//庭院灯布局
		mLsiGardenLightSwitch = findViewById(R.id.lsi_garden_light_switch);
		mLlLightSeekbar = findViewById(R.id.ll_light_seekbar);
		mLightSeekbar = findViewById(R.id.light_seekbar);
		mLlSmartLayout = findViewById(R.id.ll_smart_layout);
		mLsiPirAlarm = findViewById(R.id.lsi_pir_alarm);
		mLsiSmartAlarm = findViewById(R.id.lsi_smart_alarm);

		mNumberPickDialog = new NumberPickDialog();
		mNumberPickDialog.setTimeSettingListener(this);
	}


	private String devId;

	private void initData() {
		devId = getIntent().getStringExtra("devId");
		mChnId = getIntent().getIntExtra("chnId",-1);
		presenter.setDevId(devId);
		presenter.setChnId(mChnId);
		initWhiteLightSwitch();
		initDuration();
		initSensitivity();
		initColorWhiteLight();
		initSmartAlarmListener();

		initSmartAlarmDuration();
		resetView();
		presenter.getVoiceLightAlarmConfig();

	}


	@Override
	protected void onResume() {
		super.onResume();
		resetView();
		presenter.getVoiceLightAlarmConfig();
	}

	/**
	 * 报警时间
	 */
	private void initSmartAlarmDuration() {
		mAlarmDurationArray = new String[]{"5s", "10s", "15s", "20s"};
		mSpSmartAlarmDuration = mLsiSmartAlarmDuration.getExtraSpinner();
		mSpSmartAlarmDuration.initData(mAlarmDurationArray, new Integer[]{0, 1, 2, 3});
		mLsiSmartAlarmDuration.setOnClickListener(v ->
				mLsiSmartAlarmDuration.toggleExtraView(mView));
		mSpSmartAlarmDuration.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
			@Override
			public void onItemClick(int position, String key, Object value) {
				presenter.saveSmartAlarmDurationConfig(Integer.parseInt(
						mAlarmDurationArray[position].substring(0, mAlarmDurationArray[position].length() - 1)));

				mLsiSmartAlarmDuration.setRightText(key);
				mLsiSmartAlarmDuration.toggleExtraView(true, mView);
			}
		});
	}


	/**
	 * 智能警戒开关
	 */
	private void initSmartAlarmListener() {
		mLsiSmartAlarmSwitch.setOnClickListener(v -> {
			mLsiSmartAlarmSwitch.setSwitchState(mLsiSmartAlarmSwitch.getSwitchState() == SDKCONST.Switch.Open
					? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
			boolean isOpen = mLsiSmartAlarmSwitch.getSwitchState() == SDKCONST.Switch.Open;
			if (isOpen) {
				mLlSmartAlarm.setVisibility(View.VISIBLE);
			} else {
				mLlSmartAlarm.setVisibility(View.GONE);
			}
			presenter.saveSmartAlarmSwitchConfig(isOpen);

		});
		//庭院灯智能警戒开关
		mLsiSmartAlarm.setOnClickListener(v -> {
			mLsiSmartAlarm.setSwitchState(mLsiSmartAlarm.getSwitchState() == SDKCONST.Switch.Open
					? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
			presenter.saveSmartAlarmSwitchConfig(mLsiSmartAlarm.getSwitchState() == SDKCONST.Switch.Open);
		});

		mLsiSmartAlarmBodyTrigger.setOnClickListener(v -> turnToIntelligentActivity());
		mLsiPirAlarm.setOnClickListener(v -> turnToIntelligentActivity());

		mLsiSmartAlarmLight.setOnClickListener(v -> {
			mLsiSmartAlarmLight.setSwitchState(mLsiSmartAlarmLight.getSwitchState() == SDKCONST.Switch.Open
					? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
			presenter.saveSmartAlarmLightSwitchConfig(mLsiSmartAlarmLight.getSwitchState() == SDKCONST.Switch.Open);

		});
	}


	/**
	 * 跳转智能报警设置页
	 */
	private void turnToIntelligentActivity() {

		if (IdrDefine.isIDR(DevDataCenter.getInstance().getDevInfo(devId).getDevType())) {
			Intent intent = new Intent(WhiteLightActivity.this, DevAlarmSetActivity.class);
			intent.putExtra("devId", devId);
			startActivity(intent);
		} else {
			Intent intent = new Intent(WhiteLightActivity.this, IntelligentVigilanceActivity.class);
			intent.putExtra("devId", presenter.getDevId());
			startActivity(intent);
		}
	}

	public void resetView() {
		if (mLsiDoubleLightSwitch != null && mLsiDoubleLightSwitch.isExtraViewShow()) {
			mLsiDoubleLightSwitch.toggleExtraView(true);
		}
		if (mListIntelligentDuration != null && mListIntelligentDuration.isExtraViewShow()) {
			mListIntelligentDuration.toggleExtraView(true);
		}
		if (mLisWhiteLightSwitch != null && mLisWhiteLightSwitch.isExtraViewShow()) {
			mLisWhiteLightSwitch.toggleExtraView(true);
		}

		if (mListIntelligentSensitivity != null && mListIntelligentSensitivity.isExtraViewShow()) {
			mListIntelligentSensitivity.toggleExtraView(true);
		}
		if (mLsiSmartAlarmDuration != null && mLsiSmartAlarmDuration.isExtraViewShow()) {
			mLsiSmartAlarmDuration.toggleExtraView(true);
		}
		if (mLsiSmartAlarmVoice != null && mLsiSmartAlarmVoice.isExtraViewShow()) {
			mLsiSmartAlarmVoice.toggleExtraView(true);
		}
		if (mLsiSmartAlarmRing != null && mLsiSmartAlarmRing.isExtraViewShow()) {
			mLsiSmartAlarmRing.toggleExtraView(true);
		}
	}

	/**
	 * 灯光开关选项
	 */
	protected String[] getWhiteLightSwitchData() {
		if (FunSDK.GetDevAbility(devId, "OtherFunction/NotSupportAutoAndIntelligent") == 1) {
			//AOV设备隐藏常亮
			if (FunSDK.GetDevAbility(devId,"OtherFunction/AovMode") > 0) {
				return new String[]{FunSDK.TS("close")};
			} else {
				return new String[]{FunSDK.TS("open"), FunSDK.TS("close")};
			}
		} else {
			if (FunSDK.GetDevAbility(devId,"OtherFunction/AovMode") > 0) {
				return new String[]{FunSDK.TS("Auto_model"),
						FunSDK.TS("close"), FunSDK.TS("timing")};
			} else {
				return new String[]{FunSDK.TS("Auto_model"), FunSDK.TS("open"),
						FunSDK.TS("close"), FunSDK.TS("timing")};
			}
		}
	}

	/**
	 * 灯光开关选项
	 */
	protected Integer[] getWhiteLightSwitchIntValue() {
		if (FunSDK.GetDevAbility(devId, "OtherFunction/NotSupportAutoAndIntelligent") == 1) {
			//AOV设备隐藏常亮
			if (FunSDK.GetDevAbility(devId,"OtherFunction/AovMode") > 0) {
				return new Integer[]{2};
			} else {
				return new Integer[]{1, 2};
			}
		} else {
			if (FunSDK.GetDevAbility(devId,"OtherFunction/AovMode") > 0) {
				return new Integer[]{0, 2, 3};
			} else {
				return new Integer[]{0, 1, 2, 3};
			}
		}
	}


	/**
	 * 灯光开关选项布局
	 */
	private void initWhiteLightSwitch() {
		mWhiteLightSwitchArray = getWhiteLightSwitchData();
		mLisWhiteLightSwitch.setTip(XUtils.getLightViewTips(mWhiteLightSwitchArray));
		mSpWhiteLightSwitch = mLisWhiteLightSwitch.getExtraSpinner();
		if (FunSDK.GetDevAbility(devId,"OtherFunction/AovMode") > 0) {
			//AOV设备，灯光开关改为白光开关
			mLisWhiteLightSwitch.setTitle(FunSDK.TS("TR_White_Light_Switch"));
		}
		mSpWhiteLightSwitch.initData(mWhiteLightSwitchArray, getWhiteLightSwitchIntValue());
		mLisWhiteLightSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLisWhiteLightSwitch.toggleExtraView(mView);
			}
		});
		mSpWhiteLightSwitch.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
			@Override
			public void onItemClick(int position, String key, Object value) {
				mLisWhiteLightSwitch.toggleExtraView(true);
				WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
				if (null != mWhiteLight) {
					int keyValue = (int) value;
					switch (keyValue) {
						case 0:
							mTimeSettingLayout.setVisibility(View.GONE);
							mIntelligentModelLayout.setVisibility(View.GONE);
							mWhiteLight.setWorkMode("Auto");
							presenter.saveWhiteLight();
							break;
						case 1:
//                            注:设置开始和结束时间相同，且在定时模式下，为打开灯泡
							mTimeSettingLayout.setVisibility(View.GONE);
							mIntelligentModelLayout.setVisibility(View.GONE);
							mWhiteLight.setWorkMode("KeepOpen");
							presenter.saveWhiteLight();
							break;
						case 2:
							mTimeSettingLayout.setVisibility(View.GONE);
							mIntelligentModelLayout.setVisibility(View.GONE);
							mWhiteLight.setWorkMode("Close");
							presenter.saveWhiteLight();
							break;
						case 3:
							String openTime = ParseTimeUtil.parseTime(mWhiteLight.getWorkPeriod().getSHour(),
									mWhiteLight.getWorkPeriod().getSMinute());
							String closeTime = ParseTimeUtil.parseTime(mWhiteLight.getWorkPeriod().getEHour(),
									mWhiteLight.getWorkPeriod().getEMinute());
							mWhiteLightOpenTime.setText(openTime);
							mWhiteLightCloseTime.setText(closeTime);
							mWhiteLight.setWorkMode("Timing");
							mTimeSettingLayout.setVisibility(View.VISIBLE);
							mIntelligentModelLayout.setVisibility(View.GONE);
							presenter.saveWhiteLight();
							break;
						case 4:
							if (null != mWhiteLight.getMoveTrigLight()) {
								mWhiteLight.setWorkMode("Intelligent");
								mIntelligentModelLayout.setVisibility(View.VISIBLE);
								mTimeSettingLayout.setVisibility(View.GONE);
								int level = mWhiteLight.getMoveTrigLight().getLevel();
								int duration = mWhiteLight.getMoveTrigLight().getDuration();
								mSpIntelligentSensitivity.setValue((level - 1) / 2);
								mListIntelligentSensitivity.setRightText((CharSequence) mSpIntelligentSensitivity.getSelectedName());
								for (int i = 0; i < durationArray.length; i++) {
									if (Integer.parseInt(durationArray[i].substring(0, durationArray[i].length() - 1)) == duration) {
										mSpIntelligentDuration.setValue(i);
										mListIntelligentDuration.setRightText((CharSequence) mSpIntelligentDuration.getSelectedName());
									}
								}
								presenter.saveWhiteLight();
							}
							break;
						default:
							break;
					}
				}
				mLisWhiteLightSwitch.setRightText(key);

			}
		});


	}

	/**
	 * 持续亮灯时间
	 */
	private void initDuration() {
		durationArray = new String[]{"5s", "10s", "30s", "60s", "90s", "120s"};
		mSpIntelligentDuration = mListIntelligentDuration.getExtraSpinner();
		mSpIntelligentDuration.initData(durationArray, new Integer[]{0, 1, 2, 3, 4, 5});
		mListIntelligentDuration.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListIntelligentDuration.toggleExtraView(mView);
			}
		});
		mSpIntelligentDuration.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
			@Override
			public void onItemClick(int position, String key, Object value) {
				WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
				if (mWhiteLight != null && null != mWhiteLight.getMoveTrigLight()) {
					mWhiteLight.getMoveTrigLight().setDuration(Integer.parseInt(durationArray[position].substring(0, durationArray[position].length() - 1)));
					presenter.saveWhiteLight();
				} else {
					Toast.makeText(getApplicationContext(), "当前灯泡不支持该模式", Toast.LENGTH_SHORT).show();
				}
				mListIntelligentDuration.setRightText(key);
				mListIntelligentDuration.toggleExtraView(true, mView);
			}
		});
	}

	/**
	 * 灵敏度设置
	 */
	private void initSensitivity() {
		levelArray = new String[]{
				FunSDK.TS("Intelligent_level_Low"),
				FunSDK.TS("Intelligent_level_Middle"),
				FunSDK.TS("Intelligent_level_Height")
		};
		mSpIntelligentSensitivity = mListIntelligentSensitivity.getExtraSpinner();
		mSpIntelligentSensitivity.initData(levelArray, new Integer[]{0, 1, 2});
		mListIntelligentSensitivity.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListIntelligentSensitivity.toggleExtraView(mView);
			}
		});
		mSpIntelligentSensitivity.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
			@Override
			public void onItemClick(int position, String key, Object value) {
				WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
				if (mWhiteLight != null && null != mWhiteLight.getMoveTrigLight()) {
					mWhiteLight.getMoveTrigLight().setLevel(position * 2 + 1);/**注:位置0,1,2对应值1,3,5 ***/
					presenter.saveWhiteLight();
				} else {
					Toast.makeText(getApplicationContext(), "当前灯泡不支持该模式", Toast.LENGTH_SHORT).show();
				}

				mListIntelligentSensitivity.setRightText(key);
				mListIntelligentSensitivity.toggleExtraView(true, mView);
			}
		});
	}

	/**
	 * 设置灯光类型
	 */
	private void initColorWhiteLight() {
		mSpColorWhiteLight = mListColorWhiteLight.getExtraSpinner();
		if (FunSDK.GetDevAbility(devId, "OtherFunction/SupportSoftPhotosensitive") == 1) {
			mSpColorWhiteLight.initData(new String[]{FunSDK.TS("Auto_Color"),
							FunSDK.TS("Setting_Color"),
							FunSDK.TS("Setting_White_Black"),
							FunSDK.TS("WhiteLamp_Auto"),
							FunSDK.TS("IrLamp_Auto")},
					new Long[]{0L, 1L, 2L, 4L, 5L});
		} else {
			mSpColorWhiteLight.initData(new String[]{FunSDK.TS("Auto_Color"),
							FunSDK.TS("Setting_Color"),
							FunSDK.TS("Setting_White_Black")},
					new Long[]{0L, 1L, 2L});
		}

		mSpColorWhiteLight.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener<Integer>() {
			@Override
			public void onItemClick(int position, String key, Integer value) {
				presenter.saveDayNightColorConfig(G.getHexFromInt(value));
				mListColorWhiteLight.setRightText(key);
				mListColorWhiteLight.toggleExtraView(true, mView);
			}
		});

		mListColorWhiteLight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListColorWhiteLight.toggleExtraView(mView);
			}
		});
	}

	/**
	 * 设置灯光打开时间
	 */
	public boolean setTime(int hour, int min, boolean isOpenTime) {
		WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
		WhiteLightBean.WorkPeriod workPeriod = mWhiteLight.getWorkPeriod();
		if (workPeriod == null) {
			return false;
		}

		if (isOpenTime) {
			if (hour == workPeriod.getEHour()
					&& min == workPeriod.getEMinute()) {
				Toast.makeText(WhiteLightActivity.this, FunSDK.TS("Start_And_End_Time_Unable_Equal"), Toast.LENGTH_LONG).show();
				return false;
			}

			mWhiteLightOpenTime.setText(ParseTimeUtil.combineTime(hour) + ":" + ParseTimeUtil.combineTime(min));
			mWhiteLight.getWorkPeriod().setSHour(hour);
			mWhiteLight.getWorkPeriod().setSMinute(min);
			presenter.saveWhiteLight();
		} else {
			if (hour == workPeriod.getSHour()
					&& min == workPeriod.getSMinute()) {
				Toast.makeText(WhiteLightActivity.this, FunSDK.TS("Start_And_End_Time_Unable_Equal"), Toast.LENGTH_LONG).show();
				return false;
			}

			mWhiteLightCloseTime.setText(ParseTimeUtil.combineTime(hour) + ":" + ParseTimeUtil.combineTime(min));
			mWhiteLight.getWorkPeriod().setEHour(hour);
			mWhiteLight.getWorkPeriod().setEMinute(min);
			presenter.saveWhiteLight();
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.setting_open_time_rl_white_light: {
				String openTime = mWhiteLightOpenTime.getText().toString().trim();
				if (!mNumberPickDialog.isAdded()) {
					mNumberPickDialog.show(getSupportFragmentManager(), "mNumberPickDialog");
					mNumberPickDialog.updateTime(Integer.parseInt((openTime.substring(0, 2))), Integer.parseInt(openTime.substring(3)), true);
				}
				break;
			}
			case R.id.setting_close_time_rl_white_light: {
				String closeTime = mWhiteLightCloseTime.getText().toString().trim();
				if (!mNumberPickDialog.isAdded()) {
					mNumberPickDialog.show(getSupportFragmentManager(), "mNumberPickDialog");
					mNumberPickDialog.updateTime(Integer.parseInt((closeTime.substring(0, 2))), Integer.parseInt(closeTime.substring(3)), false);
				}
				break;
			}
			default:
				break;
		}
	}


	/**
	 * 报警铃声设置
	 */
	@Override
	public void initRingtone(final List<VoiceTipBean> mVoiceTipBeanList,final IntelliAlertAlarmBean mIntelliAlertAlarmBean,final String voiceType) {
		if (mView == null) {
			return;
		}
		mLsiSmartAlarmRing.setRightText(voiceType);
		mLsiSmartAlarmRing.setVisibility(View.VISIBLE);
		String[] name = new String[mVoiceTipBeanList.size()];
		Integer[] values = new Integer[mVoiceTipBeanList.size()];
		for (int i = 0; i < mVoiceTipBeanList.size(); i++) {
			VoiceTipBean bean = mVoiceTipBeanList.get(i);
			name[i] = bean.getVoiceText();
			values[i] = bean.getVoiceEnum();
		}
		mLsiSmartAlarmRing.setOnClickListener(v -> mLsiSmartAlarmRing.toggleExtraView(mView));
		ExtraSpinner<Integer> spinner = mLsiSmartAlarmRing.getExtraSpinner();
		if (spinner != null) {
			spinner.initData(name, values);
			spinner.setValue(mIntelliAlertAlarmBean.EventHandler.VoiceType);
			spinner.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
				@Override
				public void onItemClick(int position, String key, Object value) {
					presenter.saveIntelliAlertAlarmVoiceType((int) value);

					mLsiSmartAlarmRing.setRightText(key);
					mLsiSmartAlarmRing.toggleExtraView(true, mView);
				}
			});
		}
	}

	/**
	 * 报警音量设置
	 */
	@Override
	public void initVolume(DevVolumeBean mDevHornVolumeBean) {
		mLsiSmartAlarmVoice.setVisibility(View.VISIBLE);
		mSeekBarVolume = mLsiSmartAlarmVoice.getExtraSeekbar();
		mSeekBarVolume.setMax(100);
		mSeekBarVolume.setProgress(mDevHornVolumeBean.getLeftVolume());
		mSeekBarVolume.setOnSeekBarChangeListener(this);
		mLsiSmartAlarmVoice.setRightText(String.valueOf(mDevHornVolumeBean.getLeftVolume()));
		mLsiSmartAlarmVoice.setOnClickListener(v -> mLsiSmartAlarmVoice.toggleExtraView(mView));
	}


	/**
	 * 智能警戒配置
	 */
	@Override
	public void initSmartAlarmData(boolean isNvr,IntelliAlertAlarmBean mIntelliAlertAlarmBean,
								   boolean mSupportGardenLight,boolean mSupportSetAlarmLed,boolean isNoWBS) {
		findViewById(R.id.intelligent_alert_layout).setVisibility(View.VISIBLE);
		mLsiSmartAlarmSwitch.setSwitchState(mIntelliAlertAlarmBean.Enable ?
				SDKCONST.Switch.Open : SDKCONST.Switch.Close);
		if (mIntelliAlertAlarmBean.Enable) {
			mLlSmartAlarm.setVisibility(View.VISIBLE);
		} else {
			mLlSmartAlarm.setVisibility(View.GONE);
		}
		if (!isNvr || mSupportGardenLight) {
			//布局调整
			findViewById(R.id.garden_light_layout).setVisibility(View.VISIBLE);
			findViewById(R.id.ll_garden_light_view).setVisibility(mSupportGardenLight ? View.VISIBLE : View.GONE);
			mLsiSmartAlarmSwitch.setVisibility(View.GONE);
			mLlSmartAlarm.setVisibility(View.VISIBLE);
			mLsiSmartAlarmDuration.setShowTopLine(false);
			mLlSmartLayout.setVisibility(View.VISIBLE);
			mLsiSmartAlarm.setVisibility(View.VISIBLE);
			mLsiSmartAlarm.setSwitchState(mIntelliAlertAlarmBean.Enable ?
					SDKCONST.Switch.Open : SDKCONST.Switch.Close);
			if (mSupportSetAlarmLed) {
				mLsiSmartAlarmLight.setVisibility(View.VISIBLE);
				mLsiSmartAlarmLight.setSwitchState(mIntelliAlertAlarmBean.EventHandler.AlarmOutEnable ?
						SDKCONST.Switch.Open : SDKCONST.Switch.Close);
			}
		}
		for (int i = 0; i < mAlarmDurationArray.length; i++) {
			if (Integer.parseInt(mAlarmDurationArray[i].substring(0, mAlarmDurationArray[i].length() - 1))
					== mIntelliAlertAlarmBean.Duration) {
				if (mSpSmartAlarmDuration != null) {
					mSpSmartAlarmDuration.setValue(i);
				}
				if (mLsiSmartAlarmDuration != null) {
					mLsiSmartAlarmDuration.setRightText((CharSequence) mSpSmartAlarmDuration.getSelectedName());
				}
			}
		}
		if(!isNvr){
			//单品标题名称改为智能警戒
			mLsiSmartAlarmSwitch.setTitle(FunSDK.TS("Intelligent_Vigilance"));
		} else {
			if(isNoWBS){
				mLsiSmartAlarmSwitch.setTitle(FunSDK.TS("Intelligent_Vigilance"));
			}
		}

	}


	/**
	 * 灯光设置
	 */
	@Override
	public void initDoubleLightSwitch(WorkModeBean workModeBean, LP4GLedParameterBean mLp4GLedParameterBean) {
		mLsiDoubleLightSwitch.setVisibility(View.VISIBLE);
		boolean isSupportLPDoubleLight = FunSDK.GetDevAbility(devId, "OtherFunction/SupportLPDoubleLightAlert") > 0;
		String[] stringName;
		Integer[] values;
		if (isSupportLPDoubleLight) {
			if (workModeBean != null && workModeBean.getModeType() == 0) {
				//超级省电模式不显示双光警戒
				stringName = new String[]{FunSDK.TS("TR_Infrared_Light"),
						FunSDK.TS("TR_White_Light")};
				values = new Integer[]{1, 2};
			} else {
				stringName = new String[]{FunSDK.TS("TR_Infrared_Light"),
						FunSDK.TS("TR_White_Light"), FunSDK.TS("Double_Light_Vision")};
				values = new Integer[]{1, 2, 3};
			}
		} else {
			stringName = new String[]{
					FunSDK.TS("TR_Infrared_Light"),
					FunSDK.TS("TR_White_Light")};
			values = new Integer[]{1, 2};
		}
		mLsiDoubleLightSwitch.setTip(XUtils.getLightViewTips(stringName));
		mSpDoubleLightSwitch = mLsiDoubleLightSwitch.getExtraSpinner();
		if (mSpDoubleLightSwitch != null) {
			mSpDoubleLightSwitch.initData(stringName, values);
			mLsiDoubleLightSwitch.setOnClickListener(v -> mLsiDoubleLightSwitch.toggleExtraView(mView));
			mSpDoubleLightSwitch.setValue(mLp4GLedParameterBean.getType());
			if (mLp4GLedParameterBean.getType() == 1) {
				mLsiDoubleLightSwitch.setRightText(FunSDK.TS("TR_Infrared_Light"));
			} else if (mLp4GLedParameterBean.getType() == 2) {
				mLsiDoubleLightSwitch.setRightText(FunSDK.TS("TR_White_Light"));
			} else if (mLp4GLedParameterBean.getType() == 3) {
				mLsiDoubleLightSwitch.setRightText(FunSDK.TS("Double_Light_Vision"));
			}
			mSpDoubleLightSwitch.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener<Integer>() {
				@Override
				public void onItemClick(int position, String key, Integer value) {
					presenter.saveDoubleLightSwitch(value);

					mLsiDoubleLightSwitch.setRightText(key);
					mLsiDoubleLightSwitch.toggleExtraView(true, mView);
				}
			});
		}
	}


	/**
	 * 庭院灯照明开关
	 */

	@Override
	public void initGardenLightSwitch(final LP4GLedParameterBean mLp4GLedParameterBean,final boolean mSupportSetBrightness) {
		//庭院灯照明开关
		//type代表开关状态：等于2表示开，等于1表示关
		findViewById(R.id.garden_light_layout).setVisibility(View.VISIBLE);
		boolean isOpen = mLp4GLedParameterBean.getType() == 2;
		mLsiGardenLightSwitch.setSwitchState(isOpen ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
		if (mSupportSetBrightness) {
			mLlLightSeekbar.setVisibility(isOpen ? View.VISIBLE : View.GONE);
		} else {
			mLlLightSeekbar.setVisibility(View.GONE);
		}
		mLsiGardenLightSwitch.setOnClickListener((v) -> {
			mLsiGardenLightSwitch.setSwitchState(mLsiGardenLightSwitch.getSwitchState() == SDKCONST.Switch.Open
					? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
			boolean isOpenGardenLightSwitch = mLsiGardenLightSwitch.getSwitchState() == SDKCONST.Switch.Open;
			presenter.saveGardenLightSwitch(isOpenGardenLightSwitch);
			if (presenter.isSupportSetBrightness()) {
				mLlLightSeekbar.setVisibility(isOpenGardenLightSwitch ? View.VISIBLE : View.GONE);
			}
		});
		if (mSupportSetBrightness) {
			mLightSeekbar.setProgress(Math.max(mLp4GLedParameterBean.getBrightness() - 1, 0));
			mLightSeekbar.setOnSeekBarChangeListener(this);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (seekBar == mSeekBarVolume) {
			if (progress <= 0) {
				seekBar.setProgress(1);
				return;
			}
			mLsiSmartAlarmVoice.setRightText(String.valueOf(progress));
			presenter.saveDevHornVolume(progress);

		} else if (seekBar == mLightSeekbar) {
			presenter.saveBrightness(Math.min(progress + 1, 100));

		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (seekBar == mSeekBarVolume) {
			presenter.saveVolume();
		} else if (seekBar == mLightSeekbar) {
			presenter.saveGardenConfig();
		}
	}


	@Override
	public void onShowWaitDialog() {
		showWaitDialog();
	}

	@Override
	public void onHideWaitDialog() {
		hideWaitDialog();
	}

	/**
	 * 灯光颜色
	 */
	@Override
	public void showColorWhiteLight(long selectValue){
		mSpColorWhiteLight.setValue(selectValue);
		mListColorWhiteLight.setRightText((CharSequence) mSpColorWhiteLight.getSelectedName());
	}


	/**
	 *根据pir报警配置显示设置项
	 */
	@Override
	public void showPIRAlarmInfo(AlarmInfoBean mPIRAlarmInfoBean, WorkModeBean workModeBean,boolean mSupportGardenLight){
		mLsiPirAlarm.setVisibility(View.VISIBLE);
		if (mPIRAlarmInfoBean != null) {
			//支持庭院灯功能，标题不同
			if (mSupportGardenLight) {
				mLsiPirAlarm.setTitle(FunSDK.TS("TR_PIR_Detection"));
			} else {
				if (workModeBean != null) {
					int modeType = workModeBean.getModeType();
					if (modeType == 0) {
						mLsiPirAlarm.setTitle(FunSDK.TS("TR_Human_body_induction_alarm"));
					} else {
						if (workModeBean.getWorkStateNow() < 0 || workModeBean.getWorkStateNow() == 1) { // 在常电模式下如果不支持这个配置的设备，就直接隐藏或者是当前工作模式是在实时下隐藏
							mLsiPirAlarm.setVisibility(View.GONE);
						}

					}
				} else {
					mLsiPirAlarm.setTitle(FunSDK.TS("TR_Human_body_induction_alarm"));
				}

			}

			mLsiPirAlarm.setRightText(mPIRAlarmInfoBean.Enable
					? FunSDK.TS("TR_Open_Alarm") : FunSDK.TS("close"));
			if (mLsiPirAlarm.getVisibility() == View.VISIBLE) {
				//人形检测功能显示时，才控制智能检测显示
				mLsiSmartAlarm.setVisibility(mPIRAlarmInfoBean.Enable ? View.VISIBLE : View.GONE);
			}
		}
	}


	/**
	 * 灯光开关工作模式
	 */
	@Override
	public void showWorkMode(WhiteLightBean mWhiteLight){
		if (mWhiteLight.getWorkMode().equals("Auto")) {
			if (mSpWhiteLightSwitch != null) {
				mSpWhiteLightSwitch.setValue(0);
			}
			if (mLisWhiteLightSwitch != null) {
				mLisWhiteLightSwitch.setRightText((CharSequence) mSpWhiteLightSwitch.getSelectedName());
			}
		} else if (mWhiteLight.getWorkMode().equals("KeepOpen")) {
			if (mSpWhiteLightSwitch != null) {
				mSpWhiteLightSwitch.setValue(1);
			}
			if (mLisWhiteLightSwitch != null) {
				mLisWhiteLightSwitch.setRightText((CharSequence) mSpWhiteLightSwitch.getSelectedName());
			}
		} else if (mWhiteLight.getWorkMode().equals("Timing")) {
			if (mSpWhiteLightSwitch != null) {
				mSpWhiteLightSwitch.setValue(3);
			}
			if (mLisWhiteLightSwitch != null) {
				mLisWhiteLightSwitch.setRightText((CharSequence) mSpWhiteLightSwitch.getSelectedName());
			}
			if (mTimeSettingLayout != null) {
				mTimeSettingLayout.setVisibility(View.VISIBLE);
			}
			String openTime = ParseTimeUtil.parseTime(mWhiteLight.getWorkPeriod().getSHour(), mWhiteLight.getWorkPeriod().getSMinute());
			String closeTime = ParseTimeUtil.parseTime(mWhiteLight.getWorkPeriod().getEHour(), mWhiteLight.getWorkPeriod().getEMinute());
			if (mWhiteLightOpenTime != null) {
				mWhiteLightOpenTime.setText(openTime);
			}
			if (mWhiteLightCloseTime != null) {
				mWhiteLightCloseTime.setText(closeTime);
			}
		} else if (mWhiteLight.getWorkMode().equals("Close")) {
			if (mSpWhiteLightSwitch != null) {
				mSpWhiteLightSwitch.setValue(2);
			}
			if (mLisWhiteLightSwitch != null) {
				mLisWhiteLightSwitch.setRightText((CharSequence) mSpWhiteLightSwitch.getSelectedName());
			}
		} else if (mWhiteLight.getWorkMode().equals("Intelligent") && null != mWhiteLight.getMoveTrigLight()) {
			if (this instanceof DoubleLightActivity) {
				if (mSpWhiteLightSwitch != null) {
					mSpWhiteLightSwitch.setValue(4);
				}
				if (mLisWhiteLightSwitch != null) {
					mLisWhiteLightSwitch.setRightText((CharSequence) mSpWhiteLightSwitch.getSelectedName());
				}
				if (mIntelligentModelLayout != null) {
					mIntelligentModelLayout.setVisibility(View.VISIBLE);
				}
				int level = mWhiteLight.getMoveTrigLight().getLevel();
				int duration = mWhiteLight.getMoveTrigLight().getDuration();
				if (mSpIntelligentSensitivity != null) {
					mSpIntelligentSensitivity.setValue((level - 1) / 2);
				}
				if (mListIntelligentSensitivity != null) {
					mListIntelligentSensitivity.setRightText((CharSequence) mSpIntelligentSensitivity.getSelectedName());
				}
				for (int i = 0; i < durationArray.length; i++) {
					if (Integer.parseInt(durationArray[i].substring(0, durationArray[i].length() - 1)) == duration) {
						if (mSpIntelligentDuration != null) {
							mSpIntelligentDuration.setValue(i);
						}
						if (mListIntelligentDuration != null) {
							mListIntelligentDuration.setRightText((CharSequence) mSpIntelligentDuration.getSelectedName());
						}
					}
				}
			}

		}
	}


	/**
	 * pir报警标题
	 */
	public void showPirAlarmTitle(WorkModeBean workModeBean,boolean mSupportGardenLight){
		//支持庭院灯功能，标题不同
		if (mSupportGardenLight) {
			mLsiPirAlarm.setTitle(FunSDK.TS("TR_PIR_Detection"));
		} else {
			if (workModeBean != null) {
				int modeType = workModeBean.getModeType();
				if (modeType == 0) {
					mLsiPirAlarm.setTitle(FunSDK.TS("TR_Human_body_induction_alarm"));
				} else {
					// 如果不支持这个配置的设备，就直接隐藏或者是当前工作模式是在实时下隐藏
					if (workModeBean.getWorkStateNow() < 0 || workModeBean.getWorkStateNow() == 1) {
						mLsiPirAlarm.setVisibility(View.GONE);
					}
				}
			} else {
				mLsiPirAlarm.setTitle(FunSDK.TS("TR_Human_body_induction_alarm"));
			}

		}
	}

	@Override
	public Activity getActivity() {
		return WhiteLightActivity.this;
	}
}
