package demo.xm.com.xmfunsdkdemo.ui.user.info.listener;

import android.content.Context;

import com.manager.account.BaseAccountManager;

/**
 * 用户信息界面,包括ID,用户名,邮箱,手机,性别,注册时间
 * User information interface, including ID, username, email, mobile phone, gender, registration time
 * Created by jiangping on 2018-10-23.
 */
public class UserInfoContract {
    public interface IUserInfoView {
        void onUpdateView();
        void onDeleteAccountResult(boolean isSuccess,int errorId);
        Context getContext();

    }

    public interface IUserInfoPresenter extends BaseAccountManager.OnAccountManagerListener {
        String getUserId();

        String getUserName();

        String getEmail();

        String getPhoneNo();

        int getErrorId();

        /**
         * 删除账号
         */
        void deleteAccount();
    }
}