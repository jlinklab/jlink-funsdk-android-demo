package demo.xm.com.xmfunsdkdemo.ui.device.config.cameralink.listener;

import android.content.Context;

import org.json.JSONObject;

import java.util.HashMap;

public class CameraLinkSetContract {
    public interface ICameraLinkSetView {
        void getCameraLinkFailed(String ex,int i, String s2 ,int i2);

        void getCameraLinkSuccess(JSONObject jsonObject);

        void setSaveSuccess();
        void showWaitDlgShow(boolean show);
        void showWaitDlgShow(String str);

        void initFailed();

        Context getContext();
    }

    public interface ICameraLinkSetPresenter {

    }
}
