package demo.xm.com.xmfunsdkdemo.ui.device.config.alarmcenter.presenter;

import com.manager.device.DeviceManager;

import com.xm.activity.base.XMBasePresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.alarmcenter.listener.DevAlarmCenterContract;

/**
 * 报警中心界面,包括协议类型,启用,服务器地址,端口,警报上报,日志上报等.
 * Created by jiangping on 2018-10-23.
 */
public class DevAlarmCenterPresenter extends XMBasePresenter<DeviceManager> implements DevAlarmCenterContract.IDevAlarmCenterPresenter {

    private DevAlarmCenterContract.IDevAlarmCenterView iDevAlarmCenterView;

    public DevAlarmCenterPresenter(DevAlarmCenterContract.IDevAlarmCenterView iDevAlarmCenterView) {
        this.iDevAlarmCenterView = iDevAlarmCenterView;
    }

    @Override
    public void devAlarmCenter() {

    }

    @Override
    protected DeviceManager getManager() {
        return null;
    }
}

