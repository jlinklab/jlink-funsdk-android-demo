package demo.xm.com.xmfunsdkdemo.ui.user.info.listener;

import com.manager.account.BaseAccountManager;

/**
 * 用户信息界面,包括ID,用户名,邮箱,手机,性别,注册时间
 * User information interface, including ID, username, email, mobile phone, gender, registration time
 * Created by jiangping on 2018-10-23.
 */
public class UserInfoContract {
    public interface IUserInfoView {
        void onUpdateView();
    }

    public interface IUserInfoPresenter extends BaseAccountManager.OnAccountManagerListener {
        String getUserId();
        String getUserName();
        String getEmail();
        String getPhoneNo();
        int getErrorId();
    }
}