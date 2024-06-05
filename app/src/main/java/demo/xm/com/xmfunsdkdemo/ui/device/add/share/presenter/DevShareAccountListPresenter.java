package demo.xm.com.xmfunsdkdemo.ui.device.add.share.presenter;

import com.alibaba.fastjson.JSON;
import com.lib.sdk.bean.share.MyShareUserInfoBean;
import com.manager.account.share.ShareInfo;
import com.manager.account.share.ShareManager;
import com.xm.activity.base.XMBasePresenter;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.ui.device.add.share.listener.DevShareAccountListContract;

import static com.manager.account.share.ShareManager.OPERATING.CANCEL_SHARE;
import static com.manager.account.share.ShareManager.OPERATING.GET_MY_SHARE_DEV_USER_LIST;

public class DevShareAccountListPresenter extends XMBasePresenter<ShareManager> implements DevShareAccountListContract.IDevShareAccountListPresenter, ShareManager.OnShareManagerListener {
    private DevShareAccountListContract.IDevShareAccountListView iDevShareAccountListView;
    private List<MyShareUserInfoBean> myShareUserInfoBeans;

    public DevShareAccountListPresenter(DevShareAccountListContract.IDevShareAccountListView iDevShareAccountListView) {
        this.iDevShareAccountListView = iDevShareAccountListView;
        manager = ShareManager.getInstance(iDevShareAccountListView.getContext());
        manager.init();
        manager.addShareManagerListener(this);
    }

    @Override
    protected ShareManager getManager() {
        return manager;
    }

    @Override
    public void searchUsersByShareThisDev() {
        manager.getMyShareDevUserList(getDevId());
    }

    @Override
    public void cancelShare(String shareId) {
        manager.cancelShare(shareId);
    }

    @Override
    public void onShareResult(ShareInfo shareInfo) {
        if (shareInfo == null) {
            return;
        }

        if (shareInfo.getOperating() == GET_MY_SHARE_DEV_USER_LIST) {
            if (shareInfo.getResultJson() != null) {
                myShareUserInfoBeans = JSON.parseArray(shareInfo.getResultJson(), MyShareUserInfoBean.class);
                if (iDevShareAccountListView != null) {
                    iDevShareAccountListView.onSearchMyShareUsersResult(myShareUserInfoBeans);
                    return;
                }
            }

            if (iDevShareAccountListView != null) {
                iDevShareAccountListView.onSearchMyShareUsersResult(null);
                return;
            }
        } else if (shareInfo.getOperating() == CANCEL_SHARE) {
            if (iDevShareAccountListView != null) {
                iDevShareAccountListView.onCancelShareResult(shareInfo.isSuccess());
            }
        }
    }
}
