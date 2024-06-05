package demo.xm.com.xmfunsdkdemo.ui.device.config;

import android.os.Bundle;

import com.xm.activity.base.XMBaseActivity;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.widget.XTitleBar;

/**
 * @author hws
 * @name XMFunSDKDemo_Android
 * @class name：demo.xm.com.xmfunsdkdemo.ui.device.config
 * @class describe
 * @time 2018-10-27 15:15
 */
public abstract class BaseConfigActivity<T extends XMBasePresenter> extends XMBaseActivity<T> {
    protected XTitleBar titleBar;
    @Override
    public T getPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (titleBar != null) {
            titleBar.setBottomTip(getClass().getName());
        }
    }
}
