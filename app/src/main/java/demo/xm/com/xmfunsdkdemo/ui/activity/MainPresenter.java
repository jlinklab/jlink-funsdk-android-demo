package demo.xm.com.xmfunsdkdemo.ui.activity;

import android.os.Message;

import com.lib.MsgContent;
import com.manager.account.AccountManager;
import com.manager.db.DevDataCenter;
import com.xm.activity.base.XMBasePresenter;

public class MainPresenter extends XMBasePresenter<AccountManager> implements MainContract.IMainPresenter {
    private MainContract.IMainView iMainView;
    public MainPresenter(MainContract.IMainView iMainView) {
        this.iMainView = iMainView;
    }

    @Override
    protected AccountManager getManager() {
        return AccountManager.getInstance();
    }


    @Override
    public void onSuccess(int msgId) {
        if (iMainView != null) {
            iMainView.onUpdateView();
        }
    }

    @Override
    public void onFailed(int msgId,int errorId) {

    }

    @Override
    public void onFunSDKResult(Message msg, MsgContent ex) {

    }

    @Override
    public boolean isLoginByAccount() {
        return DevDataCenter.getInstance().isLoginByAccount();
    }
}
