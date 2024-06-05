package demo.xm.com.xmfunsdkdemo.ui.device.config.frontpanel.view;

import android.os.Bundle;
import android.view.Window;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.frontpanel.listener.DevFrontSetContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.frontpanel.presenter.DevFrontSetPresenter;
import io.reactivex.annotations.Nullable;

/**
 * 前部面板操作,包括上下左右,退出,菜单及ok按钮
 * Created by jiangping on 2018-10-23.
 */
public class DevFrontSetActivity extends BaseConfigActivity<DevFrontSetPresenter> implements DevFrontSetContract.IDevFrontSetView {
    @Override
    public DevFrontSetPresenter getPresenter() {
        return new DevFrontSetPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_setup_devfrontctr);

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_setup_frontctr));
        titleBar.setLeftClick(this);
    }

    @Override
    public void onUpdateView() {

    }
}
