package demo.xm.com.xmfunsdkdemo.ui.user.register.presenter;

import android.os.Message;

import com.lib.MsgContent;
import com.manager.account.XMAccountManager;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.user.register.listener.UserRegisterContract;

/**
 * 用户注册界面,通过邮箱或手机并获得相应的验证码来注册账号
 * Created by jiangping on 2018-10-23.
 */
public class UserRegisterPresenter extends XMBasePresenter<XMAccountManager> implements UserRegisterContract.IUserRegisterPresenter {
    private UserRegisterContract.IUserRegisterView iUserRegisterView;
    private int errorId;
    private int msgId;

    public UserRegisterPresenter(UserRegisterContract.IUserRegisterView iUserRegisterView) {
        this.iUserRegisterView = iUserRegisterView;
    }

    /*判断用户是否重名*/
    /* Check if the user has the same name */
    @Override
    public void userCheck(String userName) { //Check that the username is valid
        manager.checkUserName(userName, this);
    }

    /*判断邮箱是否有效*/
    /* Check if email is valid */
    public boolean isEmailValid(String email) {
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        return email.matches(regex);
    }

    /*将验证码发送至邮箱*/
    /* Send CAPTchas to email */
    @Override
    public boolean emailCode(String email) {//Send code by Email
        return manager.sendEmailCodeForRegister(email, this);
    }

    /*将验证码发送至用户手机*/
    /* Send the CAPTCHA to the user's phone */
    @Override
    public boolean phoneMsg(String userName, String phoneNo) {//Send code by phone
        return manager.sendPhoneCodeForRegister(userName, phoneNo, this);
    }

    /*通过电话号码进行注册*/
    /* Register by phone number */
    public boolean registerPhone(String userName, String passWord, String verifyCode, String phoneNo) {//Mobile phone number registered
        return manager.registerByPhoneNo(userName, passWord, verifyCode, phoneNo, this);
    }

    /*通过邮箱进行注册*/
    /* Register via email */
    public boolean registerEmail(String userName, String passWord, String email, String verifyCode) { //Email Registration
        return manager.registerByEmail(userName, passWord, email, verifyCode, this);
    }

    /*无需绑定手机号或者邮箱直接注册*/
    /* Sign up without phone number or email */
    public void registerByNotBind(String userName,String passWord) {//Registration without mobile phone number or email address
        manager.register(userName,passWord,this);
    }

    @Override
    protected XMAccountManager getManager() {
        return XMAccountManager.getInstance();
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
    public void onSuccess(int msgId) {
        this.errorId = 0;
        this.msgId = msgId;
        if (iUserRegisterView != null) {
            iUserRegisterView.onUpdateView();
        }
    }

    @Override
    public void onFailed(int msgId, int errorId) {
        this.errorId = errorId;
        this.msgId = 0;
        if (iUserRegisterView != null) {
            iUserRegisterView.onUpdateView();
        }
    }

    @Override
    public void onFunSDKResult(Message msg, MsgContent ex) {

    }
}
