package demo.xm.com.xmfunsdkdemo.ui.device.add.sn.presenter;

import static com.manager.db.Define.LOGIN_NONE;

import android.os.Message;

import com.basic.G;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;

import com.manager.device.DeviceManager;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.device.add.sn.listener.DevSnConnectContract;

/**
 * Connect the device with the serial number, and login to the device according to the serial number
 * and account password of the device type, or the IP device port and account password.
 * <p>
 * Created by jiangping on 2018-10-23.
 */
public class DevSnConnectPresenter extends XMBasePresenter<AccountManager> implements DevSnConnectContract.IDevSnConnectPresenter {

    private DevSnConnectContract.IDevSnConnectView iDevSnConnectView;

    public DevSnConnectPresenter(DevSnConnectContract.IDevSnConnectView iDevSnConnectView) {
        this.iDevSnConnectView = iDevSnConnectView;
    }

    @Override
    protected AccountManager getManager() {
        return AccountManager.getInstance();
    }

    /**
     * Add device
     *
     * @param devId    serial number
     * @param userName login userName
     * @param pwd      login password
     * @param devType  device Type
     */

    /*序列号连接设备*/
    /*Connect the device by its sequence number*/
    @Override
    public void addDev(String devId, String userName, String pwd, String devToken, int devType) {
        SDBDeviceInfo deviceInfo = new SDBDeviceInfo();
        G.SetValue(deviceInfo.st_0_Devmac, devId);//设备序列号
        G.SetValue(deviceInfo.st_1_Devname, devId);
        G.SetValue(deviceInfo.st_4_loginName, userName);
        deviceInfo.st_7_nType = devType;
        XMDevInfo xmDevInfo = new XMDevInfo();
        xmDevInfo.setDevPassword(pwd);
        xmDevInfo.setDevUserName(userName);
        xmDevInfo.sdbDevInfoToXMDevInfo(deviceInfo);
        if (!StringUtils.isStringNULL(devToken)) {
            xmDevInfo.setDevToken(devToken);
        }

        //未使用AccountManager(包括XMAccountManager或LocalAccountManager)登录（包括账号登录和本地临时登录），只能将设备信息临时缓存，重启应用后无法查到设备信息。
        if (DevDataCenter.getInstance().getLoginType() == LOGIN_NONE) {
            DevDataCenter.getInstance().addDev(xmDevInfo);
            FunSDK.AddDevInfoToDataCenter(G.ObjToBytes(xmDevInfo.getSdbDevInfo()), 0, 0, "");
            if (iDevSnConnectView != null) {
                iDevSnConnectView.onAddDevResult(true, 0);
            }
        } else {
            manager.addDev(xmDevInfo, new BaseAccountManager.OnAccountManagerListener() {  //The WiFi-paired addDev is missing a parameter to unbind the current device
                @Override
                public void onSuccess(int i) {
                    if (iDevSnConnectView != null) {
                        iDevSnConnectView.onAddDevResult(true, 0);
                    }
                }

                @Override
                public void onFailed(int i, int errorId) {
                    if (iDevSnConnectView != null) {
                        iDevSnConnectView.onAddDevResult(false, errorId);
                    }
                }

                @Override
                public void onFunSDKResult(Message message, MsgContent msgContent) {

                }
            });
        }
    }
}

