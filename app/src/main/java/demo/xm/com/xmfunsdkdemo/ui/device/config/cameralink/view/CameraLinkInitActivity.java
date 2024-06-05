package demo.xm.com.xmfunsdkdemo.ui.device.config.cameralink.view;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;

import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.cameralink.listener.CameraLinkInitContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.cameralink.presenter.CameraLinkInitPresenter;

public class CameraLinkInitActivity extends DemoBaseActivity<CameraLinkInitPresenter> implements CameraLinkInitContract.ICameraLinkInitView {
    @Override
    public CameraLinkInitPresenter getPresenter() {
        return new CameraLinkInitPresenter(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void showWaitDlgShow(boolean show) {
        if(show){
            showWaitDialog();
        }else {
            hideWaitDialog();
        }
    }

    @Override
    public void initFailed() {
        finish();
    }
}
