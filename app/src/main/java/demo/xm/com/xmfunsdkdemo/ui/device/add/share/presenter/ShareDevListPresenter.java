package demo.xm.com.xmfunsdkdemo.ui.device.add.share.presenter;

import com.alibaba.fastjson.JSON;
import com.lib.sdk.bean.share.MyShareUserInfoBean;
import com.lib.sdk.bean.share.OtherShareDevUserBean;
import com.manager.account.share.ShareInfo;
import com.manager.account.share.ShareManager;
import com.xm.activity.base.XMBasePresenter;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.ui.device.add.share.listener.DevShareAccountListContract;
import demo.xm.com.xmfunsdkdemo.ui.device.add.share.listener.ShareDevListContract;

import static com.manager.account.share.ShareManager.OPERATING.ACCPET_SHARE;
import static com.manager.account.share.ShareManager.OPERATING.CANCEL_SHARE;
import static com.manager.account.share.ShareManager.OPERATING.GET_MY_SHARE_DEV_USER_LIST;
import static com.manager.account.share.ShareManager.OPERATING.GET_OTHER_SHARE_DEV_USER_LIST;
import static com.manager.account.share.ShareManager.OPERATING.REJECT_SHARE;

public class ShareDevListPresenter extends XMBasePresenter<ShareManager> implements ShareDevListContract.IShareDevListPresenter, ShareManager.OnShareManagerListener {
    private ShareDevListContract.IShareDevListView iShareDevListView;
    private List<OtherShareDevUserBean> otherShareDevUserBeans;

    public ShareDevListPresenter(ShareDevListContract.IShareDevListView iShareDevListView) {
        this.iShareDevListView = iShareDevListView;
        manager = ShareManager.getInstance(iShareDevListView.getContext());
        manager.init();
        manager.addShareManagerListener(this);
    }

    @Override
    protected ShareManager getManager() {
        return manager;
    }

    @Override
    public void rejectShare(OtherShareDevUserBean otherShareDevUserBean) {
        manager.rejectShare(otherShareDevUserBean);
    }

    @Override
    public void acceptShare(OtherShareDevUserBean otherShareDevUserBean) {
        manager.acceptShare(otherShareDevUserBean);
    }

    @Override
    public void onShareResult(ShareInfo shareInfo) {
        if (shareInfo == null) {
            return;
        }

        if (shareInfo.getOperating() == GET_OTHER_SHARE_DEV_USER_LIST) {
            if (shareInfo.getResultJson() != null) {
                otherShareDevUserBeans = JSON.parseArray(shareInfo.getResultJson(), OtherShareDevUserBean.class);
                if (iShareDevListView != null) {
                    iShareDevListView.onSearchShareDevListResult(otherShareDevUserBeans);
                    return;
                }
            }

            if (iShareDevListView != null) {
                iShareDevListView.onSearchShareDevListResult(null);
                return;
            }
        } else if (shareInfo.getOperating() == REJECT_SHARE) {
            if (iShareDevListView != null) {
                iShareDevListView.onRejectShareResult(shareInfo.isSuccess());
            }
        } else if (shareInfo.getOperating() == ACCPET_SHARE) {
            if (iShareDevListView != null) {
                iShareDevListView.onAcceptShareResult(shareInfo.isSuccess());
            }
        }
    }

    @Override
    public void searchShareDevList() {
        manager.getOtherShareDevList();
    }
}
