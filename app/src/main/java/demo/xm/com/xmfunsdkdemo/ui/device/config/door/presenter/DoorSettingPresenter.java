package demo.xm.com.xmfunsdkdemo.ui.device.config.door.presenter;

import com.lib.sdk.bean.CameraParamExBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.SystemManageShutDown;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.device.config.door.contract.DoorSettingContract;

import static com.lib.JSONCONFIG.CAMERA_PARAMEX;
import static com.lib.sdk.bean.JsonConfig.SYSTEM_MANAGE_SHUTDOWN;

/**
 * @author hws
 * @class
 * @time 2020/8/14 13:33
 */
public class DoorSettingPresenter extends XMBasePresenter<DeviceManager> implements DoorSettingContract.IDoorSettingPresenter {
    private DevConfigManager devConfigManager;
    private SystemManageShutDown systemManageShutDown;
    private CameraParamExBean cameraParamExBean;
    private DoorSettingContract.IDoorSettingView iDoorSettingView;
    public DoorSettingPresenter(DoorSettingContract.IDoorSettingView iDoorSettingView) {
        this.iDoorSettingView = iDoorSettingView;
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
    public void updateManageShutDown() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<SystemManageShutDown>() {
            @Override
            public void onSuccess(String s, int i, SystemManageShutDown result) {
                if (result != null) {
                    systemManageShutDown = result;
                    iDoorSettingView.onUpdateManageShutDownResult(true,systemManageShutDown.ShutDownMode);
                }
            }

            @Override
            public void onFailed(String s, int i, String s1, int i1) {
                iDoorSettingView.onUpdateManageShutDownResult(false,0);
            }
        });

        devConfigInfo.setJsonName(SYSTEM_MANAGE_SHUTDOWN);
        devConfigInfo.setChnId(-1);

        devConfigManager.getDevConfig(devConfigInfo);
    }

    @Override
    public void saveManageShutDown(int sleepTime) {
        if (systemManageShutDown == null) {
            return;
        }

        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String s, int i, Object o) {
            }

            @Override
            public void onFailed(String s, int i, String s1, int i1) {

            }
        });

        devConfigInfo.setJsonName(SYSTEM_MANAGE_SHUTDOWN);
        systemManageShutDown.ShutDownMode = sleepTime;
        String jsonData = HandleConfigData.getSendData(SYSTEM_MANAGE_SHUTDOWN,"0x08",systemManageShutDown);
        devConfigInfo.setChnId(-1);
        devConfigInfo.setJsonData(jsonData);

        devConfigManager.setDevConfig(devConfigInfo);
    }

    @Override
    public void updateCorridorMode() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String s, int i, String result) {
                if (result != null) {
                    HandleConfigData handleConfigData = new HandleConfigData();
                    if (handleConfigData.getDataObj(result, CameraParamExBean.class)) {
                        cameraParamExBean = (CameraParamExBean) handleConfigData.getObj();
                        if (cameraParamExBean != null) {
                            iDoorSettingView.onUpdateCorridorModeResult(true,cameraParamExBean.CorridorMode);
                        }
                    }
                }
            }

            @Override
            public void onFailed(String s, int i, String s1, int i1) {
                iDoorSettingView.onUpdateCorridorModeResult(false,0);
            }
        });

        devConfigInfo.setJsonName(CAMERA_PARAMEX);
        devConfigInfo.setChnId(0);

        devConfigManager.getDevConfig(devConfigInfo);
    }

    @Override
    public void saveCorridorMode(int mode) {
        if (cameraParamExBean == null) {
            return;
        }

        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String s, int i, Object o) {
            }

            @Override
            public void onFailed(String s, int i, String s1, int i1) {

            }
        });

        devConfigInfo.setJsonName(CAMERA_PARAMEX);
        cameraParamExBean.CorridorMode = mode;
        String jsonData = HandleConfigData.getSendData(HandleConfigData.getFullName(CAMERA_PARAMEX,0),"0x08",cameraParamExBean);
        devConfigInfo.setChnId(0);
        devConfigInfo.setJsonData(jsonData);

        devConfigManager.setDevConfig(devConfigInfo);
    }
}
