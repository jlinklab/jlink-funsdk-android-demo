package demo.xm.com.xmfunsdkdemo.ui.device.config.idr.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.lib.FunSDK;
import com.lib.SDKCONST;
import com.lib.sdk.bean.AlarmInfoBean;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.listselectitem.extra.view.ExtraSpinner;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.idr.listener.IdrSettingContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.idr.presenter.IdrSettingPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.simpleconfig.view.DevSimpleConfigActivity;

/**
 * 低功耗设备相关配置
 */
public class IDRSettingActivity extends BaseConfigActivity<IdrSettingPresenter> implements
        IdrSettingContract.IIdrSettingView, View.OnClickListener {

    public static final int DETECTION_DURATION_SET_REQUEST = 300;
    /**
     * PIR报警配置
     */
    private ListSelectItem lsiPirSettings;

    /**
     * PIR报警开关
     */
    private ListSelectItem mLsiPirAlarm;

    /**
     * PIR报警灵敏度
     */
    private ListSelectItem mLsiPirSensitive;

    /**
     * PIR报警时间段
     */
    private ListSelectItem mLsiPirDetectionSchedule;




    private ExtraSpinner<Integer> mSpPirSensitive;

    /**
     * 录像开关
     */
    private ListSelectItem mLsiRecordEnable;
    /**
     * 录像时长
     */
    private ListSelectItem mLsiRecordDuration;

    private ExtraSpinner<Integer> mSpPirRecordDuration;

    /**
     * 人形报警开关
     */

    private ListSelectItem mLsiHumanDetection;







    @Override
    public IdrSettingPresenter getPresenter() {
        return new IdrSettingPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idr_setting);
        initView();
        initListener();
        presenter.initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.xb_idr_setting);
        titleBar.setTitleText(getString(R.string.idr_setting));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });

        lsiPirSettings = findViewById(R.id.lsi_idr_pir_settings);
        lsiPirSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnToActivity(DevSimpleConfigActivity.class, new Object[][]{{"jsonName", "Alarm.PIR"}, {"configName", "PIR报警设置\nPIR Alarm Settings"}, {"cmdId", 1042}});
            }
        });
        mLsiPirAlarm = findViewById(R.id.detection_alarm);
        mLsiPirSensitive = findViewById(R.id.detection_sensitive);
        mLsiHumanDetection = findViewById(R.id.lsi_detect_switch);
        mLsiPirDetectionSchedule = findViewById(R.id.detection_schedule);

        mLsiRecordEnable = findViewById(R.id.lsi_record_enable);
        mLsiRecordDuration = findViewById(R.id.lsi_record_duration);
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
    public void showPirSensitiveView(AlarmInfoBean mAlarmInfoBean) {
        //判断是否支持灵敏度
        mLsiPirSensitive.setVisibility(View.VISIBLE);
        mSpPirSensitive.setValue(mAlarmInfoBean.PirSensitive);
        mLsiPirSensitive.setRightText(mSpPirSensitive.getSelectedName());
    }

    @Override
    public void showPirTimeSection(AlarmInfoBean mAlarmInfoBean){
        //判断是否支持报警时间段
        mLsiPirDetectionSchedule.setVisibility(View.VISIBLE);
        if (mAlarmInfoBean.PirTimeSection != null) {
            AlarmInfoBean.PirTimeSections.PirTimeSection pirTimeSection1 = mAlarmInfoBean.PirTimeSection.PirTimeSectionOne;
            if (pirTimeSection1 != null) {
                String startEndTime = pirTimeSection1.StartTime + "-" + pirTimeSection1.EndTime;

                AlarmInfoBean.PirTimeSections.PirTimeSection pirTimeSection2 = mAlarmInfoBean.PirTimeSection.PirTimeSectionTwo;
                if (pirTimeSection2 != null && pirTimeSection2.Enable) {
                    //true表示是跨天设置的，否则都是非跨天
                    if("00:00".equals(pirTimeSection2.StartTime) && "23:59".equals(pirTimeSection1.EndTime)){
                        startEndTime = pirTimeSection1.StartTime + "-" + pirTimeSection2.EndTime;
                    }
                }

                if (pirTimeSection1.Enable) {
                    mLsiPirDetectionSchedule.setRightText(startEndTime + "\n" + IdrSettingPresenter.getWeeks(pirTimeSection1.WeekMask));
                } else {
                    mLsiPirDetectionSchedule.setRightText("");
                }
            }
        }
    }

    @Override
    public Activity getActivity() {
        return IDRSettingActivity.this;
    }

    @Override
    public void updatePirView(AlarmInfoBean mAlarmInfoBean) {
        findViewById(R.id.ll_pir_function).setVisibility(View.VISIBLE);
        findViewById(R.id.detection_alarm_ll).setVisibility(mAlarmInfoBean.Enable ? View.VISIBLE : View.GONE);
        mLsiPirAlarm.setSwitchState(mAlarmInfoBean.Enable ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
        mLsiRecordDuration.setVisibility(mAlarmInfoBean.Enable ? View.VISIBLE : View.GONE);


        mLsiRecordDuration.setRightText(mAlarmInfoBean.EventHandler.RecordLatch + FunSDK.TS("s"));
        mSpPirRecordDuration.setValue(mAlarmInfoBean.EventHandler.RecordLatch);
    }







    @Override
    public void onClick(View v){
        int id = v.getId();
        if (id == R.id.detection_alarm) {
            //PIR移动侦测
            if (presenter.getAlarmInfoBean() != null) {
                mLsiPirAlarm.setSwitchState(mLsiPirAlarm.getSwitchState() == SDKCONST.Switch.Open
                        ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
                boolean isOpen = mLsiPirAlarm.getSwitchState() == SDKCONST.Switch.Open;
                if (isOpen) {
                    findViewById(R.id.detection_alarm_ll).setVisibility(View.VISIBLE);
                    mLsiRecordDuration.setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.detection_alarm_ll).setVisibility(View.GONE);
                    mLsiRecordDuration.setVisibility(View.GONE);
                }
                presenter.getAlarmInfoBean().Enable = isOpen;
            }
            presenter.savePirAlarmConfig();
        } else if (id == R.id.detection_sensitive) {
            //PIR灵敏度
            mLsiPirSensitive.toggleExtraView();
        } else if (id == R.id.lsi_detect_switch) {
            //人形检测
            mLsiHumanDetection.setSwitchState(mLsiHumanDetection.getSwitchState() == SDKCONST.Switch.Open
                    ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
            presenter.saveHumanDetectionConfig(mLsiHumanDetection.getSwitchState() == SDKCONST.Switch.Open);
        } else if (id == R.id.detection_schedule) {
            //侦测时间段设置
            presenter.turnToDetectionSchedule();
        } else if (id == R.id.lsi_record_enable) {
            //录像开关
            mLsiRecordEnable.setSwitchState(mLsiRecordEnable.getSwitchState() == SDKCONST.Switch.Open
                    ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
            presenter.saveNetWorkSetEnableVideoConfig(mLsiRecordEnable.getSwitchState() == SDKCONST.Switch.Open);
        } else if (id == R.id.lsi_record_duration) {
            //录像时间
            if (presenter.getAlarmInfoBean() != null) {
                mLsiRecordDuration.toggleExtraView();
            }
        }
    }


    private void initListener() {
        mLsiPirAlarm.setOnClickListener(this);
        mLsiPirSensitive.setOnClickListener(this);
        mLsiHumanDetection.setOnClickListener(this);
        initPirSensitive();
        mLsiPirDetectionSchedule.setOnClickListener(this);

        mLsiRecordDuration.setOnClickListener(this);
        mLsiRecordEnable.setOnClickListener(this);
    }




    /**
     * 报警录像时长配置
     */
    @Override
    public void initPirRecordDuration(boolean isSupportLowPowerLongAlarmRecord) {
        String[] duration = new String[]{"5" + FunSDK.TS("s"), "10" + FunSDK.TS("s"), "15" + FunSDK.TS("s")};
        String[] duration1 = new String[]{"10" + FunSDK.TS("s"), "20" + FunSDK.TS("s"), "30" + FunSDK.TS("s")};
        mSpPirRecordDuration = mLsiRecordDuration.getExtraSpinner();
        if (isSupportLowPowerLongAlarmRecord) {  // 支持这种能力集的报警录像时间改成 10 20 30
            mSpPirRecordDuration.initData(duration1, new Integer[]{10, 20, 30});
        } else {
            mSpPirRecordDuration.initData(duration, new Integer[]{5, 10, 15});
        }

        mSpPirRecordDuration.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
            @Override
            public void onItemClick(int position, String key, Object value) {
                if (presenter.getAlarmInfoBean() != null) {
                    mLsiRecordDuration.setRightText(key);
                    presenter.getAlarmInfoBean().EventHandler.RecordLatch = (int) value;
                }
                presenter.savePirAlarmConfig();
            }
        });
    }

    private void initPirSensitive() {
        //徘徊检测灵敏度
        mSpPirSensitive = mLsiPirSensitive.getExtraSpinner();
        mSpPirSensitive.initData(new String[]{
                FunSDK.TS("TR_PIR_lowest"),
                FunSDK.TS("TR_PIR_Lower"),
                FunSDK.TS("TR_PIR_Medium"),
                FunSDK.TS("TR_PIR_Higher"),
                FunSDK.TS("TR_PIR_Hightext")
        }, new Integer[]{1, 2, 3, 4, 5});
        mSpPirSensitive.setOnExtraSpinnerItemListener((position, key, value) -> {
            mLsiPirSensitive.setRightText(key);
//            mLsiPirSensitive.toggleExtraView(true);
            presenter.saveSensitive((int) value);

        });
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == DETECTION_DURATION_SET_REQUEST) {
                boolean enable = data.getBooleanExtra("enable", false);
                int[][] startEndTimeOne = new int[2][2];
                startEndTimeOne[0] = data.getIntArrayExtra("startTime");
                startEndTimeOne[1] = data.getIntArrayExtra("endTime");
                int[] startTime = data.getIntArrayExtra("startTime");
                int[] endTime = data.getIntArrayExtra("endTime");
                int weekMaskOne = data.getIntExtra("weekMaskOne", 0);
                String times = String.format("%02d:%02d-%02d:%02d", startEndTimeOne[0][0], startEndTimeOne[0][1],
                        startEndTimeOne[1][0], startEndTimeOne[1][1]);
                if (enable) {
                    mLsiPirDetectionSchedule.setRightText(times + "\n" + WeekSelectActivity.getWeeks(weekMaskOne));
                } else {
                    mLsiPirDetectionSchedule.setRightText("");
                }
                presenter.savePirTimeSection(startTime,endTime,enable,weekMaskOne,times);

            }
        }
    }


    public void setLsiHumanDetectionState(boolean isEnable){
        mLsiHumanDetection.setSwitchState(isEnable
                ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
        mLsiHumanDetection.setVisibility(View.VISIBLE);
    }



    public void setLsiRecordEnableState(boolean isEnable){
        mLsiRecordEnable.setVisibility(View.VISIBLE);
        mLsiRecordEnable.setSwitchState(isEnable ?
                SDKCONST.Switch.Open : SDKCONST.Switch.Close);
    }

    public void hidePirView(){
        findViewById(R.id.ll_pir_function).setVisibility(View.GONE);
    }
}
