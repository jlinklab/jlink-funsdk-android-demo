
package demo.xm.com.xmfunsdkdemo.utils;

import static demo.xm.com.xmfunsdkdemo.base.DemoConstant.ROUTER_WIFI_SSID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.lib.SDKCONST;
import com.lib.SDKCONST.NetWorkType;
import com.lib.sdk.bean.StringUtils;
import com.utils.BaseThreadPool;
import com.utils.CheckNetWork;
import com.utils.XMWifiManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.ui.device.config.networksetting.listener.WifiStateListener;


public class APAutomaticSwitch {
    private static String MYLOG = APAutomaticSwitch.class.getClass()
            .getSimpleName();
    private HashMap<String, ScanResult> mEnableWifi = null;
    private Context mContext;
    private XMWifiManager mWifiManager;
    private IntentFilter mWifiFilter = null;
    private String mTryConnectSSID;
    private static final String TAG = "APAutomaticSwitch";

    public APAutomaticSwitch(Context context, XMWifiManager wifiManager) {
        initData(context, wifiManager);
    }

    private void initData(Context context, XMWifiManager wifiManager) {
        this.mContext = context;
        this.mWifiManager = wifiManager;
        mEnableWifi = new HashMap<String, ScanResult>();
        synchronized (mWifiConnectReceiver) {
            if (mWifiFilter == null) {
                mWifiFilter = new IntentFilter();
                mWifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                mWifiFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                mWifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    mContext.registerReceiver(mWifiConnectReceiver, mWifiFilter, Context.RECEIVER_EXPORTED);
                } else {
                    mContext.registerReceiver(mWifiConnectReceiver, mWifiFilter);
                }
            } else {
                onRelease();
            }
        }
        BaseThreadPool.getInstance().doTaskBySinglePool(new Thread() {
            @Override
            public void run() {
                mWifiManager.startScan(0, 1000);
                List<ScanResult> lists = mWifiManager.getWifiList();
                for (ScanResult result : lists) {
                    mEnableWifi.put(result.SSID, result);
                }
                super.run();
            }
        }, 4);
    }

    private BroadcastReceiver mWifiConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction() == ConnectivityManager.CONNECTIVITY_ACTION) {// 监听是否连接上wifi
                if (null != mWifiStateLs && null != mWifiManager) {
                    WifiInfo wifiInfo = mWifiManager.getWifiInfo();
                    if (null == wifiInfo) {
                        return;
                    }
                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    // 为了获取IP 延时让APP与设备交互
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mWifiStateLs.onNetWorkState(info.getDetailedState(), info.getType(), wifiInfo.getSSID(),wifiInfo.getBSSID());
                    Log.d(TAG, "state  :" + info.toString());
                    Log.d(TAG, "state  :" + wifiInfo.getSSID());
                }

            } else if (intent.getAction() == WifiManager.NETWORK_STATE_CHANGED_ACTION) {// 网络状态变化
                if (null != mWifiStateLs && null != mWifiManager) {
                    WifiInfo wifiInfo = mWifiManager.getWifiInfo();
                    if (null == wifiInfo) {
                        return;
                    }
                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    mWifiStateLs.onNetWorkChange(info.getDetailedState(), info.getType(), wifiInfo.getSSID());
                }
            } else if (intent.getAction() == WifiManager.WIFI_STATE_CHANGED_ACTION) {// WIFI
                int wifiState = intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE, 0);

                if (mWifiStateLs != null) {
                    mWifiStateLs.onIsWiFiAvailable(wifiState == WifiManager.WIFI_STATE_ENABLED);
                }
            }

        }

    };
    private WifiStateListener mWifiStateLs;

    public void setWifiStateListener(WifiStateListener ls) {
        this.mWifiStateLs = ls;
    }

    public void removeWifiStateListener() {
        this.mWifiStateLs = null;
    }


    public int RouterToDevAP(String ssid, boolean bScan) {
        mTryConnectSSID = ssid;
        int network_state = CheckNetWork.NetWorkUseful(mContext);
        if (network_state == NetWorkType.No_NetWork) {
            if (!mWifiManager.openWifi()) {
                return -4;
            }
        }
        String router_ssid = mWifiManager.getSSID();

        if (!StringUtils.contrast(ssid, router_ssid) && XMWifiManager.isXMDeviceWifi(ssid)) {
            if (!StringUtils.isStringNULL(router_ssid) && !router_ssid.endsWith("0x")
                    && !XMWifiManager.isXMDeviceWifi(router_ssid)) {
                SPUtil.getInstance(mContext).setSettingParam(ROUTER_WIFI_SSID, router_ssid);
            }
            if (mTryConnectSSID == null) {
                if (bScan) {
                    mWifiManager.startScan(1, 1000);
                }
                final List<ScanResult> wifiList = mWifiManager.getWifiList();
                if (wifiList != null && wifiList.size() > 0) {
                    Collections.sort(wifiList, new Comparator<ScanResult>() {

                        @Override
                        public int compare(ScanResult arg0, ScanResult arg1) {
                            return (arg0.level + "").compareTo(arg1.level + "");
                        }
                    });
                    mTryConnectSSID = wifiList.get(0).SSID;
                } else {
                    return -3;
                }
            }

            boolean iRet = false;
            if (mWifiManager.isSSIDExist(mTryConnectSSID)) {
                iRet = mWifiManager.enableNetwork(mTryConnectSSID);
            }else {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    WifiConfiguration wcg;
                    if (IsSportsUI(mTryConnectSSID)) {
                        Log.d(TAG, "无密码WiFi");
                        wcg = mWifiManager.createWifiInfo(mTryConnectSSID, "", 1);
                    } else {
                        Log.d(TAG, "有密码WiFi");
                        wcg = mWifiManager.createWifiInfo(mTryConnectSSID, "1234567890", 3);
                    }
                    iRet = mWifiManager.addNetwork(wcg);
                }
            }

            if (iRet) {
                return 1;
            } else {
                return 0;
            }

        } else {
            return -2;
        }
    }








    public boolean DevAPToRouter(String ssid) {
        if (ssid == null) {
            return false;
        }

        int network_state = CheckNetWork.NetWorkUseful(mContext);
        if (network_state == NetWorkType.No_NetWork) {
            if (!mWifiManager.openWifi()) {
                return false;
            }
        }

        return mWifiManager.enableNetwork(ssid);
    }



    public void onRelease() {
        synchronized (mWifiConnectReceiver) {
            if (mWifiFilter != null) {
                mContext.unregisterReceiver(mWifiConnectReceiver);
                mWifiFilter = null;
            }
        }
    }



    public static final boolean IsSportsUI(String ssid) {
        switch (XMWifiManager.getXMDeviceAPType(ssid)) {
            case SDKCONST.DEVICE_TYPE.MOV:
            case SDKCONST.DEVICE_TYPE.DASH_CAMERA:
            case SDKCONST.DEVICE_TYPE.DRIVE_BEYE:
                return true;
            default:
                return false;
        }
    }
}
