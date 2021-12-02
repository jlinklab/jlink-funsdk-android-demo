package demo.xm.com.xmfunsdkdemo.ui.device.add.share.listener;

import android.content.Context;

import com.lib.sdk.bean.share.MyShareUserInfoBean;
import com.lib.sdk.bean.share.OtherShareDevUserBean;

import java.util.List;

/**
 * 分享的设备列表
 * List of shared devices
 */
public class ShareDevListContract {
    public interface IShareDevListView {
        Context getContext();

        void onSearchShareDevListResult(List<OtherShareDevUserBean> otherShareDevUserBeans);

        void onRejectShareResult(boolean isSuccess);

        void onAcceptShareResult(boolean isSuccess);
    }

    public interface IShareDevListPresenter {
        /**
         * 查询当前账号分享的设备列表
         * Query the list of devices shared by the current account
         */
        void searchShareDevList();

        /**
         * 拒绝分享
         * Refuse to share
         * @param otherShareDevUserBean 分享信息
         */
        void rejectShare(OtherShareDevUserBean otherShareDevUserBean);

        /**
         * 接受分享
         * Accept sharing
         * @param otherShareDevUserBean
         */
        void acceptShare(OtherShareDevUserBean otherShareDevUserBean);
    }
}
