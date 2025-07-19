package demo.xm.com.xmfunsdkdemo.ui.device.retrievepassword.view;

import static com.manager.device.config.DevRetrievePasswordManager.RETRIEVE_PWD_NO_SUPPORT;
import static com.manager.device.config.DevRetrievePasswordManager.RETRIEVE_PWD_SUPPORT_QRCODE_AND_QUESTION;
import static com.manager.device.config.DevRetrievePasswordManager.RETRIEVE_PWD_SUPPORT_QRCODE_AND_QUESTION_NOT_SET;
import static com.manager.device.config.DevRetrievePasswordManager.RETRIEVE_PWD_SUPPORT_QUESTION;
import static com.manager.device.config.DevRetrievePasswordManager.RETRIEVE_PWD_SUPPORT_QUESTION_NOT_SET;
import static demo.xm.com.xmfunsdkdemo.base.DemoConstant.APP_KEY;
import static demo.xm.com.xmfunsdkdemo.base.DemoConstant.APP_UUID;
import static demo.xm.com.xmfunsdkdemo.base.DemoConstant.REQUEST_DEV_RETRIEVE_PWD_CODE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lib.FunSDK;
import com.lib.MsgContent;
import com.manager.device.config.DevRetrievePasswordManager;
import com.utils.VerificationUtils;
import com.utils.XUtils;
import com.xm.activity.base.XMBaseActivity;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.BtnColorBK;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.dialog.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.retrievepassword.contract.RetrievePasswordContract;
import demo.xm.com.xmfunsdkdemo.ui.device.retrievepassword.presenter.RetrievePasswordPresenter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RetrievePasswordActivity extends XMBaseActivity
        implements RetrievePasswordContract.IRetrievePasswordView, View.OnClickListener {
    private LinearLayout mLlWay1;
    private LinearLayout mLlWay2;

    private TextView mTvWay1;
    private TextView mTvWay2;
    private TextView mTvWay1Hint;
    private TextView mTvWay2Hint;
    private TextView mTvQuestion1;
    private TextView mTvQuestion2;
    private TextView mTvNewPasswordHint;

    private EditText mEtQuestion1;
    private EditText mEtQuestion2;
    private EditText mEtNewPassword;
    private EditText mEtConfirmPassword;

    private ListSelectItem mLsiGetQRCode;
    private BtnColorBK mBtnConfirm;

    private RetrievePasswordContract.IRetrievePasswordPresenter mPresenter;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            MessageData data = (MessageData) msg.obj;
            switch (msg.what) {
                case 0:
                case 1:
                    Toast.makeText(RetrievePasswordActivity.this,
                            FunSDK.TS("RegCode_Send_To") + " " + data.getPhone(),
                            Toast.LENGTH_LONG).show();
                    CheckVerifyCodeActivity.startActivity(RetrievePasswordActivity.this, data.getSn(), "");
                    break;
                case 2:
                    Toast.makeText(RetrievePasswordActivity.this,getString(R.string.verification_code_populated),
                            Toast.LENGTH_SHORT).show();
                    CheckVerifyCodeActivity.startActivity(RetrievePasswordActivity.this, data.getSn(), data.getPhone());
                    break;
                case 3:
                    Toast.makeText(RetrievePasswordActivity.this, getString(R.string.get_code_failed), Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_retrieve_password);
        initView();
        initData();
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        String devId = getIntent().getStringExtra("devId");
        if (!XUtils.isSn(devId)) {
            finish();
            return;
        }

        mPresenter = new RetrievePasswordPresenter(this, devId);
        mTvWay1Hint.setText(R.string.answer_security_question);
        mTvWay2Hint.setText(R.string.get_verification_retrieve_password);
        showWaitDialog();
        DevRetrievePasswordManager.getInstance().getSafetyAbility(devId, new DevRetrievePasswordManager.OnDevRetrievePwdListener() {
            @Override
            public void onSupportDevRetrievePwd(String devId, int supportType) {
                hideWaitDialog();
                if (supportType == RETRIEVE_PWD_SUPPORT_QUESTION || supportType == RETRIEVE_PWD_SUPPORT_QRCODE_AND_QUESTION || supportType == RETRIEVE_PWD_SUPPORT_QRCODE_AND_QUESTION) {
                    if (DevRetrievePasswordManager.getInstance().isSupportQuestion(devId)) {
                        mLlWay1.setVisibility(View.VISIBLE);
                        mBtnConfirm.setVisibility(View.VISIBLE);

                        LoadingDialog.getInstance(RetrievePasswordActivity.this).show();
                        mPresenter.setLanguage();
                    } else {
                        mLlWay1.setVisibility(View.GONE);
                        mBtnConfirm.setVisibility(View.GONE);
                        mTvWay2.setText(getString(R.string.method) + " 1:");
                    }

                    if (DevRetrievePasswordManager.getInstance().isSupportVerifyQRCode(devId)) {
                        mLlWay2.setVisibility(View.VISIBLE);
                    } else {
                        mLlWay2.setVisibility(View.GONE);
                    }
                }else if(supportType == RETRIEVE_PWD_SUPPORT_QUESTION_NOT_SET || supportType == RETRIEVE_PWD_SUPPORT_QRCODE_AND_QUESTION_NOT_SET){
                    showToast(getString( R.string.this_network_not_support),Toast.LENGTH_LONG);
                    finish();
                }else {
                    showToast(getString( R.string.device_alarm_func_alarmout_tip),Toast.LENGTH_LONG);
                    finish();
                }
            }
        });
    }

    private void initView() {
        XTitleBar titleBar = findViewById(R.id.layoutTop);
        titleBar.setLeftClick(this::finish);

        mLlWay1 = findViewById(R.id.ll_way1);
        mLlWay2 = findViewById(R.id.ll_way2);

        mTvWay1 = findViewById(R.id.tv_way1);
        mTvWay2 = findViewById(R.id.tv_way2);
        mTvWay1Hint = findViewById(R.id.tv_way1_hint);
        mTvWay2Hint = findViewById(R.id.tv_way2_hint);
        mTvQuestion1 = findViewById(R.id.tv_question1);
        mTvQuestion2 = findViewById(R.id.tv_question2);
        mTvNewPasswordHint = findViewById(R.id.tv_new_password_hint);

        mEtQuestion1 = findViewById(R.id.et_question1);
        mEtQuestion2 = findViewById(R.id.et_question2);
        mEtNewPassword = findViewById(R.id.et_new_password);
        mEtConfirmPassword = findViewById(R.id.et_new_password_confirm);

        mLsiGetQRCode = findViewById(R.id.lsi_get_qr_code);
        mBtnConfirm = findViewById(R.id.btn_confirm_retrieve_pwd);

        mLsiGetQRCode.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            finish();
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initQuestions(List<String> questions) {
        LoadingDialog.getInstance(this).dismiss();
        if (questions.size() <= 1) {
            // 问题异常
            mLlWay1.setVisibility(View.GONE);
            mBtnConfirm.setVisibility(View.GONE);
            mTvWay2.setText(getString(R.string.method) + " 1:");
            mTvWay2Hint.setText(R.string.get_verification_retrieve_password);
        } else {
            mLlWay1.setVisibility(View.VISIBLE);
            mBtnConfirm.setVisibility(View.VISIBLE);

            mTvWay1.setText(getString(R.string.method) + " 1:");
            mTvWay2.setText(getString(R.string.method) + " 2:");
            mTvQuestion1.setText(getString(R.string.question) + " 1");
            mTvQuestion2.setText(getString(R.string.question) + " 2");

            mTvNewPasswordHint.setText("*" + getString(R.string.dev_pwd_input_tip));

            mEtQuestion1.setHint(questions.get(0));
            mEtQuestion2.setHint(questions.get(1));
        }
    }

    @Override
    public String getNewPassword() {
        return mEtNewPassword.getText().toString();
    }

    @Override
    public void getSafetyQuestionResult(boolean success) {
        LoadingDialog.getInstance(this).dismiss();
    }

    @Override
    public void setNewPasswordResult(boolean success) {
        LoadingDialog.getInstance(this).dismiss();
        if (success) {
            Toast.makeText(this, R.string.reset_s, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void requestCheckCode(String code) {
        try {
            code = URLEncoder.encode(code, "UTF-8");
            String url = String.format(REQUEST_DEV_RETRIEVE_PWD_CODE_URL, code, APP_UUID, APP_KEY);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
            final Request request = new Request.Builder().get().url(url).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    FunSDK.Log("requestCheckCode onFailure\n");
                    Message msg = new Message();
                    msg.what = 3;
                    msg.obj = new MessageData("", "");
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        FunSDK.Log("requestCheckCode result:" + json + "\n");
                        if (jsonObject.has("sendStatus")) {
                            // 发送失败
                            boolean sendStatus = jsonObject.optBoolean("sendStatus");
                            String sn = jsonObject.optString("sn");
                            if (!sendStatus) {
                                Message msg = new Message();
                                msg.what = 3;
                                msg.obj = new MessageData(sn, "");
                                mHandler.sendMessage(msg);
                                return;
                            }
                        }
                        if (jsonObject.has("notifyPhone")) {
                            String phone = jsonObject.optString("notifyPhone");
                            String sn = jsonObject.optString("sn");
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = new MessageData(sn, phone);
                            mHandler.sendMessage(msg);
                        } else if (jsonObject.has("notifyEmail")) {
                            String email = jsonObject.optString("notifyEmail");
                            String sn = jsonObject.optString("sn");
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = new MessageData(sn, email);
                            mHandler.sendMessage(msg);
                        } else if (jsonObject.has("securityCode")) {
                            String securityCode = jsonObject.optString("securityCode");
                            String sn = jsonObject.optString("sn");
                            Message msg = new Message();
                            msg.what = 2;
                            msg.obj = new MessageData(sn, securityCode);
                            mHandler.sendMessage(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public XMBasePresenter getPresenter() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm_retrieve_pwd:
                List<String> mQuestions = mPresenter.getQuestions();
                if (mQuestions == null || mQuestions.size() < 1) {
                    return;
                }
                String answer1 = mEtQuestion1.getText().toString();
                String answer2 = mEtQuestion2.getText().toString();
                String password = mEtNewPassword.getText().toString();
                String confirmPwd = mEtConfirmPassword.getText().toString();
                if (XUtils.isEmpty(answer1)) {
                    Toast.makeText(getApplicationContext(), R.string.enter_answer, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mQuestions.size() > 1 && XUtils.isEmpty(answer2)) {
                    Toast.makeText(getApplicationContext(), R.string.enter_answer, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (XUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(),R.string.user_change_password_error_emptypassw, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPwd)) {
                    Toast.makeText(getApplicationContext(),R.string.EE_AS_RESET_PWD_CODE4, Toast.LENGTH_SHORT).show();
                    return;
                }
                LoadingDialog.getInstance(this).show();
                mPresenter.checkQuestionsAnswer(answer1, answer2);
                break;
            case R.id.lsi_get_qr_code:
                LoadingDialog.getInstance(this).show();
                mPresenter.requestVerifyCode();
                break;
            default:
                break;
        }
    }

    public static class MessageData {
        String sn;
        String phone;

        public MessageData(String sn, String phone) {
            this.sn = sn;
            this.phone = phone;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
