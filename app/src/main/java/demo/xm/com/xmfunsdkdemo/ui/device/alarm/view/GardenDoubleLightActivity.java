package demo.xm.com.xmfunsdkdemo.ui.device.alarm.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basic.G;
import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.WhiteLightBean;
import com.utils.XUtils;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.listselectitem.extra.view.ExtraSpinner;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.listener.GardenDoubleLightContract;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.presenter.GardenDoubleLightPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.dialog.NumberPickDialog;
import demo.xm.com.xmfunsdkdemo.utils.ParseTimeUtil;

public class GardenDoubleLightActivity extends BaseConfigActivity<GardenDoubleLightPresenter> implements
        GardenDoubleLightContract.IGardenDoubleLightView,View.OnClickListener, NumberPickDialog.TimeSettingListener{


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
    private ExtraSpinner<Integer> mSpColorWhiteLight;
    private String[] durationArray;
    private String[] levelArray;

    protected ListSelectItem mLisWhiteLightSwitch;
    protected ExtraSpinner mSpWhiteLightSwitch;
    private String[] mWhiteLightSwitchArray;




    private View mView;

    private String devId;

    private int mChnId = -1;

    private NumberPickDialog mNumberPickDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.white_light);
        initView();
        initData();
    }

    @Override
    public GardenDoubleLightPresenter getPresenter() {
        return new GardenDoubleLightPresenter(this);
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
        mListColorWhiteLight = findViewById(R.id.lsi_expert_color_white_light);


        mNumberPickDialog = new NumberPickDialog();
        mNumberPickDialog.setTimeSettingListener(this);

    }


    private void initData() {
        devId = getIntent().getStringExtra("devId");
        mChnId = getIntent().getIntExtra("chnId",-1);
        presenter.setDevId(devId);
        presenter.setChnId(mChnId);
        initWhiteLightSwitch();
        initDuration();
        initSensitivity();
        initColorWhiteLight();
        onShowWaitDialog();
        presenter.getWhiteLight();

    }



    private void initWhiteLightSwitch() {
        Integer[] values;
        if (FunSDK.GetDevAbility(devId, "OtherFunction/NotSupportAutoAndIntelligent") == 1) {
            mWhiteLightSwitchArray = new String[]{
                    FunSDK.TS("TR_Turn_light_on"),
                    FunSDK.TS("TR_Turn_light_off")
            };
            values = new Integer[]{1, 2};
        } else {
            mWhiteLightSwitchArray = new String[]{
                    FunSDK.TS("Auto_model"),
                    FunSDK.TS("TR_Turn_light_on"),
                    FunSDK.TS("TR_Turn_light_off"),
                    FunSDK.TS("timing"),
                    FunSDK.TS("Intelligent_Vigilance")
            };
            values = new Integer[]{0, 1, 2, 3, 4};
        }

        mLisWhiteLightSwitch.setTip(XUtils.getLightViewTips(mWhiteLightSwitchArray));
        mSpWhiteLightSwitch = mLisWhiteLightSwitch.getExtraSpinner();
        mSpWhiteLightSwitch.initData(mWhiteLightSwitchArray, values);
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
                    switch (position) {
                        case 0:
                            mTimeSettingLayout.setVisibility(View.GONE);
                            mIntelligentModelLayout.setVisibility(View.GONE);
                            mWhiteLight.setWorkMode("Auto");
                            presenter.saveWhiteLight();
                            break;
                        case 1:
                            //注:设置开始和结束时间相同，且在定时模式下，为打开灯泡
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

    @Override
    public void showColorWhiteLight(int selectValue){
        mSpColorWhiteLight.setValue(selectValue);
        mListColorWhiteLight.setRightText((CharSequence) mSpColorWhiteLight.getSelectedName());

    }


    @Override
    public void showWorkMode(WhiteLightBean mWhiteLight){
        if (mWhiteLight != null) {
            if (mWhiteLight.getWorkMode().equals("Auto")) {
                mSpWhiteLightSwitch.setValue(0);
                mLisWhiteLightSwitch.setRightText((CharSequence)mSpWhiteLightSwitch.getSelectedName());

            } else if (mWhiteLight.getWorkMode().equals("KeepOpen")) {
                mSpWhiteLightSwitch.setValue(1);
                mLisWhiteLightSwitch.setRightText((CharSequence)mSpWhiteLightSwitch.getSelectedName());
            } else if (mWhiteLight.getWorkMode().equals("Timing")) {
                mSpWhiteLightSwitch.setValue(3);
                mLisWhiteLightSwitch.setRightText((CharSequence)mSpWhiteLightSwitch.getSelectedName());
                mTimeSettingLayout.setVisibility(View.VISIBLE);
                String openTime = ParseTimeUtil.parseTime(mWhiteLight.getWorkPeriod().getSHour(), mWhiteLight.getWorkPeriod().getSMinute());
                String closeTime = ParseTimeUtil.parseTime(mWhiteLight.getWorkPeriod().getEHour(), mWhiteLight.getWorkPeriod().getEMinute());
                mWhiteLightOpenTime.setText(openTime);
                mWhiteLightCloseTime.setText(closeTime);
            } else if (mWhiteLight.getWorkMode().equals("Close")) {
                mSpWhiteLightSwitch.setValue(2);
                mLisWhiteLightSwitch.setRightText((CharSequence)mSpWhiteLightSwitch.getSelectedName());
            } else if (mWhiteLight.getWorkMode().equals("Intelligent") && null != mWhiteLight.getMoveTrigLight()) {
                mSpWhiteLightSwitch.setValue(4);
                mLisWhiteLightSwitch.setRightText((CharSequence)mSpWhiteLightSwitch.getSelectedName());
                mIntelligentModelLayout.setVisibility(View.VISIBLE);
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
            }
        } else {
            Toast.makeText(getApplicationContext(), FunSDK.TS("Data_exception"), Toast.LENGTH_SHORT).show();
            onHideWaitDialog();
        }
    }

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

    private void initColorWhiteLight() {
        mSpColorWhiteLight = mListColorWhiteLight.getExtraSpinner();
        if (FunSDK.GetDevAbility(devId, "OtherFunction/SupportSoftPhotosensitive") == 1) {
            mSpColorWhiteLight.initData(new String[]{FunSDK.TS("Auto_Color"),
                            FunSDK.TS("Setting_Color"),
                            FunSDK.TS("Setting_White_Black"),
                            FunSDK.TS("WhiteLamp_Auto"),
                            FunSDK.TS("IrLamp_Auto")},
                    new Integer[]{0, 1, 2, 4, 5});
        } else {
            mSpColorWhiteLight.initData(new String[]{FunSDK.TS("Auto_Color"),
                            FunSDK.TS("Setting_Color"),
                            FunSDK.TS("Setting_White_Black")},
                    new Integer[]{0, 1, 2});
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

    @Override
    public boolean setTime(int hour, int min, boolean isOpenTime) {
        WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
        if (isOpenTime) {
            String openTime = ParseTimeUtil.combineTime(hour) + ":" + ParseTimeUtil.combineTime(min);
            if (StringUtils.contrast(openTime, mWhiteLightCloseTime.getText().toString().trim())) {
                Toast.makeText(getApplicationContext(), FunSDK.TS("TR_Open_Time_Not_Equal_Close_Time"), Toast.LENGTH_LONG).show();
                return true;
            }

            mWhiteLightOpenTime.setText(openTime);
            mWhiteLight.getWorkPeriod().setSHour(hour);
            mWhiteLight.getWorkPeriod().setSMinute(min);
            presenter.saveWhiteLight();
        } else {
            String closeTime = ParseTimeUtil.combineTime(hour) + ":" + ParseTimeUtil.combineTime(min);
            if (StringUtils.contrast(closeTime, mWhiteLightOpenTime.getText().toString().trim())) {
                Toast.makeText(getApplicationContext(), FunSDK.TS("TR_Open_Time_Not_Equal_Close_Time"), Toast.LENGTH_LONG).show();
                return true;
            }
            mWhiteLightCloseTime.setText(closeTime);
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
                }                break;
            }
            case R.id.setting_close_time_rl_white_light: {
                String closeTime = mWhiteLightCloseTime.getText().toString().trim();
                if (!mNumberPickDialog.isAdded()) {
                    mNumberPickDialog.show(getSupportFragmentManager(), "mNumberPickDialog");
                    mNumberPickDialog.updateTime(Integer.parseInt((closeTime.substring(0, 2))), Integer.parseInt(closeTime.substring(3)), false);
                }                break;
            }
            default:
                break;
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

    @Override
    public Activity getActivity() {
        return GardenDoubleLightActivity.this;
    }

}
