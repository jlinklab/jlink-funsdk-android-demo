package demo.xm.com.xmfunsdkdemo.ui.device.add.list.listener;

import android.content.Context;

import com.manager.account.BaseAccountManager;

import java.util.HashMap;
import java.util.List;

/**
 * 分享的设备界面,显示相关的列表菜单
 * Created by jiangping on 2018-10-23.
 */
public class DevListConnectContract {
    public interface IDevListConnectView {
        void onUpdateDevListView();

        /**
         * 更新设备状态结果返回
         * Update Device Status Result Return
         *
         * @param isSuccess
         */
        void onUpdateDevStateResult(boolean isSuccess);

        /**
         * 修改设备名称的结果
         * Result of modifying device name
         *
         * @param isSuccess
         */
        void onModifyDevNameFromServerResult(boolean isSuccess);

        /**
         * 删除设备的结果
         * Result of deleting device
         *
         * @param isSuccess
         */
        void onDeleteDevResult(boolean isSuccess);

        /**
         * 接受分享设备的结果
         * Accept the results of the shared device
         *
         * @param isSuccess
         */
        void onAcceptDevResult(boolean isSuccess);

        void onGetChannelListResult(boolean isSuccess, int resultId);

        Context getContext();
    }

    public interface IDevListConnectPresenter extends BaseAccountManager.OnAccountManagerListener {
        List<HashMap<String, Object>> getDevList();

        String getDevId(int position);

        void updateDevState();

        boolean isOnline(int position);

        /**
         * 修改服务器端的设备名称/Modify the device name on the server side
         *
         * @param position
         * @param devName  设备名称/Device name
         */
        void modifyDevNameFromServer(int position, String devName);

        void deleteDev(int position);

        /**
         * 删除所有设备
         */
        void deleteAllDevs();

        void getChannelList();

        /**
         * 编辑本地设备登录用户名和密码
         *
         * @param position
         * @param devId    设备序列号或者IP:Port
         * @param devUser  设备登录名
         * @param devPwd   设备密码
         */
        void editLocalDevUserPwd(int position, String devId, String devUser, String devPwd);

        /**
         * 清除资源
         */
        void clear();

        /**
         * 唤醒设备（包括主控）
         */
        void wakeUpDev(int position, String devId);
    }
}
