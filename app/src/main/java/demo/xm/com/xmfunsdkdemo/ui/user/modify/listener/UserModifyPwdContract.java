package demo.xm.com.xmfunsdkdemo.ui.user.modify.listener;

import com.manager.account.BaseAccountManager;

/**
 * 修改密码界面,通过输入账号及新旧密码来进行修改
 * Change password interface:by entering the account number and the old and new password to modify
 * Created by jiangping on 2018-10-23.
 */
public class UserModifyPwdContract {
    public interface IUserModifyPwdView {
        void onUpdateView();
    }

    public interface IUserModifyPwdPresenter extends BaseAccountManager.OnAccountManagerListener {
        void userModifyPwd();

        int getErrorId();

        String getReturnData();
    }
}
