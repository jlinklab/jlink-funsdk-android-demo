package demo.xm.com.xmfunsdkdemo.ui.device.config.door.presenter;

import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.doorlock.DoorLockAuthManageBean;
import com.lib.sdk.bean.doorlock.OPDoorLockProCmd;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import java.util.ArrayList;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.ui.device.config.door.contract.DoorSettingPasswordContract;

import static com.lib.sdk.bean.JsonConfig.DOOR_LOCK_CHANGE_NAME;

/**
 * @author hws
 * @class
 * @time 2020/8/17 14:16
 */
public class DoorSettingPasswordPresenter extends XMBasePresenter<DeviceManager> implements DoorSettingPasswordContract.IDoorSettingPasswordPresenter {
    public static final int LOCK_MANAGER_TYPE_PWD = 0;//密码
    public static final int LOCK_MANAGER_TYPE_FINGER = 1;//指纹
    public static final int LOCK_MANAGER_TYPE_CARD = 2;//卡
    private int managerType = LOCK_MANAGER_TYPE_PWD;
    private DevConfigManager devConfigManager;
    private DoorLockAuthManageBean.UserListBean userListBean;
    private List<DoorLockAuthManageBean.UserListBean.UserBean> userBeans;
    private DoorLockAuthManageBean doorLockAuthManageBean;
    private DoorSettingPasswordContract.IDoorSettingPasswordView iDoorSettingPasswordView;
    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    public DoorSettingPasswordPresenter(DoorSettingPasswordContract.IDoorSettingPasswordView iDoorSettingPasswordView) {
        this.iDoorSettingPasswordView = iDoorSettingPasswordView;
    }

    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        devConfigManager = manager.getDevConfigManager(devId);
    }

    @Override
    public void setUserList(int managerType,DoorLockAuthManageBean doorLockAuthManageBean) {
        this.managerType = managerType;
        this.doorLockAuthManageBean = doorLockAuthManageBean;
        if (doorLockAuthManageBean != null) {

            if (managerType == LOCK_MANAGER_TYPE_PWD) {
                userListBean = doorLockAuthManageBean.PassWdManger;
            }else if (managerType == LOCK_MANAGER_TYPE_FINGER) {
                userListBean = doorLockAuthManageBean.FingerManger;
            }else if (managerType == LOCK_MANAGER_TYPE_CARD) {
                userListBean = doorLockAuthManageBean.CardManger;
            }

            if (userListBean != null) {
                userBeans = new ArrayList<>();
                if (userListBean.Admin != null) {
                    userBeans.addAll(userListBean.Admin);
                }

                if (userListBean.Force != null) {
                    userBeans.addAll(userListBean.Force);
                }

                if (userListBean.General != null) {
                    userBeans.addAll(userListBean.General);
                }

                if (userListBean.Guest != null) {
                    userBeans.addAll(userListBean.Guest);
                }

                if (userListBean.Temporary != null) {
                    userBeans.addAll(userListBean.Temporary);
                }
            }
        }
    }

    @Override
    public List<DoorLockAuthManageBean.UserListBean.UserBean> getDoorUsers() {
        return userBeans;
    }

    @Override
    public DoorLockAuthManageBean.UserListBean.UserBean getDoorUser(int position) {
        if (userBeans != null && position < userBeans.size()) {
            return userBeans.get(position);
        }

        return null;
    }

    @Override
    public int getDoorUsersCount() {
        return userBeans != null ? userBeans.size() : 0;
    }

    @Override
    public void modifyNickName(int position,String nickName) {
        DoorLockAuthManageBean.UserListBean.UserBean userBean = getDoorUser(position);
        userBean.NickName = nickName;
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String s, int i, String result) {
                iDoorSettingPasswordView.onModifyNickNameResult(true);
            }

            @Override
            public void onFailed(String s, int i, String s1, int i1) {
                iDoorSettingPasswordView.onModifyNickNameResult(false);
            }
        });

        ArrayList doorLockAuthMaanges = new ArrayList();
        doorLockAuthMaanges.add(doorLockAuthManageBean);
        OPDoorLockProCmd opDoorLockProCmd = new OPDoorLockProCmd();
        opDoorLockProCmd.Cmd = DOOR_LOCK_CHANGE_NAME;
        opDoorLockProCmd.DoorLockAuthManage = doorLockAuthMaanges;

        devConfigInfo.setCmdId(OPDoorLockProCmd.JSON_ID);
        devConfigInfo.setJsonData(HandleConfigData.getSendData(OPDoorLockProCmd.JSON_NAME, "0x08", opDoorLockProCmd));
        devConfigManager.setDevCmd(devConfigInfo);
    }

    @Override
    public void deleteLockManager(int position) {

    }
}
