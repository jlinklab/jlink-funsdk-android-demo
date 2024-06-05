package demo.xm.com.xmfunsdkdemo.ui.user.local.view;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.user.local.listener.UserSaveLocalPwdContract;
import demo.xm.com.xmfunsdkdemo.ui.user.local.presenter.UserSaveLocalPwdPresenter;
import demo.xm.com.xmfunsdkdemo.ui.user.modify.view.UserModifyPwdActivity;
import io.reactivex.annotations.Nullable;

/**
 * 密码本地保存界面,设置是否允许调用接口来取得本地保存的密码.
 * Password local saving interface, set whether to allow to call the interface to get the local saved password.
 * Created by jiangping on 2018-10-23.
 */
public class UserSaveLocalPwdActivity extends DemoBaseActivity<UserSaveLocalPwdPresenter> implements View.OnClickListener, UserSaveLocalPwdContract.IUserSaveLocalPwdView {
    private ImageView switchImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_debug);
        initView();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.set_user_save_local_pwd));
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(UserSaveLocalPwdActivity.class.getName());
        switchImage = findViewById(R.id.debugswitch);
        switchImage.setSelected(presenter.getSaveNativePassword(this));
        switchImage.setOnClickListener(this);
    }

    @Override
    public void onUpdateView() {

    }

    @Override
    public UserSaveLocalPwdPresenter getPresenter() {
        return new UserSaveLocalPwdPresenter(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.debugswitch:
                switchImage.setSelected(!switchImage.isSelected());
                presenter.setSaveNativePassword(this, switchImage.isSelected());
                break;
            default:
                break;
        }
    }
}
