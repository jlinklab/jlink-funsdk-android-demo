package demo.xm.com.xmfunsdkdemo.ui.device.config.frontpanel.presenter;

import com.manager.device.DeviceManager;

import com.xm.activity.base.XMBasePresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.frontpanel.listener.DevFrontSetContract;

/**
 * 前部面板操作,包括上下左右,退出,菜单及ok按钮
 * Created by jiangping on 2018-10-23.
 */
public class DevFrontSetPresenter extends XMBasePresenter<DeviceManager> implements DevFrontSetContract.IDevFrontSetPresenter {

    private DevFrontSetContract.IDevFrontSetView iDevFrontSetView;

    public DevFrontSetPresenter(DevFrontSetContract.IDevFrontSetView iDevFrontSetView) {
        this.iDevFrontSetView = iDevFrontSetView;
    }

    @Override
    public void devFrontSet() {

    }

    @Override
    protected DeviceManager getManager() {
        return null;
    }
}

