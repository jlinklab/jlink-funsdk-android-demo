package demo.xm.com.xmfunsdkdemo.ui.device.config.frontpanel.listener;

/**
 * 前部面板操作,包括上下左右,退出,菜单及ok按钮
 * Created by jiangping on 2018-10-23.
 */
public class DevFrontSetContract {
    public interface IDevFrontSetView {
        void onUpdateView();
    }

    public interface IDevFrontSetPresenter {
        void devFrontSet();
    }
}
