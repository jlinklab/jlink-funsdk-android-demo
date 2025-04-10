package demo.xm.com.xmfunsdkdemo.ui.user.register.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lib.EUIMSG;
import com.xm.activity.base.XMBaseActivity;
import com.xm.base.code.ErrorCodeManager;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.user.register.listener.UserRegisterContract;
import demo.xm.com.xmfunsdkdemo.ui.user.register.presenter.UserRegisterPresenter;

/**
 * 用户注册界面,通过邮箱或手机并获得相应的验证码来注册账号
 * User registration interface, through email or mobile phone and get the corresponding verification code to register an account
 * Created by jiangping on 2018-10-23.
 */
public class UserRegisterActivity extends DemoBaseActivity<UserRegisterPresenter>
        implements OnClickListener, OnCheckedChangeListener, UserRegisterContract.IUserRegisterView {
    private boolean byEmail = true;
    private RadioGroup rgRegisterMode;
    private Button btn_get_verify_code;
    private Button btnPhoneInCountry;

    private EditText etUserName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etVerifyCode;
    private EditText etPassWord;
    private EditText etPassWordConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_register);
        initView();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.set_user_register));
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(UserRegisterActivity.class.getName());
        rgRegisterMode = findViewById(R.id.rg_register_mode);
        rgRegisterMode.setOnCheckedChangeListener(this);
        etUserName = findViewById(R.id.et_user_register_username);
        etEmail = findViewById(R.id.et_user_register_email);
        etPhone = findViewById(R.id.et_user_register_phone);
        etVerifyCode = findViewById(R.id.et_user_register_verify_code);
        etPassWord = findViewById(R.id.et_user_register_psw);
        etPassWordConfirm = findViewById(R.id.et_user_register_psw_confirm);
        Button btnRegisterByNotBind = findViewById(R.id.btn_register_not_bind);
        btnRegisterByNotBind.setOnClickListener(this);

        btn_get_verify_code = findViewById(R.id.btn_get_verify_code);
        btn_get_verify_code.setOnClickListener(this);
        Button registerBtn = (Button) findViewById(R.id.userRegisterBtn);
        registerBtn.setOnClickListener(this);
        Button userNameCheckBtn = findViewById(R.id.btn_username_repeat);
        userNameCheckBtn.setOnClickListener(this);
        btnPhoneInCountry = findViewById(R.id.btn_phone_in_country);
        btnPhoneInCountry.setOnClickListener(this);

        rgRegisterMode.check(R.id.btn_register_by_email);
        showRegisterLayout(byEmail);
    }

    @SuppressLint("NonConstantResourceId")
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        switch (id) {
            case R.id.btn_register_by_email: {
                byEmail = true;
                showRegisterLayout(true);
            }
            break;
            case R.id.btn_register_by_phone: {
                byEmail = false;
                showRegisterLayout(false);
            }
            break;
        }
    }

    private void setVerifyCodeButton() {
        btn_get_verify_code.setEnabled(true);
        btn_get_verify_code.setTextColor(getResources().getColor(R.color.white));
        btn_get_verify_code.setBackgroundResource(R.drawable.common_button_selector);
    }

    private void showRegisterLayout(boolean byEmail) {
        if (byEmail) {
            findViewById(R.id.layoutRegisterPhone).setVisibility(View.GONE);
            findViewById(R.id.layoutRegisterEmail).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.layoutRegisterPhone).setVisibility(View.VISIBLE);
            findViewById(R.id.layoutRegisterEmail).setVisibility(View.GONE);
        }
        setVerifyCodeButton();
    }

    private void checkUsername() {
        String userName = etUserName.getText().toString();
        if (userName.length() != 0) {
            presenter.userCheck(userName);
        }
    }

    private void tryGetVerifyCode() {
        String userName = etUserName.getText().toString();
        String phoneNum = etPhone.getText().toString().trim();
        String emailStr = etEmail.getText().toString().trim();

        if (userName.length() == 0) {
            // 用户名为空
            showToast(getString(R.string.user_login_error_emptyusername), Toast.LENGTH_LONG);
            return;
        }

        if (byEmail) {
            if (!presenter.isEmailValid(emailStr)) {
                // 邮箱不正确
                showToast(getString(R.string.user_login_error_email), Toast.LENGTH_LONG);
                return;
            }
            showWaitDialog();

            if (!presenter.emailCode(emailStr)) {
                showToast(getString(R.string.guide_message_error_call), Toast.LENGTH_LONG);
            }
        } else {
            if (phoneNum.length() != 11) {
                // 手机号不正确
                showToast(getString(R.string.user_login_error_phone_number), Toast.LENGTH_LONG);
                return;
            }
            showWaitDialog();

            if (!presenter.phoneMsg(userName, phoneNum)) {
                showToast(getString(R.string.guide_message_error_call), Toast.LENGTH_LONG);
            }
        }
    }

    private void tryToRegister() {
        String userName = etUserName.getText().toString();
        String passWord = etPassWord.getText().toString();
        String passWordConfirm = etPassWordConfirm.getText().toString();

        if (userName.length() == 0) {
            // 用户名为空
            showToast(getString(R.string.user_login_error_emptyusername), Toast.LENGTH_LONG);
            return;
        }

        if (passWord.length() == 0) {
            // 密码为空
            showToast(getString(R.string.user_login_error_emptypassword), Toast.LENGTH_LONG);
            return;
        }

        if (!passWord.equals(passWordConfirm)) {
            showToast(getString(R.string.user_register_error_password_unmatched), Toast.LENGTH_LONG);
            return;
        }

        if (userName.length() > 16 || userName.length() < 6) {
            showToast(getString(R.string.user_register_error_username_length), Toast.LENGTH_LONG);
            return;
        }

        if (passWord.length() < 8) {
            showToast(getString(R.string.user_register_error_password_length), Toast.LENGTH_LONG);
            return;
        }

        if (R.id.btn_register_by_email
                == rgRegisterMode.getCheckedRadioButtonId()) {
            // 通过邮箱注册
            String email = etEmail.getText().toString().trim();
            String verifyCode = this.etVerifyCode.getText().toString().trim();

            if (email.length() == 0 || !email.contains("@") || !presenter.isEmailValid(email)) {
                // 邮箱格式不正确
                showToast(getString(R.string.user_login_error_email), Toast.LENGTH_LONG);
                return;
            }

            if (verifyCode.length() == 0) {
                // 验证码为空
                showToast(getString(R.string.user_login_error_emptyverifycode), Toast.LENGTH_LONG);
                return;
            }

            showWaitDialog();
            if (!presenter.registerEmail(userName, passWord, email, verifyCode)) {
                showToast(getString(R.string.guide_message_error_call), Toast.LENGTH_LONG);
            }
        } else {
            // 通过手机号注册
            String phoneNo = etPhone.getText().toString().trim();
            String verifyCode = this.etVerifyCode.getText().toString().trim();

            if (phoneNo.length() != 11) {
                // 手机号不正确
                showToast(getString(R.string.user_login_error_phone_number), Toast.LENGTH_LONG);
                return;
            }

            if (verifyCode.length() == 0) {
                // 验证码为空
                showToast(getString(R.string.user_login_error_emptyverifycode), Toast.LENGTH_LONG);
                return;
            }

            showWaitDialog();
            if (!presenter.registerPhone(userName, passWord, verifyCode, phoneNo)) {
                showToast(getString(R.string.guide_message_error_call), Toast.LENGTH_LONG);
            }
        }
    }

    private void tryToRegisterByNotBind() {
        String userName = etUserName.getText().toString();
        String passWord = etPassWord.getText().toString();
        String passWordConfirm = etPassWordConfirm.getText().toString();

        if (userName.length() == 0) {
            // 用户名为空
            showToast(getString(R.string.user_login_error_emptyusername), Toast.LENGTH_LONG);
            return;
        }

        if (passWord.length() == 0) {
            // 密码为空
            showToast(getString(R.string.user_login_error_emptypassword), Toast.LENGTH_LONG);
            return;
        }

        if (!passWord.equals(passWordConfirm)) {
            showToast(getString(R.string.user_register_error_password_unmatched), Toast.LENGTH_LONG);
            return;
        }

        if (userName.length() > 16 || userName.length() < 6) {
            showToast(getString(R.string.user_register_error_username_length), Toast.LENGTH_LONG);
            return;
        }

        if (passWord.length() < 8) {
            showToast(getString(R.string.user_register_error_password_length), Toast.LENGTH_LONG);
            return;
        }

        presenter.registerByNotBind(userName, passWord);
    }

    private void trytoPhoneInCountry() {
        String[] countryPhone = getResources().getStringArray(R.array.phone_in_country);
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.please_to_choose))
                .setSingleChoiceItems(countryPhone, 0, (dialog, which) -> {
                    btnPhoneInCountry.setText(countryPhone[which]);
                    dialog.dismiss();
                }).setNegativeButton(getResources().getString(R.string.common_cancel), (dialog, which) -> dialog.dismiss()).show();
    }

    @Override
    public void onUpdateView() {
        hideWaitDialog();
        if (presenter.getErrorId() == 0) {
            switch (presenter.getMsgId()) { //Depending on the MsgId returned, different subsequent actions are processed
                case EUIMSG.SYS_CHECK_USER_REGISTE:
                    showToast(getString(R.string.guide_message_username_fine), Toast.LENGTH_LONG);
                    break;
                case EUIMSG.SYS_GET_PHONE_CHECK_CODE:
                case EUIMSG.SYS_SEND_EMAIL_CODE:
                    showToast(getString(R.string.guide_message_request_phone_msg_success), Toast.LENGTH_LONG);
                    break;
                case EUIMSG.SYS_REGISER_USER_XM:
                case EUIMSG.SYS_REGISTE_BY_EMAIL:
                    showToast(getString(R.string.guide_message_register_user_success), Toast.LENGTH_LONG);
                    finish();
            }
        } else {
            showToast("" + ErrorCodeManager.getSDKStrErrorByNO(presenter.getErrorId()), Toast.LENGTH_LONG);
        }
    }

    @Override
    public UserRegisterPresenter getPresenter() {
        return new UserRegisterPresenter(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_username_repeat: {
                checkUsername();
            }
            break;
            case R.id.btn_get_verify_code: {
                tryGetVerifyCode();
            }
            break;
            case R.id.userRegisterBtn: {
                tryToRegister();
            }
            break;
            case R.id.btn_register_not_bind: {
                tryToRegisterByNotBind();
            }
            break;
            case R.id.btn_phone_in_country: {
                trytoPhoneInCountry();
            }
            break;
            default:
                break;
        }
    }
}
