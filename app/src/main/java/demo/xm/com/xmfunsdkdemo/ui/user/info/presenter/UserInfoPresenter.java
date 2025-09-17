package demo.xm.com.xmfunsdkdemo.ui.user.info.presenter;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;

import com.lib.MsgContent;
import com.manager.account.BaseAccountManager;
import com.manager.account.XMAccountManager;
import com.manager.account.share.ShareManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMUserInfo;

import com.manager.push.XMPushManager;
import com.utils.XUtils;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.dialog.EditDialog;

import demo.xm.com.xmfunsdkdemo.R;
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
        //取消客服订阅
        new XMPushManager(null).unLinkCustomService();
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

    /**
     * 删除账号
     */
    @Override
    public void deleteAccount() {
        XMAccountManager.getInstance().deleteXMAccount("", new BaseAccountManager.OnAccountManagerListener() {
            @Override
            public void onSuccess(int msgId) {

            }

            @Override
            public void onFailed(int msgId, int errorId) {

            }

            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {
                if (msg.arg1 >= 0) {
                    iUserInfoView.onDeleteAccountResult(true, msg.arg1);
                } else {
                    if (msg.arg1 == -604302) {
                        //如果账号绑定了手机号或者邮箱的话，系统已经发送验证码到您的手机号或者邮箱了，需要验证验证码后才能正常删除账号
                        XMPromptDlg.onShowEditDialog(iUserInfoView.getContext(),
                                iUserInfoView.getContext().getString(R.string.please_input_code), "", new EditDialog.OnEditContentListener() {
                                    @Override
                                    public void onResult(String content) {
                                        checkDeleteCode(content);
                                    }
                                });
                    } else {
                        iUserInfoView.onDeleteAccountResult(false, msg.arg1);
                    }
                }
            }
        });
    }

    /**
     * 如果账号绑定了邮箱或者手机号，需要先验证码才能删除设备
     */
    private void checkDeleteCode(String code) {
        XMAccountManager.getInstance().deleteXMAccount(code, new BaseAccountManager.OnAccountManagerListener() {
            @Override
            public void onSuccess(int msgId) {

            }

            @Override
            public void onFailed(int msgId, int errorId) {

            }

            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {
                iUserInfoView.onDeleteAccountResult(msg.arg1 >= 0, msg.arg1);
            }
        });
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