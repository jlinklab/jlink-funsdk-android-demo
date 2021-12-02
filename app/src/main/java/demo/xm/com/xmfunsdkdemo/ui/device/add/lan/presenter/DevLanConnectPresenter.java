package demo.xm.com.xmfunsdkdemo.ui.device.add.lan.presenter;

import static com.manager.db.Define.LOGIN_NONE;

import android.os.Message;

import com.basic.G;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.xm.activity.base.XMBasePresenter;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.ui.device.add.lan.listener.DevLanConnectContract;

/**
 * 局域网连接设备界面,显示设备列表菜单,包括名称,类型,mac,sn,ip,状态,以及是否在线
 * LAN connection device interface, display device list menu, including name,
 * type,mac,sn,ip, status, and whether online
 * Created by jiangping on 2018-10-23.
 */
public class DevLanConnectPresenter extends XMBasePresenter<DeviceManager>
        implements DevLanConnectContract.IDevLanConnectPresenter,DeviceManager.OnSearchLocalDevListener,
        BaseAccountManager.OnAccountManagerListener {

    private DevLanConnectContract.IDevLanConnectView iDevLanConnectView;
    private List<XMDevInfo> localDevList;
    private AccountManager accountManager;
    public DevLanConnectPresenter(DevLanConnectContract.IDevLanConnectView IDevLanConnectView) {
        this.iDevLanConnectView = IDevLanConnectView;
        accountManager = AccountManager.getInstance();
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void searchLanDevice() {
        manager.searchLanDevice(this);
    }

    /**
     * 获取设备列表
     * get a list of devices
     * @return
     */
    @Override
    public List<XMDevInfo> getLanDevList() {
        return localDevList;
    }

    /**
     * 获取某个设备信息
     * get information about a device
     * @param position
     * @return
     */
    @Override
    public XMDevInfo getLanDevInfo(int position) {
        return localDevList == null ? null : localDevList.get(position);
    }

    /**
     * 将设备添加到账号上
     * add the device to the account
     * @param xmDevInfo
     */
    @Override
    public void addDeviceToAccount(XMDevInfo xmDevInfo) {
        //未使用AccountManager(包括XMAccountManager或LocalAccountManager)登录（包括账号登录和本地临时登录），只能将设备信息临时缓存，重启应用后无法查到设备信息。
        if (DevDataCenter.getInstance().getLoginType() == LOGIN_NONE) {
            DevDataCenter.getInstance().addDev(xmDevInfo);
            FunSDK.AddDevInfoToDataCenter(G.ObjToBytes(xmDevInfo.getSdbDevInfo()), 0, 0, "");
            if (iDevLanConnectView != null) {
                iDevLanConnectView.onAddDevResult(true,0);
            }
        }else {
            accountManager.addDev(xmDevInfo, this);
        }
    }

    /**
     * 局域网搜索设备回调
     * LAN search device callback
     * @param localDevList
     */
    @Override
    public void onSearchLocalDevResult(List<XMDevInfo> localDevList) {
        this.localDevList = localDevList;
        if (iDevLanConnectView != null) {
            iDevLanConnectView.onUpdateView();
        }
    }

    @Override
    public void onSuccess(int msgId) {
        if (msgId == EUIMSG.SYS_ADD_DEVICE) {
            if (iDevLanConnectView != null) {
                iDevLanConnectView.onAddDevResult(true,0);
            }
        }
    }

    @Override
    public void onFailed(int msgId, int errorId) {
        if (msgId == EUIMSG.SYS_ADD_DEVICE) {
            if (iDevLanConnectView != null) {
                iDevLanConnectView.onAddDevResult(false,errorId);
            }
        }
    }

    @Override
    public void onFunSDKResult(Message msg, MsgContent ex) {

    }
}

