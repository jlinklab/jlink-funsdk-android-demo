package demo.xm.com.xmfunsdkdemo.ui.device.alarm.presenter;

import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.WhiteLightBean;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.device.alarm.listener.AlarmByVoiceLightContract;

/**
 * 声光报警界面,可改变控制模式
 */
public class AlarmByVoiceLightPresenter extends XMBasePresenter<DeviceManager> implements AlarmByVoiceLightContract.IAlarmByVoiceLightPresenter {
    private AlarmByVoiceLightContract.IAlarmByVoiceLightView iDevRecordSetView;
    private DevConfigManager mDevConfigManager;

    protected WhiteLightBean mWhiteLight;

    public AlarmByVoiceLightPresenter(AlarmByVoiceLightContract.IAlarmByVoiceLightView iDevRecordSetView) {
        this.iDevRecordSetView = iDevRecordSetView;
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void setDevId(String devId) {
        mDevConfigManager = manager.getDevConfigManager(devId);
        super.setDevId(devId);
    }

    /**
     * 获取声光报警配置
     */
    @Override
    public void getAlarmByVoiceLightConfig() {
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<WhiteLightBean>() {
            @Override
            public void onSuccess(String devId, int msgId, WhiteLightBean result) {
                System.out.println("result:" + result);
                if (result != null) {
                    mWhiteLight = result;
                    iDevRecordSetView.onUpdateView(true,mWhiteLight);
                } else {
                    mWhiteLight = null;
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                mWhiteLight = null;
                iDevRecordSetView.onUpdateView(false, null);
            }
        });
        mainConfigInfo.setJsonName(JsonConfig.WHITE_LIGHT);
        mainConfigInfo.setChnId(-1);
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }

    /**
     * 保存声光报警配置
     */
    @Override
    public void saveAlarmByVoiceLightConfig() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                iDevRecordSetView.onSaveResult(true);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevRecordSetView.onSaveResult(false);
            }
        });
        devConfigInfo.setJsonName(JsonConfig.WHITE_LIGHT);
        devConfigInfo.setChnId(-1);
        devConfigInfo.setJsonData(HandleConfigData.getSendData(JsonConfig.WHITE_LIGHT, "0x01", mWhiteLight));
        mDevConfigManager.setDevConfig(devConfigInfo);
    }

    @Override
    public WhiteLightBean getWhiteLightBean() {
        return mWhiteLight;
    }
}

