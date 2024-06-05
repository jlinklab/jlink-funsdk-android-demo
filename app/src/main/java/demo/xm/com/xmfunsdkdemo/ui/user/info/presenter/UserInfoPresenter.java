package demo.xm.com.xmfunsdkdemo.ui.user.info.presenter;

import android.content.Context;
import android.os.Message;

import com.lib.MsgContent;
import com.manager.account.XMAccountManager;
import com.manager.account.share.ShareManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMUserInfo;

import com.utils.XUtils;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.user.info.listener.UserInfoContract;
import demo.xm.com.xmfunsdkdemo.utils.SPUtil;

/**
 * 用户信息界面,包括ID,用户名,邮箱,手机,性别,注册时间
 * User information interface, including ID, username, email, mobile phone, gender, registration time
 * Created by jiangping on 2018-10-23.
 */
public class UserInfoPresenter extends XMBasePresenter<XMAccountManager> implements UserInfoContract.IUserInfoPresenter {


    private final UserInfoContract.IUserInfoView iUserInfoView;
    private int errorId;

    public UserInfoPresenter(UserInfoContract.IUserInfoView iUserInfoView) {
        this.iUserInfoView = iUserInfoView;
    }

    public boolean getInfo() {
        return manager.getUserInfo(this);
    }

    public void logout(Context context) {
        SPUtil.getInstance(context).setSettingParam("Password", null);
        manager.logout();
        //释放分享
        ShareManager.getInstance(context).unInit();
        //清除缓存的数据
        DevDataCenter.getInstance().clear();
    }

    @Override
    protected XMAccountManager getManager() {
        return XMAccountManager.getInstance();
    }

    @Override
    public String getUserId() {
        XMUserInfo xmUserInfo = manager.getXmUserInfo();
        return xmUserInfo == null ? "" : xmUserInfo.getUserId();
    }

    @Override
    public String getUserName() {
        XMUserInfo xmUserInfo = manager.getXmUserInfo();
        return xmUserInfo == null ? "" : xmUserInfo.getUserName();
    }

    @Override
    public String getEmail() {
        XMUserInfo xmUserInfo = manager.getXmUserInfo();
        return xmUserInfo == null ? "" : xmUserInfo.getEmail();
    }

    @Override
    public String getPhoneNo() {
        XMUserInfo xmUserInfo = manager.getXmUserInfo();
        return xmUserInfo == null ? "" : xmUserInfo.getPhoneNo();
    }

    @Override
    public int getErrorId() {
        return errorId;
    }


    @Override
    public void onSuccess(int msgId) {
        if (iUserInfoView != null) {
            iUserInfoView.onUpdateView();
        }
    }

    @Override
    public void onFailed(int msgId, int errorId) {
        this.errorId = errorId;
        if (iUserInfoView != null) {
            iUserInfoView.onUpdateView();
        }
    }

    @Override
    public void onFunSDKResult(Message msg, MsgContent ex) {

    }
}