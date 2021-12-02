package demo.xm.com.xmfunsdkdemo.ui.device.add.quick.listener;

import android.content.Context;

/**
 * WiFi快速配置界面,根据WiFi的SSID及密码,快速配置WIFI
 * WiFi quick configuration interface, according to the WiFi SSID and password, quickly configure WIFI
 * Created by jiangping on 2018-10-23.
 */
public class DevQuickConnectContract {
    public interface IDevQuickConnectView {
        void onUpdateView();

        void onAddDevResult(boolean isSuccess);

        void onPrintConfigDev(String printLog);
        Context getContext();
    }

    public interface IDevQuickConnectPresenter {
        String getCurSSID();

        void startQuickSetWiFi(String pwd);

        void startQuickSetWiFiSimple(String ssid, String pwd, int pwdType);

        void stopQuickSetWiFi();
    }
}
