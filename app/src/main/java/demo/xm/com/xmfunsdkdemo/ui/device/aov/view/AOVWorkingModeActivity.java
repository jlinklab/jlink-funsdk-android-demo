package demo.xm.com.xmfunsdkdemo.ui.device.aov.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static demo.xm.com.xmfunsdkdemo.ui.device.aov.presenter.AOVWorkingModePresenter.BALANCE_MODE;
import static demo.xm.com.xmfunsdkdemo.ui.device.aov.presenter.AOVWorkingModePresenter.CUSTOM_MODE;
import static demo.xm.com.xmfunsdkdemo.ui.device.aov.presenter.AOVWorkingModePresenter.PERFORMANCE_MODE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.aov.listener.AOVWorkingModeContract;
import demo.xm.com.xmfunsdkdemo.ui.device.aov.presenter.AOVWorkingModePresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.entity.AovWorkModeBean;

/**
 * AOV设备工作模式配置
 */
public class AOVWorkingModeActivity extends BaseConfigActivity<AOVWorkingModePresenter> implements
        AOVWorkingModeContract.IAOVWorkingModeView, View.OnClickListener{


    private ConstraintLayout clPowerSaveing;
    private ConstraintLayout clPerformanceMode;
    private ConstraintLayout clCustomMode;
    private ListSelectItem lisFpsSetting;
    private ListSelectItem lisRecordSetting;
    private ListSelectItem lisAovAlarmTimeInterval;


    private TextView tvPowerSaveingTip;
    private TextView tvTips;
    private TextView tvPerformanceModeTip;
    private ImageView ivPowerSaveingSelect;
    private ImageView ivCustomModeSelect;
    private ImageView ivPerformanceModeSelect;
    private LinearLayout llCustomModeSetting;

    private String devId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aov_working_mode_layout);
        initView();
        initData();
        initListener();
    }

    public void initView() {
        clPowerSaveing = findViewById(R.id.clPowerSaveing);
        clPerformanceMode = findViewById(R.id.clPerformanceMode);
        clCustomMode = findViewById(R.id.clCustomMode);
        lisFpsSetting = findViewById(R.id.lisFpsSetting);
        lisRecordSetting = findViewById(R.id.lisRecordSetting);
        lisAovAlarmTimeInterval = findViewById(R.id.lisAovAlarmTimeInterval);
        tvPowerSaveingTip = findViewById(R.id.tvPowerSaveingTip);
        tvPerformanceModeTip = findViewById(R.id.tvPerformanceModeTip);
        ivPowerSaveingSelect = findViewById(R.id.ivPowerSaveingSelect);
        ivCustomModeSelect = findViewById(R.id.ivCustomModeSelect);
        ivPerformanceModeSelect = findViewById(R.id.ivPerformanceModeSelect);
        llCustomModeSetting = findViewById(R.id.llCustomModeSetting);
        tvTips = findViewById(R.id.tvTips);
    }

    @Override
    public AOVWorkingModePresenter getPresenter() {
        return new AOVWorkingModePresenter(this);
    }

    public void initListener() {

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(FunSDK.TS("TR_Setting_Mode_Of_Work"));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        titleBar.setBottomTip(getClass().getName());

        clPowerSaveing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter.getWorkBean() != null) {
                    presenter.getWorkBean().setMode(BALANCE_MODE);
                }
                saveConfig();
                upDataUI(BALANCE_MODE);
            }

        });
        lisFpsSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AovWorkModeBean workModeBean = presenter.getWorkBean();
                if(workModeBean!=null && workModeBean.getCustom()!=null){
                    Intent intent = new Intent(AOVWorkingModeActivity.this, SetFPSActivity.class);
                    intent.putExtra("fps", workModeBean.getCustom().getFps());
                    intent.putExtra("mode", SetFPSActivity.MODE_FPS);
                    intent.putExtra("devId",devId);
                    startActivityForResult(intent, 0x01);
                }
            }
        });
        lisRecordSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AovWorkModeBean workModeBean = presenter.getWorkBean();
                if(workModeBean!=null && workModeBean.getCustom()!=null){
                    Intent intent = new Intent(AOVWorkingModeActivity.this, SetFPSActivity.class);
                    intent.putExtra("recordLatch", workModeBean.getCustom().getRecordLatch());
                    intent.putExtra("mode", SetFPSActivity.MODE_RECORD_LATCH);
                    intent.putExtra("devId",devId);
                    startActivityForResult(intent, 0x01);
                }
            }
        });
        lisAovAlarmTimeInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AovWorkModeBean workModeBean = presenter.getWorkBean();
                if(workModeBean!=null){
                    Intent intent = new Intent(AOVWorkingModeActivity.this, SetFPSActivity.class);
                    intent.putExtra("alarmTimeInterval", workModeBean.getAlarmHoldTime());
                    intent.putExtra("mode", SetFPSActivity.MODE_ALARM_TIME_INTERVAL);
                    intent.putExtra("devId",devId);
                    startActivityForResult(intent, 0x01);
                }
            }
        });
        clPerformanceMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AovWorkModeBean workModeBean = presenter.getWorkBean();
                if(workModeBean!=null){
                    workModeBean.setMode(PERFORMANCE_MODE);
                }
                saveConfig();
                upDataUI(PERFORMANCE_MODE);
            }
        });
        clCustomMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AovWorkModeBean workModeBean = presenter.getWorkBean();
                if(workModeBean!=null){
                    workModeBean.setMode(CUSTOM_MODE);
                }
                saveConfig();
                upDataUI(CUSTOM_MODE);
            }
        });

    }

    public void showTips(boolean showTips,int powerThreshold) {
        if (showTips) {
            tvTips.setText(String.format(FunSDK.TS("TR_Setting_AOV_Low_Battery_Mode_Description"),""+ powerThreshold+"%"));
            tvTips.setVisibility(VISIBLE);
        } else {
            tvTips.setVisibility(GONE);
        }
    }
    public void getConfigBack(boolean getConfigBack) {
        if (getConfigBack) {
            hideWaitDialog();
        } else {
            showWaitDialog();
        }
    }


    public void saveSuccess(boolean saveSuccess) {
        hideWaitDialog();
        if (saveSuccess) {
            Toast.makeText(this, FunSDK.TS("Save_Success"), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void showAovInterval(boolean isSupport) {
        lisAovAlarmTimeInterval.setVisibility(isSupport ? VISIBLE : GONE);
    }

    public void showWorkModel(AovWorkModeBean workBean) {
        if (workBean != null) {
            upDataUI(workBean.getMode());
            if(workBean.getAlarmHoldTime() == 0){
                lisAovAlarmTimeInterval.setRightText(FunSDK.TS("TR_Smart_PowerReal"));
            }else{
                lisAovAlarmTimeInterval.setRightText( ""+workBean.getAlarmHoldTime()+"s" );
            }
        }
    }

    public void onBackPressed() {
        AovWorkModeBean workModeBean = presenter.getWorkBean();
        if (workModeBean != null) {
            Intent intent = new Intent();
            intent.putExtra("mode", workModeBean.getMode());
            setResult(0x02, intent);
        }
        super.onBackPressed();

    }

    public void saveConfig() {
        showWaitDialog();
        presenter.setAovWorkModel();
    }
    @Override
    public void onSupportDoubleLightBoxCameraResult(boolean isSupport) {
        if(isSupport){
            tvPowerSaveingTip.setText(String.format(FunSDK.TS("TR_Setting_AOV_FPS_Description"),
                    presenter.getWorkBean().getBalance().getFps()));
            tvPerformanceModeTip.setText(String.format(FunSDK.TS("TR_Setting_AOV_FPS_Description")
                    ,presenter.getWorkBean().getBalance().getFps()));
        }else{
            tvPowerSaveingTip.setText(String.format(FunSDK.TS("TR_Setting_AOV_BlackLight_FPS_Description")
                    ,presenter.getWorkBean().getBalance().getFps()));
            tvPerformanceModeTip.setText(String.format(FunSDK.TS("TR_Setting_AOV_BlackLight_FPS_Description")
                    ,presenter.getWorkBean().getBalance().getFps()));
        }
    }
    public void upDataUI(String mode) {
        presenter.checkIsSupportDoubleLightBoxCamera();
        if (StringUtils.contrast(mode, BALANCE_MODE)) {
            ivPowerSaveingSelect.setImageResource(R.drawable.check_pre);
            ivCustomModeSelect.setImageResource(R.drawable.check_nor);
            ivPerformanceModeSelect.setImageResource(R.drawable.check_nor);


            llCustomModeSetting.setVisibility(GONE);


        } else if (StringUtils.contrast(mode, PERFORMANCE_MODE)) {
            ivPowerSaveingSelect.setImageResource(R.drawable.check_nor);
            ivCustomModeSelect.setImageResource(R.drawable.check_nor);
            ivPerformanceModeSelect.setImageResource(R.drawable.check_pre);
            llCustomModeSetting.setVisibility(GONE);

        } else if (StringUtils.contrast(mode, CUSTOM_MODE)) {
            ivPowerSaveingSelect.setImageResource(R.drawable.check_nor);
            ivCustomModeSelect.setImageResource(R.drawable.check_pre);
            ivPerformanceModeSelect.setImageResource(R.drawable.check_nor);
            AovWorkModeBean workModeBean = presenter.getWorkBean();
            if (workModeBean != null && workModeBean.getCustom() != null) {
                lisRecordSetting.setRightText( ""+workModeBean.getCustom().getRecordLatch()+"s" );
                lisFpsSetting.setRightText(""+workModeBean.getCustom().getFps()+"fps");
            }

            llCustomModeSetting.setVisibility(VISIBLE);

        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String mode = data.getStringExtra("mode");
            AovWorkModeBean workModeBean = presenter.getWorkBean();
            if (StringUtils.contrast(mode, SetFPSActivity.MODE_FPS)) {
                if(workModeBean!=null && workModeBean.getCustom()!=null){
                    workModeBean.getCustom().setFps(data.getStringExtra(SetFPSActivity.MODE_FPS));
                    lisFpsSetting.setRightText(""+workModeBean.getCustom().getFps()+"fps");
                }

            } else if (StringUtils.contrast(mode, SetFPSActivity.MODE_RECORD_LATCH)) {
                if(workModeBean!=null && workModeBean.getCustom()!=null){
                    workModeBean.getCustom().setRecordLatch(data.getIntExtra(SetFPSActivity.MODE_RECORD_LATCH, 10));
                    lisRecordSetting.setRightText( ""+workModeBean.getCustom().getRecordLatch()+"s" );
                }
            }else if(StringUtils.contrast(mode,SetFPSActivity.MODE_ALARM_TIME_INTERVAL)){
                if(workModeBean!=null){
                    workModeBean.setAlarmHoldTime(data.getIntExtra(SetFPSActivity.MODE_ALARM_TIME_INTERVAL, 0));
                    if(workModeBean.getAlarmHoldTime() == 0){
                        lisAovAlarmTimeInterval.setRightText(FunSDK.TS("TR_Smart_PowerReal"));
                    }else{
                        lisAovAlarmTimeInterval.setRightText( ""+workModeBean.getAlarmHoldTime()+"s" );
                    }
                }


            }
            saveConfig();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.releaseIDRModel();
    }

    public void initData() {
        showWaitDialog();
        devId = getIntent().getStringExtra("devId");
        presenter.setDevId(devId);
        presenter.getDevBattery(this);
        presenter.getAovWorkModel();
        presenter.getLowElectrMode();
        if(getIntent() != null){
            presenter.setPercent(getIntent().getIntExtra("curBattery",-1));
        }
        presenter.checkIsSupportAovAlarmHold();

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
    public void onClick(View v) {

    }

    @Override
    public Activity getActivity() {
        return AOVWorkingModeActivity.this;
    }



}
