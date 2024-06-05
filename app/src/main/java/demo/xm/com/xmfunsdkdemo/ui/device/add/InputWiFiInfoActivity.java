package demo.xm.com.xmfunsdkdemo.ui.device.add;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.utils.XMWifiManager;
import com.xm.activity.base.XMBaseActivity;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth.DevBluetoothListActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth.adapter.DevBluetoothListAdapter;
import demo.xm.com.xmfunsdkdemo.ui.device.add.quick.view.DevQuickConnectActivity;
import demo.xm.com.xmfunsdkdemo.utils.SPUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * WiFi SSID和密码输入
 */
@RuntimePermissions
public class InputWiFiInfoActivity extends DemoBaseActivity {
    private EditText wifiSSIDEdit;//WiFi 热点名称
    private EditText wifiPasswdEdit;// WiFi 密码
    private Button btnNext;//下一步
    private Disposable disposable;
    private XMWifiManager xmWifiManager;
    private ScanResult scanResult;
    private String mac;//蓝牙mac地址

    @Override
    public XMBasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_set_wifi);
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });

        titleBar.setTitleText(getString(R.string.input_wifi_info));
        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //WiFi热点
                String ssid = wifiSSIDEdit.getText().toString().trim();
                //WiFi密码
                String password = wifiPasswdEdit.getText().toString().trim();
                SPUtil.getInstance(InputWiFiInfoActivity.this).setSettingParam("wifi_ssid", ssid);
                SPUtil.getInstance(InputWiFiInfoActivity.this).setSettingParam("wifi_password", password);
                Intent intent = new Intent();
                intent.putExtra("ssid", ssid);
                intent.putExtra("password", password);
                intent.putExtra("mac",mac);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        wifiSSIDEdit = findViewById(R.id.editWifiSSID);
        wifiPasswdEdit = findViewById(R.id.editWifiPasswd);

        InputWiFiInfoActivityPermissionsDispatcher.initDataWithPermissionCheck(this);
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    protected void initData() {
        xmWifiManager = XMWifiManager.getInstance(this);
        if (checkLocationService()) {// Whether to enable location permission
            checkGetWiFiInfoOk();
        } else {
            XMPromptDlg.onShow(InputWiFiInfoActivity.this, FunSDK.TS("System_SDK_INT_Tip"), FunSDK.TS("cancel"), FunSDK.TS("confirm"), null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
        }
    }

    protected boolean checkLocationService() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean isOpenGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            return isOpenGPS;
        } else {
            return true;
        }
    }

    /**
     * Confirm that the current WiFi information can be obtained normally
     */
    private void checkGetWiFiInfoOk() {
        disposable = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Exception {
                String curSSID = xmWifiManager.getSSID();
                scanResult = xmWifiManager.getCurScanResult(curSSID);
                if (scanResult == null) {
                    emitter.onNext(0);
                } else {
                    emitter.onNext(scanResult);
                }
            }
        }).subscribeOn(Schedulers.newThread()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) {

            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object result) throws Exception {
                if (disposable != null) {
                    disposable.dispose();
                    disposable = null;
                }
                if (result instanceof Integer && (Integer) result == 0) {
                    Toast.makeText(InputWiFiInfoActivity.this, FunSDK.TS("Network_Error"), Toast.LENGTH_LONG).show();
                } else {
                    if ((((ScanResult) result).frequency > 4900 && ((ScanResult) result).frequency < 5900)) {
                        Toast.makeText(InputWiFiInfoActivity.this, FunSDK.TS("Frequency_support"), Toast.LENGTH_LONG).show();
                    }

                    String curSSID = xmWifiManager.getSSID();
                    if (!StringUtils.isStringNULL(curSSID)) {
                        wifiSSIDEdit.setText(curSSID);
                        String password = SPUtil.getInstance(InputWiFiInfoActivity.this).getSettingParam("wifi_password", "");
                        wifiPasswdEdit.setText(password);
                    }
                }
            }
        });
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        InputWiFiInfoActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
