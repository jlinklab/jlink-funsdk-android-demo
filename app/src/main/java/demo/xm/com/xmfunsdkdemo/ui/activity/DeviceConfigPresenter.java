package demo.xm.com.xmfunsdkdemo.ui.activity;

import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.lib.EDEV_JSON_ID;
import com.lib.MsgContent;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;

import com.xm.activity.base.XMBasePresenter;

public class DeviceConfigPresenter extends XMBasePresenter<DeviceManager> implements DeviceConfigContract.IDeviceConfigPresenter {
    private DeviceConfigContract.IDeviceConfigView iDeviceConfigView;
    private DevConfigManager devConfigManager;
    public DeviceConfigPresenter(DeviceConfigContract.IDeviceConfigView iDeviceConfigView) {
        this.iDeviceConfigView = iDeviceConfigView;
    }

    @Override
    public void onSuccess(int msgId) {

    }

    @Override
    public void onFailed(int msgId, int errorId) {

    }

    @Override
    public void onFunSDKResult(Message msg, MsgContent ex) {

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
    public void rebootDev() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String s, int i, Object o) {

            }

            @Override
            public void onFailed(String s, int i, String s1, int i1) {

            }
        });

        // 0表示重启 1表示关闭
        /*0 means restart and 1 means close*/

        JSONObject obj = new JSONObject();
        obj.put("Action", "Reboot");
        devConfigInfo.setJsonName(JsonConfig.OPERATION_OPMACHINE);
        devConfigInfo.setJsonData(HandleConfigData.getSendData(JsonConfig.OPERATION_OPMACHINE, "0x01", obj));
        devConfigInfo.setCmdId(EDEV_JSON_ID.OPMACHINE);
        devConfigInfo.setChnId(-1);

        devConfigManager.setDevCmd(devConfigInfo);
    }
}
