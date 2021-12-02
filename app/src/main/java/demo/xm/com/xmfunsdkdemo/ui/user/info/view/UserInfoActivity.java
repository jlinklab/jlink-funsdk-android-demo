package demo.xm.com.xmfunsdkdemo.ui.user.info.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.manager.db.DevDataCenter;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.push.view.DevPushService;
import demo.xm.com.xmfunsdkdemo.ui.user.info.listener.UserInfoContract;
import demo.xm.com.xmfunsdkdemo.ui.user.info.presenter.UserInfoPresenter;
import demo.xm.com.xmfunsdkdemo.ui.user.login.view.UserLoginActivity;
import demo.xm.com.xmfunsdkdemo.utils.SPUtil;
import io.reactivex.annotations.Nullable;

/**
 * 用户信息界面,包括ID,用户名,邮箱,手机,性别,注册时间
 * User information interface, including ID, username, email, mobile phone, gender, registration time
 * Created by jiangping on 2018-10-23.
 */
public class UserInfoActivity extends DemoBaseActivity<UserInfoPresenter> implements View.OnClickListener, UserInfoContract.IUserInfoView {
    private TextView tvUserId;

    private TextView tvUserName;
    private TextView tvUserEmail;

    private TextView tvUserPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_info);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.set_user_info));
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(UserInfoActivity.class.getName());
        tvUserId = findViewById(R.id.tv_user_Id);
        tvUserName = findViewById(R.id.tv_user_name);

        tvUserEmail = findViewById(R.id.tv_user_email);
        tvUserPhone = findViewById(R.id.tv_user_phone);

        Button btnLogout = (Button) findViewById(R.id.btn_user_logout);
        btnLogout.setOnClickListener(this);
    }

    private void initData() {
        if (DevDataCenter.getInstance().isLoginByAccount()) {
            showWaitDialog();
            findViewById(R.id.layout_user_Info).setVisibility(View.VISIBLE);
            tryToGetUserInfo();
        }
    }

    private void tryToGetUserInfo() {
        showWaitDialog();
        if (!presenter.getInfo()) {
            showToast(getString(R.string.user_info_not_login), Toast.LENGTH_LONG);
            finish();
            Intent intent = new Intent();
            intent.setClass(this, UserLoginActivity.class);
            startActivity(intent);
        }
    }

    private void tryToLogout() {
        presenter.logout(this);
        stopService(new Intent(this, DevPushService.class));
        turnToActivity(UserLoginActivity.class);
        if (getApplication() instanceof SDKDemoApplication) {
            ((SDKDemoApplication) getApplication()).exit();
        }
        finish();
    }

    /*获取用户信息并更新数据至界面*/
    @Override
    public void onUpdateView() {
        hideWaitDialog();
        if (presenter != null) {
            tvUserId.setText(presenter.getUserId());
            tvUserName.setText(presenter.getUserName());

            if (SPUtil.isEmpty(presenter.getEmail())) {
                findViewById(R.id.layoutUserEmail).setVisibility(View.GONE);
            } else {
                tvUserEmail.setText(presenter.getEmail());
            }
            if (SPUtil.isEmpty(presenter.getPhoneNo())) {
                findViewById(R.id.layoutUserPhone).setVisibility(View.GONE);
            } else {
                tvUserPhone.setText(presenter.getPhoneNo());
            }
        } else {
            showToast("", Toast.LENGTH_LONG);
        }
    }

    @Override
    public UserInfoPresenter getPresenter() {
        return new UserInfoPresenter(this);
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_user_logout) {
            showWaitDialog();
            tryToLogout();
        }
    }
}
