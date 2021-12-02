package demo.xm.com.xmfunsdkdemo.ui.device.add.ap.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.lib.FunSDK;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.ap.listener.DevApConnectContract;
import demo.xm.com.xmfunsdkdemo.ui.device.add.ap.presenter.DevApConnectPresenter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * AP直连设备界面,显示设备列表菜单
 * AP direct connection device interface, display device list menu
 * Created by jiangping on 2018-10-23.
 */
@RuntimePermissions
public class DevApConnectActivity extends DemoBaseActivity<DevApConnectPresenter> implements DevApConnectContract.IDevApConnectView {
    private Disposable disposable;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dev_ap_connect);

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setLeftClick(this);
        titleBar.setTitleText(getString(R.string.guide_module_title_device_ap));
        titleBar.setBottomTip(getClass().getName());

        DevApConnectActivityPermissionsDispatcher.initDataWithPermissionCheck(this);
    }

    /**
     * 获取摄像头权限
     * Get camera permission
     */
    @NeedsPermission({ Manifest.permission.ACCESS_FINE_LOCATION})
    protected void initData() {
        checkLocationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onUpdateView() {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public DevApConnectPresenter getPresenter() {
        return new DevApConnectPresenter(this);
    }


    /**
     * 检查定位权限
     * Check location permission
     */
    private void checkLocationPermission() {
        if (checkLocationService()) {
            disposable = Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Exception {
                    ScanResult result = presenter.getCurScanResult("WiFi-Repeater");
                    if (result == null) {
                        emitter.onNext(0);
                    } else {
                        emitter.onNext(result);
                    }
                }
            }).subscribeOn(Schedulers.newThread()).doOnSubscribe(new Consumer<Disposable>() {
                @Override
                public void accept(Disposable disposable) {
                    showWaitDialog();
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
                @Override
                public void accept(Object result) throws Exception {
                    if (disposable != null) {
                        disposable.dispose();
                        disposable = null;
                    }
                    hideWaitDialog();
                    if (result instanceof Integer && (Integer) result == 0) {
                        showToast(FunSDK.TS("Network_Error"), Toast.LENGTH_LONG);
                    } else {
                        if ((((ScanResult) result).frequency > 4900 && ((ScanResult) result).frequency < 5900)) {
                            showToast(FunSDK.TS("Frequency_support"),Toast.LENGTH_LONG);
                        } else {
                            presenter.devApConnect();
                        }
                    }
                }
            });
        } else {
            XMPromptDlg.onShow(DevApConnectActivity.this, FunSDK.TS("System_SDK_INT_Tip"), FunSDK.TS("cancel"), FunSDK.TS("confirm"), null,
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DevApConnectActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }
}