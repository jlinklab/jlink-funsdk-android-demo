package demo.xm.com.xmfunsdkdemo.ui.device.config.systemfunc.presenter;

import com.manager.device.DeviceManager;

import com.xm.activity.base.XMBasePresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.systemfunc.listener.DevSysFunctionSetContract;

/**
 * 系统功能列表界面,显示仅Demo使用的列表菜单
 * Created by jiangping on 2018-10-23.
 */
public class DevSysFunctionSetPresenter extends XMBasePresenter<DeviceManager> implements DevSysFunctionSetContract.IDevSysFunctionSetPresenter {

    private DevSysFunctionSetContract.IDevSysFunctionSetView iDevSysFunctionSetView;

    public DevSysFunctionSetPresenter(DevSysFunctionSetContract.IDevSysFunctionSetView iDevSysFunctionSetView) {
        this.iDevSysFunctionSetView = iDevSysFunctionSetView;
    }

    @Override
    public void devSysFunctionSet() {

    }

    @Override
    protected DeviceManager getManager() {
        return null;
    }
}

