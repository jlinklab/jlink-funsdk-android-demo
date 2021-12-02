package demo.xm.com.xmfunsdkdemo.ui.device.add.ap.presenter;

import android.net.NetworkInfo;
import android.net.wifi.ScanResult;

import com.manager.device.DeviceManager;
import com.utils.XMWiFiConnectManager;
import com.utils.XMWifiManager;

import com.xm.activity.base.XMBasePresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.add.ap.listener.DevApConnectContract;

/**
 * AP直连设备界面,显示设备列表菜单
 * AP direct connection device interface, display device list menu
 * Created by jiangping on 2018-10-23.
 */
public class DevApConnectPresenter extends XMBasePresenter<DeviceManager> implements
        DevApConnectContract.IDevApConnectPresenter,XMWiFiConnectManager.OnWifiStateListener {
    public static final String WIFI_SSID = "WiFi-Repeater";
    private DevApConnectContract.IDevApConnectView iDevApConnectView;
    private XMWiFiConnectManager xmWiFiConnectManager;
    private ScanResult scanResult;
    public DevApConnectPresenter(DevApConnectContract.IDevApConnectView iDevApConnectView) {
        this.iDevApConnectView = iDevApConnectView;
        xmWiFiConnectManager = new XMWiFiConnectManager(iDevApConnectView.getContext(),
                XMWifiManager.getInstance(iDevApConnectView.getContext()));
        xmWiFiConnectManager.setWifiStateListener(this);
    }

    @Override
    public void devApConnect() {
        xmWiFiConnectManager.connectSSIDNoPwd(WIFI_SSID);
    }

    @Override
    public ScanResult getCurScanResult(String ssid) {
        XMWifiManager xmWifiManager = xmWiFiConnectManager.getWifiManager();
        if (xmWifiManager != null) {
            scanResult = xmWifiManager.getCurScanResult(ssid);
        }
        return scanResult;
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void onNetWorkState(NetworkInfo.DetailedState state, int type, String ssid) {

    }

    @Override
    public void onIsWiFiAvailable(boolean isWiFiAvailable) {

    }

    @Override
    public void onNetWorkChange(NetworkInfo.DetailedState state, int type, String ssid) {
        switch (state) {
            case CONNECTED:
                break;
            case CONNECTING:
                break;
            case DISCONNECTED:
                break;
            case DISCONNECTING:
                break;
        }
    }
}
