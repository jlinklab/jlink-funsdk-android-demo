package demo.xm.com.xmfunsdkdemo.ui.device.config.systemfunc.view;

import android.os.Bundle;
import android.view.Window;
import android.widget.ExpandableListView;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.systemfunc.listener.DevSysFunctionSetContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.systemfunc.presenter.DevSysFunctionSetPresenter;
import io.reactivex.annotations.Nullable;

/**
 * 系统功能列表界面,显示仅Demo使用的列表菜单
 * Created by jiangping on 2018-10-23.
 */
public class DevSysFunctionSetActivity extends BaseConfigActivity<DevSysFunctionSetPresenter> implements DevSysFunctionSetContract.IDevSysFunctionSetView {
    private ExpandableListView viewExList = null;

    @Override
    public DevSysFunctionSetPresenter getPresenter() {
        return new DevSysFunctionSetPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_system_function);

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_setup_system_function));
        titleBar.setLeftClick(this);
        viewExList = findViewById(R.id.listSystemFunction);
    }

    @Override
    public void onUpdateView() {

    }
}
