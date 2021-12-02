package demo.xm.com.xmfunsdkdemo.ui.user.local.listener;

import android.content.Context;

import com.manager.account.BaseAccountManager;

/**
 * 密码本地保存界面,设置是否允许调用接口来取得本地保存的密码.
 * Password local saving interface, set whether to allow to call the interface to get the local saved password.
 * Created by jiangping on 2018-10-23.
 */
public class UserSaveLocalPwdContract {
    public interface IUserSaveLocalPwdView {
        void onUpdateView();
    }

    public interface ISaveLocalPwdPresenter extends BaseAccountManager.OnAccountManagerListener {
        void saveLocalPwd();

        boolean getSaveNativePassword(Context context);

        void setSaveNativePassword(Context context, boolean isSelected);
    }
}
