package demo.xm.com.xmfunsdkdemo.ui.device.add.share.listener;

import android.content.Context;

import com.lib.sdk.bean.share.MyShareUserInfoBean;
import com.lib.sdk.bean.share.SearchUserInfoBean;

import java.util.List;

/**
 * 分享的设备界面,显示相关的列表菜单
 * The shared device interface, displays the relevant list menu
 * Created by jiangping on 2018-10-23.
 */
public class DevShareAccountListContract {
    public interface IDevShareAccountListView {
        Context getContext();

        void onSearchMyShareUsersResult(List<MyShareUserInfoBean> myShareUserInfoBean);

        void onCancelShareResult(boolean isSuccess);
    }

    public interface IDevShareAccountListPresenter {
        /**
         * 搜索设备分享的所有账号信息
         * Search for all account information shared by the device
         */
        void searchUsersByShareThisDev();

        /**
         * 取消分享
         *
         * @param shareId 分享Id
         */
        void cancelShare(String shareId);
    }
}
