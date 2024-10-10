package demo.xm.com.xmfunsdkdemo.ui.device.aov.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lib.FunSDK;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.aov.listener.AovSettingContract;
import demo.xm.com.xmfunsdkdemo.ui.device.aov.presenter.AovSettingPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;

/**
 * AOV设备相关配置，包括电池管理/工作模式/灯光设置
 */
public class AovSettingActivity extends BaseConfigActivity<AovSettingPresenter> implements
        AovSettingContract.IAovSettingView{

    private ListSelectItem lsiBatteryManagerSettings;
    private ListSelectItem lsiWorkModeSettings;
    private ListSelectItem lsLightSettings;


    private String devId;

    @Override
    public AovSettingPresenter getPresenter() {
        return new AovSettingPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aov_setting);
        devId = getIntent().getStringExtra("devId");
        presenter.setDevId(devId);
        initView();
        presenter.checkSupportAovAbility();
    }

    private void initView() {
        titleBar = findViewById(R.id.xb_idr_setting);
        titleBar.setTitleText(getString(R.string.aov_device));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });

        lsiBatteryManagerSettings = findViewById(R.id.lsi_battery_manager_settings);
        lsiBatteryManagerSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // AOV电池管理
                Intent intent = new Intent(AovSettingActivity.this, AovBatteryManagerActivity.class);
                intent.putExtra("devId",devId);
                startActivity(intent);
            }
        });

        lsiWorkModeSettings = findViewById(R.id.lsi_work_mode_settings);
        lsLightSettings = findViewById(R.id.lsi_light_settings);
        lsiWorkModeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AovSettingActivity.this, AOVWorkingModeActivity.class);
                intent.putExtra("devId",devId);
                startActivityForResult(intent,0x01);
            }
        });

        lsLightSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //灯光设置
                presenter.checkSupportLightType();
            }
        });

    }


    @Override
    public void onSupportBlackLightResult() {
        //黑光
        Intent intent = new Intent(AovSettingActivity.this, AOVBlackLightSettingActivity.class);
        intent.putExtra("devId",devId);
        startActivity(intent);
    }

    @Override
    public void onSupportDoubleLightResult() {
        //双光
        Intent intent = new Intent(AovSettingActivity.this, AOVDoubleLightSettingActivity.class);
        intent.putExtra("devId",devId);
        startActivity(intent);
    }

    /**
     * 显示AOV功能的支持状态
     * 包括对灯光的支持、工作模式的支持以及电池管理的支持
     *
     * @param isSupportLight           表示是否支持灯光功能
     * @param isSupportWorkMode        表示是否支持工作模式的切换
     * @param isSupportBatteryManager  表示是否支持电池管理功能
     */
    public void showSupportAovAbility(boolean isSupportLight,boolean isSupportWorkMode,
                                      boolean isSupportBatteryManager){
        lsLightSettings.setVisibility(isSupportLight?View.VISIBLE:View.GONE);
        lsiBatteryManagerSettings.setVisibility(isSupportWorkMode?View.VISIBLE:View.GONE);
        lsiWorkModeSettings.setVisibility(isSupportBatteryManager?View.VISIBLE:View.GONE);
        if(!isSupportLight && !isSupportWorkMode && !isSupportBatteryManager){
            Toast.makeText(AovSettingActivity.this, FunSDK.TS("not_support_aov_ability"), Toast.LENGTH_LONG).show();
        }

    }
}
