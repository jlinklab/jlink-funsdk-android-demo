package demo.xm.com.xmfunsdkdemo.ui.user.forget.listener;

import com.manager.account.BaseAccountManager;

/**
 * 忘记密码界面,可通过邮箱或手机,获取验证码来重置密码
 * Forget password interface, through email or mobile phone, get verification code to reset the password
 * Created by jiangping on 2018-10-23.
 */
public class UserForgetPwdContract {
    public interface IUserForgetPwdView {
        void onUpdateView();

        void onCheckPwdSuccess(String result);

        void onCheckPasswFailed(int errCode, String returnData);
    }

    public interface IUserForgetPwdPresenter extends BaseAccountManager.OnAccountManagerListener {
        void userForgetPwd();

        int getErrorId();

        int getMsgId();
    }
}
