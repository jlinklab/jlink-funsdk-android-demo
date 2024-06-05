package demo.xm.com.xmfunsdkdemo.ui.user.register.listener;

import com.manager.account.BaseAccountManager;

/**
 * 用户注册界面,通过邮箱或手机并获得相应的验证码来注册账号
 * User registration interface, through email or mobile phone and get the corresponding verification code to register an account
 * Created by jiangping on 2018-10-23.
 */
public class UserRegisterContract {
    public interface IUserRegisterView {
        void onUpdateView();
    }

    public interface IUserRegisterPresenter extends BaseAccountManager.OnAccountManagerListener {
        void userCheck(String userName);

        int getErrorId();

        int getMsgId();

        boolean emailCode(String email);

        boolean phoneMsg(String userName, String phoneNo);

    }
}



