package demo.xm.com.xmfunsdkdemo.ui.device.cloud.presenter;

import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.SystemInfoBean;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.device.cloud.listener.CloudWebContract;

/**
 * @author hws
 * @class describe
 * @time 2019-10-23 10:02
 */
public class CloudWebPresenter extends XMBasePresenter<DeviceManager> implements CloudWebContract.ICloudWebPresenter {
    String goodsType;
    SystemInfoBean systemInfoBean;
    DevConfigManager devConfigManager;
    CloudWebContract.ICloudWebView iCloudWebView;

    public CloudWebPresenter(CloudWebContract.ICloudWebView iCloudWebView) {
        this.iCloudWebView = iCloudWebView;
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        devConfigManager = manager.getDevConfigManager(devId);
    }

    @Override
    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    @Override
    public String getGoodsType() {
        return goodsType;
    }

    @Override
    public void initSystemInfo() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int operationType, String result) {
                if (result != null) {
                    HandleConfigData handleConfigData = new HandleConfigData();
                    if (handleConfigData.getDataObj(result, SystemInfoBean.class)) {
                        systemInfoBean = (SystemInfoBean) handleConfigData.getObj();
                        if (systemInfoBean != null && iCloudWebView != null) {
                            iCloudWebView.onUpdateSysInfoResult(true,0);
                        }
                    }
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                if(iCloudWebView != null) {
                    iCloudWebView.onUpdateSysInfoResult(false,errorId);
                }
            }
        });

        devConfigInfo.setJsonName(JsonConfig.SYSTEM_INFO);
        devConfigInfo.setChnId(-1);
        devConfigManager.getDevConfig(devConfigInfo);
    }

    @Override
    public String getDevHardWare() {
        return systemInfoBean != null ? systemInfoBean.getHardWare() : "";
    }

    @Override
    public String getDevSoftWare() {
        return systemInfoBean != null ? systemInfoBean.getSoftWareVersion() : "";
    }
}
