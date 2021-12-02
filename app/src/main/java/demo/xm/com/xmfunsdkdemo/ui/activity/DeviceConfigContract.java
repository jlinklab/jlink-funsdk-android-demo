package demo.xm.com.xmfunsdkdemo.ui.activity;

import com.manager.account.BaseAccountManager;

public class DeviceConfigContract {
    public interface IDeviceConfigView {
        void onUpdateView();
    }

    public interface IDeviceConfigPresenter extends BaseAccountManager.OnAccountManagerListener {
        void setDevId(String devId);
        String getDevId();
        /**
         * 重启设备
         */
        void rebootDev();
    }
}
