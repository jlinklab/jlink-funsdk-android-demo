package demo.xm.com.xmfunsdkdemo.ui.device.config.shadow.presenter;

import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.basic.G;
import com.google.gson.Gson;
import com.lib.MsgContent;
import com.lib.sdk.bean.FbExtraStateCtrlBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.NetworkPmsBean;
import com.lib.sdk.bean.StringUtils;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.manager.device.config.shadow.DevShadowManager;
import com.manager.device.config.shadow.IDevShadowManager;
import com.manager.device.config.shadow.OnDevShadowManagerListener;
import com.xm.activity.base.XMBasePresenter;

import java.util.HashMap;

import demo.xm.com.xmfunsdkdemo.ui.device.config.shadow.listener.DevShadowConfigContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.simpleconfig.listener.DevSimpleConfigContract;

/**
 * 影子服务
 */
public class DevShadowConfigPresenter extends XMBasePresenter<DeviceManager> implements DevShadowConfigContract.IDevShadowConfigPresenter {
    private DevShadowConfigContract.IDevShadowConfigView iDevShadowConfigView;
    private IDevShadowManager iDevShadowManager;

    public DevShadowConfigPresenter(DevShadowConfigContract.IDevShadowConfigView iDevShadowConfigView) {
        this.iDevShadowConfigView = iDevShadowConfigView;
    }

    @Override
    public void setDevId(String devId) {
        iDevShadowManager = DevShadowManager.getInstance();
        iDevShadowManager.addDevShadowListener(onDevShadowManagerListener);
        super.setDevId(devId);
    }

    private OnDevShadowManagerListener onDevShadowManagerListener = new OnDevShadowManagerListener() {
        @Override
        public void onDevShadowConfigResult(String devId, String configData, int errorId) {
            iDevShadowConfigView.onReceiveDataResult("获取成功", configData);
        }

        @Override
        public void onSetDevShadowConfigResult(String devId, boolean isSuccess, int errorId) {
            iDevShadowConfigView.onSendDataResult(isSuccess ? "设置成功" : "设备失败:" + errorId);
        }

        @Override
        public void onLinkShadow(String s, int i) {

        }

        @Override
        public void onUnLinkShadow(String s, int i) {

        }

        @Override
        public void onLinkShadowDisconnect() {

        }
    };

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void getConfig(String configName) {
        iDevShadowManager.getDevCfgsFromShadowService(getDevId(), configName);
    }

    @Override
    public void setConfig(String configName, Object jsonData) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(configName,jsonData);
        iDevShadowManager.setDevOffLineCfgsToShadowService(getDevId(), hashMap);
    }

    @Override
    public void startConfigMonitoring(String configName) {
        iDevShadowManager.startRealTimeDataCallback(getDevId(), configName);
    }

    @Override
    public void stopConfigMonitoring() {
        iDevShadowManager.stopRealTimeDataCallback(getDevId());
    }

    @Override
    public void release() {
        if (iDevShadowManager != null) {
            iDevShadowManager.removeDevShadowListener(onDevShadowManagerListener);
        }
    }
}

