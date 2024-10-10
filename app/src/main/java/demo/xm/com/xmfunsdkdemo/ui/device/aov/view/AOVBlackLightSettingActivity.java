package demo.xm.com.xmfunsdkdemo.ui.device.aov.view;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.lib.FunSDK;
import com.lib.SDKCONST;
import com.lib.sdk.bean.CameraParamExBean;
import com.lib.sdk.bean.FbExtraStateCtrlBean;
import com.lib.sdk.bean.WhiteLightBean;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.NumberPicker;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.aov.listener.AOVBlackLightSettingContract;
import demo.xm.com.xmfunsdkdemo.ui.device.aov.presenter.AOVBlackLightSettingPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.widget.MySeekBar;


/**
 * AOV设备黑光灯配置
 */
public class AOVBlackLightSettingActivity extends BaseConfigActivity<AOVBlackLightSettingPresenter> implements
        AOVBlackLightSettingContract.IAOVBlackLightSettingView, View.OnClickListener{
    private ListSelectItem mLsiLightSwitch;
    private ListSelectItem mLsiAutoLight;
    private ListSelectItem mLsiTimeLight;
    private ListSelectItem mLsiKeepOn;
    private ConstraintLayout mClBright;
    private ConstraintLayout mClSensitive;
    private ConstraintLayout mClLightMode;
    private TextView mTvBright;
    private MySeekBar mSbBright;
    private TextView mTvSensitive;
    private MySeekBar mSbSensitive;
    private ConstraintLayout mClTime;
    private TextView mTvStartTime;
    private TextView mTvEndTime;
    private RelativeLayout mBottomLayout;
    private RelativeLayout mBottomTimePickerLayout;
    private NumberPicker mHourNumPicker;
    private NumberPicker mMinNumPicker;
    private TextView tvCancel;
    private TextView tvConfirm;
    private TextView tvErrorTip;
    private TextView tvTimeType;
    private ListSelectItem mLisIndicatorLight; // 指示灯开关
    private ListSelectItem mMicroLight; // 微光补灯
    private View backgroundView;

    private String[] mSensitiveLevel = new String[]{
            FunSDK.TS("TR_PIR_lowest"),
            FunSDK.TS("TR_PIR_Lower"),
            FunSDK.TS("TR_PIR_Medium"),
            FunSDK.TS("TR_PIR_Higher"),
            FunSDK.TS("TR_PIR_Hightext")};
    private static final int MAX_PRESS_TO_LOG_COUNT = 10;
    private long pressTime = 0;
    private int pressCount = 0;

    private String devId;


    @Override
    public AOVBlackLightSettingPresenter getPresenter() {
        return new AOVBlackLightSettingPresenter(this);
    }

    @Override
    public void onShowWaitDialog() {
        showWaitDialog();
    }

    @Override
    public void onHideWaitDialog() {
        hideWaitDialog();
    }

    @Override
    public void showIndicatorLightState(int state) {
        mLisIndicatorLight.setSwitchState(state);
    }

    @Override
    public void showMicroLight(int state) {
        mMicroLight.setSwitchState(state);
    }

    @Override
    public void showSensitive(int softLedThr) {
        switch (softLedThr) {
            case 1:
                mSbSensitive.setProgress(0);
                break;
            case 2:
                mSbSensitive.setProgress(20);
                break;
            case 3:
                mSbSensitive.setProgress(40);
                break;
            case 4:
                mSbSensitive.setProgress(60);
                break;
            case 5:
                mSbSensitive.setProgress(80);
                break;
        }
        mTvSensitive.setText(FunSDK.TS("Intelligent_sensitivity") + " (" + mSensitiveLevel[softLedThr - 1] + ")");

    }


    @Override
    public Activity getActivity() {
        return AOVBlackLightSettingActivity.this;
    }

    /**
     * 设置开始时间和关闭时间，并在页面上显示
     */
    public void setTime() {
        String hourStr = "";
        String minStr = "";
        int hour = mHourNumPicker.getValue();
        int min = mMinNumPicker.getValue();
        if (String.valueOf(hour).length() < 2) {
            hourStr = "0" + String.valueOf(hour);
        } else {
            hourStr = String.valueOf(hour);
        }
        if (String.valueOf(min).length() < 2) {
            minStr = "0" + String.valueOf(min);
        } else {
            minStr = String.valueOf(min);
        }
        WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
        if (presenter.getTimeSelect() == 1) {
            if (hour == mWhiteLight.getWorkPeriod().getEHour()
                    && min == mWhiteLight.getWorkPeriod().getEMinute()) {
//                tvErrorTip.setText(FunSDK.TS("TR_Alarm_Period_Repeat_Time"));
//                tvErrorTip.setVisibility(View.VISIBLE);
                Toast.makeText(AOVBlackLightSettingActivity.this, FunSDK.TS("Start_And_End_Time_Unable_Equal"), Toast.LENGTH_SHORT).show();
                hideBottomLayout();
                return;
            }
            mTvStartTime.setText(FunSDK.TS("start_time") + ":" + "  " + hourStr + ":" + minStr);
            presenter.saveWorkPeriod(hour, min,true);

        } else if (presenter.getTimeSelect() == 2) {
            if (hour == mWhiteLight.getWorkPeriod().getSHour()
                    && min == mWhiteLight.getWorkPeriod().getSMinute()) {
//                tvErrorTip.setText(FunSDK.TS("TR_Alarm_Period_Repeat_Time"));
//                tvErrorTip.setVisibility(View.VISIBLE);
                Toast.makeText(AOVBlackLightSettingActivity.this, FunSDK.TS("Start_And_End_Time_Unable_Equal"), Toast.LENGTH_SHORT).show();
                hideBottomLayout();
                return;
            }
            mTvEndTime.setText(FunSDK.TS("end_time") + ":" + "  " + hourStr + ":" + minStr);
            presenter.saveWorkPeriod(hour, min,false);

        }
        hideBottomLayout();
    }


    public void updateView(WhiteLightBean mWhiteLight,boolean mSupportSetBrightness,boolean mSoftLedThr) {
        mClBright.setVisibility(mSupportSetBrightness ? View.VISIBLE : View.GONE);
        mSbBright.setProgress(Math.max(0, mWhiteLight.getBrightness() - 1));
        mTvBright.setText(FunSDK.TS("Bright") + " (" + mWhiteLight.getBrightness() + "%)");
        if ("Auto".equals(mWhiteLight.getWorkMode())) {
            //自动
            mClSensitive.setVisibility(mSoftLedThr ? View.VISIBLE : View.GONE);
            mClTime.setVisibility(View.GONE);
            mClLightMode.setVisibility(View.VISIBLE);
            mLsiLightSwitch.setSwitchState(SDKCONST.Switch.Open);
            mLsiAutoLight.setRightImage(SDKCONST.Switch.Open);
            mLsiTimeLight.setRightImage(SDKCONST.Switch.Close);
            mLsiKeepOn.setRightImage(SDKCONST.Switch.Close);
        } else if ("Timing".equals(mWhiteLight.getWorkMode())) {
            //定时
            mClSensitive.setVisibility(View.GONE);
            mClTime.setVisibility(View.VISIBLE);
            mClLightMode.setVisibility(View.VISIBLE);
            mLsiLightSwitch.setSwitchState(SDKCONST.Switch.Open);
            mLsiAutoLight.setRightImage(SDKCONST.Switch.Close);
            mLsiTimeLight.setRightImage(SDKCONST.Switch.Open);
            mLsiKeepOn.setRightImage(SDKCONST.Switch.Close);
        } else if ("KeepOpen".equals(mWhiteLight.getWorkMode())) {
            //常亮
            mClSensitive.setVisibility(View.GONE);
            mClTime.setVisibility(View.GONE);
            mClLightMode.setVisibility(View.VISIBLE);
            mLsiLightSwitch.setSwitchState(SDKCONST.Switch.Open);
            mLsiAutoLight.setRightImage(SDKCONST.Switch.Close);
            mLsiTimeLight.setRightImage(SDKCONST.Switch.Close);
            mLsiKeepOn.setRightImage(SDKCONST.Switch.Open);
        } else {
            //关闭
            mClBright.setVisibility(View.GONE);
            mClLightMode.setVisibility(View.GONE);
            mLsiLightSwitch.setSwitchState(SDKCONST.Switch.Close);
        }
        int sHour = mWhiteLight.getWorkPeriod().getSHour();
        int sMinute = mWhiteLight.getWorkPeriod().getSMinute();
        int eHour = mWhiteLight.getWorkPeriod().getEHour();
        int eMinute = mWhiteLight.getWorkPeriod().getEMinute();
        mTvStartTime.setText(FunSDK.TS("start_time") + ":  " + (sHour > 9 ? sHour : "0" + sHour) + ":" + (sMinute > 9 ? sMinute : "0" + sMinute));
        mTvEndTime.setText(FunSDK.TS("end_time") + ":  " + (eHour > 9 ? eHour : "0" + eHour) + ":" + (eMinute > 9 ? eMinute : "0" + eMinute));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_aov_black_light_setting);
        devId = getIntent().getStringExtra("devId");
        presenter.setDevId(devId);
        initView();
        initBottomView();
        initListener();
        initData();
        initNumPicker();
    }

    private void initData() {
        onShowWaitDialog();
        presenter.getWhiteLight();
        presenter.checkIsSupportSoftLedThr();
        presenter.checkIsSupportStatusLed();
        presenter.checkIsSupportMicroFillLight();
    }

    @Override
    public void showMicroFillLight(boolean isSupport) {
        if(isSupport){
            mMicroLight.setVisibility(View.VISIBLE);
        }else {
            mMicroLight.setVisibility(View.GONE);
        }
    }
    @Override
    public void showIndicator(boolean isSupport){
        if(isSupport){
            mLisIndicatorLight.setVisibility(View.VISIBLE);
        } else {
            mLisIndicatorLight.setVisibility(View.GONE);
        }
    }


    private void initListener() {
        mLsiLightSwitch.setOnClickListener(v -> {
            WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
            if (mWhiteLight != null) {
                boolean open = mLsiLightSwitch.getSwitchState() == SDKCONST.Switch.Open;
                if (open) {
                    //关闭
                    mClBright.setVisibility(View.GONE);
                    mClLightMode.setVisibility(View.GONE);
                    mLsiLightSwitch.setSwitchState(SDKCONST.Switch.Close);
                    presenter.saveWorkMode("Close");
                } else {
                    //打开，默认模式设置为自动
                    mClBright.setVisibility(presenter.isSupportSetBrightness() ? View.VISIBLE : View.GONE);
                    mClLightMode.setVisibility(View.VISIBLE);
                    mClSensitive.setVisibility(presenter.isSoftLedThr() ? View.VISIBLE : View.GONE);
                    mClTime.setVisibility(View.GONE);
                    mLsiLightSwitch.setSwitchState(SDKCONST.Switch.Open);
                    mLsiAutoLight.setRightImage(SDKCONST.Switch.Open);
                    mLsiTimeLight.setRightImage(SDKCONST.Switch.Close);
                    mLsiKeepOn.setRightImage(SDKCONST.Switch.Close);
                    presenter.saveWorkMode("Auto");
                }
                
            }
        });
        mLsiAutoLight.setOnClickListener(v -> {
            //自动
            WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
            if (mWhiteLight != null) {
                mClBright.setVisibility(presenter.isSupportSetBrightness() ? View.VISIBLE : View.GONE);
                mClLightMode.setVisibility(View.VISIBLE);
                mClSensitive.setVisibility(presenter.isSoftLedThr() ? View.VISIBLE : View.GONE);
                mClTime.setVisibility(View.GONE);
                mLsiLightSwitch.setSwitchState(SDKCONST.Switch.Open);
                mLsiAutoLight.setRightImage(SDKCONST.Switch.Open);
                mLsiTimeLight.setRightImage(SDKCONST.Switch.Close);
                mLsiKeepOn.setRightImage(SDKCONST.Switch.Close);
                presenter.saveWorkMode("Auto");
            }
        });
        mLsiTimeLight.setOnClickListener(v -> {
            //定时
            WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
            if (mWhiteLight != null) {
                mClBright.setVisibility(presenter.isSupportSetBrightness() ? View.VISIBLE : View.GONE);
                mClLightMode.setVisibility(View.VISIBLE);
                mClSensitive.setVisibility(View.GONE);
                mClTime.setVisibility(View.VISIBLE);
                mLsiLightSwitch.setSwitchState(SDKCONST.Switch.Open);
                mLsiAutoLight.setRightImage(SDKCONST.Switch.Close);
                mLsiTimeLight.setRightImage(SDKCONST.Switch.Open);
                mLsiKeepOn.setRightImage(SDKCONST.Switch.Close);
                presenter.saveWorkMode("Timing");

            }
        });
        mLsiKeepOn.setOnClickListener(v -> {
            //常亮
            WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
            if (mWhiteLight != null) {
                mClBright.setVisibility(presenter.isSupportSetBrightness() ? View.VISIBLE : View.GONE);
                mClLightMode.setVisibility(View.VISIBLE);
                mClSensitive.setVisibility(View.GONE);
                mClTime.setVisibility(View.GONE);
                mLsiLightSwitch.setSwitchState(SDKCONST.Switch.Open);
                mLsiAutoLight.setRightImage(SDKCONST.Switch.Close);
                mLsiTimeLight.setRightImage(SDKCONST.Switch.Close);
                mLsiKeepOn.setRightImage(SDKCONST.Switch.Open);
                presenter.saveWorkMode("KeepOpen");

            }
        });
        mSbBright.setMySeekBarOnSeekBarChangeListener(new MySeekBar.MySeekBarOnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
            }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
                    if (mWhiteLight != null) {
                        int brightness = Math.min(100, seekBar.getProgress() + 1);
                        presenter.saveBrightness(brightness);
                        mTvBright.setText(FunSDK.TS("Bright") + " (" + brightness + "%)");
                    }
            }
            });
        mSbSensitive.setMySeekBarOnSeekBarChangeListener(new MySeekBar.MySeekBarOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                CameraParamExBean mCameraParamEx = presenter.getCameraParamExBean();
                if (mCameraParamEx != null) {
                    int process = seekBar.getProgress();
                    if (process >= 0 && process < 10) {
                        presenter.saveSensitive(1);
                        mSbSensitive.setProgress(0);
                    } else if (process >= 10 && process < 30) {
                        presenter.saveSensitive(2);
                        mSbSensitive.setProgress(20);
                    } else if (process >= 30 && process < 50) {
                        presenter.saveSensitive(3);
                        mSbSensitive.setProgress(40);
                    } else if (process >= 50 && process < 70) {
                        presenter.saveSensitive(4);
                        mSbSensitive.setProgress(60);
                    } else {
                        presenter.saveSensitive(5);
                        mSbSensitive.setProgress(80);
                    }
                    mTvSensitive.setText(FunSDK.TS("Intelligent_sensitivity") + " (" + mSensitiveLevel[mCameraParamEx.SoftLedThr - 1] + ")");
                }
            }
        });
        mTvStartTime.setOnClickListener(this);
        mTvEndTime.setOnClickListener(this);
        mLisIndicatorLight.setOnClickListener(this);
        mMicroLight.setOnClickListener(this);
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(FunSDK.TS("TR_Light_Settings"));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        mLsiLightSwitch = findViewById(R.id.lsi_light_switch);
        mLsiAutoLight = findViewById(R.id.lsi_auto_light);
        mLsiTimeLight = findViewById(R.id.lsi_time_light);
        mLsiKeepOn = findViewById(R.id.lsi_keep_on);
        mClLightMode = findViewById(R.id.cl_light_mode);
        mLisIndicatorLight = findViewById(R.id.lis_indicator_light);
        mMicroLight = findViewById(R.id.lis_Micro_light);
        mClBright = findViewById(R.id.cl_bright);
        mSbBright = findViewById(R.id.seekbar_bright);
        mSbBright.setLeftText("1%");
        mSbBright.setRightText("100%");
        mSbBright.setMax(99);
        mSbBright.setTopTipUnit("%");
        mSbBright.setSeekBarIncreaseScope(1);
        mTvBright = findViewById(R.id.tv_bright);
        mClSensitive = findViewById(R.id.cl_sensitive);
        mSbSensitive = findViewById(R.id.seekbar_sensitivity);
        mSbSensitive.setLeftText(FunSDK.TS("TR_PIR_lowest"));
        mSbSensitive.setRightText(FunSDK.TS("TR_PIR_Hightext"));
        mSbSensitive.setScreenBreathStyle0(true);
        mSbSensitive.setSetPir(true);
        mSbSensitive.setMax(80);
        mTvSensitive = findViewById(R.id.tv_sensitive);
        mClTime = findViewById(R.id.cl_time);
        mTvStartTime = findViewById(R.id.tv_start_time);
        mTvEndTime = findViewById(R.id.tv_end_time);
        mBottomLayout = findViewById(R.id.alarm_setting_buttom_timepick);
        mBottomLayout.setOnClickListener(this);
        mBottomTimePickerLayout = findViewById(R.id.timepicker_rl);
        mBottomTimePickerLayout.setOnClickListener(this);
        findViewById(R.id.xm_root_layout).setOnClickListener(view -> {
            if (System.currentTimeMillis() - pressTime > 5000) {
                pressTime = System.currentTimeMillis();
                pressCount = 1;
            } else {
                pressCount++;
                if (pressCount == MAX_PRESS_TO_LOG_COUNT) {
                    mLsiKeepOn.setVisibility(View.VISIBLE);
                    pressCount = 0;
                }
            }
        });
    }

    private void initBottomView() {
        mHourNumPicker = findViewById(R.id.numpicker_hour);
        mMinNumPicker = findViewById(R.id.numpicker_min);
        tvCancel = findViewById(R.id.tv_cancel);
        tvConfirm = findViewById(R.id.tv_sure);
        tvErrorTip = findViewById(R.id.error_tip);
        tvTimeType = findViewById(R.id.tv_selected);
        backgroundView = findViewById(R.id.background_view);
        mHourNumPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        tvConfirm.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        backgroundView.setOnClickListener(this);
    }

    private void initNumPicker() {
        String[] hour = new String[24];
        for (int i = 0; i < hour.length; i++) {
            if (i < 10) {
                hour[i] = "0" + i;
            } else {
                hour[i] = "" + i;
            }
        }
        String[] min = new String[60];
        for (int i = 0; i < min.length; i++) {
            if (i < 10) {
                min[i] = "0" + i;
            } else {
                min[i] = "" + i;
            }
        }

        mHourNumPicker.setMaxValue(hour.length - 1);
        mHourNumPicker.setMinValue(0);
        mHourNumPicker.setDisplayedValues(hour);
        mHourNumPicker.setValue(presenter.getHours());
        mMinNumPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mMinNumPicker.setMaxValue(min.length - 1);
        mMinNumPicker.setMinValue(0);
        mMinNumPicker.setDisplayedValues(min);
        mMinNumPicker.setValue(presenter.getMinute());
    }

    private void showBottomLayout() {
        tvTimeType.setText(presenter.getTimeSelect() == 1 ? FunSDK.TS("TR_Alarm_Period_Select_Start_Time") : FunSDK.TS("TR_Alarm_Period_Select_End_Time"));
        tvErrorTip.setVisibility(View.INVISIBLE);
        if (mBottomLayout.getVisibility() == View.VISIBLE) {
            mBottomLayout.setVisibility(View.INVISIBLE);
        } else {
            mBottomLayout.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.popshow_anim);
            mBottomLayout.setAnimation(animation);
        }
    }

    private void hideBottomLayout() {
        if (mBottomLayout.getVisibility() == View.VISIBLE) {
            mBottomLayout.setVisibility(View.INVISIBLE);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.pophidden_anim);
            mBottomLayout.setAnimation(animation);
        }

    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
        int minute = 0;
        int hours = 0;
        if (mWhiteLight != null) {
            minute = mWhiteLight.getWorkPeriod().getSMinute();
            hours = mWhiteLight.getWorkPeriod().getSHour();
        }
        switch (id) {
            case R.id.tv_start_time:
                if (mWhiteLight != null) {
                    presenter.setTimeData(1,hours,minute);
                    UpdateTimeUI(minute, hours);
                }
                break;
            case R.id.tv_end_time:
                if (mWhiteLight != null) {
                    presenter.setTimeData(2,hours,minute);
                    UpdateTimeUI(minute, hours);
                }
                break;
            case R.id.tv_cancel:
            case R.id.background_view:
                hideBottomLayout();
                break;
            case R.id.tv_sure:
                setTime();
                break;
            case R.id.lis_indicator_light:
                FbExtraStateCtrlBean mFbExtraStateInfo = presenter.getFbExtraStateInfo();
                if (mFbExtraStateInfo != null) {
                    mLisIndicatorLight.setSwitchState(mLisIndicatorLight.getSwitchState() == SDKCONST.Switch.Open
                            ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
                    presenter.saveIndicatorLight(mLisIndicatorLight.getSwitchState());
                }
                break;
            case R.id.lis_Micro_light:
                CameraParamExBean mCameraParamEx = presenter.getCameraParamExBean();
                if(mCameraParamEx != null && mCameraParamEx.MicroFillLight!= null){
                    mMicroLight.setSwitchState(mMicroLight.getSwitchState() == SDKCONST.Switch.Open
                            ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
                    presenter.saveMicroLight(mMicroLight.getSwitchState());
                }
                break;
            default:
                break;
        }
    }





    public void UpdateTimeUI(int min, int hours) {
        mMinNumPicker.setValue(min);
        mHourNumPicker.setValue(hours);
        showBottomLayout();
    }
}
