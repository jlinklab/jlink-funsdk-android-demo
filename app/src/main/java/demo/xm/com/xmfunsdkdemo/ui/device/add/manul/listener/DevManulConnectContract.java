package demo.xm.com.xmfunsdkdemo.ui.device.add.manul.listener;

/**
 * 用户添加设备界面,根据设备类型及序列号添加设备,还可以选择局域网中的设备
 * User add device interface, according to the device type and serial number to add devices, but also can select the local area network device
 * Created by jiangping on 2018-10-23.
 */
public class DevManulConnectContract {
    public interface IDevManulConnectView {
        void onUpdateView();
    }

    public interface IDevManulConnectPresenter {
        void devManulConnect();
    }
}
