package demo.xm.com.xmfunsdkdemo.ui.device.config.pwdmodify.listener;

/**
 * 密码修改界面,为了保护隐私,可以更改设备的访问密码
 * Created by jiangping on 2018-10-23.
 */
public class DevModifyPwdContract {
    public interface IDevModifyPwdView {
        void onUpdateView(String result);
    }

    public interface IDevModifyPwdPresenter {
        void modifyDevPwd(String oldPwd,String newPwd);
    }
}
