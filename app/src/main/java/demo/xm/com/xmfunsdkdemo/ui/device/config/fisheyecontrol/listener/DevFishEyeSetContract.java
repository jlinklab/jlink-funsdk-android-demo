package demo.xm.com.xmfunsdkdemo.ui.device.config.fisheyecontrol.listener;

/**
 * 鱼眼灯泡控制,包括模式,类型及自动模式时间间隔
 * Created by jiangping on 2018-10-23.
 */
public class DevFishEyeSetContract {
    public interface IDevFishEyeSetView {
        void onUpdateView();
    }

    public interface IDevFishEyeSetPresenter {
        void devFishEyeSet();
    }
}
