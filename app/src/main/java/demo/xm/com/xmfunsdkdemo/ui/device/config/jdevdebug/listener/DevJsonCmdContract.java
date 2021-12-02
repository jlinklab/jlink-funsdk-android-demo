package demo.xm.com.xmfunsdkdemo.ui.device.config.jdevdebug.listener;

/**
 * Json 和 DevCmd 调试界面,设置或获得相应配置名称和通道编号的字符串
 * Created by jiangping on 2018-10-23.
 */
public class DevJsonCmdContract {
    public interface IDevJsonCmdView {
        void onUpdateView();
    }

    public interface IDevJsonCmdPresenter {
        void devJsonCmd();
    }
}
