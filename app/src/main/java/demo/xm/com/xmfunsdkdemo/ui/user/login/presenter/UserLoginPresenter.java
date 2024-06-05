package demo.xm.com.xmfunsdkdemo.ui.user.login.presenter;

import android.os.Message;

import com.basic.G;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.lib.sdk.struct.SDK_ChannelNameConfigAll;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.account.LocalAccountManager;
import com.manager.account.share.ShareManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.utils.PathUtils;
import com.xm.activity.base.XMBasePresenter;

import java.io.File;

import demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication;
import demo.xm.com.xmfunsdkdemo.ui.user.login.listener.UserLoginContract;
import demo.xm.com.xmfunsdkdemo.utils.SPUtil;

import static com.manager.db.Define.LOGIN_BY_INTERNET;

/**
 * 用户登录界面,通过账号或手机号及相应密码进行登录
 * User login interface, login through the account or mobile phone number and the corresponding password
 * created by hws 2018-10-27 17:09
 */
public class UserLoginPresenter extends XMBasePresenter<AccountManager> implements UserLoginContract.IUserLoginPresenter {
    private UserLoginContract.IUserLoginView iUserLoginView;
    private int errorId;
    private final String ACCOUNT_USER_NAME = "AccountUserName";
    private final String ACCOUNT_PASSWORD = "AccountPassword";
    private String accountUserName;
    private String accountPassword;

    public UserLoginPresenter(UserLoginContract.IUserLoginView iUserLoginView) {
        this.iUserLoginView = iUserLoginView;
    }

    @Override
    public AccountManager getManager() {
        return AccountManager.getInstance();
    }

    /*账号登录*/
    @Override
    public void loginByAccount(String userName, String pwd) {
        this.accountUserName = userName;
        this.accountPassword = pwd;
        manager.xmLogin(userName, pwd, LOGIN_BY_INTERNET, this);//LOGIN_BY_INTERNET（1）  Account login type
    }

    /*本地登录，本地登录后，添加的设备会保存到手机本地的App私有目录下*/
    @Override
    public void loginByLocal() {
        String dbPath = PathUtils.getAndroidPath(iUserLoginView.getContext()) + File.separator + "CSFile.db";
        LocalAccountManager.getInstance().login(dbPath, this);
    }

    /*AP登录*/
    @Override
    public void loginByAP() {
        //直连：通过IP+Port方式去登录
        setDevId("192.168.10.1:34567");
        getChannelList();
    }

    /**
     * AP直接后获取通道列表
     */
    private void getChannelList() {
        DeviceManager.getInstance().getChannelInfo(getDevId(), new DeviceManager.OnDevManagerListener<SDK_ChannelNameConfigAll>() {
            @Override
            public void onSuccess(String devId, int operationType, SDK_ChannelNameConfigAll channelInfos) {
                if (channelInfos != null) {
                    iUserLoginView.onGetChannelListResult(true, channelInfos.nChnCount);
                } else {
                    iUserLoginView.onGetChannelListResult(true, 0);
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                iUserLoginView.onGetChannelListResult(false, errorId);
            }
        });
    }

    @Override
    public int getDevCount() {
        return manager != null ? DevDataCenter.getInstance().getDevList().size() : 0;
    }

    @Override
    public int getErrorId() {
        return errorId;
    }

    @Override
    public String getUserName() {
        String userName = SPUtil.getInstance(iUserLoginView.getContext()).getSettingParam(ACCOUNT_USER_NAME, "");
        return userName;
    }

    @Override
    public String getPassword() {
        String userName = SPUtil.getInstance(iUserLoginView.getContext()).getSettingParam(ACCOUNT_PASSWORD, "");
        return userName;
    }

    @Override
    public void onSuccess(int msgId) {
        this.errorId = 0;
        if (iUserLoginView != null) {
            if (DevDataCenter.getInstance().isLoginByAccount()) { //Replace the username and password locally
                SPUtil.getInstance(iUserLoginView.getContext()).setSettingParam(ACCOUNT_USER_NAME, accountUserName);
                SPUtil.getInstance(iUserLoginView.getContext()).setSettingParam(ACCOUNT_PASSWORD, accountPassword);
            }

            iUserLoginView.onUpdateView();//Update the interface for subsequent actions
        }

    }

    @Override
    public void onFailed(int msgId, int errorId) {
        this.errorId = errorId;
        if (iUserLoginView != null) {
            iUserLoginView.onUpdateView();
        }
    }

    @Override
    public void onFunSDKResult(Message msg, MsgContent ex) {

    }
}
