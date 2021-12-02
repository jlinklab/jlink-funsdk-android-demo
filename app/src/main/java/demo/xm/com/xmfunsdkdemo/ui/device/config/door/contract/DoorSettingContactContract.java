package demo.xm.com.xmfunsdkdemo.ui.device.config.door.contract;

import com.lib.sdk.bean.doorlock.DoorLockAuthManageBean;

/**
 * @author hws
 * @class
 * @time 2020/8/14 15:32
 */
public interface DoorSettingContactContract {
    interface IDoorSettingContactView {
        void onUpdateDoorUserInfoResult(boolean isSuccess);

        void onUnlockDoorResult(boolean isSuccess);
    }

    interface IDoorSettingContactPresenter {
        void updateDoorUserInfo();

        DoorLockAuthManageBean getDoorLockAuthManager();

        void unlockDoor(String pwd);

        void unlockDoorByOneKey();
    }
}
