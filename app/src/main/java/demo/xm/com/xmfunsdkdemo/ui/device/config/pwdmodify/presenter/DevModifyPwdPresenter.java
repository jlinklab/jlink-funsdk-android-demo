package demo.xm.com.xmfunsdkdemo.ui.device.config.pwdmodify.presenter;

import android.text.TextUtils;

import com.basic.G;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigManager;

import com.xm.activity.base.XMBasePresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.pwdmodify.listener.DevModifyPwdContract;

/**
 * 密码修改界面,为了保护隐私,可以更改设备的访问密码
 * Created by jiangping on 2018-10-23.
 */
public class DevModifyPwdPresenter extends XMBasePresenter<DeviceManager> implements DevModifyPwdContract.IDevModifyPwdPresenter {

    private DevModifyPwdContract.IDevModifyPwdView iDevModifyPwdView;

    public DevModifyPwdPresenter(DevModifyPwdContract.IDevModifyPwdView iDevModifyPwdView) {
        this.iDevModifyPwdView = iDevModifyPwdView;
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void modifyDevPwd(String oldPwd, String newPwd) {
        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(getDevId());
        if (xmDevInfo != null) {
            SDBDeviceInfo deviceInfo = xmDevInfo.getSdbDevInfo();
            if (deviceInfo != null) {
                String loginName = G.ToString(deviceInfo.st_4_loginName);
                if (TextUtils.isEmpty(loginName)) {
                    loginName = "admin";
                }
                manager.modifyDevPwd(getDevId(), loginName, oldPwd, newPwd, new DeviceManager.OnDevManagerListener() {
                    @Override
                    public void onSuccess(String s, int i, Object o) {
                        iDevModifyPwdView.onUpdateView("修改成功");
                    }

                    @Override
                    public void onFailed(String s, int i, String s1, int errorId) {
                        iDevModifyPwdView.onUpdateView("修改失败:" + errorId);
                    }
                });
            }
        }
    }
}

