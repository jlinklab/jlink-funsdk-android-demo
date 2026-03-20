
package demo.xm.com.xmfunsdkdemo.ui.device.config.networksetting.listener;

import android.net.NetworkInfo;


public abstract interface WifiStateListener {
    public static final int DISCONNECT = 0;// 网络断开
    public static final int CONNECTING = 1;// 正在连接
    public static final int CONNECTED = 2;// 网络连接成功

    /**
     * 网络状态
     *
     * @param state 状态
     * @param type  网络类型 0: WIFI 1:Mobile
     * @param ssid  WiFi热点名称
     */
    public abstract void onNetWorkState(NetworkInfo.DetailedState state, int type, String ssid,String bssid);

    public abstract void onIsWiFiAvailable(boolean isWiFiAvailable);

    void onNetWorkChange(NetworkInfo.DetailedState state, int type, String SSid);
}
