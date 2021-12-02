package demo.xm.com.xmfunsdkdemo.ui.user.local.presenter;

import android.content.Context;
import android.os.Message;

import com.lib.MsgContent;
import com.manager.account.XMAccountManager;

import com.xm.activity.base.XMBasePresenter;
import demo.xm.com.xmfunsdkdemo.ui.user.local.listener.UserSaveLocalPwdContract;
import demo.xm.com.xmfunsdkdemo.utils.SPUtil;

/**
 * 密码本地保存界面,设置是否允许调用接口来取得本地保存的密码.
 * Password local saving interface, set whether to allow to call the interface to get the local saved password.
 * Created by jiangping on 2018-10-23.
 */
public class UserSaveLocalPwdPresenter extends XMBasePresenter<XMAccountManager> implements UserSaveLocalPwdContract.ISaveLocalPwdPresenter {
    private UserSaveLocalPwdContract.IUserSaveLocalPwdView iUserSaveLocalPwdView;
    private final String SHARED_PARAM_KEY_SAVENATIVEPASSWORD = "SHARED_PARAM_KEY_SAVENATIVEPASSWORD";

    public UserSaveLocalPwdPresenter(UserSaveLocalPwdContract.IUserSaveLocalPwdView iUserSaveLocalPwdView) {
        this.iUserSaveLocalPwdView = iUserSaveLocalPwdView;
    }

    @Override
    protected XMAccountManager getManager() {
        return XMAccountManager.getInstance();
    }

    @Override
    public void saveLocalPwd() {

    }

    @Override
    public boolean getSaveNativePassword(Context context) {
        return SPUtil.getInstance(context).getSettingParam(SHARED_PARAM_KEY_SAVENATIVEPASSWORD, true);
    }

    @Override
    public void setSaveNativePassword(Context context, boolean isSelected) {
        SPUtil.getInstance(context).setSettingParam(SHARED_PARAM_KEY_SAVENATIVEPASSWORD, isSelected);
    }

    @Override
    public void onSuccess(int msgId) {

    }

    @Override
    public void onFailed(int msgId, int errorId) {

    }

    @Override
    public void onFunSDKResult(Message msg, MsgContent ex) {

    }
}
