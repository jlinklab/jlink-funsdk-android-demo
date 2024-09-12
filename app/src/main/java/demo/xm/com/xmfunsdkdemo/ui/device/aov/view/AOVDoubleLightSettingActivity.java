package demo.xm.com.xmfunsdkdemo.ui.device.aov.view;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.lib.FunSDK;
import com.lib.SDKCONST;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.aov.listener.AOVDoubleLightSettingContract;
import demo.xm.com.xmfunsdkdemo.ui.device.aov.presenter.AOVDoubleLightSettingPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;

/**
 * AOV设备双光灯配置
 */
public class AOVDoubleLightSettingActivity extends BaseConfigActivity<AOVDoubleLightSettingPresenter> implements
        AOVDoubleLightSettingContract.IAOVDoubleLightSettingView, View.OnClickListener{
    private ListSelectItem mLsiNightVision;
    private ListSelectItem mLsiFullColor;
    private ListSelectItem mLsiDoubleLight;
    private ListSelectItem mLisIndicatorLight; // 指示灯开关
    private ListSelectItem mMicroLight; // 微光补灯

    private String devId;



    @Override
    public AOVDoubleLightSettingPresenter getPresenter() {
        return new AOVDoubleLightSettingPresenter(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aov_double_light_setting);
        devId = getIntent().getStringExtra("devId");
        presenter.setDevId(devId);
        initView();
        presenter.getCameraDayLightModes();
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
        titleBar.setBottomTip(getClass().getName());
        mLsiNightVision = findViewById(R.id.lsi_night_vision);
        mLsiFullColor = findViewById(R.id.lsi_full_color);
        mLsiDoubleLight = findViewById(R.id.lsi_double_light);
        mLisIndicatorLight = findViewById(R.id.lis_indicator_light);
        mMicroLight = findViewById(R.id.lis_Micro_light);
        mLsiNightVision.setOnClickListener(this);
        mLsiFullColor.setOnClickListener(this);
        mLsiDoubleLight.setOnClickListener(this);
        mLisIndicatorLight.setOnClickListener(this);
        mMicroLight.setOnClickListener(this);
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
    public void showStatusLed(boolean isSupport){
        if(isSupport){
            mLisIndicatorLight.setVisibility(View.VISIBLE);
        } else {
            mLisIndicatorLight.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.lsi_night_vision:
                mLsiFullColor.setRightImage(SDKCONST.Switch.Close);
                mLsiNightVision.setRightImage(SDKCONST.Switch.Open);
                mLsiDoubleLight.setRightImage(SDKCONST.Switch.Close);
                presenter.saveWorkMode("Close");

                break;
            case R.id.lsi_full_color:
                mLsiFullColor.setRightImage(SDKCONST.Switch.Open);
                mLsiNightVision.setRightImage(SDKCONST.Switch.Close);
                mLsiDoubleLight.setRightImage(SDKCONST.Switch.Close);
                presenter.saveWorkMode("Auto");
                break;
            case R.id.lsi_double_light:
                mLsiFullColor.setRightImage(SDKCONST.Switch.Close);
                mLsiNightVision.setRightImage(SDKCONST.Switch.Close);
                mLsiDoubleLight.setRightImage(SDKCONST.Switch.Open);
                presenter.saveWorkMode("Intelligent");
                break;
            case R.id.lis_indicator_light:
                mLisIndicatorLight.setSwitchState(mLisIndicatorLight.getSwitchState() == SDKCONST.Switch.Open
                        ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
                presenter.saveIndicatorLight(mLisIndicatorLight.getSwitchState());
                break;
            case R.id.lis_Micro_light:
                mMicroLight.setSwitchState(mMicroLight.getSwitchState() == SDKCONST.Switch.Open
                        ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
                presenter.saveMicroLight(mMicroLight.getSwitchState());
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
        return AOVDoubleLightSettingActivity.this;
    }

    @Override
    public void showWorkMode(String workMode){
        if ("Auto".equals(workMode)) {
            mLsiFullColor.setRightImage(SDKCONST.Switch.Open);
            mLsiNightVision.setRightImage(SDKCONST.Switch.Close);
            mLsiDoubleLight.setRightImage(SDKCONST.Switch.Close);
        } else if ("Close".equals(workMode)
                || "Off".equals(workMode)) {
            mLsiFullColor.setRightImage(SDKCONST.Switch.Close);
            mLsiNightVision.setRightImage(SDKCONST.Switch.Open);
            mLsiDoubleLight.setRightImage(SDKCONST.Switch.Close);
        } else if ("Intelligent".equals(workMode)) {
            mLsiFullColor.setRightImage(SDKCONST.Switch.Close);
            mLsiNightVision.setRightImage(SDKCONST.Switch.Close);
            mLsiDoubleLight.setRightImage(SDKCONST.Switch.Open);
        }
    }


    @Override
    public void showCameraDayLightModes(boolean supportNightVision,boolean supportFullColor,boolean supportDoubleLight){
        mLsiNightVision.setVisibility(supportNightVision ? View.VISIBLE : View.GONE);
        mLsiFullColor.setVisibility(supportFullColor ? View.VISIBLE : View.GONE);
        mLsiDoubleLight.setVisibility(supportDoubleLight ? View.VISIBLE : View.GONE);
    }


    @Override
    public void showIndicatorLight(int state){
        mLisIndicatorLight.setSwitchState(state);
    }
    @Override
    public void showMicroLight(int state){
        mMicroLight.setSwitchState(state);
    }

}
