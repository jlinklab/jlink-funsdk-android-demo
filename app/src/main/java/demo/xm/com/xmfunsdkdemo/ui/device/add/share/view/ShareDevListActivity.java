package demo.xm.com.xmfunsdkdemo.ui.device.add.share.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.lib.sdk.bean.share.MyShareUserInfoBean;
import com.lib.sdk.bean.share.OtherShareDevUserBean;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.adapter.ItemSetAdapter;
import demo.xm.com.xmfunsdkdemo.ui.device.add.share.listener.DevShareAccountListContract;
import demo.xm.com.xmfunsdkdemo.ui.device.add.share.listener.ShareDevListContract;
import demo.xm.com.xmfunsdkdemo.ui.device.add.share.presenter.DevShareAccountListPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.add.share.presenter.ShareDevListPresenter;

import static com.manager.account.share.ShareInfo.SHARE_ACCEPT;
import static com.manager.account.share.ShareInfo.SHARE_NOT_YET_ACCEPT;
import static com.manager.account.share.ShareInfo.SHARE_REJECT;

/**
 * Shared device list
 */
public class ShareDevListActivity extends DemoBaseActivity<ShareDevListPresenter> implements ItemSetAdapter.OnItemSetClickListener, ShareDevListContract.IShareDevListView {
    private ItemSetAdapter itemSetAdapter;
    private RecyclerView rvShareDevList;
    private List<OtherShareDevUserBean> otherShareDevUserBeans;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_dev_list);
        initView();
        initData();
    }

    private void initView() {
        rvShareDevList = findViewById(R.id.rv_share_dev_list);
        rvShareDevList.setLayoutManager(new LinearLayoutManager(this));

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.share_dev_list));
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(getClass().getName());
    }

    private void initData() {
        itemSetAdapter = new ItemSetAdapter(this);
        rvShareDevList.setAdapter(itemSetAdapter);
        presenter.searchShareDevList();
    }

    @Override
    public ShareDevListPresenter getPresenter() {
        return new ShareDevListPresenter(this);
    }

    @Override
    public void onItem(int position) {
        if (otherShareDevUserBeans != null && position < otherShareDevUserBeans.size()) {
            OtherShareDevUserBean otherShareDevUserBean = otherShareDevUserBeans.get(position);
            if (otherShareDevUserBean != null) {
                if (otherShareDevUserBean.getShareState() == SHARE_NOT_YET_ACCEPT) {
                    XMPromptDlg.onShow(this, getString(R.string.share_operation),
                            getString(R.string.reject_share), getString(R.string.accept_share), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    presenter.rejectShare(otherShareDevUserBean);
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    presenter.acceptShare(otherShareDevUserBean);
                                }
                            });
                } else {
                    XMPromptDlg.onShow(this, getString(R.string.is_sure_cancel_share),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    presenter.rejectShare(otherShareDevUserBean);
                                }
                            }, null);
                }
            }
        }

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onSearchShareDevListResult(List<OtherShareDevUserBean> otherShareDevUserBeans) {
        if (otherShareDevUserBeans != null && !otherShareDevUserBeans.isEmpty()) {
            this.otherShareDevUserBeans = otherShareDevUserBeans;
            List<String> data = new ArrayList<>();
            for (int i = 0; i < otherShareDevUserBeans.size(); ++i) {
                OtherShareDevUserBean otherShareDevUserBean = otherShareDevUserBeans.get(i);
                if (otherShareDevUserBean != null) {
                    int shareState = otherShareDevUserBean.getShareState();
                    String strShareState = "";
                    if (shareState == SHARE_ACCEPT) {
                        strShareState = getString(R.string.share_accept);
                    } else if (shareState == SHARE_NOT_YET_ACCEPT) {
                        strShareState = getString(R.string.share_not_yet_accept);
                    } else if (shareState == SHARE_REJECT) {
                        strShareState = getString(R.string.share_reject);
                    }

                    data.add(otherShareDevUserBean.getDevName() + getString(R.string.come_from) + otherShareDevUserBean.getAccount() + "[" + strShareState + "]");
                }
            }

            itemSetAdapter.setData(data);
        } else {
            itemSetAdapter.setData(null);
            ToastUtils.showLong(R.string.not_found);
        }
    }

    @Override
    public void onRejectShareResult(boolean isSuccess) {
        ToastUtils.showLong(isSuccess ? getString(R.string.reject_share_s) : getString(R.string.reject_share_f));
        if (isSuccess) {
            presenter.searchShareDevList();
        }
    }

    @Override
    public void onAcceptShareResult(boolean isSuccess) {
        ToastUtils.showLong(isSuccess ? getString(R.string.accept_share_s) : getString(R.string.accept_share_f));
        if (isSuccess) {
            presenter.searchShareDevList();
        }
    }
}
