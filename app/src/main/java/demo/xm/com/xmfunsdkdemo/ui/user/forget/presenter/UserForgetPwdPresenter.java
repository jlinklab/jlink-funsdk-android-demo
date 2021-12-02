package demo.xm.com.xmfunsdkdemo.ui.user.forget.presenter;

import android.os.Message;

import com.lib.EUIMSG;
import com.lib.MsgContent;
import com.manager.account.XMAccountManager;

import com.xm.activity.base.XMBasePresenter;
import demo.xm.com.xmfunsdkdemo.ui.user.forget.listener.UserForgetPwdContract;

/**
 * 忘记密码界面,可通过邮箱或手机,获取验证码来重置密码
 * Forget password interface, through email or mobile phone, get verification code to reset the password
 * Created by jiangping on 2018-10-23.
 */
public class UserForgetPwdPresenter extends XMBasePresenter<XMAccountManager> implements UserForgetPwdContract.IUserForgetPwdPresenter {
    private UserForgetPwdContract.IUserForgetPwdView iUserForgetPwdView;
    private int errorId = 0;
    private int msgId;

    public UserForgetPwdPresenter(UserForgetPwdContract.IUserForgetPwdView iUserForgetPwdView) {
        this.iUserForgetPwdView = iUserForgetPwdView;
    }

    @Override
    public void userForgetPwd() {

    }

    @Override
    public int getErrorId() {
        return errorId;
    }

    @Override
    public int getMsgId() {
        return msgId;
    }

    @Override
    protected XMAccountManager getManager() {
        return XMAccountManager.getInstance();
    }

    public boolean checkPwd(String password) {
        return manager == null ? false : manager.checkPwd(password, this);
    }

    public boolean requestSendEmailCodeForResetPW(String email) {
        return manager == null ? false : manager.sendEmailCodeForResetPwd(email, this);
    }

    public boolean requestSendPhoneMsgForResetPW(String phone) {
        return manager == null ? false : manager.sendPhoneCodeForResetPwd(phone, this);
    }

    public boolean requestVerifyEmailCode(String email, String verifyCode) {
        return manager == null ? false : manager.verifyEmailCode(email, verifyCode, this);
    }

    public boolean requestVerifyPhoneCode(String phone, String verifyCode) {
        return manager == null ? false : manager.verifyPhoneCode(phone, verifyCode, this);
    }

    public boolean requestResetPasswByEmail(String email, String newPwd) {
        return manager == null ? false : manager.resetPwdByEmail(email, newPwd, this);
    }

    public boolean requestResetPasswByPhone(String phone, String newPwd) {
        return manager == null ? false : manager.resetPwdByPhone(phone, newPwd, this);
    }

    @Override
    public void onSuccess(int msgId) {
        this.errorId = 0;
        this.msgId = msgId;
        if (iUserForgetPwdView != null) {
            iUserForgetPwdView.onUpdateView();
        }
    }

    @Override
    public void onFailed(int msgId, int errorId) {
        this.errorId = errorId;
        if (iUserForgetPwdView != null) {
            iUserForgetPwdView.onUpdateView();
        }
    }

    @Override
    public void onFunSDKResult(Message msg, MsgContent ex) {
        if (msg.what == EUIMSG.SYS_CHECK_PWD_STRENGTH) {
            if (iUserForgetPwdView != null) {
                if (msg.arg1 >= 0) {
                    iUserForgetPwdView.onCheckPwdSuccess(ex.str);
                } else {
                    iUserForgetPwdView.onCheckPasswFailed(msg.arg1, ex.str);
                }
            }
        }
    }
}
