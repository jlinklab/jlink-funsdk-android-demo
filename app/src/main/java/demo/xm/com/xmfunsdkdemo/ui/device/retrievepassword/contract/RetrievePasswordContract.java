package demo.xm.com.xmfunsdkdemo.ui.device.retrievepassword.contract;

import android.app.Activity;
import android.content.Context;

import com.lib.IFunSDKResult;

import java.util.List;

public interface RetrievePasswordContract {
    interface IRetrievePasswordView{
        Activity getActivity();
        void initQuestions(List<String> questions);
        String getNewPassword();
        void getSafetyQuestionResult(boolean success);
        void setNewPasswordResult(boolean success);
        void requestCheckCode(String code);
    }

    interface IRetrievePasswordPresenter extends IFunSDKResult {
        List<String> getQuestions();
        void setLanguage();
        void requestVerifyCode();
        void checkQuestionsAnswer(String answer1, String answer2);
    }
}
