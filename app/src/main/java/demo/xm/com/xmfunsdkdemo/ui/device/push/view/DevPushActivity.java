package demo.xm.com.xmfunsdkdemo.ui.device.push.view;

import android.os.Bundle;
import android.view.View;

import com.lib.SDKCONST;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.push.listener.DevPushContract;
import demo.xm.com.xmfunsdkdemo.ui.device.push.presenter.DevPushPresenter;
import demo.xm.com.xmfunsdkdemo.ui.user.modify.view.UserModifyPwdActivity;

/**
 * @author hws
 * @class 设备推送设置
 * @time 2020/7/24 16:44
 */
public class DevPushActivity extends DemoBaseActivity<DevPushPresenter> implements DevPushContract.IDevPushView {
    private ListSelectItem lsiPushSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_push);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.push_set));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        titleBar.setBottomTip(DevPushActivity.class.getName());
        lsiPushSwitch = findViewById(R.id.lsi_push_switch);
        lsiPushSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lsiPushSwitch.setRightImage(lsiPushSwitch.getRightValue() == SDKCONST.Switch.Open
                        ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
                if (lsiPushSwitch.getRightValue() == SDKCONST.Switch.Open) {
                    presenter.openPush();
                }else {
                    presenter.closePush();
                }
            }
        });
    }

    private void initData() {
        presenter.checkPushLinkState();
    }

    @Override
    public DevPushPresenter getPresenter() {
        return new DevPushPresenter(this);
    }

    @Override
    public void onPushStateResult(boolean isPushOpen) {
        lsiPushSwitch.setRightImage(isPushOpen ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
    }
}
