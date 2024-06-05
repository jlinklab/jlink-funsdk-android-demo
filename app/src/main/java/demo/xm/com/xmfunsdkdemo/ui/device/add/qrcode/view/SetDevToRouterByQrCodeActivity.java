package demo.xm.com.xmfunsdkdemo.ui.device.add.qrcode.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.manager.db.XMDevInfo;
import com.utils.LogUtils;
import com.xm.activity.base.XMBaseActivity;
import com.xm.base.code.ErrorCodeManager;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;

import java.util.logging.ErrorManager;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.qrcode.contract.SetDevToRouterByQrCodeContract;
import demo.xm.com.xmfunsdkdemo.ui.device.add.qrcode.presenter.SetDevToRouterByQrCodePresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.view.DevMonitorActivity;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * 通过二维码扫描方式添加设备
 * Devices are added by means of QR code scanning
 * @author hws
 * @class
 * @time 2020/8/11 17:19
 */
@RuntimePermissions
public class SetDevToRouterByQrCodeActivity extends DemoBaseActivity<SetDevToRouterByQrCodePresenter> implements SetDevToRouterByQrCodeContract.ISetDevToRouterByQrCodeView {
    private ImageView ivQrCode;
    private EditText etSsid;
    private EditText etPwd;
    private TextView tvResult;
    private Button btnShowQrCode;

    @Override
    public SetDevToRouterByQrCodePresenter getPresenter() {
        return new SetDevToRouterByQrCodePresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_to_router_by_qr_code);
        initView();
        initListener();
        SetDevToRouterByQrCodeActivityPermissionsDispatcher.initDataWithPermissionCheck(this);
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.libfunsdk_set_dev_to_router_by_qr_code));
        titleBar.setBottomTip(getClass().getName());
        ivQrCode = findViewById(R.id.iv_qr_code);
        etSsid = findViewById(R.id.editWifiSSID);
        etPwd = findViewById(R.id.editWifiPasswd);
        btnShowQrCode = findViewById(R.id.btn_show_qr_code);
        tvResult = findViewById(R.id.tv_result_info);
    }

    private void initListener() {
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });

        btnShowQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.stopSetDevToRouterByQrCode();
                Bitmap bitmap = presenter.startSetDevToRouterByQrCode(etSsid.getText().toString().trim(),etPwd.getText().toString().trim());//Generate a QR code with distribution network information
                if (bitmap != null) {
                    ivQrCode.setImageBitmap(bitmap);
                } else {
                    showToast(getString(R.string.libfunsdk_set_dev_to_router_f), Toast.LENGTH_LONG);
                }
                findViewById(R.id.rl_show_qr_code).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.btn_show_qr_code_simple).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ssid = etSsid.getText().toString().trim();
                String pwd = etPwd.getText().toString().trim();
                if (StringUtils.isStringNULL(ssid)) {
                    ToastUtils.showLong(R.string.the_ssid_is_not_empty);
                    return;
                }

                int pwdType = 0;
                if (!TextUtils.isEmpty(pwd)) {
                    pwdType = 1;
                }

                presenter.stopSetDevToRouterByQrCode();
                Bitmap bitmap = presenter.startSetDevToRouterByQrCodeSimple(ssid, pwd,pwdType);
                if (bitmap != null) {
                    ivQrCode.setImageBitmap(bitmap);
                } else {
                    showToast(getString(R.string.libfunsdk_set_dev_to_router_f), Toast.LENGTH_LONG);
                }
                findViewById(R.id.rl_show_qr_code).setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 需要获取定位权限（获取WiFI列表及信息的时候需要）
     * Location permission is required (when accessing WiFI list and information)
     */
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    protected void initData() {
        if (checkLocationService()) {
            presenter.initWiFi();
            etSsid.setText(presenter.getConnectWiFiSsid());
        } else {
            XMPromptDlg.onShow(SetDevToRouterByQrCodeActivity.this, FunSDK.TS("System_SDK_INT_Tip"), FunSDK.TS("cancel"), FunSDK.TS("confirm"), null,
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
    public Context getContext() {
        return this;
    }

    @Override
    public void onSetDevToRouterResult(boolean isSuccess, XMDevInfo xmDevInfo) {
        if (isSuccess) {
            showToast(getString(R.string.libfunsdk_set_dev_to_router_s), Toast.LENGTH_LONG);
            showWaitDialog();
        }
    }

    @Override
    public void onAddDevToAccountResult(boolean isSuccess, int errorId) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                hideWaitDialog();
                if (isSuccess) {
                    showToast(getString(R.string.add_s), Toast.LENGTH_LONG);
                    tvResult.setText(tvResult.getText().toString() + "\n" + getString(R.string.add_s));
                    presenter.syncDevTimeZone();
                } else {
                    if (errorId == -604101) {
                        showToast(getString(R.string.the_dev_already_exist), Toast.LENGTH_LONG);
                        tvResult.setText(tvResult.getText().toString() + "\n" + getString(R.string.the_dev_already_exist));
                        presenter.syncDevTimeZone();
                    } else {
                        showToast(getString(R.string.add_f) + ":" + errorId, Toast.LENGTH_LONG);
                        tvResult.setText(tvResult.getText().toString() + "\n" + getString(R.string.add_f) + ":" + errorId);
                    }
                }
            }
        });
    }

    @Override
    public void onPrintConfigDev(String printLog) {
        LogUtils.debugInfo("QrCodeConfig",printLog);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                tvResult.setText(tvResult.getText().toString() + "\n" + printLog);
            }
        });
    }

    @Override
    public void onSyncDevTimeResult(boolean isSuccess, int errorId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                XMPromptDlg.onShow(SetDevToRouterByQrCodeActivity.this, getString(R.string.need_turn_to_monitor), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        turnToActivity(DevMonitorActivity.class);
                        finish();
                    }
                },null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stopSetDevToRouterByQrCode();
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SetDevToRouterByQrCodeActivityPermissionsDispatcher.initDataWithPermissionCheck(this);
    }
}
