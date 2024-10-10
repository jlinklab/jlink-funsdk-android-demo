package demo.xm.com.xmfunsdkdemo.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.xm.activity.base.XMBaseActivity;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.widget.XTitleBar;

import java.util.Locale;

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
    public static final String androidDoc = "https://docs.jftech.com/docs?menusId=ab0ed73834f54368be3e375075e27fb2&siderId=45357c529496431590a7e3463b7cc520&lang=" + Locale.getDefault().getLanguage();

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

    public void openBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
