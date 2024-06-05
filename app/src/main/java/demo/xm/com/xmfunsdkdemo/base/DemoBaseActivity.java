package demo.xm.com.xmfunsdkdemo.base;

import android.Manifest;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.xm.activity.base.XMBaseActivity;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication;
import demo.xm.com.xmfunsdkdemo.ui.dialog.PermissionDialog;
import demo.xm.com.xmfunsdkdemo.utils.MobileInfoUtils;
import io.reactivex.functions.Consumer;

/**
 * 动态申请权限封装 Activity（Android 6.0 以上）
 * Dynamically apply for permission
 * Created by hanzhenbo on 2017-06-28.
 */

public abstract class DemoBaseActivity <T extends XMBasePresenter> extends XMBaseActivity<T> {
    protected XTitleBar titleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (titleBar != null) {
            titleBar.setBottomTip(getClass().getName());
        }

        if (getApplication() instanceof SDKDemoApplication) {
            ((SDKDemoApplication) getApplication()).addActivity(this);
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
}
