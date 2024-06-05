package demo.xm.com.xmfunsdkdemo.ui.device.config.alarmcenter.listener;

/**
 * 报警中心界面,包括协议类型,启用,服务器地址,端口,警报上报,日志上报等.
 * Created by jiangping on 2018-10-23.
 */
public class DevAlarmCenterContract {
    public interface IDevAlarmCenterView {
        void onUpdateView();
    }

    public interface IDevAlarmCenterPresenter {
        void devAlarmCenter();
    }
}
