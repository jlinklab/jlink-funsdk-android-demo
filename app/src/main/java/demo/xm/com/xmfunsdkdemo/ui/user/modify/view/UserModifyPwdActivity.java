package demo.xm.com.xmfunsdkdemo.ui.user.modify.view;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.widget.XTitleBar;

import org.json.JSONObject;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.user.modify.listener.UserModifyPwdContract;
import demo.xm.com.xmfunsdkdemo.ui.user.modify.presenter.UserModifyPwdPresenter;
import io.reactivex.annotations.Nullable;

/**
 * 修改密码界面,通过输入账号及新旧密码来进行修改
 * Change password interface, by entering the account number and the old and new password to modify
 * Created by jiangping on 2018-10-23.
 */
public class UserModifyPwdActivity extends DemoBaseActivity<UserModifyPwdPresenter> implements View.OnClickListener, UserModifyPwdContract.IUserModifyPwdView {
    private EditText etUserName;
    private EditText etOldPwd;
    private EditText etNewPwd;
    private TextView tvPwdGrade;
    private EditText etNewPwdConfirm;
    private Resources pwdRes;
    private final int MESSAGE_CHECK_PASSWORD = 0x100;
    private final int MESSAGE_LOADING = 0x101;
    int passwordGrade = 0;
    String loadingStr = ".";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_change_password);
        initView();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.set_user_modify_pwd));
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(UserModifyPwdActivity.class.getName());
        etUserName = findViewById(R.id.et_modify_pwd_username);
        etOldPwd = findViewById(R.id.et_modify_pwd_old_psw);
        tvPwdGrade = findViewById(R.id.tv_modify_pwd_text_garde);
        etNewPwd = findViewById(R.id.et_modify_pwd_new_psw);
        etNewPwdConfirm = findViewById(R.id.et_modify_pwd_new_psw_confirm);
        Button btnChangePwd = findViewById(R.id.btn_modify_pwd_submit);
        btnChangePwd.setOnClickListener(this);
        pwdRes = getResources();
        etNewPwd.addTextChangedListener(textWatcher);
    }

    @Override
    public UserModifyPwdPresenter getPresenter() {
        return new UserModifyPwdPresenter(this);
    }

    @Override
    public void onUpdateView() {
        if (presenter.getErrorId() == 0) {
            if (presenter.getReturnData() == null) {
                hideWaitDialog();
                showToast(getString(R.string.user_change_password_sucess), Toast.LENGTH_LONG);
                finish();
            } else {
                onCheckPasswSuccess(presenter.getReturnData());
            }
        } else {
            hideWaitDialog();
            onCheckPasswFailed(presenter.getErrorId());
        }
    }

    /*修改密码*/
    /*Change the password*/
    private void tryToChangePwd() {
        String userName = etUserName.getText().toString();
        String oldPwd = etOldPwd.getText().toString();
        String newPwd = etNewPwd.getText().toString();
        String newPwdConfirm = etNewPwdConfirm.getText().toString();

        if (TextUtils.isEmpty(userName)) {
            showToast(getString(R.string.user_change_password_error_emptyusername), Toast.LENGTH_LONG);
            return;
        }
        if (TextUtils.isEmpty(oldPwd) || TextUtils.isEmpty(newPwd) || TextUtils.isEmpty(newPwdConfirm)) {
            showToast(getString(R.string.user_change_password_error_emptypassw), Toast.LENGTH_LONG);
            return;
        } else if (newPwd.length() < 6 || newPwd.length() > 16) {
            showToast(getString(R.string.user_change_password_error_passwtooshort), Toast.LENGTH_LONG);
            return;
        } else if (!newPwd.equals(newPwdConfirm)) {
            showToast(getString(R.string.user_change_password_error_passwnotequal), Toast.LENGTH_LONG);
            return;
        }
        showWaitDialog();

        if (!presenter.changePwd(userName, oldPwd, newPwd)) {
            showToast(getString(R.string.guide_message_error_call), Toast.LENGTH_LONG);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_modify_pwd_submit) {
            tryToChangePwd();
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            handlerPwd.removeMessages(MESSAGE_LOADING);
            handlerPwd.sendEmptyMessageDelayed(MESSAGE_LOADING, 700);

            handlerPwd.removeMessages(MESSAGE_CHECK_PASSWORD);
            Message message = new Message();
            message.what = MESSAGE_CHECK_PASSWORD;
            message.obj = s.toString();
            handlerPwd.sendMessageDelayed(message, 1000);
        }
    };

    @SuppressLint("HandlerLeak")
    private final Handler handlerPwd = new Handler() {
        @SuppressLint({"HandlerLeak", "UseCompatLoadingForColorStateLists"})
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_CHECK_PASSWORD:
                    String password = (String) msg.obj;
                    if (TextUtils.isEmpty(password)) {
                        tvPwdGrade.setText(R.string.password_checker_empty);
                        tvPwdGrade.setTextColor(pwdRes.getColorStateList(R.color.passw_weak));
                        break;
                    }
                    if (password.length() < 6) {
                        tvPwdGrade.setText(R.string.password_checker_short);
                        tvPwdGrade.setTextColor(pwdRes.getColorStateList(R.color.passw_weak));
                        break;
                    }
                    if (password.length() > 20) {
                        tvPwdGrade.setText(R.string.password_checker_long);
                        tvPwdGrade.setTextColor(pwdRes.getColorStateList(R.color.passw_weak));
                        break;
                    }
                    presenter.checkPwd(password);
                    break;
                case MESSAGE_LOADING:
                    loadingStr += ".";
                    if (loadingStr.equals("....")) {
                        loadingStr = ".";
                    }
                    StringBuilder loadingDiaplay = new StringBuilder(loadingStr);
                    for (int i = loadingDiaplay.length(); i < 3; i++) {
                        loadingDiaplay.append(" ");
                    }
                    tvPwdGrade.setText(loadingDiaplay.toString());
                    tvPwdGrade.setTextColor(pwdRes.getColorStateList(R.color.passw_changing));
                    handlerPwd.sendEmptyMessageDelayed(MESSAGE_LOADING, 700);
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("UseCompatLoadingForColorStateLists")
    public void onCheckPasswSuccess(String returnData) {
        handlerPwd.removeMessages(MESSAGE_LOADING);
        try {
            JSONObject jsonObject = new JSONObject(returnData);
            int code = jsonObject.getInt("code");
            String grade = jsonObject.getString("grade");
            if (code == 10001) {
                passwordGrade = Integer.parseInt(grade);
                switch (Integer.parseInt(grade)) {
                    case 1:
                        tvPwdGrade.setText(R.string.password_checker_weak);
                        tvPwdGrade.setTextColor(pwdRes.getColorStateList(R.color.passw_weak));
                        break;
                    case 2:
                        tvPwdGrade.setText(R.string.password_checker_medium);
                        tvPwdGrade.setTextColor(pwdRes.getColorStateList(R.color.passw_medium));
                        break;
                    case 3:
                        tvPwdGrade.setText(R.string.password_checker_strong);
                        tvPwdGrade.setTextColor(pwdRes.getColorStateList(R.color.passw_strong));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCheckPasswFailed(int errCode) {
        handlerPwd.removeMessages(MESSAGE_LOADING);
        if (null != tvPwdGrade) {
            tvPwdGrade.setText(R.string.password_checker_weak);
        }
        showToast(getResources().getString(R.string.loaded_failed) + errCode, Toast.LENGTH_LONG);
    }
}
