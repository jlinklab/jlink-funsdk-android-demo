package demo.xm.com.xmfunsdkdemo.ui.device.config.networksetting.view;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lib.FunSDK;
import  demo.xm.com.xmfunsdkdemo.bean.wifi.WifiAP;
import com.utils.XMWifiManager;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ButtonCheck;
import com.xm.ui.widget.ItemSetLayout;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.dialog.LoadingDialog;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;

import demo.xm.com.xmfunsdkdemo.ui.activity.MainActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.networksetting.listener.NetworkSettingContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.networksetting.presenter.NetworkSettingPresenter;
import demo.xm.com.xmfunsdkdemo.utils.WifiNameAndPwdManager;

/**
 * IMP 网络设置
 */

public class DevWirelessNetWorkSettingActivity extends DemoBaseActivity<NetworkSettingPresenter>
        implements View.OnClickListener , NetworkSettingContract.INetworkSettingView{
    private static final String TAG = "DevWirelessNetWorkSettingActivity";
    private static final int REQUEST_CODE = 0x100;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    protected TextView mWifiName;
    protected EditText mWifiPassword;
    protected ButtonCheck mShowPassword;
    protected CheckBox mAPSetting;
    protected CheckBox mRouterSetting;

    protected LinearLayout mRouterLayout;
    private LinearLayout mLlNetworkModeSelect;
    protected TextView mAPModelTip;

    private WifiAPAdapter wifiAPAdapter;




    @Override
    public NetworkSettingPresenter getPresenter() {
        return new NetworkSettingPresenter(this);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_setting);
        presenter.setDevId(getIntent().getStringExtra("devId"));
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(FunSDK.TS("Network_setting"));
        titleBar.setBottomTip(getClass().getName());
        mSwipeRefreshLayout = ((ItemSetLayout)findViewById(R.id.il_wifi_list))
                .getMainLayout().findViewById(R.id.sr_wifi_list);
        mSwipeRefreshLayout.setEnabled(true);
        mRecyclerView = ((ItemSetLayout)findViewById(R.id.il_wifi_list))
                .getMainLayout().findViewById(R.id.recycler_wifi_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWifiName = findViewById(R.id.wifi);
        mWifiPassword = findViewById(R.id.wifi_psd);
        mShowPassword = findViewById(R.id.psd_show);
        mAPSetting = findViewById(R.id.ap_setting);
        mRouterSetting = findViewById(R.id.router_setting);
        mLlNetworkModeSelect = findViewById(R.id.ll_network_mode_select);
        mRouterLayout = findViewById(R.id.router_layout);
        mAPModelTip = findViewById(R.id.AP_model_tip);
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        mAPSetting.setOnClickListener(this);

        wifiAPAdapter = new WifiAPAdapter(this);
        mRecyclerView.setAdapter(wifiAPAdapter);
        wifiAPAdapter.setOnItemWifiListener(new WifiAPAdapter.OnItemWifiListener() {
            @Override
            public void onItemClick(int pos) {
                WifiAP scanResult = wifiAPAdapter.getItem(pos);
                if (scanResult != null) {
                    if (XMWifiManager.isXMDeviceWifi(scanResult.getSSID())) {
                        presenter.getAPSwitch().RouterToDevAP(scanResult.getSSID(), false);
                    } else {
                        mWifiName.setText(scanResult.getSSID());
                        String pwd = WifiNameAndPwdManager.getInstance(DevWirelessNetWorkSettingActivity.this)
                                .getWiFiPwd(DevWirelessNetWorkSettingActivity.this, mWifiName.getText().toString());
                        mWifiPassword.setText(pwd);
                    }
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mSwipeRefreshLayout.isEnabled()) {
                    presenter.scanWifi(XMWifiManager.WIFI_TYPE.ROUTER);
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        mShowPassword.setOnButtonClick(new ButtonCheck.OnButtonClickListener() {
            @Override
            public boolean onButtonClick(ButtonCheck bc, boolean bChecked) {
                if (mWifiPassword.getTransformationMethod() == null) {
                    mWifiPassword.setTransformationMethod(new PasswordTransformationMethod());
                } else {
                    mWifiPassword.setTransformationMethod(null);
                }
                return true;
            }
        });
        mAPSetting.setOnClickListener(this);
        mRouterSetting.setOnClickListener(this);

        if (presenter.isisXMDeviceWifi()) {
            mRouterLayout.setVisibility(View.GONE);
            mAPModelTip.setVisibility(View.VISIBLE);
            mAPSetting.setChecked(true);
        } else {
            mRouterSetting.setChecked(true);
            mRouterLayout.setVisibility(View.VISIBLE);
            mAPModelTip.setVisibility(View.GONE);
        }
        if (presenter.isShareDev()) {
            mShowPassword.setVisibility(View.GONE);
        }

        mWifiPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mWifiPassword.getText().length() == 0 && !presenter.isShareDev()) {
                    mShowPassword.setVisibility(View.VISIBLE);
                }
            }
        });

        titleBar.setRightTitleText(FunSDK.TS("Save"));
        titleBar.setRightTvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                saveConfig();
            }
        });

        if (presenter.isWBS()) {
            mLlNetworkModeSelect.setVisibility(View.GONE);
        }else {
            mLlNetworkModeSelect.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ap_setting:
                if (mAPSetting.isChecked()) {
                    mRouterSetting.setChecked(false);
                    mRouterLayout.setVisibility(View.GONE);
                    mAPModelTip.setVisibility(View.VISIBLE);
                } else {
                    mRouterSetting.setChecked(true);
                    mRouterLayout.setVisibility(View.VISIBLE);
                    mAPModelTip.setVisibility(View.GONE);
                }
                break;
            case R.id.router_setting:
                if (mRouterSetting.isChecked()) {
                    mAPSetting.setChecked(false);
                    mRouterLayout.setVisibility(View.VISIBLE);
                    mAPModelTip.setVisibility(View.GONE);
                } else {
                    mAPSetting.setChecked(true);
                    mRouterLayout.setVisibility(View.GONE);
                    mAPModelTip.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }

    }

    private void saveConfig() {

        if (mAPSetting.isChecked()) {
            XMPromptDlg.onShow(this, FunSDK.TS("AP_model_tip"), FunSDK.TS("cancel"), FunSDK.TS("confirm"), null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoadingDialog.getInstance(DevWirelessNetWorkSettingActivity.this).show();
                    presenter.switchToDeviceAp();
                }
            });
        } else if (mRouterSetting.isChecked()) {
            if (!mWifiName.getText().equals("")) {
                XMPromptDlg.onShow(this, FunSDK.TS("Switch_link_model_router"), FunSDK.TS("cancel"), FunSDK.TS("confirm"), null, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.changeRouterWifi(mWifiName.getText().toString(),mWifiPassword.getText().toString());
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), FunSDK.TS("Enter_link_wifi"), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), FunSDK.TS("Select_switch_model"), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.scanWifi(XMWifiManager.WIFI_TYPE.ROUTER);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        presenter.onRelease();
    }


    public void dealWithChangeWifiFailed(String ssid) {
        String info = String.format(FunSDK.TS("TR_Switch_WiFi_F"), ssid);

        XMPromptDlg.onShow(DevWirelessNetWorkSettingActivity.this, info,
                FunSDK.TS("cancel"), FunSDK.TS("System_wifi_setting"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        turnToActivity(MainActivity.class);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        openActivity(MainActivity.class);
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                }, false);
    }


    @Override
    public void onShowWaitDialog() {

    }

    @Override
    public void onHideWaitDialog() {
        LoadingDialog.getInstance(this).dismiss();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onShowWifiNameAndPassword(String ssid, String password) {
        mWifiName.setText(ssid);
        mWifiPassword.setText(password);
    }


    @Override
    public void goMainActivity() {
        finish();
        turnToActivity(MainActivity.class);
    }

    @Override
    public void onSwitchToDeviceSuccess() {
        Toast.makeText(this, FunSDK.TS("Save_Success"), Toast.LENGTH_SHORT).show();
        XMPromptDlg.onShow(this, FunSDK.TS("TR_Dev_WiFi_To_AP_S"), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMainActivity();
            }
        });
    }

    @Override
    public void onGetWifiListResult(boolean isSuccess,List<WifiAP> wifiList) {
        LoadingDialog.getInstance(this).dismiss();
        mSwipeRefreshLayout.setRefreshing(false);
        if(isSuccess){
            wifiAPAdapter.setWifiApList(wifiList);
        } else  {
            Toast.makeText(this,FunSDK.TS("get_config_f"),
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(presenter.isSearchDevice()){
            LoadingDialog.getInstance(this).show(FunSDK.TS("Search_device"));
        }
    }
}
