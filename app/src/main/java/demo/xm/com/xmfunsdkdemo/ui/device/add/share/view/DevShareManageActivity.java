package demo.xm.com.xmfunsdkdemo.ui.device.add.share.view;

import android.os.Bundle;
import android.view.View;

import com.xm.activity.base.XMBaseActivity;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import org.jetbrains.annotations.Nullable;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.share.listener.DevShareConnectContract;

/**
 * Device sharing management
 */
public class DevShareManageActivity extends DemoBaseActivity {
    /**
     * share device button
     */
    private ListSelectItem lsiShareDev;
    /**
     * List of accounts shared by the device
     */
    private ListSelectItem lsiGetDevShareAccountList;

    @Override
    public XMBasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_share_manage);

        lsiShareDev = findViewById(R.id.lsi_share_dev);
        lsiShareDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnToActivity(ShareDevToOtherAccountActivity.class);
            }
        });

        lsiGetDevShareAccountList = findViewById(R.id.lsi_get_dev_share_user_list);
        lsiGetDevShareAccountList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnToActivity(DevShareAccountListActivity.class);
            }
        });

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_share_manage));
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(getClass().getName());
    }
}
