package demo.xm.com.xmfunsdkdemo.ui.device.config.fisheyecontrol.presenter;

import com.manager.device.DeviceManager;

import com.xm.activity.base.XMBasePresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.fisheyecontrol.listener.DevFishEyeSetContract;

/**
 * 鱼眼灯泡控制,包括模式,类型及自动模式时间间隔
 * Created by jiangping on 2018-10-23.
 */
public class DevFishEyeSetPresenter extends XMBasePresenter<DeviceManager> implements DevFishEyeSetContract.IDevFishEyeSetPresenter {

    private DevFishEyeSetContract.IDevFishEyeSetView iDevFishEyeSetView;

    public DevFishEyeSetPresenter(DevFishEyeSetContract.IDevFishEyeSetView iDevFishEyeSetView) {
        this.iDevFishEyeSetView = iDevFishEyeSetView;
    }

    @Override
    public void devFishEyeSet() {

    }

    @Override
    protected DeviceManager getManager() {
        return null;
    }
}

