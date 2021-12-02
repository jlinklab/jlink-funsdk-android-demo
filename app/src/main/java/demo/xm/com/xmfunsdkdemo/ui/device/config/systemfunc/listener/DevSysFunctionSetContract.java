package demo.xm.com.xmfunsdkdemo.ui.device.config.systemfunc.listener;

/**
 * 系统功能列表界面,显示仅Demo使用的列表菜单
 * Created by jiangping on 2018-10-23.
 */
public class DevSysFunctionSetContract {
    public interface IDevSysFunctionSetView {
        void onUpdateView();
    }

    public interface IDevSysFunctionSetPresenter {
        void devSysFunctionSet();
    }
}
