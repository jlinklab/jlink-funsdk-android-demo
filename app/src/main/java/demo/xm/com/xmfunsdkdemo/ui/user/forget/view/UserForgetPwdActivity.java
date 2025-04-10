package demo.xm.com.xmfunsdkdemo.ui.user.forget.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.EUIMSG;
import com.lib.sdk.bean.StringUtils;
import com.xm.activity.base.XMBaseActivity;
import com.xm.base.code.ErrorCodeManager;
import com.xm.ui.widget.XTitleBar;

import org.json.JSONObject;

import java.util.regex.Pattern;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.user.forget.listener.UserForgetPwdContract;
import demo.xm.com.xmfunsdkdemo.ui.user.forget.presenter.UserForgetPwdPresenter;
import io.reactivex.annotations.Nullable;

/**
 * 忘记密码界面,可通过邮箱或手机,获取验证码来重置密码
 * Forget password interface, through email or mobile phone, get verification code to reset the password
 * Created by jiangping on 2018-10-23.
 */
public class UserForgetPwdActivity extends DemoBaseActivity<UserForgetPwdPresenter>
        implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, UserForgetPwdContract.IUserForgetPwdView {
    private Resources pwdRes;
    private boolean byEmail = true;
    private boolean isVerifyCodeConfirmed = false;
    int passwordGrade = 0;
    String loadingStr = ".";

    private final int MESSAGE_CHECK_PASSWORD = 0x100;
    private final int MESSAGE_LOADING = 0x101;

    private EditText emailEdit;
    private EditText phoneEdit;
    private EditText verifyCodeEdit;

    private EditText newPwdEdit;
    private TextView pwdGradeText;
    private EditText newPassConfirmEdit;

    private Button btnPhoneInCountry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_forget_passw);
        initView();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.set_user_forget_pwd));
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(UserForgetPwdActivity.class.getName());
        RadioGroup resetPasswModeRadio = findViewById(R.id.rg_forget_psw_mode);
        resetPasswModeRadio.setOnCheckedChangeListener(this);
        resetPasswModeRadio.check(R.id.btn_reset_pwd_by_email);

        emailEdit = findViewById(R.id.et_forget_psw_user_email);
        phoneEdit = findViewById(R.id.et_user_phone);
        verifyCodeEdit = findViewById(R.id.et_user_verify_code);
        newPwdEdit = findViewById(R.id.et_user_new_psw);
        pwdGradeText = findViewById(R.id.tv_passw_garde);
        newPassConfirmEdit = findViewById(R.id.et_user_new_psw_confirm);

        Button sendVerifyCodeBtn = findViewById(R.id.btn_send_verify_code);
        sendVerifyCodeBtn.setOnClickListener(this);
        Button verifyCodeBtn = findViewById(R.id.btn_verify);
        verifyCodeBtn.setOnClickListener(this);
        Button submitBtn = findViewById(R.id.btn_submit);
        submitBtn.setOnClickListener(this);
        showForgetPasswLayout(byEmail);
        btnPhoneInCountry = findViewById(R.id.btn_phone_in_country);
        btnPhoneInCountry.setOnClickListener(this);
        pwdRes = getResources();
        newPwdEdit.addTextChangedListener(textWatcher);
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

    public int getPasswGrade() {
        return passwordGrade;
    }

    /*密码格式判断，长度要求大于等于6，小于等于16*/
    @SuppressLint("HandlerLeak")
    private final Handler handlerPwd = new Handler() {
        @SuppressLint({"HandlerLeak", "UseCompatLoadingForColorStateLists"})
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_CHECK_PASSWORD:
                    String password = (String) msg.obj;
                    if (TextUtils.isEmpty(password)) {
                        pwdGradeText.setText(R.string.password_checker_empty);
                        pwdGradeText.setTextColor(pwdRes.getColorStateList(R.color.passw_weak));
                        break;
                    }
                    if (password.length() < 6) {
                        pwdGradeText.setText(R.string.password_checker_short);
                        pwdGradeText.setTextColor(pwdRes.getColorStateList(R.color.passw_weak));
                        break;
                    }
                    if (password.length() > 16) {
                        pwdGradeText.setText(R.string.password_checker_long);
                        pwdGradeText.setTextColor(pwdRes.getColorStateList(R.color.passw_weak));
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
                    pwdGradeText.setText(loadingDiaplay.toString());
                    pwdGradeText.setTextColor(pwdRes.getColorStateList(R.color.passw_changing));
                    handlerPwd.sendEmptyMessageDelayed(MESSAGE_LOADING, 700);
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.btn_reset_pwd_by_email:
                byEmail = true;
                showForgetPasswLayout(byEmail);
                break;
            case R.id.btn_reset_pwd_by_phone:
                byEmail = false;
                showForgetPasswLayout(byEmail);
                break;
            default:
                break;
        }
    }

    private void showForgetPasswLayout(boolean byEmail) {
        if (byEmail) {
            findViewById(R.id.rl_forget_psw_phone).setVisibility(View.GONE);
            findViewById(R.id.rl_forget_psw_email).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.rl_forget_psw_email).setVisibility(View.GONE);
            findViewById(R.id.rl_forget_psw_phone).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUpdateView() {
        hideWaitDialog();
        if (presenter.getErrorId() == 0) {
            switch (presenter.getMsgId()) {
                case EUIMSG.SYS_FORGET_PWD_XM:
                case EUIMSG.SYS_SEND_EMAIL_FOR_CODE:
                    showToast(getString(R.string.guide_message_request_phone_msg_success), Toast.LENGTH_LONG);
                    break;
                case EUIMSG.SYS_REST_PWD_CHECK_XM:
                case EUIMSG.SYS_CHECK_CODE_FOR_EMAIL:
                    isVerifyCodeConfirmed = true;
                    showToast(getString(R.string.user_forget_pwd_verify_success), Toast.LENGTH_LONG);
                    break;
                case EUIMSG.SYS_RESET_PWD_XM:
                case EUIMSG.SYS_PSW_CHANGE_BY_EMAIL:
                    showToast(getString(R.string.user_forget_pwd_reset_passw_success), Toast.LENGTH_LONG);
                    finish();
                    break;
                default:
                    break;
            }
        } else {
            isVerifyCodeConfirmed = false;
            showToast("" + ErrorCodeManager.getSDKStrErrorByNO(presenter.getErrorId()), Toast.LENGTH_LONG);
        }
    }

    @Override
    public UserForgetPwdPresenter getPresenter() {
        return new UserForgetPwdPresenter(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_verify_code:
                tryToSendVerifyCode();
                break;
            case R.id.btn_verify:
                tryToVerifyCode();
                break;
            case R.id.btn_submit:
                tryToSubmit();
                break;
            case R.id.btn_phone_in_country:
                trytoPhoneInCountry();
            default:
                break;
        }
    }

    /*尝试提交修改密码请求*/
    private void tryToSubmit() {
        String newPwd = newPwdEdit.getText().toString();
        String newPwdConfirm = newPassConfirmEdit.getText().toString();
        if (!isVerifyCodeConfirmed) {
            showToast(getString(R.string.user_forget_pwd_verify_code_first), Toast.LENGTH_LONG);
            return;
        }

        if (TextUtils.isEmpty(newPwd) || newPwd.length() < 6 || newPwd.length() > 16) {
            showToast(getString(R.string.user_forget_pwd_new_password_error), Toast.LENGTH_LONG);
            return;
        }

        if (getPasswGrade() == 1) {
            showToast(getString(R.string.password_checker_weak_error), Toast.LENGTH_LONG);
        }

        if (!newPwd.equals(newPwdConfirm)) {
            showToast(getString(R.string.user_forget_pwd_new_password_confirm_error), Toast.LENGTH_LONG);
            return;
        }
        showWaitDialog();

        //通过邮箱修改密码
        if (byEmail) {
            String email = emailEdit.getText().toString().trim();
            if (!presenter.requestResetPasswByEmail(email, newPwd)) {
                showToast(getString(R.string.guide_message_error_call), Toast.LENGTH_LONG);
            }
            //通过手机修改密码
        } else {
            String phone = phoneEdit.getText().toString().trim();
            if (!presenter.requestResetPasswByPhone(phone, newPwd)) {
                showToast(getString(R.string.guide_message_error_call), Toast.LENGTH_LONG);
            }
        }
    }

    /*邮箱是否有效*/
    public boolean isEmailValid(String email) {
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        return email.matches(regex);
    }

    /*验证验证码是否正确*/
    private void tryToVerifyCode() {
        if (byEmail) {
            String email = emailEdit.getText().toString().trim();
            String verifyCode = verifyCodeEdit.getText().toString().trim();
            showWaitDialog();
            if (!presenter.requestVerifyEmailCode(email, verifyCode)) {
                showToast(getString(R.string.guide_message_error_call), Toast.LENGTH_LONG);
            }
        } else {
            String phone = phoneEdit.getText().toString().trim();
            String verifyCode = verifyCodeEdit.getText().toString().trim();
            Pattern pattern = Pattern.compile("[0-9]{4}");
            if (!pattern.matcher(verifyCode).matches()) {
                showToast(getString(R.string.user_forget_pwd_verify_code_format_erroe), Toast.LENGTH_LONG);
            } else {
                showWaitDialog();
                if (!presenter.requestVerifyPhoneCode(phone, verifyCode)) {
                    showToast(getString(R.string.guide_message_error_call), Toast.LENGTH_LONG);
                }
            }
        }
    }

    /*尝试发送验证码*/
    private void tryToSendVerifyCode() {
        if (byEmail) {
            String email = emailEdit.getText().toString().trim();
            if (!isEmailValid(email)) {
                // 邮箱不正确
                showToast(getString(R.string.user_login_error_email), Toast.LENGTH_LONG);
                return;
            }

            showWaitDialog();
            if (!presenter.requestSendEmailCodeForResetPW(email)) {
                showToast(getString(R.string.guide_message_error_call), Toast.LENGTH_LONG);
            }
        } else {
            String phoneNum = phoneEdit.getText().toString().trim();
            if (phoneNum.length() != 11) {
                // 手机号不正确
                showToast(getString(R.string.user_login_error_phone_number), Toast.LENGTH_LONG);
                return;
            }

            showWaitDialog();
            if (!presenter.requestSendPhoneMsgForResetPW(phoneNum)) {
                showToast(getString(R.string.guide_message_error_call), Toast.LENGTH_LONG);
            }
        }
    }

    public void onCheckPasswFailed(int errCode, String returnData) {
        handlerPwd.removeMessages(MESSAGE_LOADING);
        if (null != pwdGradeText) {
            pwdGradeText.setText(R.string.password_checker_weak);
        }
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    public void onCheckPwdSuccess(String result) {
        if (StringUtils.isStringNULL(result)) {
            return;
        }
        handlerPwd.removeMessages(MESSAGE_LOADING);
        try {
            JSONObject jsonObject = new JSONObject(result);
            int code = jsonObject.getInt("code");
            String grade = jsonObject.getString("grade");
            if (code == 10001) {
                passwordGrade = Integer.parseInt(grade);
                switch (Integer.parseInt(grade)) {
                    case 1:
                        pwdGradeText.setText(R.string.password_checker_weak);
                        pwdGradeText.setTextColor(pwdRes.getColorStateList(R.color.passw_weak));
                        break;
                    case 2:
                        pwdGradeText.setText(R.string.password_checker_medium);
                        pwdGradeText.setTextColor(pwdRes.getColorStateList(R.color.passw_medium));
                        break;
                    case 3:
                        pwdGradeText.setText(R.string.password_checker_strong);
                        pwdGradeText.setTextColor(pwdRes.getColorStateList(R.color.passw_strong));
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void trytoPhoneInCountry() {
        String[] countryPhone = getResources().getStringArray(R.array.phone_in_country);
        new AlertDialog.Builder(this).setTitle("请选择")
                .setSingleChoiceItems(countryPhone, 0, (dialog, which) -> {
                    btnPhoneInCountry.setText(countryPhone[which]);
                    dialog.dismiss();
                }).setNegativeButton("取消", (dialog, which) -> dialog.dismiss()).show();
    }
}
