package demo.xm.com.xmfunsdkdemo.ui.user.modify.presenter;

import android.os.Message;

import com.lib.MsgContent;
import com.manager.account.XMAccountManager;

import com.xm.activity.base.XMBasePresenter;
import demo.xm.com.xmfunsdkdemo.ui.user.modify.listener.UserModifyPwdContract;

/**
 * 修改密码界面,通过输入账号及新旧密码来进行修改
 * Change password interface, by entering the account number and the old and new password to modify
 * Created by jiangping on 2018-10-23.
 */
public class UserModifyPwdPresenter extends XMBasePresenter<XMAccountManager> implements UserModifyPwdContract.IUserModifyPwdPresenter {
    private UserModifyPwdContract.IUserModifyPwdView iUserModifyPwdView;
    private int errorId;
    private String dataCheck;

    public UserModifyPwdPresenter(UserModifyPwdContract.IUserModifyPwdView iUserModifyPwdView) {
        this.iUserModifyPwdView = iUserModifyPwdView;
    }

    @Override
    protected XMAccountManager getManager() {
        return XMAccountManager.getInstance();
    }

    @Override
    public void userModifyPwd() {

    }

    @Override
    public int getErrorId() {
        return errorId;
    }

    @Override
    public String getReturnData() {
        return dataCheck;
    }

    public boolean checkPwd(String password) {
        return manager == null ? false : manager.checkPwd(password, this);
    }

    public boolean changePwd(String userName, String oldPwd, String newPwd) {
        return manager == null ? false : manager.modifyPwd(userName, oldPwd, newPwd, this);
    }

    @Override
    public void onSuccess(int msgId) {

    }

    @Override
    public void onFailed(int msgId, int errorId) {
        this.errorId = errorId;
        if (iUserModifyPwdView != null) {
            iUserModifyPwdView.onUpdateView();
        }
    }

    @Override
    public void onFunSDKResult(Message msg, MsgContent ex) {
        if (msg.arg1 >= 0) {
            dataCheck = ex.str;
            if (iUserModifyPwdView != null) {
                iUserModifyPwdView.onUpdateView();
            }
        }
    }
}
