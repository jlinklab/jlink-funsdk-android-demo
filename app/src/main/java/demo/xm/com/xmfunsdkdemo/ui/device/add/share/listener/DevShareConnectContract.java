package demo.xm.com.xmfunsdkdemo.ui.device.add.share.listener;

import android.content.Context;
import android.graphics.Bitmap;

import com.lib.sdk.bean.share.SearchUserInfoBean;

/**
 * 分享的设备界面,显示相关的列表菜单
 * Created by jiangping on 2018-10-23.
 */
public class DevShareConnectContract {
    public interface IDevShareConnectView {
        Context getContext();

        void onSearchShareAccountResult(boolean isSuccess, SearchUserInfoBean searchUserInfoBean);

        void onShareDevResult(boolean isSuccess);

        void onUpdateView();
    }

    public interface IDevShareConnectPresenter {
        /**
         * 查询要分享的账号信息
         * Find the account information you want to share
         *
         * @param accountName 分享的账号
         */
        void searchShareAccount(String accountName);

        /**
         * 将设备分享给其他账号
         *
         * @param accountId
         */
        void shareDevToOther(String accountId);

        /**
         * 获取分享的二维码
         *
         * @return
         */
        Bitmap getShareDevQrCode(Context context);

        void release();
    }
}
