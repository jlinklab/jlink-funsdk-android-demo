package demo.xm.com.xmfunsdkdemo.ui.device.config.jdevdebug.presenter;

import com.manager.device.DeviceManager;

import com.xm.activity.base.XMBasePresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.jdevdebug.listener.DevJsonCmdContract;

/**
 * Json 和 DevCmd 调试界面,设置或获得相应配置名称和通道编号的字符串
 * Created by jiangping on 2018-10-23.
 */
public class DevJsonCmdPresenter extends XMBasePresenter<DeviceManager> implements DevJsonCmdContract.IDevJsonCmdPresenter {

    private DevJsonCmdContract.IDevJsonCmdView iDevJsonCmdView;

    public DevJsonCmdPresenter(DevJsonCmdContract.IDevJsonCmdView iDevJsonCmdView) {
        this.iDevJsonCmdView = iDevJsonCmdView;
    }

    @Override
    public void devJsonCmd() {

    }

    @Override
    protected DeviceManager getManager() {
        return null;
    }
}

