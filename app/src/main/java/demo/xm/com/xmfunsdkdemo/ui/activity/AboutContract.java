package demo.xm.com.xmfunsdkdemo.ui.activity;

import com.manager.account.BaseAccountManager;

public class AboutContract {
    public interface IAboutView {
        void onUpdateView();
    }

    public interface IAboutPresenter extends BaseAccountManager.OnAccountManagerListener {

    }
}
