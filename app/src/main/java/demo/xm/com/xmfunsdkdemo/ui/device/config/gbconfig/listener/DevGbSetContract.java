package demo.xm.com.xmfunsdkdemo.ui.device.config.gbconfig.listener;

/**
 * GB配置界面,包括使能标记,服务器地址,报警编号,设备编号,通道编号等
 * Created by jiangping on 2018-10-23.
 */
public class DevGbSetContract {
    public interface IDevGbSetView {
        void onUpdateView();
    }

    public interface IDevGbSetPresenter {
        void devGbSet();
    }
}
