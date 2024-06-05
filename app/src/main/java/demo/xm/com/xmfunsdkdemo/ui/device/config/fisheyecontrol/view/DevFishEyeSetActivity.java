package demo.xm.com.xmfunsdkdemo.ui.device.config.fisheyecontrol.view;

import android.os.Bundle;
import android.view.Window;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.fisheyecontrol.listener.DevFishEyeSetContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.fisheyecontrol.presenter.DevFishEyeSetPresenter;
import io.reactivex.annotations.Nullable;

/**
 * 鱼眼灯泡控制,包括模式,类型及自动模式时间间隔
 * Created by jiangping on 2018-10-23.
 */
public class DevFishEyeSetActivity extends BaseConfigActivity<DevFishEyeSetPresenter> implements DevFishEyeSetContract.IDevFishEyeSetView {
    @Override
    public DevFishEyeSetPresenter getPresenter() {
        return new DevFishEyeSetPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_setup_camerafisheye);

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_setup_camerafisheye));
        titleBar.setLeftClick(this);
    }

    @Override
    public void onUpdateView() {

    }
}
