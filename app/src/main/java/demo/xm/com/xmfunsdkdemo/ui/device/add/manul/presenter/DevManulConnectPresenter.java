package demo.xm.com.xmfunsdkdemo.ui.device.add.manul.presenter;

import com.manager.device.DeviceManager;

import com.xm.activity.base.XMBasePresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.add.manul.listener.DevManulConnectContract;

/**
 * 用户添加设备界面,根据设备类型及序列号添加设备,还可以选择局域网中的设备
 * User add device interface, according to the device type and serial number to add devices, but also can select the local area network device
 * Created by jiangping on 2018-10-23.
 */
public class DevManulConnectPresenter extends XMBasePresenter<DeviceManager> implements DevManulConnectContract.IDevManulConnectPresenter {

    private DevManulConnectContract.IDevManulConnectView iDevManulConnectView;

    public DevManulConnectPresenter(DevManulConnectContract.IDevManulConnectView iDevManulConnectView) {
        this.iDevManulConnectView = iDevManulConnectView;
    }

    @Override
    public void devManulConnect() {

    }

    @Override
    protected DeviceManager getManager() {
        return null;
    }
}

