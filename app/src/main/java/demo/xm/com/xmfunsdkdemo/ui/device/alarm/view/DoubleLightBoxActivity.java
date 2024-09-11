package demo.xm.com.xmfunsdkdemo.ui.device.alarm.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.lib.FunSDK;
import com.lib.SDKCONST;
import com.lib.sdk.bean.DevVolumeBean;
import com.lib.sdk.bean.IntelliAlertAlarmBean;
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
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.listener.DoubleLightBoxContract;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.presenter.DoubleLightBoxPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.alarmconfig.view.DevAlarmSetActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.view.IntelligentVigilanceActivity;


/**
 * 声光报警：双光枪机界面,可改变控制模式
 */
public class DoubleLightBoxActivity extends BaseConfigActivity<DoubleLightBoxPresenter> implements
        DoubleLightBoxContract.IDoubleLightBoxView, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "DoubleLightBox";


    private XTitleBar titleBar;


    //控制类型
    public static final String WORK_MODE_AUTO = "Auto";//星光全彩
    public static final String WORK_MODE_CLOSE = "Close";//紅外夜視
    public static final String WORK_MODE_INTELLIGENT = "Intelligent";//双光警戒


    //控制模式控件
    private ListSelectItem mLisControlMode;
    //控制模式选项
    private ExtraSpinner mSpControlMode;
    //双光警戒可配置项的控件
    protected LinearLayout mIntelligentModelLayout;


    //持续亮灯时间控件
    private ListSelectItem mListIntelligentDuration;
    //持续亮灯时间选项
    private ExtraSpinner<Integer> mSpIntelligentDuration;
    //持续亮灯时间可选项
    private String[] mDurationArray;


    //灵敏度控件
    private ListSelectItem mListIntelligentSensitivity;
    //灵敏度选项
    private ExtraSpinner<Integer> mSpIntelligentSensitivity;
    //灵敏度可选项
    private String[] mLevelArray;


    //智能警戒控件
    private ListSelectItem mListIntelligentVigilance;

    //智能警戒开关
    private ListSelectItem mLsiSmartAlarmSwitch;
    //报警时间设置
    private ListSelectItem mLsiSmartAlarmDuration;
    //报警音量设置
    private ListSelectItem mLsiSmartAlarmVoice;
    //报警铃声设置
    private ListSelectItem mLsiSmartAlarmRing;
    //人体感应
    private ListSelectItem mLsiSmartAlarmBodyTrigger;
    private LinearLayout mLlSmartAlarm;
    private SeekBar mSeekBarVolume;
    private String[] mAlarmDurationArray;
    private ExtraSpinner<Integer> mSpSmartAlarmDuration;

    @Override
    public DoubleLightBoxPresenter getPresenter() {
        return new DoubleLightBoxPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_alarm_voice_light_setting);
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
        mLisControlMode = findViewById(R.id.lsi_control_mode);
        mIntelligentModelLayout = findViewById(R.id.ll_intelligent_model_setting);
        mListIntelligentDuration = findViewById(R.id.lsi_intelligent_duration);
        mListIntelligentSensitivity = findViewById(R.id.lsi_intelligent_sensitivity);
        mListIntelligentVigilance = findViewById(R.id.intelligent_vigilance_set);
        initDuration();
        initSensitivity();
        mListIntelligentVigilance.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoubleLightBoxActivity.this, IntelligentVigilanceActivity.class);
                intent.putExtra("devId", presenter.getDevId());
                startActivity(intent);
            }
        });

        mLlSmartAlarm = findViewById(R.id.ll_smart_alarm_set);
        mLsiSmartAlarmSwitch = findViewById(R.id.lsi_smart_alarm_switch);
        mLsiSmartAlarmDuration = findViewById(R.id.lsi_smart_alarm_duration);
        mLsiSmartAlarmVoice = findViewById(R.id.lsi_smart_alarm_voice);
        mLsiSmartAlarmRing = findViewById(R.id.lsi_smart_alarm_ring);
        mLsiSmartAlarmBodyTrigger = findViewById(R.id.lsi_body_trigger);
    }

    private void initData() {
        String devId = getIntent().getStringExtra("devId");
        presenter.setDevId(devId);
        presenter.setChnId(-1);
        showWaitDialog();
        presenter.getSystemFunction();
        initSmartAlarmDuration();
        initSmartAlarmListener();
    }

    /**
     * 报警时间
     */
    private void initSmartAlarmDuration() {
        mAlarmDurationArray = new String[]{"5s", "10s", "15s", "20s"};
        mSpSmartAlarmDuration = mLsiSmartAlarmDuration.getExtraSpinner();
        mSpSmartAlarmDuration.initData(mAlarmDurationArray, new Integer[]{0, 1, 2, 3});
        mLsiSmartAlarmDuration.setOnClickListener(v ->
                mLsiSmartAlarmDuration.toggleExtraView());
        mSpSmartAlarmDuration.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
            @Override
            public void onItemClick(int position, String key, Object value) {
                presenter.saveSmartAlarmDurationConfig(Integer.parseInt(
                        mAlarmDurationArray[position].substring(0, mAlarmDurationArray[position].length() - 1)));

                mLsiSmartAlarmDuration.setRightText(key);
                mLsiSmartAlarmDuration.toggleExtraView(true);
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

        mLsiSmartAlarmBodyTrigger.setOnClickListener(v -> turnToIntelligentActivity());
    }


    /**
     * 跳转智能报警设置页
     */
    private void turnToIntelligentActivity() {
        if (IdrDefine.isIDR(DevDataCenter.getInstance().getDevInfo(presenter.getDevId()).getDevType())) {
            Intent intent = new Intent(DoubleLightBoxActivity.this, DevAlarmSetActivity.class);
            intent.putExtra("devId", presenter.getDevId());
            startActivity(intent);
        } else {
            Intent intent = new Intent(DoubleLightBoxActivity.this, IntelligentVigilanceActivity.class);
            intent.putExtra("devId", presenter.getDevId());
            startActivity(intent);
        }
    }

    /**
     * 初始话控制模式布局
     */
    @Override
    public void initWhiteLightSwitch(String[] modeName, Integer[] modeValue) {
        mSpControlMode = mLisControlMode.getExtraSpinner();
        mSpControlMode.initData(modeName, modeValue);
        mLisControlMode.setTip(XUtils.getLightViewTips(modeName));

        mLisControlMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLisControlMode.toggleExtraView();
            }
        });
        mSpControlMode.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener<Integer>() {
            @Override
            public void onItemClick(int position, String key, Integer value) {
                mLisControlMode.toggleExtraView(true);
                WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
                if (null != mWhiteLight) {
                    switch (value) {
                        case 0:
                            //星光全彩
                            mIntelligentModelLayout.setVisibility(View.GONE);
                            mWhiteLight.setWorkMode(WORK_MODE_AUTO);
                            saveConfig();
                            break;
                        case 1:
                            //紅外夜視
                            mIntelligentModelLayout.setVisibility(View.GONE);
                            mWhiteLight.setWorkMode(WORK_MODE_CLOSE);
                            saveConfig();
                            break;
                        case 2:
                            //双光警戒，该模式可设置灵敏度，持续亮灯时间及智能警戒
                            if (null != mWhiteLight.getMoveTrigLight()) {
                                mWhiteLight.setWorkMode(WORK_MODE_INTELLIGENT);
                                mIntelligentModelLayout.setVisibility(View.VISIBLE);
                                int level = mWhiteLight.getMoveTrigLight().getLevel();
                                int duration = mWhiteLight.getMoveTrigLight().getDuration();
                                //灵敏度
                                mSpIntelligentSensitivity.setValue((level - 1) / 2);/**注:位置0,1,2对应值1,3,5 ***/
                                mListIntelligentSensitivity.setRightText((CharSequence) mSpIntelligentSensitivity.getSelectedName());
                                //持续亮灯时间
                                for (int i = 0; i < mDurationArray.length; i++) {
                                    if (Integer.parseInt(mDurationArray[i].substring(0, mDurationArray[i].length() - 1)) == duration) {
                                        mSpIntelligentDuration.setValue(i);
                                        mListIntelligentDuration.setRightText((CharSequence) mSpIntelligentDuration.getSelectedName());
                                    }
                                }
                                saveConfig();
                            } else {
                                mIntelligentModelLayout.setVisibility(View.GONE);
                            }
                            break;
                        default:
                            break;
                    }
                }
                mLisControlMode.setRightText(key);
            }
        });

        presenter.getAlarmByVoiceLightConfig();

    }

    /**
     * 初始持续亮灯时间布局
     */
    private void initDuration() {
        //持续亮灯时间可选项
        mDurationArray = new String[]{"5s", "10s", "30s", "60s", "90s", "120s"};
        mSpIntelligentDuration = mListIntelligentDuration.getExtraSpinner();
        mSpIntelligentDuration.initData(mDurationArray, new Integer[]{0, 1, 2, 3, 4, 5});
        mListIntelligentDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListIntelligentDuration.toggleExtraView();
            }
        });
        mSpIntelligentDuration.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
            @Override
            public void onItemClick(int position, String key, Object value) {
                WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
                if (mWhiteLight != null && null != mWhiteLight.getMoveTrigLight()) {
                    mWhiteLight.getMoveTrigLight().setDuration(Integer.parseInt(mDurationArray[position].substring(0, mDurationArray[position].length() - 1)));
                    saveConfig();
                } else {
                    Toast.makeText(DoubleLightBoxActivity.this, getString(R.string.EE_DVR_OPT_CAPS_ERROR), Toast.LENGTH_SHORT).show();
                }
                mListIntelligentDuration.setRightText(key);
                mListIntelligentDuration.toggleExtraView(true);
            }
        });
    }

    /**
     * 初始灵敏度布局
     */
    private void initSensitivity() {
        //灵敏度水平
        mLevelArray = new String[]{
                FunSDK.TS("Intelligent_level_Low"),
                FunSDK.TS("Intelligent_level_Middle"),
                FunSDK.TS("Intelligent_level_Height")
        };
        mSpIntelligentSensitivity = mListIntelligentSensitivity.getExtraSpinner();
        mSpIntelligentSensitivity.initData(mLevelArray, new Integer[]{0, 1, 2});
        mListIntelligentSensitivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListIntelligentSensitivity.toggleExtraView();
            }
        });
        mSpIntelligentSensitivity.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
            @Override
            public void onItemClick(int position, String key, Object value) {
                WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
                if (mWhiteLight != null && null != mWhiteLight.getMoveTrigLight()) {
                    mWhiteLight.getMoveTrigLight().setLevel(position * 2 + 1);/**注:位置0,1,2对应值1,3,5 ***/
                    saveConfig();
                } else {
                    Toast.makeText(DoubleLightBoxActivity.this, getString(R.string.EE_DVR_OPT_CAPS_ERROR), Toast.LENGTH_SHORT).show();
                }

                mListIntelligentSensitivity.setRightText(key);
                mListIntelligentSensitivity.toggleExtraView(true);
            }
        });
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
        return this;
    }

    @Override
    public void onUpdateView(boolean isSuccess, WhiteLightBean mWhiteLight) {
        hideWaitDialog();
        if (mWhiteLight != null) {
            if (WORK_MODE_AUTO.equals(mWhiteLight.getWorkMode())) {
                mSpControlMode.setValue(0);
                mIntelligentModelLayout.setVisibility(View.GONE);
                mLisControlMode.setRightText((CharSequence) mSpControlMode.getSelectedName());
            } else if (WORK_MODE_CLOSE.equals(mWhiteLight.getWorkMode())) {
                mSpControlMode.setValue(1);
                mIntelligentModelLayout.setVisibility(View.GONE);
                mLisControlMode.setRightText((CharSequence) mSpControlMode.getSelectedName());
            } else if (WORK_MODE_INTELLIGENT.equals(mWhiteLight.getWorkMode()) && null != mWhiteLight.getMoveTrigLight()) {
                //双光警戒模式可设置灵敏度，持续亮灯时间及智能警戒
                mSpControlMode.setValue(2);
                mLisControlMode.setRightText((CharSequence) mSpControlMode.getSelectedName());
                mIntelligentModelLayout.setVisibility(View.VISIBLE);


                int level = mWhiteLight.getMoveTrigLight().getLevel();
                int duration = mWhiteLight.getMoveTrigLight().getDuration();
                if (mSpIntelligentSensitivity != null) {
                    mSpIntelligentSensitivity.setValue((level - 1) / 2); /**注:位置0,1,2对应值1,3,5 ***/
                }
                if (mListIntelligentSensitivity != null) {
                    mListIntelligentSensitivity.setRightText((CharSequence) mSpIntelligentSensitivity.getSelectedName());
                }
                for (int i = 0; i < mDurationArray.length; i++) {
                    if (Integer.parseInt(mDurationArray[i].substring(0, mDurationArray[i].length() - 1)) == duration) {
                        if (mSpIntelligentDuration != null) {
                            mSpIntelligentDuration.setValue(i);
                        }
                        if (mListIntelligentDuration != null) {
                            mListIntelligentDuration.setRightText((CharSequence) mSpIntelligentDuration.getSelectedName());
                        }
                    }
                }
            } else {
                Toast.makeText(DoubleLightBoxActivity.this, getString(R.string.get_dev_config_failed), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(DoubleLightBoxActivity.this, getString(R.string.get_dev_config_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveResult(boolean isSuccess) {
        hideWaitDialog();
        if (!isSuccess) {
            Toast.makeText(DoubleLightBoxActivity.this, getString(R.string.set_dev_config_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(DoubleLightBoxActivity.this, getString(R.string.set_dev_config_success), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void initRingtone(List<VoiceTipBean> mVoiceTipBeanList, IntelliAlertAlarmBean mIntelliAlertAlarmBean, String voiceType) {
        mLsiSmartAlarmRing.setRightText(voiceType);
        mLsiSmartAlarmRing.setVisibility(View.VISIBLE);
        String[] name = new String[mVoiceTipBeanList.size()];
        Integer[] values = new Integer[mVoiceTipBeanList.size()];
        for (int i = 0; i < mVoiceTipBeanList.size(); i++) {
            VoiceTipBean bean = mVoiceTipBeanList.get(i);
            name[i] = bean.getVoiceText();
            values[i] = bean.getVoiceEnum();
        }
        mLsiSmartAlarmRing.setOnClickListener(v -> mLsiSmartAlarmRing.toggleExtraView());
        ExtraSpinner<Integer> spinner = mLsiSmartAlarmRing.getExtraSpinner();
        if (spinner != null) {
            spinner.initData(name, values);
            spinner.setValue(mIntelliAlertAlarmBean.EventHandler.VoiceType);
            spinner.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
                @Override
                public void onItemClick(int position, String key, Object value) {
                    presenter.saveIntelliAlertAlarmVoiceType((int) value);

                    mLsiSmartAlarmRing.setRightText(key);
                    mLsiSmartAlarmRing.toggleExtraView(true);
                }
            });
        }
    }

    @Override
    public void initVolume(DevVolumeBean mDevHornVolumeBean) {
        mLsiSmartAlarmVoice.setVisibility(View.VISIBLE);
        mSeekBarVolume = mLsiSmartAlarmVoice.getExtraSeekbar();
        mSeekBarVolume.setMax(100);
        mSeekBarVolume.setProgress(mDevHornVolumeBean.getLeftVolume());
        mSeekBarVolume.setOnSeekBarChangeListener(this);
        mLsiSmartAlarmVoice.setRightText(String.valueOf(mDevHornVolumeBean.getLeftVolume()));
        mLsiSmartAlarmVoice.setOnClickListener(v -> mLsiSmartAlarmVoice.toggleExtraView());
    }

    @Override
    public void initSmartAlarmData(IntelliAlertAlarmBean mIntelliAlertAlarmBean) {
        mLsiSmartAlarmSwitch.setVisibility(View.VISIBLE);
        mLsiSmartAlarmSwitch.setSwitchState(mIntelliAlertAlarmBean.Enable ?
                SDKCONST.Switch.Open : SDKCONST.Switch.Close);
        if (mIntelliAlertAlarmBean.Enable) {
            mLlSmartAlarm.setVisibility(View.VISIBLE);
        } else {
            mLlSmartAlarm.setVisibility(View.GONE);
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
    }

    /**
     * 保存声光报警配置
     */
    private void saveConfig() {
        showWaitDialog();
        presenter.saveAlarmByVoiceLightConfig();
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

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == mSeekBarVolume) {
            presenter.saveVolume();
        }
    }
}
