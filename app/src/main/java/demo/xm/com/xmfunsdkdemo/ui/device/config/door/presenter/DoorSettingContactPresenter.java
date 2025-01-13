package demo.xm.com.xmfunsdkdemo.ui.device.config.door.presenter;

import com.constant.DeviceConstant;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.doorlock.DoorLockAuthManageBean;
import com.lib.sdk.bean.doorlock.OPDoorLockProCmd;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.ui.device.config.door.contract.DoorSettingContactContract;

import static com.lib.sdk.bean.JsonConfig.DOOR_LOCK_UNLOCK;
import static com.lib.sdk.bean.JsonConfig.DOOR_LOCK_USER_INFO;

/**
 * @author hws
 * @class
 * @time 2020/8/14 15:31
 */
public class DoorSettingContactPresenter extends XMBasePresenter<DeviceManager> implements DoorSettingContactContract.IDoorSettingContactPresenter {
    private DevConfigManager devConfigManager;
    private List<DoorLockAuthManageBean> doorLockBeans;
    private DoorSettingContactContract.IDoorSettingContactView iDoorSettingContactView;

    public DoorSettingContactPresenter(DoorSettingContactContract.IDoorSettingContactView iDoorSettingContactView) {
        this.iDoorSettingContactView = iDoorSettingContactView;
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
    public void updateDoorUserInfo() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String s, int i, String result) {
                System.out.println("result:" + result);
                if (result != null) {
                    HandleConfigData handleConfigData = new HandleConfigData();
                    if (handleConfigData.getDataObj(result, DoorLockAuthManageBean.class)) {
                        doorLockBeans = (List<DoorLockAuthManageBean>) handleConfigData.getObj();
                        iDoorSettingContactView.onUpdateDoorUserInfoResult(true);
                    }
                }
            }

            @Override
            public void onFailed(String s, int i, String s1, int i1) {
                iDoorSettingContactView.onUpdateDoorUserInfoResult(false);
            }
        });

        OPDoorLockProCmd opDoorLockProCmd = new OPDoorLockProCmd();
        opDoorLockProCmd.Cmd = DOOR_LOCK_USER_INFO;
        devConfigInfo.setJsonName(DOOR_LOCK_USER_INFO);
        devConfigInfo.setCmdId(OPDoorLockProCmd.JSON_ID);
        devConfigInfo.setJsonData(HandleConfigData.getSendData(OPDoorLockProCmd.JSON_NAME, "0x08", opDoorLockProCmd));

        devConfigManager.setDevCmd(devConfigInfo);
    }

    @Override
    public DoorLockAuthManageBean getDoorLockAuthManager() {
        return doorLockBeans != null ? doorLockBeans.get(0) : null;
    }

    @Override
    public void unlockDoor(String pwd) {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String s, int i, String result) {
                iDoorSettingContactView.onUnlockDoorResult(true);
            }

            @Override
            public void onFailed(String s, int i, String s1, int i1) {
                iDoorSettingContactView.onUnlockDoorResult(false);
            }
        });

        OPDoorLockProCmd opDoorLockProCmd = new OPDoorLockProCmd();
        opDoorLockProCmd.Cmd = DOOR_LOCK_UNLOCK;
        opDoorLockProCmd.Arg1 = doorLockBeans.get(0).DoorLockID;
        opDoorLockProCmd.Arg2 = pwd;
        devConfigInfo.setCmdId(OPDoorLockProCmd.JSON_ID);
        devConfigInfo.setJsonData(HandleConfigData.getSendData(OPDoorLockProCmd.JSON_NAME, "0x08", opDoorLockProCmd));

        devConfigManager.setDevCmd(devConfigInfo);
    }

    /**
     * 一键解锁
     */
    @Override
    public void unlockDoorByOneKey() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String s, int i, String result) {
                iDoorSettingContactView.onUnlockDoorResult(true);
            }

            @Override
            public void onFailed(String s, int i, String s1, int i1) {
                iDoorSettingContactView.onUnlockDoorResult(false);
            }
        });

        OPDoorLockProCmd opDoorLockProCmd = new OPDoorLockProCmd();
        opDoorLockProCmd.Cmd = "RemoteOneKeyUnlock";
        devConfigInfo.setCmdId(OPDoorLockProCmd.JSON_ID);
        devConfigInfo.setTimeOut(10000);
        devConfigInfo.setJsonData(HandleConfigData.getSendData(OPDoorLockProCmd.JSON_NAME, "0x08", opDoorLockProCmd));

        devConfigManager.setDevCmd(devConfigInfo);
    }
}
