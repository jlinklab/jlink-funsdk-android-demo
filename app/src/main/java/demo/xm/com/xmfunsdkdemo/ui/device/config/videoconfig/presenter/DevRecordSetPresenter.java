package demo.xm.com.xmfunsdkdemo.ui.device.config.videoconfig.presenter;

import com.alibaba.fastjson.JSON;
import com.lib.sdk.bean.JsonConfig;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.device.config.DevConfigState;
import demo.xm.com.xmfunsdkdemo.ui.device.config.videoconfig.listener.DevRecordSetContract;

/**
 * 录像配置界面,可改变录像方式,关掉音频,改变文件长度
 * Created by jiangping on 2018-10-23.
 */
public class DevRecordSetPresenter extends XMBasePresenter<DeviceManager> implements DevRecordSetContract.IDevRecordSetPresenter {
    private DevRecordSetContract.IDevRecordSetView iDevRecordSetView;
    private DevConfigManager devConfigManager;

    public DevRecordSetPresenter(DevRecordSetContract.IDevRecordSetView iDevRecordSetView) {
        this.iDevRecordSetView = iDevRecordSetView;
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void setDevId(String devId) {
        devConfigManager = manager.getDevConfigManager(devId);
        super.setDevId(devId);
    }

    @Override
    public void getRecordInfo() {
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                iDevRecordSetView.onUpdateView(result instanceof String ? (String) result : JSON.toJSONString(result),
                        JsonConfig.RECORD, DevConfigState.DEV_CONFIG_VIEW_VISIABLE);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevRecordSetView.onUpdateView(null, JsonConfig.RECORD, DevConfigState.DEV_CONFIG_VIEW_INVISIABLE);
            }
        });
        mainConfigInfo.setJsonName(JsonConfig.RECORD);
        mainConfigInfo.setChnId(-1);
        devConfigManager.getDevConfig(mainConfigInfo);

        DevConfigInfo subConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                iDevRecordSetView.onUpdateView(result instanceof String ? (String) result : JSON.toJSONString(result),
                        JsonConfig.EXRECORD, DevConfigState.DEV_CONFIG_VIEW_VISIABLE);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevRecordSetView.onUpdateView(null, JsonConfig.EXRECORD, DevConfigState.DEV_CONFIG_VIEW_INVISIABLE);
            }
        });
        subConfigInfo.setJsonName(JsonConfig.EXRECORD);
        subConfigInfo.setChnId(-1);
        devConfigManager.getDevConfig(subConfigInfo);

        DeviceManager.getInstance().getDevAbility(getDevId(), new DeviceManager.OnDevManagerListener<Boolean>() {
            @Override
            public void onSuccess(String devId, int operationType, Boolean result) {
                //判断是否支持定时录像
                if (result) {
                    //获取定时录像
                    DevConfigInfo epitomeRecordConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
                        @Override
                        public void onSuccess(String devId, int msgId, Object result) {
                            iDevRecordSetView.onUpdateView(result instanceof String ? (String) result : JSON.toJSONString(result),
                                    "Storage.EpitomeRecord", DevConfigState.DEV_CONFIG_VIEW_VISIABLE);
                        }

                        @Override
                        public void onFailed(String devId, int msgId, String s1, int errorId) {
                            iDevRecordSetView.onUpdateView(null, "Storage.EpitomeRecord", DevConfigState.DEV_CONFIG_VIEW_INVISIABLE);
                        }
                    });
                    epitomeRecordConfigInfo.setJsonName("Storage.EpitomeRecord");
                    epitomeRecordConfigInfo.setChnId(-1);
                    devConfigManager.getDevConfig(epitomeRecordConfigInfo);
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {

            }
        }, "OtherFunction", "SupportEpitomeRecord");
    }

    @Override
    public void setRecordInfo(String key, String jsonData) {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                iDevRecordSetView.onSaveResult(key, DevConfigState.DEV_CONFIG_UPDATE_SUCCESS);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevRecordSetView.onSaveResult(key, DevConfigState.DEV_CONFIG_UPDATE_FAILED);
            }
        });
        devConfigInfo.setJsonName(key);
        devConfigInfo.setChnId(-1);
        devConfigInfo.setJsonData(jsonData);
        devConfigManager.setDevConfig(devConfigInfo);
    }
}

