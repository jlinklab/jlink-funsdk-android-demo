package demo.xm.com.xmfunsdkdemo.ui.device.add.share.view;

import static com.lib.EFUN_ATTR.LOGIN_USER_ID;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.lib.FunSDK;
import com.lib.sdk.bean.share.SearchUserInfoBean;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.share.listener.DevShareConnectContract;
import demo.xm.com.xmfunsdkdemo.ui.device.add.share.presenter.DevShareConnectPresenter;
import io.reactivex.annotations.Nullable;

/**
 * Shared device interface, display related list menu
 */
public class ShareDevToOtherAccountActivity extends DemoBaseActivity<DevShareConnectPresenter> implements DevShareConnectContract.IDevShareConnectView {
    private EditText etShareAccount;
    private ListSelectItem lsiShareAccountInfo;
    private ImageView ivQrCode;//分享的二维码布局

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dev_share_connect);

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.guide_module_title_device_connect2));
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(getClass().getName());

        etShareAccount = findViewById(R.id.et_search_bar_input);
        lsiShareAccountInfo = findViewById(R.id.lsi_share_account);

        lsiShareAccountInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.shareDevToOther(lsiShareAccountInfo.getTip());
            }
        });

        ivQrCode = findViewById(R.id.iv_qr_code);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Bitmap bitmap = presenter.getShareDevQrCode(this);
        ivQrCode.setImageBitmap(bitmap);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onSearchShareAccountResult(boolean isSuccess, SearchUserInfoBean searchUserInfoBean) {
        if (isSuccess) {
            if (searchUserInfoBean != null) {
                lsiShareAccountInfo.setTitle(searchUserInfoBean.getAccount());
                lsiShareAccountInfo.setTip(searchUserInfoBean.getId());
            }
        } else {
            ToastUtils.showLong(R.string.not_found);
        }
    }

    @Override
    public void onShareDevResult(boolean isSuccess) {
        if (isSuccess) {
            turnToActivity(DevShareAccountListActivity.class);
        }

        ToastUtils.showLong(isSuccess ? getString(R.string.share_s) : getString(R.string.share_f));
    }

    @Override
    public void onUpdateView() {

    }

    @Override
    public DevShareConnectPresenter getPresenter() {
        return new DevShareConnectPresenter(this);
    }

    /**
     * 分享的账号查询
     * Shared account query
     *
     * @param view
     */
    public void onSearchAccount(View view) {
        presenter.searchShareAccount(etShareAccount.getText().toString().trim());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.release();
        }
    }
}
