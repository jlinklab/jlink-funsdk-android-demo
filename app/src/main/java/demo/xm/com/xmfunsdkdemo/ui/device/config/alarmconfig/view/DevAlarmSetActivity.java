package demo.xm.com.xmfunsdkdemo.ui.device.config.alarmconfig.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.basic.G;
import com.lib.DevSDK;
import com.lib.sdk.bean.AlarmInfoBean;
import com.lib.sdk.bean.HumanDetectionBean;
import com.lib.sdk.bean.JsonConfig;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.DevConfigState;
import demo.xm.com.xmfunsdkdemo.ui.device.config.alarmconfig.listener.DevAlarmSetContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.alarmconfig.presenter.DevAlarmSetPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.alarmvoice.view.AlarmVoiceListActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.view.IntelligentVigilanceActivity;
import io.reactivex.annotations.Nullable;

/**
 * 报警配置界面,包括移动侦测,视频遮挡,报警输出等
 * Created by jiangping on 2018-10-23.
 */
public class DevAlarmSetActivity extends BaseConfigActivity<DevAlarmSetPresenter>
        implements DevAlarmSetContract.IDevAlarmSetView, OnClickListener {
    private static final int MOTION_VOICE_SEL_REQUEST_CODE = 0x01;//移动侦测警铃
    private static final int BLIND_VOICE_SEL_REQUEST_CODE = 0x02;//视频遮挡警铃
    private static final int HUMAN_VOICE_SEL_REQUEST_CODE = 0x03;//人形遮挡警铃


    /**
     * 视频丢失警报
     */
    private ImageButton btnLossVideo;


    /**
     * 移动侦测总按钮
     */
    private ImageButton btnMotion;
    /**
     * 移动侦测之录像
     */
    private ImageButton btnMotionRecord;
    /**
     * 移动侦测之拍照抓图
     */
    private ImageButton btnMotionCapture;
    /**
     * 移动侦测设备警铃布局
     */
    private ViewGroup alarmMotionVoiceLayout;
    /**
     * 移动侦测自定义语音提示布局
     */
    private ViewGroup alarmMotionVoiceTypeLayout;
    /**
     * 移动侦测设备警铃开关
     */
    private CheckBox cbMotionVoice;

    /**
     * 移动侦测之手机推送
     */
    private ImageButton btnMotionPushMsg;
    /**
     * 移动侦测之报警灵敏度
     */
    private Spinner spMotionSensitivity;
    /**
     * 灵敏度层级：6：高级，3：中级，1：低级
     */
    private static final int[] senLevels = {1, 3, 6};

    /**
     * 视频遮挡总按钮
     */
    private ImageButton btnBlind;
    /**
     * 视频遮挡之录像
     */
    private ImageButton btnBlindRecord;
    /**
     * 视频遮挡之拍照抓图
     */
    private ImageButton btnBlindCapture;

    /**
     * 视频遮挡之手机推送
     */
    private ImageButton btnBlindPushMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_setup_alarm);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_opt_alarm_config));
        titleBar.setLeftClick(this);
        titleBar.setRightBtnResource(R.mipmap.icon_save_normal, R.mipmap.icon_save_pressed);
        titleBar.setRightIvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                toSaveDevAlarm();
            }
        });

        btnLossVideo = findViewById(R.id.btn_dev_alarm_loss_video);//视频丢失警报
        btnLossVideo.setOnClickListener(this);

        btnMotion = findViewById(R.id.btn_dev_alarm_motion);//移动侦测
        btnMotion.setOnClickListener(this);
        btnMotionRecord = findViewById(R.id.btn_dev_alarm_motion_record);
        btnMotionRecord.setOnClickListener(this);
        btnMotionCapture = findViewById(R.id.btn_dev_alarm_motion_capture);
        btnMotionCapture.setOnClickListener(this);
        btnMotionPushMsg = findViewById(R.id.btn_dev_alarm_motion_push_msg);
        btnMotionPushMsg.setOnClickListener(this);
        spMotionSensitivity = findViewById(R.id.sp_dev_alarm_motion_sensitivity);
        String[] alarmLevels = getResources().getStringArray(R.array.device_setup_alarm_level);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.right_spinner_item, alarmLevels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMotionSensitivity.setAdapter(adapter);
        spMotionSensitivity.setTag(senLevels);

        btnBlind = findViewById(R.id.btn_dev_alarm_blind); //视频遮挡
        btnBlind.setOnClickListener(this);
        btnBlindRecord = findViewById(R.id.btn_dev_alarm_blind_record);
        btnBlindRecord.setOnClickListener(this);
        btnBlindCapture = findViewById(R.id.btn_dev_alarm_blind_capture);
        btnBlindCapture.setOnClickListener(this);
        btnBlindPushMsg = findViewById(R.id.btn_dev_alarm_blind_push_msg);
        btnBlindPushMsg.setOnClickListener(this);

        alarmMotionVoiceLayout = findViewById(R.id.alarm_motion_enable_ll);
        cbMotionVoice = alarmMotionVoiceLayout.findViewById(R.id.alarm_motion_voice);
        alarmMotionVoiceTypeLayout = alarmMotionVoiceLayout.findViewById(R.id.ll_alarm_motion_next);
        alarmMotionVoiceLayout.setOnClickListener(this);

        findViewById(R.id.ll_dev_alarm_human).setOnClickListener(this);
    }

    private void initData() {
        showWaitDialog();
        presenter.getDevAlarm();
    }

    @Override
    public DevAlarmSetPresenter getPresenter() {
        return new DevAlarmSetPresenter(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_dev_alarm_loss_video:                     // 视频丢失警报
                btnLossVideo.setSelected(!btnLossVideo.isSelected());   //点击打开，再点击关闭
                break;
            case R.id.btn_dev_alarm_motion:                // 移动侦测总按钮
                btnMotion.setSelected(!btnMotion.isSelected());
                openOrCloseChildControl(JsonConfig.DETECT_MOTIONDETECT, btnMotion.isSelected());  //设置子按钮
                if (presenter.isSupportHumanDetect()) {
                    //移动侦测开启后，判断人形检测是否支持，支持的话需要显示出来
                    findViewById(R.id.ll_dev_alarm_human).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.ll_dev_alarm_human).setVisibility(View.GONE);
                }
                break;
            case R.id.btn_dev_alarm_motion_record:          //移动侦测之录像
                btnMotionRecord.setSelected(!btnMotionRecord.isSelected());
                break;
            case R.id.btn_dev_alarm_motion_capture:         //移动侦测之抓图
                btnMotionCapture.setSelected(!btnMotionCapture.isSelected());
                break;
            case R.id.btn_dev_alarm_motion_push_msg:        //移动侦测之推送
                btnMotionPushMsg.setSelected(!btnMotionPushMsg.isSelected());
                break;
            case R.id.btn_dev_alarm_blind:                    // 视频遮挡
                btnBlind.setSelected(!btnBlind.isSelected());
                openOrCloseChildControl(JsonConfig.DETECT_BLINDDETECT, btnBlind.isSelected());
                break;
            case R.id.btn_dev_alarm_blind_record:
                btnBlindRecord.setSelected(!btnBlindRecord.isSelected());
                break;
            case R.id.btn_dev_alarm_blind_capture:
                btnBlindCapture.setSelected(!btnBlindCapture.isSelected());
                break;
            case R.id.btn_dev_alarm_blind_push_msg:
                btnBlindPushMsg.setSelected(!btnBlindPushMsg.isSelected());
                break;
            case R.id.ll_dev_alarm_human:                //人形检测
                turnToActivity(IntelligentVigilanceActivity.class);
                break;
            case R.id.alarm_motion_enable_ll: {
                Intent intent = new Intent(DevAlarmSetActivity.this, AlarmVoiceListActivity.class);
                intent.putExtra("voiceType", presenter.getmMotion().EventHandler.VoiceType);
                intent.putExtra("devId", presenter.getDevId());
                //警铃间隔时间 需要判断能力集：SupportAlarmVoiceTipInterval
                intent.putExtra("alarmVoiceIntervalTime", presenter.getmMotion().EventHandler.VoiceTipInterval);
                startActivityForResult(intent, MOTION_VOICE_SEL_REQUEST_CODE);
            }
            break;
            case R.id.alarm_blind_enable_ll: {
                Intent intent = new Intent(DevAlarmSetActivity.this, AlarmVoiceListActivity.class);
                intent.putExtra("voiceType", presenter.getmBlind().EventHandler.VoiceType);
                intent.putExtra("devId", presenter.getDevId());
                startActivityForResult(intent, BLIND_VOICE_SEL_REQUEST_CODE);
            }
            break;
            default:
                break;

        }
    }

    @Override
    public void updateUI(final AlarmInfoBean alarmInfoBean, final String key, final int state) {
        switch (key) {
            case JsonConfig.DETECT_LOSSDETECT:      //视频丢失警报
                if (state == DevConfigState.DEV_CONFIG_VIEW_VISIABLE && alarmInfoBean != null) { //onParse：判断json数据格式是否正确，并且若正确即处理
                    btnLossVideo.setSelected(alarmInfoBean.Enable);
                    findViewById(R.id.rl_dev_alarm_video_loss_alarm).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.rl_dev_alarm_video_loss_alarm).setVisibility(View.GONE);
                }
                break;
            case JsonConfig.DETECT_MOTIONDETECT:    //移动侦测
                if (state == DevConfigState.DEV_CONFIG_VIEW_VISIABLE && alarmInfoBean != null) {
                    btnMotion.setSelected(alarmInfoBean.Enable);
                    cbMotionVoice.setChecked(alarmInfoBean.EventHandler.VoiceEnable);
                    openOrCloseChildControl(key, btnMotion.isSelected());
                    findViewById(R.id.ll_dev_alarm_motion).setVisibility(View.VISIBLE);

                    if (presenter.isSupportHumanDetect() && presenter.getmMotion().Enable) {
                        //人形检测是基于移动侦测的，只有移动侦测开启的情况下才支持，并且相关的联动功能（包括联动图片、联动视频、警铃）都是在移动侦测上设置
                        //// Human detection is based on motion detection, it is only supported when motion detection is enabled, and related linkage functions (including linked images, linked videos, siren) are all configured under motion detection.
                        findViewById(R.id.ll_dev_alarm_human).setVisibility(View.VISIBLE);
                    }else {
                        findViewById(R.id.ll_dev_alarm_human).setVisibility(View.GONE);
                    }
                } else {
                    findViewById(R.id.ll_dev_alarm_motion).setVisibility(View.GONE);
                }
                break;
            case JsonConfig.DETECT_BLINDDETECT:     //视频遮挡
                if (state == DevConfigState.DEV_CONFIG_VIEW_VISIABLE && alarmInfoBean != null) {
                    btnBlind.setSelected(alarmInfoBean.Enable);
                    openOrCloseChildControl(key, btnBlind.isSelected());
                    findViewById(R.id.ll_dev_alarm_blind).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.ll_dev_alarm_blind).setVisibility(View.GONE);
                }

                hideWaitDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void saveLossResult(int state) {
        hideWaitDialog();
        if (state == DevConfigState.DEV_CONFIG_UPDATE_SUCCESS) {
            showToast(getResources().getString(R.string.set_dev_config_success), Toast.LENGTH_SHORT);
            finish();
        } else {
            showToast(getResources().getString(R.string.get_dev_config_failed), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void saveMotionResult(int state) {
        hideWaitDialog();
        if (state == DevConfigState.DEV_CONFIG_UPDATE_SUCCESS) {
            showToast(getResources().getString(R.string.set_dev_config_success), Toast.LENGTH_SHORT);
            finish();
        } else {
            showToast(getResources().getString(R.string.get_dev_config_failed), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void saveBlindResult(int state) {
        hideWaitDialog();
        if (state == DevConfigState.DEV_CONFIG_UPDATE_SUCCESS) {
            showToast(getResources().getString(R.string.set_dev_config_success), Toast.LENGTH_SHORT);
            finish();
        } else {
            showToast(getResources().getString(R.string.get_dev_config_failed), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void saveHumanResult(int state) {
        hideWaitDialog();
        if (state == DevConfigState.DEV_CONFIG_UPDATE_SUCCESS) {
            showToast(getResources().getString(R.string.set_dev_config_success), Toast.LENGTH_SHORT);
            finish();
        } else {
            showToast(getResources().getString(R.string.get_dev_config_failed), Toast.LENGTH_SHORT);
        }
    }

    /**
     * 是否支持人形检测回调
     *
     * @param isSupport true支持 false不支持
     */
    @Override
    public void isSupportHumanDetectResult(boolean isSupport) {
        if (isSupport && presenter.getmMotion() != null && presenter.getmMotion().Enable) {
            //人形检测是基于移动侦测的，只有移动侦测开启的情况下才支持，并且相关的联动功能（包括联动图片、联动视频、警铃）都是在移动侦测上设置
            //// Human detection is based on motion detection, it is only supported when motion detection is enabled, and related linkage functions (including linked images, linked videos, siren) are all configured under motion detection.
            findViewById(R.id.ll_dev_alarm_human).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.ll_dev_alarm_human).setVisibility(View.GONE);
        }
    }

    @Override
    public void isSupportVoiceTipsResult(boolean isSupport) {
        if (isSupport) {
            alarmMotionVoiceLayout.setVisibility(View.VISIBLE);
        } else {
            alarmMotionVoiceLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void isSupportVoiceTipsTypeResult(boolean isSupport) {
        if (isSupport) {
            alarmMotionVoiceTypeLayout.setVisibility(View.VISIBLE);
        } else {
            alarmMotionVoiceTypeLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 去保存修改的数据
     */
    private void toSaveDevAlarm() {
        showWaitDialog();
        setValues();
        presenter.saveLossDetect();
        presenter.saveMotionDetect();
        presenter.saveBlindDetect();
    }

    /**
     * 根据总按钮状态去设置子控件的状态
     *
     * @param key    移动侦测、视频遮挡、人形检测
     * @param isOpen 总按钮状态
     */
    private void openOrCloseChildControl(final String key, final boolean isOpen) {
        switch (key) {
            case JsonConfig.DETECT_MOTIONDETECT:
                btnMotionRecord.setEnabled(isOpen);
                btnMotionCapture.setEnabled(isOpen);
                btnMotionPushMsg.setEnabled(isOpen);
                btnMotionRecord.setSelected(isOpen && presenter.getmMotion().EventHandler.RecordEnable);
                btnMotionCapture.setSelected(isOpen && presenter.getmMotion().EventHandler.SnapEnable);
                btnMotionPushMsg.setSelected(isOpen && presenter.getmMotion().EventHandler.MessageEnable);
                if (isOpen) {
                    int tmp = -1;
                    switch (presenter.getmMotion().Level) {
                        case 1:
                        case 2:
                            tmp = 1;
                            break;
                        case 3:
                        case 4:
                            tmp = 3;
                            break;
                        case 5:
                        case 6:
                            tmp = 6;
                        default:
                            break;
                    }

                    presenter.getmMotion().Level = tmp;
                    for (int i = 0; i < senLevels.length; i++) {
                        if (tmp == senLevels[i]) {
                            spMotionSensitivity.setSelection(i);
                            break;
                        }
                    }
                } else {
                    spMotionSensitivity.setEnabled(false);
                }
                break;
            case JsonConfig.DETECT_BLINDDETECT:
                btnBlindRecord.setEnabled(isOpen);
                btnBlindCapture.setEnabled(isOpen);
                btnBlindPushMsg.setEnabled(isOpen);
                btnBlindRecord.setSelected(isOpen && presenter.getmBlind().EventHandler.RecordEnable);
                btnBlindCapture.setSelected(isOpen && presenter.getmBlind().EventHandler.SnapEnable);
                btnBlindPushMsg.setSelected(isOpen && presenter.getmBlind().EventHandler.MessageEnable);
                break;
            default:
                break;
        }
    }

    /**
     * 保存控件数据
     */
    private void setValues() {
        if (presenter.getmLoss() != null) {
            presenter.getmLoss().Enable = btnLossVideo.isSelected();
        }

        if (presenter.getmMotion() != null) {
            presenter.getmMotion().Enable = btnMotion.isSelected();
            presenter.getmMotion().EventHandler.RecordEnable = btnMotionRecord.isSelected();
            presenter.getmMotion().EventHandler.SnapEnable = btnMotionCapture.isSelected();
            presenter.getmMotion().EventHandler.MessageEnable = btnMotionPushMsg.isSelected();
            presenter.getmMotion().EventHandler.VoiceEnable = cbMotionVoice.isChecked();
            presenter.getmMotion().Level = senLevels[spMotionSensitivity.getSelectedItemPosition()];

        }

        if (presenter.getmBlind() != null) {
            presenter.getmBlind().Enable = btnBlind.isSelected();
            presenter.getmBlind().EventHandler.RecordEnable = btnBlindRecord.isSelected();
            presenter.getmBlind().EventHandler.SnapEnable = btnBlindCapture.isSelected();
            presenter.getmBlind().EventHandler.MessageEnable = btnBlindPushMsg.isSelected();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int voiceType = data.getIntExtra("voiceType", -1);
            int alarmVoiceIntervalTime = data.getIntExtra("alarmVoiceIntervalTime", 0);
            if (voiceType >= 0) {
                if (requestCode == MOTION_VOICE_SEL_REQUEST_CODE) {
                    presenter.getmMotion().EventHandler.VoiceType = voiceType;
                    presenter.getmMotion().EventHandler.VoiceTipInterval = alarmVoiceIntervalTime;
                } else if (requestCode == BLIND_VOICE_SEL_REQUEST_CODE) {
                    presenter.getmBlind().EventHandler.VoiceType = voiceType;
                }
            }
        }

    }
}
