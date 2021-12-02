package demo.xm.com.xmfunsdkdemo.ui.device.config.door.contract;

import com.lib.sdk.bean.doorlock.DoorLockAuthManageBean;

import java.util.List;

/**
 * @author hws
 * @class
 * @time 2020/8/17 14:16
 */
public interface DoorSettingPasswordContract {
    interface IDoorSettingPasswordView {
        void onModifyNickNameResult(boolean isSuccess);
        void onDeleteLockAuthMangerResult(boolean isSuccess);
    }

    interface IDoorSettingPasswordPresenter {
        void setUserList(int managerType,DoorLockAuthManageBean doorLockAuthManageBean);
        List<DoorLockAuthManageBean.UserListBean.UserBean> getDoorUsers();
        DoorLockAuthManageBean.UserListBean.UserBean getDoorUser(int position);
        int getDoorUsersCount();
        void modifyNickName(int position,String nickName);
        void deleteLockManager(int position);
    }
}
