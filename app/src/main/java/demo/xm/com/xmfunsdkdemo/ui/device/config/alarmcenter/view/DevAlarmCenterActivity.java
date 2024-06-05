package demo.xm.com.xmfunsdkdemo.ui.device.config.alarmcenter.view;

import android.os.Bundle;
import android.view.Window;

import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.alarmcenter.listener.DevAlarmCenterContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.alarmcenter.presenter.DevAlarmCenterPresenter;
import io.reactivex.annotations.Nullable;

/**
 * 报警中心界面,包括协议类型,启用,服务器地址,端口,警报上报,日志上报等.
 * Created by jiangping on 2018-10-23.
 */
public class DevAlarmCenterActivity extends BaseConfigActivity<DevAlarmCenterPresenter> implements DevAlarmCenterContract.IDevAlarmCenterView {
    @Override
    public DevAlarmCenterPresenter getPresenter() {
        return new DevAlarmCenterPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_setup_alarm_center);

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_setup_alarm_center));
        titleBar.setRightBtnResource(R.mipmap.icon_save_normal,R.mipmap.icon_save_pressed);
        titleBar.setLeftClick(this);
        titleBar.setRightIvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                //tryTosaveConfig();
            }
        });
    }

    @Override
    public void onUpdateView() {

    }
}
