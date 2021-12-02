package demo.xm.com.xmfunsdkdemo.ui.device.config.gbconfig.presenter;

import com.manager.device.DeviceManager;

import com.xm.activity.base.XMBasePresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.gbconfig.listener.DevGbSetContract;

/**
 * GB配置界面,包括使能标记,服务器地址,报警编号,设备编号,通道编号等
 * Created by jiangping on 2018-10-23.
 */
public class DevGbSetPresenter extends XMBasePresenter<DeviceManager> implements DevGbSetContract.IDevGbSetPresenter {

    private DevGbSetContract.IDevGbSetView iDevGbSetView;

    public DevGbSetPresenter(DevGbSetContract.IDevGbSetView iDevGbSetView) {
        this.iDevGbSetView = iDevGbSetView;
    }

    @Override
    public void devGbSet() {

    }

    @Override
    protected DeviceManager getManager() {
        return null;
    }
}

