package demo.xm.com.xmfunsdkdemo.ui.device.config.powersavingmode.presenter;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.lib.sdk.bean.JsonConfig;
import com.manager.base.BaseManager;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import java.util.HashMap;

import demo.xm.com.xmfunsdkdemo.ui.device.config.powersavingmode.listener.PowerSavingModeContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.powersavingmode.view.PowerSavingModeActivity;

public class PowerSavingModePresenter extends XMBasePresenter<DeviceManager> implements PowerSavingModeContract.IPowerSavingModePresenter {
    private DevConfigManager devConfigManager;
    private PowerSavingModeContract.IPowerSavingModeView iPowerSavingModeView;
    private HashMap<Object, Object> sleepModeInfo = new HashMap<>();//省电模式配置信息

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    public PowerSavingModePresenter(PowerSavingModeContract.IPowerSavingModeView iPowerSavingModeView) {
        this.iPowerSavingModeView = iPowerSavingModeView;
    }

    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        devConfigManager = manager.getDevConfigManager(devId);
        getConfig();
    }

    private void getConfig() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int operationType, String result) {
                Gson gson = new Gson();
                sleepModeInfo = gson.fromJson(result, HashMap.class);
                iPowerSavingModeView.onGetConfigResult(sleepModeInfo, 0);
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                iPowerSavingModeView.onGetConfigResult(null, errorId);
            }
        });
        devConfigInfo.setCmdId(1042);
        devConfigInfo.setJsonName(JsonConfig.SleepMode);
        devConfigInfo.setChnId(-1);
        devConfigManager.getDevConfig(devConfigInfo);
    }

    public void saveConfig() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int operationType, String result) {

            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {

            }
        });
        devConfigInfo.setCmdId(1042);
        devConfigInfo.setJsonName(JsonConfig.SleepMode);
        devConfigInfo.setJsonData(new Gson().toJson(sleepModeInfo));
        devConfigInfo.setChnId(-1);
        devConfigManager.setDevConfig(devConfigInfo);
    }

    public HashMap<Object, Object> getSleepModeInfo() {
        return sleepModeInfo;
    }
}
