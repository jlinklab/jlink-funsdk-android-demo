package demo.xm.com.xmfunsdkdemo.ui.device.add.ap.listener;

import android.content.Context;
import android.net.wifi.ScanResult;

/**
 * AP直连设备界面,显示设备列表菜单
 * AP direct connection device interface, display device list menu
 * Created by jiangping on 2018-10-23.
 */
public class DevApConnectContract {
    public interface IDevApConnectView {
        void onUpdateView();
        Context getContext();
    }

    public interface IDevApConnectPresenter {
        void devApConnect();
        ScanResult getCurScanResult(String ssid);
    }
}