package demo.xm.com.xmfunsdkdemo.ui.device.add.share.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.lib.sdk.bean.share.MyShareUserInfoBean;
import com.xm.activity.base.XMBaseActivity;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.adapter.ItemSetAdapter;
import demo.xm.com.xmfunsdkdemo.ui.device.add.share.listener.DevShareAccountListContract;
import demo.xm.com.xmfunsdkdemo.ui.device.add.share.presenter.DevShareAccountListPresenter;

import static com.manager.account.share.ShareInfo.SHARE_ACCEPT;
import static com.manager.account.share.ShareInfo.SHARE_NOT_YET_ACCEPT;
import static com.manager.account.share.ShareInfo.SHARE_REJECT;

/**
 * List of accounts shared by the device
 */
public class DevShareAccountListActivity extends DemoBaseActivity<DevShareAccountListPresenter> implements ItemSetAdapter.OnItemSetClickListener, DevShareAccountListContract.IDevShareAccountListView {
    private ItemSetAdapter itemSetAdapter;
    private RecyclerView rvAccountList;
    private List<MyShareUserInfoBean> myShareUserInfoBeans;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_share_account_list);
        initView();
        initData();
    }

    private void initView() {
        rvAccountList = findViewById(R.id.rv_dev_share_account_list);
        rvAccountList.setLayoutManager(new LinearLayoutManager(this));

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.dev_share_account_list));
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(getClass().getName());
    }

    private void initData() {
        itemSetAdapter = new ItemSetAdapter(this);
        rvAccountList.setAdapter(itemSetAdapter);
        presenter.searchUsersByShareThisDev();
    }

    @Override
    public DevShareAccountListPresenter getPresenter() {
        return new DevShareAccountListPresenter(this);
    }

    @Override
    public void onItem(int position) {
        XMPromptDlg.onShow(this, getString(R.string.is_sure_cancel_share), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myShareUserInfoBeans != null && position < myShareUserInfoBeans.size()) {
                    MyShareUserInfoBean myShareUserInfoBean = myShareUserInfoBeans.get(position);
                    if (myShareUserInfoBean != null) {
                        presenter.cancelShare(myShareUserInfoBean.getShareId());
                    }
                }
            }
        }, null);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onSearchMyShareUsersResult(List<MyShareUserInfoBean> myShareUserInfoBeans) {
        if (myShareUserInfoBeans != null) {
            this.myShareUserInfoBeans = myShareUserInfoBeans;
            List<String> data = new ArrayList<>();
            for (int i = 0; i < myShareUserInfoBeans.size(); ++i) {
                MyShareUserInfoBean myShareUserInfoBean = myShareUserInfoBeans.get(i);
                if (myShareUserInfoBean != null) {
                    int shareState = myShareUserInfoBean.getShareState();
                    String strShareState = "";
                    if (shareState == SHARE_ACCEPT) {
                        strShareState = getString(R.string.share_accept);
                    } else if (shareState == SHARE_NOT_YET_ACCEPT) {
                        strShareState = getString(R.string.share_not_yet_accept);
                    } else if (shareState == SHARE_REJECT) {
                        strShareState = getString(R.string.share_reject);
                    }

                    data.add(myShareUserInfoBean.getAccount() + "[" + strShareState + "]");
                }
            }

            itemSetAdapter.setData(data);
        } else {
            itemSetAdapter.setData(null);
            ToastUtils.showLong(R.string.not_found);
        }
    }

    @Override
    public void onCancelShareResult(boolean isSuccess) {
        ToastUtils.showLong(isSuccess ? getString(R.string.cancel_share_s) : getString(R.string.cancel_share_f));
        if (isSuccess) {
            presenter.searchUsersByShareThisDev();
        }
    }
}
