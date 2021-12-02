package demo.xm.com.xmfunsdkdemo.ui.device.config.simpleconfig.listener;

/**
 * 关于设备界面,包含设备基本信息(序列号,设备型号,硬件版本,软件版本,
 * 发布时间,设备时间,运行时间,网络模式,云连接状态,固件更新及恢复出厂设置)
 * Created by jiangping on 2018-10-23.
 */
public class DevSimpleConfigContract {
    public interface IDevSimpleConfigView {
        void onSendDataResult(String result);
        void onReceiveDataResult(String state,String result);
    }

    public interface IDevSimpleConfigPresenter {
        void getConfig(String jsonName,int chnId);
        void saveConfig(String jsonName,int chnId,String jsonData);
        void cmdConfig(String jsonName,int cmdId,int chnId,String jsonData);
    }
}
