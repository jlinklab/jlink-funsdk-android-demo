package demo.xm.com.xmfunsdkdemo.ui.device.retrievepassword.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basic.G;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.sdk.bean.JsonConfig;
import com.manager.device.DeviceManager;
import com.utils.VerificationUtils;
import com.utils.XUtils;
import com.xm.activity.base.XMBaseActivity;
import com.xm.activity.base.XMBasePresenter;
import com.xm.base.code.ErrorCodeManager;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.ErrorManager;

import demo.xm.com.xmfunsdkdemo.R;

/**
 * Created by lmy on 2018-12-18.
 */
public class CheckVerifyCodeActivity extends XMBaseActivity implements View.OnClickListener, IFunSDKResult {

    private EditText mEditTextVerifyCode;
    private EditText mEditTextPwd;
    private EditText mEditTextConfirmPwd;
    private String mDevSN;
    private String mVerifyCode;
    private String mPassword;
    private TextView mTips;
    private int userId;

    public static void startActivity(Activity activity, String devId, String verifyCode) {
        Intent intent = new Intent(activity, CheckVerifyCodeActivity.class);
        intent.putExtra("devId", devId);
        intent.putExtra("VerifyCode", verifyCode);
        activity.startActivityForResult(intent, 1);
    }

    @Override
    public XMBasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_check_verify_code);
        initView();
        initData();
    }

    private void initView() {
        XTitleBar titleBar = findViewById(R.id.layoutTop);
        titleBar.setLeftClick(this::finish);

        mEditTextVerifyCode = findViewById(R.id.et_verifyCode);
        mEditTextPwd = findViewById(R.id.et_pwd);
        mTips = findViewById(R.id.tips);
        mTips.setText("*" + getString(R.string.dev_pwd_input_tip));
        mEditTextConfirmPwd = findViewById(R.id.et_confirmPwd);

        mDevSN = getIntent().getStringExtra("devId");
        mVerifyCode = getIntent().getStringExtra("VerifyCode");
        if (XUtils.isEmpty(mVerifyCode)) {
            mEditTextVerifyCode.setEnabled(true);
        } else {
            findViewById(R.id.tv_hint).setVisibility(View.VISIBLE);
            mEditTextVerifyCode.setText(mVerifyCode);
            mEditTextVerifyCode.setEnabled(false);
        }

        findViewById(R.id.btn_confirm_retrieve_pwd).setOnClickListener(this);
    }

    private void initData() {
        userId = FunSDK.GetId(userId,this);
    }

    @Override
    public int OnFunSDKResult(Message msg, MsgContent ex) {
        switch (msg.what) {
            case EUIMSG.DEV_CONFIG_JSON_NOT_LOGIN:
                if (JsonConfig.CHECK_VERIFY_CODE.equals(ex.str)) {
                    if (msg.arg1 < 0) {
                        LoadingDialog.getInstance(this).dismiss();
                        if (ex.pData != null && ex.pData.length > 0) {
                            try {
                                JSONObject jsonObject = new JSONObject(G.ToString(ex.pData));
                                int ret = jsonObject.optInt("Ret");
                                if (ret == 221) {//验证次数超过限制，需重启设备再尝试
                                    Toast.makeText(getApplicationContext(), R.string.pls_dev_restart, Toast.LENGTH_SHORT).show();
                                    return 0;
                                } else if (ret == 222) {//验证码错误
                                    Toast.makeText(getApplicationContext(), FunSDK.TS("EE_AS_REGISTE_BY_EMAIL_CODE4"), Toast.LENGTH_SHORT).show();
                                    return 0;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            XMPromptDlg.onShowErrorDlg(this, ErrorCodeManager.getSDKStrErrorByNO(msg.arg1),null,false);
                        }
                    } else {
                        String loginName = FunSDK.DevGetLocalUserName(mDevSN);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("Name", JsonConfig.SET_NEW_PASSWORD);
                            jsonObject.put("SessionId", "0x1");
                            JSONObject object = new JSONObject();
                            jsonObject.put(JsonConfig.SET_NEW_PASSWORD, object);
                            object.put("UserName", TextUtils.isEmpty(loginName) ? "admin" : loginName);
                            object.put("NewPassword", mPassword);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        FunSDK.DevConfigJsonNotLogin(userId, mDevSN, JsonConfig.SET_NEW_PASSWORD, jsonObject.toString(), 1650, -1, 0,
                                5000, 0);
                    }
                } else if (JsonConfig.SET_NEW_PASSWORD.equals(ex.str)) {
                    LoadingDialog.getInstance(this).dismiss();
                    if (msg.arg1 < 0) {
                        XMPromptDlg.onShowErrorDlg(this, ErrorCodeManager.getSDKStrErrorByNO(msg.arg1),null,false);
                    } else {
                        String loginName = FunSDK.DevGetLocalUserName(mDevSN);
                        DeviceManager.getInstance().setLocalDevUserPwd(mDevSN,TextUtils.isEmpty(loginName) ? "admin" : loginName, mPassword);
                        Toast.makeText(getApplicationContext(), FunSDK.TS("Reset_S"), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                }
                break;
            default:
                break;
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm_retrieve_pwd:
                if (XUtils.isEmpty(mVerifyCode)) {
                    if (XUtils.isEmpty(mEditTextVerifyCode.getText().toString())) {
                        Toast.makeText(this,R.string.input_code, Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        mVerifyCode = mEditTextVerifyCode.getText().toString().trim();
                    }
                }

                if (XUtils.isEmpty(mEditTextPwd.getText().toString())) {
                    Toast.makeText(getApplicationContext(),R.string.user_change_password_error_emptypassw, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mEditTextPwd.getText().toString().equals(mEditTextConfirmPwd.getText().toString())) {
                    Toast.makeText(getApplicationContext(),R.string.EE_AS_RESET_PWD_CODE4, Toast.LENGTH_SHORT).show();
                    return;
                }
                mPassword = mEditTextPwd.getText().toString().trim();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Name", JsonConfig.CHECK_VERIFY_CODE);
                    jsonObject.put("SessionId", "0x1");
                    JSONObject object = new JSONObject();
                    jsonObject.put(JsonConfig.CHECK_VERIFY_CODE, object);
                    object.put("VerifyCode", mVerifyCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LoadingDialog.getInstance(this).show();
                FunSDK.DevConfigJsonNotLogin(userId, mDevSN, JsonConfig.CHECK_VERIFY_CODE, jsonObject.toString(), 1650, -1, 0,
                        5000, 0);
                break;
            default:
                break;
        }
    }
}
