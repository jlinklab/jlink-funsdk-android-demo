package demo.xm.com.xmfunsdkdemo.ui.user.login.listener;

import android.content.Context;

import com.manager.account.BaseAccountManager;

/**
 * 用户登录界面,通过账号或手机号及相应密码进行登录
 * User login interface, login through the account or mobile phone number and the corresponding password
 * Created by jiangping on 2018-10-23.
 */
public class UserLoginContract {
    public interface IUserLoginView {
        void onUpdateView();

        /**
         * 跳转到警戒设置
         * Jump to Alert Settings
         */
        void onTurnToIntelligentVigilance();

        /**
         * 通道列表返回结果
         *
         * @param isSuccess
         * @param resultId
         */
        void onGetChannelListResult(boolean isSuccess, int resultId);

        Context getContext();
    }

    public interface IUserLoginPresenter extends BaseAccountManager.OnAccountManagerListener {
        /**
         * 账号登录
         * Account login
         *
         * @param userName
         * @param pwd
         */
        void loginByAccount(String userName, String pwd);

        /**
         * 本地登录
         * Local login
         */
        void loginByLocal();

        /**
         * AP直连
         * AP direct connection
         */
        void loginByAP();

        int getDevCount();

        int getErrorId();

        String getUserName();

        String getPassword();
    }
}
