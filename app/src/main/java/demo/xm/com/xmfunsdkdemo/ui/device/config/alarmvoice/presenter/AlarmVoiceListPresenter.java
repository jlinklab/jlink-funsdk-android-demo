package demo.xm.com.xmfunsdkdemo.ui.device.config.alarmvoice.presenter;

import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.basic.G;
import com.google.gson.Gson;
import com.lib.MsgContent;
import com.lib.sdk.bean.BrowserLanguageBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.VoiceTipTypeBean;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.utils.DevLanguageUtils;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.device.config.alarmvoice.listener.AlarmVoiceListContract;

public class AlarmVoiceListPresenter extends XMBasePresenter<DeviceManager> implements AlarmVoiceListContract.IAlarmVoiceListPresenter {
    private AlarmVoiceListContract.IAlarmVoiceListView iAlarmVoiceListView;
    private DevConfigManager devConfigManager;

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    public AlarmVoiceListPresenter(AlarmVoiceListContract.IAlarmVoiceListView iAlarmVoiceListView) {
        this.iAlarmVoiceListView = iAlarmVoiceListView;
    }

    @Override
    public void getConfig() {
        checkSupportAlarmVoiceTipInterval();
        setVoiceListLanguage();
    }

    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        devConfigManager = manager.getDevConfigManager(devId);
    }

    /**
     * 检查是否支持警铃间隔时间设置
     */
    private void checkSupportAlarmVoiceTipInterval() {
        DeviceManager.getInstance().getDevAbility(getDevId(), new DeviceManager.OnDevManagerListener() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, Object isSupport) {
                //isSupport-> ture表示支持，false表示不支持
                if (isSupport instanceof Boolean) {
                    iAlarmVoiceListView.isSupportAlarmVoiceTipIntervalResult((Boolean) isSupport);
                } else {
                    iAlarmVoiceListView.isSupportAlarmVoiceTipIntervalResult(false);
                }
            }

            /**
             * 失败回调
             *
             * @param devId    设备序列号
             * @param msgId    消息ID
             * @param jsonName
             * @param errorId  错误码
             */
            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                //获取失败，通过errorId分析具体原因
                iAlarmVoiceListView.isSupportAlarmVoiceTipIntervalResult(false);
            }
        }, "OtherFunction", "SupportAlarmVoiceTipInterval");
    }

    /**
     * 设置警铃列表语言
     */
    private void setVoiceListLanguage() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DevConfigManager.OnDevConfigResultListener() {
            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {
                String jsonData = G.ToStringJson(ex.pData);
            }

            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                getVoiceNameList();
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                iAlarmVoiceListView.onGetVoiceNameListResult(null, errorId);
            }

        });

        BrowserLanguageBean bean = new BrowserLanguageBean();
        HandleConfigData handleConfigData = new HandleConfigData();
        bean.setBrowserLanguageType(DevLanguageUtils.getLanguageType());
        String jsonData = handleConfigData.getSendData(JsonConfig.CFG_BROWSER_LANGUAGE, bean);
        devConfigInfo.setJsonData(jsonData);
        devConfigInfo.setJsonName(JsonConfig.CFG_BROWSER_LANGUAGE);
        devConfigInfo.setCmdId(1040);
        devConfigInfo.setChnId(-1);
        devConfigManager.setDevCmd(devConfigInfo);
    }

    /**
     * 获取语音名称列表
     */
    private void getVoiceNameList() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int operationType, String result) {
                HandleConfigData handleConfigData = new HandleConfigData();
                if (handleConfigData.getDataObj(result, VoiceTipTypeBean.class)) {
                    iAlarmVoiceListView.onGetVoiceNameListResult((VoiceTipTypeBean) handleConfigData.getObj(), 0);
                }

            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                iAlarmVoiceListView.onGetVoiceNameListResult(null, errorId);
            }
        });

        devConfigInfo.setJsonName(JsonConfig.CFG_VOICE_TIP_TYPE);
        devConfigInfo.setChnId(-1);
        devConfigManager.getDevConfig(devConfigInfo);
    }


}
