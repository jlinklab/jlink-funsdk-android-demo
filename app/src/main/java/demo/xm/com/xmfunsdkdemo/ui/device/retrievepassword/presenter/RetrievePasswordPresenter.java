package demo.xm.com.xmfunsdkdemo.ui.device.retrievepassword.presenter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.basic.G;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.SetLanguage;
import com.manager.device.DeviceManager;
import com.utils.XUtils;
import com.xm.base.code.ErrorCodeManager;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.dialog.LoadingDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ErrorManager;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.retrievepassword.contract.RetrievePasswordContract;


public class RetrievePasswordPresenter implements RetrievePasswordContract.IRetrievePasswordPresenter {
    private int _msgId = 0xff00ff;
    private String mDevId;
    private RetrievePasswordContract.IRetrievePasswordView mView;
    private List<String> mQuestions;

    public RetrievePasswordPresenter(RetrievePasswordContract.IRetrievePasswordView view, String devId) {
        mView = view;
        mDevId = devId;
    }

    public synchronized int GetId() {
        _msgId = FunSDK.GetId(_msgId, this);
        return _msgId;
    }

    @Override
    public List<String> getQuestions() {
        return mQuestions;
    }

    @Override
    public void setLanguage() {
        SetLanguage setLanguage = new SetLanguage();
        setLanguage.setLanguage(XUtils.getSetLanguage());
        FunSDK.DevConfigJsonNotLogin(GetId(), mDevId, JsonConfig.SET_LANGUAGE,
                HandleConfigData.getSendData(JsonConfig.SET_LANGUAGE, "0x01", setLanguage),
                1650, -1, 0, 5000, 0);
    }

    @Override
    public void requestVerifyCode() {
        FunSDK.DevConfigJsonNotLoginPtl(GetId(), mDevId, JsonConfig.GET_VERIFY_QR_CODE,
                null, 1650, -1, 0, 5000, 0,0);
    }

    @Override
    public void checkQuestionsAnswer(String answer1, String answer2) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", JsonConfig.CHECK_SAFETY_ANSWER);
            jsonObject.put("SessionId", "0x1");
            JSONObject object = new JSONObject();
            jsonObject.put(JsonConfig.CHECK_SAFETY_ANSWER, object);
            JSONArray jsonArray = new JSONArray();
            object.put("Answer", jsonArray);
            if (!TextUtils.isEmpty(answer2)) {
                jsonArray.put(answer1);
                jsonArray.put(answer2);
            } else {
                jsonArray.put(answer1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FunSDK.DevConfigJsonNotLoginPtl(GetId(), mDevId, JsonConfig.CHECK_SAFETY_ANSWER, jsonObject.toString(), 1650, -1, 0, 5000, 0,0);
    }

    @Override
    public int OnFunSDKResult(Message msg, MsgContent ex) {
        switch (msg.what) {
            case EUIMSG.DEV_CONFIG_JSON_NOT_LOGIN:
                if (JsonConfig.SET_LANGUAGE.equals(ex.str)) {
                    FunSDK.DevConfigJsonNotLogin(GetId(), mDevId, JsonConfig.GET_SAFETY_QUESTION,
                            "", 1650, -1, 0, 5000, 0);
                } else if (JsonConfig.GET_SAFETY_QUESTION.equals(ex.str)) {
                    if (msg.arg1 < 0) {
                        mView.getSafetyQuestionResult(false);
                        XMPromptDlg.onShowErrorDlg(mView.getActivity(),ErrorCodeManager.getSDKStrErrorByNO(msg.arg1),null,false);
                    } else {
                        if (ex.pData != null && ex.pData.length > 0) {
                            try {
                                JSONObject jsonObject = new JSONObject(G.ToString(ex.pData));
                                JSONObject object = jsonObject.optJSONObject(JsonConfig.GET_SAFETY_QUESTION);
                                JSONArray questions = object.optJSONArray("Question");
                                mQuestions = new ArrayList<>();
                                for (int i = 0; i < questions.length(); i++) {
                                    mQuestions.add((String) questions.get(i));
                                }
                                if (mQuestions.size() > 0) {
                                    mView.initQuestions(mQuestions);
                                } else {
                                    //数据异常
                                    Toast.makeText(mView.getActivity(), FunSDK.TS("Data_exception"),
                                            Toast.LENGTH_SHORT).show();
                                    mView.getSafetyQuestionResult(false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                //数据异常
                                Toast.makeText(mView.getActivity(), FunSDK.TS("Data_exception"),
                                        Toast.LENGTH_SHORT).show();
                                mView.getSafetyQuestionResult(false);
                            }
                        }
                    }
                } else if (JsonConfig.CHECK_SAFETY_ANSWER.equals(ex.str)) {
                    if (msg.arg1 < 0) {
                        mView.setNewPasswordResult(false);
                        if (ex.pData != null && ex.pData.length > 0) {
                            try {
                                JSONObject jsonObject = new JSONObject(G.ToString(ex.pData));
                                int ret = jsonObject.optInt("Ret");
                                if (ret == 219) {//验证次数超过6次，需重启设备再尝试
                                    Toast.makeText(mView.getActivity(), FunSDK.TS("TR_pls_dev_Restart"),
                                            Toast.LENGTH_SHORT).show();
                                    return 0;
                                } else if (ret == 220) {//答案错误
                                    Toast.makeText(mView.getActivity(), FunSDK.TS("TR_answer_error"),
                                            Toast.LENGTH_SHORT).show();
                                    return 0;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        XMPromptDlg.onShowErrorDlg(mView.getActivity(),ErrorCodeManager.getSDKStrErrorByNO(msg.arg1),null,false);
                    } else {
                        String loginName = FunSDK.DevGetLocalUserName(mDevId);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("Name", JsonConfig.SET_NEW_PASSWORD);
                            jsonObject.put("SessionId", "0x1");
                            JSONObject object = new JSONObject();
                            jsonObject.put(JsonConfig.SET_NEW_PASSWORD, object);
                            object.put("UserName", TextUtils.isEmpty(loginName) ? "admin" : loginName);
                            object.put("NewPassword", mView.getNewPassword());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        FunSDK.DevConfigJsonNotLogin(GetId(), mDevId, JsonConfig.SET_NEW_PASSWORD,
                                jsonObject.toString(), 1650, -1, 0, 5000, 0);
                    }
                } else if (JsonConfig.SET_NEW_PASSWORD.equals(ex.str)) {
                    if (msg.arg1 < 0) {
                        mView.setNewPasswordResult(false);
                        XMPromptDlg.onShowErrorDlg(mView.getActivity(),ErrorCodeManager.getSDKStrErrorByNO(msg.arg1),null,false);
                    } else {
                        String loginName = FunSDK.DevGetLocalUserName(mDevId);
                        DeviceManager.getInstance().setLocalDevUserPwd(mDevId,TextUtils.isEmpty(loginName) ? "admin" : loginName, mView.getNewPassword());
                        mView.setNewPasswordResult(true);
                    }
                } else if (JsonConfig.GET_VERIFY_QR_CODE.equals(ex.str)) {
                    LoadingDialog.getInstance(mView.getActivity()).dismiss();
                    if (msg.arg1 < 0) {
                        XMPromptDlg.onShowErrorDlg(mView.getActivity(),ErrorCodeManager.getSDKStrErrorByNO(msg.arg1),null,false);
                    } else {
                        if (ex.pData != null && ex.pData.length > 0) {
                            try {
                                JSONObject jsonObject = new JSONObject(G.ToString(ex.pData));
                                JSONObject object = jsonObject.optJSONObject(JsonConfig.GET_VERIFY_QR_CODE);
                                JSONObject obj = object.optJSONObject("VerifyQRCode");
                                String code = obj.optString("Text");
                                if (!TextUtils.isEmpty(code)) {
                                    mView.requestCheckCode(code);
                                } else {
                                    //数据异常
                                    Toast.makeText(mView.getActivity(), R.string.data_exception,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(mView.getActivity(), R.string.data_exception,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
        return 0;
    }

}
