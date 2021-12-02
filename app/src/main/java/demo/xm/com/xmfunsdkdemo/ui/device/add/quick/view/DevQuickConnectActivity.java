package demo.xm.com.xmfunsdkdemo.ui.device.add.quick.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.utils.LogUtils;
import com.xm.activity.device.monitor.view.XMMonitorActivity;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.quick.listener.DevQuickConnectContract;
import demo.xm.com.xmfunsdkdemo.ui.device.add.quick.presenter.DevQuickConnectPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.view.DevMonitorActivity;
import io.reactivex.annotations.Nullable;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * WIFI快速配置
 * Quickly configure WIFI by sending the SSID and password of WIFI in the broadcast packet
 * Created by jiangping on 2018-10-23.
 */
@RuntimePermissions
public class DevQuickConnectActivity extends DemoBaseActivity<DevQuickConnectPresenter> implements DevQuickConnectContract.IDevQuickConnectView, View.OnClickListener {
    private EditText wifiSSIDEdit;
    private EditText wifiPasswdEdit;
    private Button settingBtn;

    private String settedWifiDevSN = "";
    private TextView settedText;
    private TextView tvResult;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_quick_connect);
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.guide_module_title_device_setwifi));
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(getClass().getName());
        wifiSSIDEdit = findViewById(R.id.editWifiSSID);
        wifiPasswdEdit = findViewById(R.id.editWifiPasswd);
        settingBtn = findViewById(R.id.btnWifiQuickSetting);
        settingBtn.setOnClickListener(this);
        settedText = findViewById(R.id.textWifiSettedDevices);
        DevQuickConnectActivityPermissionsDispatcher.initDataWithPermissionCheck(this);

        //Manually write to WiFi hotspot and start fast network configuration
        findViewById(R.id.btnWifiQuickSettingSimple).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWaitDialog();
                String ssid = wifiSSIDEdit.getText().toString().trim();
                String pwd = wifiPasswdEdit.getText().toString().trim();
                if (StringUtils.isStringNULL(ssid)) {
                    ToastUtils.showLong(R.string.wifi_ssid_not_empty);
                    return;
                }

                int pwdType = 0;
                if (!TextUtils.isEmpty(pwd)) {
                    pwdType = 1;
                }

                presenter.startQuickSetWiFiSimple(ssid, pwd, pwdType);
            }
        });

        tvResult = findViewById(R.id.tv_result_info);
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    protected void initData() {
        if (checkLocationService()) {// Whether to enable location permission
            presenter.checkGetWiFiInfoOk();
        } else {
            XMPromptDlg.onShow(DevQuickConnectActivity.this, FunSDK.TS("System_SDK_INT_Tip"), FunSDK.TS("cancel"), FunSDK.TS("confirm"), null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                presenter.stopQuickSetWiFi();
            }
        }).start();
        super.onDestroy();
    }

    @Override
    public void onUpdateView() {   //wifi information obtained by callback
        String curSSID = presenter.getCurSSID();
        if (!StringUtils.isStringNULL(curSSID)) {
            wifiSSIDEdit.setText(curSSID);
        }

        hideWaitDialog();
    }

    @Override
    public void onAddDevResult(boolean isSuccess) {  //Callback after adding the device
        hideWaitDialog();
        if (isSuccess) {
            turnToActivity(DevMonitorActivity.class);
        }
    }

    @Override
    public void onPrintConfigDev(String printLog) {
        LogUtils.debugInfo("QrCodeConfig", printLog);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                tvResult.setText(tvResult.getText().toString() + "\n" + printLog);
            }
        });
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public DevQuickConnectPresenter getPresenter() {
        return new DevQuickConnectPresenter(this);
    }

    @Override
    public void onClick(View view) {
        showWaitDialog();
        presenter.startQuickSetWiFi(wifiPasswdEdit.getText().toString().trim());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DevQuickConnectActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
