package demo.xm.com.xmfunsdkdemo.ui.device.config.networksetting.presenter;

import static com.lib.sdk.bean.JsonConfig.CMD_NET_MODE_SWITCH;
import static com.lib.sdk.bean.JsonConfig.NETWORK_WIFI;
import static com.manager.device.DeviceManager.RECEIVE_ROUTER_INFORMATION_SUCCESS;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.JsonConfig;
import  demo.xm.com.xmfunsdkdemo.bean.wifi.WifiAP;

import com.lib.sdk.bean.NetWorkWiFiBean;
import com.lib.sdk.bean.StringUtils;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.utils.XMWifiManager;
import com.utils.XUtils;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.widget.dialog.LoadingDialog;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


import demo.xm.com.xmfunsdkdemo.ui.device.config.networksetting.listener.NetworkSettingContract;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.view.DevMonitorActivity;
import demo.xm.com.xmfunsdkdemo.utils.APAutomaticSwitch;
import demo.xm.com.xmfunsdkdemo.utils.WifiNameAndPwdManager;
import demo.xm.com.xmfunsdkdemo.utils.SPUtil;
import demo.xm.com.xmfunsdkdemo.ui.device.config.networksetting.listener.WifiStateListener;


public class NetworkSettingPresenter extends XMBasePresenter<DeviceManager> implements WifiStateListener, IFunSDKResult,DeviceManager.OnSearchLocalDevListener{


    private static final String TAG = "NetworkSettingPresenter";
    private NetworkSettingContract.INetworkSettingView iNetworkSettingView;


    protected String devId;


    protected XMWifiManager mDevWifiManager;
    private APAutomaticSwitch mAPAutoSwitch;


    private NetWorkWiFiBean mNetworkWifi;

    private List<WifiAP> wifiList = new ArrayList();


    private ScanResult mScanResult;
    


    public NetworkSettingPresenter(NetworkSettingContract.INetworkSettingView iNetworkSettingView) {
        this.iNetworkSettingView = iNetworkSettingView;

    }
    
    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }


    protected DevConfigManager devConfigManager;

    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        this.devId = devId;
        devConfigManager = manager.getDevConfigManager(devId);

        mDevWifiManager = XMWifiManager.getInstance(iNetworkSettingView.getActivity());
        initAPAutoSwitch();
        initWifiStateListener();

        getNetworkWifi();

    }


    private void getNetworkWifi(){
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<NetWorkWiFiBean>() {
            @Override
            public void onSuccess(String s, int i, NetWorkWiFiBean networkWifi) {
                if(networkWifi!=null){
                    mNetworkWifi = networkWifi;
                    iNetworkSettingView.onShowWifiNameAndPassword(mNetworkWifi.getSsid(), mNetworkWifi.getKeys());
                }

            }

            @Override
            public void onFailed(String s, int i, String s1, int i1) {

            }
        });

        devConfigInfo.setJsonName(NETWORK_WIFI);
        devConfigInfo.setChnId(-1);
        devConfigManager.getDevConfig(devConfigInfo);
    }





    public synchronized String GetCurDevId() {
        return devId;
    }


    private void initAPAutoSwitch() {
        if (null != mDevWifiManager) {
            mAPAutoSwitch = new APAutomaticSwitch(iNetworkSettingView.getActivity(), mDevWifiManager);
        }
    }

    protected void initWifiStateListener() {
        if (null != mAPAutoSwitch)
            mAPAutoSwitch.setWifiStateListener(this);
    }

    public APAutomaticSwitch getAPSwitch() {
        if (null == mAPAutoSwitch) {
            initAPAutoSwitch();
        }
        return mAPAutoSwitch;
    }

    public void onRelease() {
        if (null != mAPAutoSwitch) {
            mAPAutoSwitch.removeWifiStateListener();
            mAPAutoSwitch.onRelease();
            mAPAutoSwitch = null;
        }
        manager.searchLanDevice(null);
    }


    @Override
    public int OnFunSDKResult(Message msg, MsgContent ex) {
        switch (msg.what) {
            case EUIMSG.DEV_SET_WIFI_CFG: {
                if (msg.arg1 < 0) {
                    iNetworkSettingView.onHideWaitDialog();
                    Toast.makeText(iNetworkSettingView.getActivity(), FunSDK.TS("operator_failed"), Toast.LENGTH_SHORT).show();
                } else if (msg.arg1 == RECEIVE_ROUTER_INFORMATION_SUCCESS) {
                    Toast.makeText(iNetworkSettingView.getActivity(), FunSDK.TS("Save_Success"), Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(iNetworkSettingView.getActivity()).show(FunSDK.TS("Connect_router_network"));
                    FunSDK.DevLogout(GetId(), AP_IP, 0);
                    //如果设备收到WiFi信息回复
                    String ssid = SPUtil.getInstance(iNetworkSettingView.getActivity()).getSettingParam("Available_Network_SSID", "");
                    if (!getAPSwitch().DevAPToRouter(ssid)) {
                        iNetworkSettingView.dealWithChangeWifiFailed(ssid);
                    } else {
                        iNetworkSettingView.goMainActivity();
                    }
                }
                break;
            }
            default:
                break;
        }
        return 0;
    }



    private int _msgId = 0xff00ff;

    public synchronized int GetId() {
        _msgId = FunSDK.GetId(_msgId, this);
        return _msgId;
    }







    public boolean isShareDev() {
        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(devId);
        if (xmDevInfo != null) {
            return xmDevInfo.isShareDev();
        }
        return false;
    }


    protected int getDevType() {
        if (StringUtils.isStringNULL(GetCurDevId())) {
            return 0;
        }

        XMDevInfo deviceInfo = DevDataCenter.getInstance().getDevInfo(GetCurDevId());
        if (deviceInfo != null) {
            return deviceInfo.getDevType();
        }

        return 0;
    }


    public boolean isWBS(){
        return isNVR(getDevType());
    }

    public static boolean isNVR(int devType) {
        if (devType == SDKCONST.DEVICE_TYPE.EE_DEV_WBS) {
            return true;
        }
        int type = 0x0F000000;
        if ((devType & type) == 0x02000000 || (devType & type) == 0x03000000) {
            return true;
        }
        return false;
    }







    @Override
    public void onNetWorkState(NetworkInfo.DetailedState state, int type, String ssid, String bssid) {
        switch (state) {
            case DISCONNECTED:
                break;
            case CONNECTED:
                if (type == ConnectivityManager.TYPE_WIFI) {
                    String temp = ssid.substring(1, ssid.length() - 1);
                    Log.d(TAG, "state  :" + ssid + "  temp  :" + temp);
                    if (SPUtil.getInstance(iNetworkSettingView.getActivity()).getSettingParam("Available_Network_SSID", "").equals(temp)) {
                        iNetworkSettingView.onHideWaitDialog();
                    }
                    if (isChangeWifi) {
                        iNetworkSettingView.onHideWaitDialog();
                        LoadingDialog.getInstance(iNetworkSettingView.getActivity()).show(FunSDK.TS("Search_device"));
                        manager.searchLanDevice(this);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onIsWiFiAvailable(boolean isWiFiAvailable) {

    }

    @Override
    public void onNetWorkChange(NetworkInfo.DetailedState state, int type, String SSid) {

    }


    private String ssid;
    private String wifiPwd;
    private int pwdType;

    private boolean isChangeWifi;

    /**
     * 发送AP配网数据
     */
    private void sendAPConfigData(String wifiName, String inputWifiPwd, ScanResult wifiResult) {
        ssid = XUtils.initSSID(wifiName);
        wifiPwd = inputWifiPwd;
        pwdType = wifiResult != null ? XUtils.getEncrypPasswordType(wifiResult.capabilities) : 1;
        if (pwdType == 3 && (wifiPwd.length() == 10 || wifiPwd.length() == 26)) {
            wifiPwd = XUtils.asciiToString(wifiPwd);
        }
        if (wifiPwd.length() == 0) {
            pwdType = 0;
        }
        FunSDK.DevStartWifiConfig(GetId(), GetCurDevId(), ssid, wifiPwd, -1);
        Log.d(TAG, "msg   :  GetCurDevId() :" + GetCurDevId() + "ssid  :  " + ssid + "  wifiPwd :" + wifiPwd);
    }



    private static final String AP_IP = "192.168.10.1";





    public void switchToDeviceAp(){
        isChangeWifi = false;
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("Action", "ToAP");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Name", CMD_NET_MODE_SWITCH);
        jsonObject.put(CMD_NET_MODE_SWITCH, jsonObject1);


        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DevConfigManager.OnDevConfigResultListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                Toast.makeText(iNetworkSettingView.getActivity(), FunSDK.TS("Save_Success"), Toast.LENGTH_SHORT).show();
                iNetworkSettingView.onHideWaitDialog();
                iNetworkSettingView.onSwitchToDeviceSuccess();

            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                Toast.makeText(iNetworkSettingView.getActivity(), FunSDK.TS("operator_failed"), Toast.LENGTH_SHORT).show();
                iNetworkSettingView.onHideWaitDialog();

            }

            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {

            }
        });

        devConfigInfo.setJsonName(CMD_NET_MODE_SWITCH);
        devConfigInfo.setCmdId(1450);
        devConfigInfo.setChnId(-1);
        devConfigInfo.setJsonData(jsonObject.toJSONString());
        devConfigManager.setDevCmd(devConfigInfo);

    }


    public void changeRouterWifi(String SSID,String wifiPwd){
        isChangeWifi = true;
        mScanResult = mDevWifiManager.getCurScanResult(SSID);
        if (null != mScanResult) {
            int pwdType = XUtils.getEncrypPasswordType(mScanResult.capabilities);
            if (pwdType == 3 && (wifiPwd.length() == 10 || wifiPwd.length() == 26)) {
                wifiPwd = XUtils.asciiToString(wifiPwd);
            }
        }
        if (mScanResult != null && (mScanResult.frequency > 4900 && mScanResult.frequency < 5900)) {
            Toast.makeText(iNetworkSettingView.getActivity(), FunSDK.TS("TR_5GHz_WiFi_Continue"), Toast.LENGTH_LONG).show();
        }


        SPUtil.getInstance(iNetworkSettingView.getActivity()).setSettingParam("Available_Network_SSID", SSID);
        SPUtil.getInstance(iNetworkSettingView.getActivity()).setSettingParam("Available_Network_Password", wifiPwd);
        WifiNameAndPwdManager.getInstance(iNetworkSettingView.getActivity()).saveWiFiPwd(iNetworkSettingView.getActivity(),wifiPwd,SSID);
        sendAPConfigData(SSID,wifiPwd,mScanResult);
    }


    public void scanWifi(final int wifiType) {
        /**
         * 获取设备wifi
         */
        LoadingDialog.getInstance(iNetworkSettingView.getActivity()).show(FunSDK.TS("Scanning_WiFi"));


        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DevConfigManager.OnDevConfigResultListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {

                try {
                    String json = (String) result;
                    JSONObject jsonObject = JSONObject.parseObject(json.contains("\\\\x") && !json.contains("%") ? URLDecoder.decode(json.replace("\\\\x", "%"), "UTF-8") : json);
                    //测试发现wifi名称中包含特殊字符%时，使用URLDecoder解码报错，这里判断处理下
                    JSONObject wifiAP = (JSONObject) jsonObject.get("WifiAP");
                    int  numbers = (int) wifiAP.get("Numbers");
                    wifiList.clear();
                    if(numbers > 0) {
                        JSONArray wifis = wifiAP.getJSONArray("WifiAP");
                        for (Object wifi : wifis) {
                            JSONObject jsonObject1 = (JSONObject) wifi;
                            WifiAP wifiAP1 = JSONObject.parseObject(((JSONObject) wifi).toJSONString(), WifiAP.class);
                            if (!StringUtils.isStringNULL(wifiAP1.getSSID())) {
                                wifiList.add(wifiAP1);
                            }
                        }
                    }
                    iNetworkSettingView.onGetWifiListResult(true,wifiList);

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(iNetworkSettingView.getActivity(),FunSDK.TS("get_config_f"),Toast.LENGTH_SHORT).show();
                    iNetworkSettingView.onGetWifiListResult(false,null);
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iNetworkSettingView.onGetWifiListResult(false,null);

            }

            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {

            }
        });

        devConfigInfo.setJsonName(JsonConfig.WIFI_AP);
        devConfigInfo.setCmdId(1020);
        devConfigInfo.setChnId(-1);
        devConfigManager.setDevCmd(devConfigInfo);
    }


    public boolean isisXMDeviceWifi(){
        return XMWifiManager.isXMDeviceWifi(mDevWifiManager.getSSID());
    }



    public boolean isSearchDevice(){
        return isChangeWifi;
    }

    @Override
    public void onSearchLocalDevResult(List<XMDevInfo> list) {
        if(list != null){
            for (XMDevInfo com : list) {
                String devId = com.getDevId();
                if (StringUtils.contrast(devId, this.devId)) { // 切换成为路由模式直接跳转预览页面
                    if (isChangeWifi) {
                        iNetworkSettingView.onHideWaitDialog();
                        Intent intent = new Intent(iNetworkSettingView.getActivity(), DevMonitorActivity.class);
                        intent.putExtra("devId", devId);
                        iNetworkSettingView.getActivity().startActivity(intent);
                        iNetworkSettingView.getActivity().finish();
                    }
                    break;
                }
            }
        }

        LoadingDialog.getInstance(iNetworkSettingView.getActivity()).show(FunSDK.TS("Search_device"));
        manager.searchLanDevice(this);


    }
}
