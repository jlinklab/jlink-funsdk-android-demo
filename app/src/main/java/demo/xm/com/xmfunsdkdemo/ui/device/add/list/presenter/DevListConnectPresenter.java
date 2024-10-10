package demo.xm.com.xmfunsdkdemo.ui.device.add.list.presenter;

import android.content.Intent;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.basic.G;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.lib.EFUN_ERROR;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.share.OtherShareDevUserBean;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.lib.sdk.struct.SDK_ChannelNameConfigAll;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.account.share.ShareInfo;
import com.manager.account.share.ShareManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.utils.LogUtils;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.dialog.XMPromptDlg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.add.list.listener.DevListConnectContract;
import demo.xm.com.xmfunsdkdemo.ui.device.add.share.listener.ShareDevListContract;
import demo.xm.com.xmfunsdkdemo.ui.device.push.view.DevPushService;

import static com.manager.account.share.ShareInfo.SHARE_ACCEPT;
import static com.manager.account.share.ShareInfo.SHARE_NOT_YET_ACCEPT;
import static com.manager.account.share.ShareManager.OPERATING.ACCPET_SHARE;
import static com.manager.account.share.ShareManager.OPERATING.CANCEL_SHARE;
import static com.manager.account.share.ShareManager.OPERATING.GET_OTHER_SHARE_DEV_USER_LIST;
import static com.manager.account.share.ShareManager.OPERATING.REJECT_SHARE;
import static com.manager.db.Define.LOGIN_NONE;
import static com.manager.db.XMDevInfo.OFF_LINE;

import org.json.JSONObject;

/**
 *
 */
public class DevListConnectPresenter extends XMBasePresenter<AccountManager> implements DevListConnectContract.IDevListConnectPresenter, ShareDevListContract.IShareDevListPresenter, BaseAccountManager.OnDevStateListener, ShareManager.OnShareManagerListener {

    private DevListConnectContract.IDevListConnectView iDevListConnectView;
    private List<HashMap<String, Object>> devList;
    private ShareManager shareManager;
    private List<OtherShareDevUserBean> otherShareDevUserBeans;
    private String curCancelShareId;//当前准备取消或者拒绝的分享Id (Id of the share that you want to cancel or reject)
    private String curAcceptShareId;//当前准备接受的分享Id  (the share Id that you want to accept)

    public DevListConnectPresenter(DevListConnectContract.IDevListConnectView iDevListConnectView) {
        this.iDevListConnectView = iDevListConnectView;
        shareManager = ShareManager.getInstance(iDevListConnectView.getContext());
        shareManager.init();
        shareManager.addShareManagerListener(this);
    }

    @Override
    public AccountManager getManager() {
        return AccountManager.getInstance();
    }


    /**
     * 获取设备列表
     * get the device list
     *
     * @return
     */
    @Override
    public List<HashMap<String, Object>> getDevList() {
        devList = new ArrayList<>();
        for (String devId : DevDataCenter.getInstance().getDevList()) {
            XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(devId);
            if (xmDevInfo != null) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("devId", devId);
                map.put("devState", manager.getDevState(devId));//获取设备状态（在线/离线）
                map.put("devName", xmDevInfo.getDevName());
                devList.add(map);
            }
        }

        otherShareDevUserBeans = DevDataCenter.getInstance().getOtherShareDevUserBeanList();
        return devList;
    }

    /**
     * 获取某个设备序列号
     * get the serial number of a device
     *
     * @param position
     * @return
     */
    @Override
    public String getDevId(int position) {
        if (position < devList.size()) {
            return (String) devList.get(position).get("devId");
        } else {
            return null;
        }
    }

    /**
     * 更新所有设备状态
     * update the status of all devices
     */
    @Override
    public void updateDevState() {
        manager.updateAllDevStateFromServer(DevDataCenter.getInstance().getDevList(), this);//Update the status of the list
    }

    /**
     * 获取当前设备是否在线
     * obtain whether the current device is online
     *
     * @param position
     * @return
     */
    @Override
    public boolean isOnline(int position) {
        if (position < devList.size()) {
            return ((int) devList.get(position).get("devState") != OFF_LINE);
        } else {
            return false;
        }
    }

    /**
     * 修改服务器端的设备名
     * change the device name on the server side
     *
     * @param position
     * @param devName
     */
    @Override
    public void modifyDevNameFromServer(int position, String devName) {
        String devId = getDevId(position);
        setDevId(devId);
        manager.modifyDevName(devId, devName, this);
    }


    /**
     * 从服务器端将设备删除
     * delete a device from the server
     *
     * @param position
     */
    @Override
    public void deleteDev(int position) {
        String devId = getDevId(position);
        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(devId);
        if (xmDevInfo != null && xmDevInfo.isShareDev()) {
            OtherShareDevUserBean otherShareDevUserBean = xmDevInfo.getOtherShareDevUserBean();
            curCancelShareId = otherShareDevUserBean.getShareId();
            shareManager.rejectShare(otherShareDevUserBean);
        } else {
            //未使用AccountManager(包括XMAccountManager或LocalAccountManager)登录（包括账号登录和本地临时登录），只能将设备信息临时缓存，重启应用后无法查到设备信息。
            if (DevDataCenter.getInstance().getLoginType() == LOGIN_NONE) {
                DevDataCenter.getInstance().removeDev(xmDevInfo.getDevId());
                if (iDevListConnectView != null) {
                    iDevListConnectView.onDeleteDevResult(true);
                }
            } else {
                manager.deleteDev(devId, this);
            }
        }
    }

    /**
     * 删除所有设备
     */
    @Override
    public void deleteAllDevs() {
        List<String> devList = DevDataCenter.getInstance().getDevList();
        for (String devId : devList) {
            manager.deleteDev(devId, this);
        }
    }

    /**
     * 获取通道列表
     * get channel list
     */
    @Override
    public void getChannelList() {
        DeviceManager.getInstance().getChannelInfo(getDevId(), new DeviceManager.OnDevManagerListener<SDK_ChannelNameConfigAll>() {
            @Override
            public void onSuccess(String devId, int operationType, SDK_ChannelNameConfigAll channelInfos) {
                if (channelInfos != null) {
                    iDevListConnectView.onGetChannelListResult(true, channelInfos.nChnCount);
                } else {
                    iDevListConnectView.onGetChannelListResult(true, 0);
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                //如果设备支持DSS，可以通过该方法获取通道数
                /*If the device supports DSS, the number of channels can be obtained by this method*/
                int count = FunSDK.GetDevChannelCount(devId);
                if (count > 0) {
                    SDBDeviceInfo sdbDeviceInfo = DevDataCenter.getInstance().getDevInfo(devId).getSdbDevInfo();
                    if (sdbDeviceInfo != null) {
                        SDK_ChannelNameConfigAll channelNameConfigAll = new SDK_ChannelNameConfigAll();
                        channelNameConfigAll.nChnCount = count;
                        sdbDeviceInfo.setChannel(channelNameConfigAll);
                    }
                    iDevListConnectView.onGetChannelListResult(true, count);
                } else {
                    iDevListConnectView.onGetChannelListResult(false, errorId);
                }

            }
        });
    }

    @Override
    public void editLocalDevUserPwd(int position, String devId, String devUser, String devPwd) {
        DeviceManager.getInstance().setLocalDevUserPwd(devId, devUser, devPwd);
    }

    @Override
    public void clear() {
        shareManager.removeShareManagerListener(this);
        manager.removeDevStateListener(this);
    }

    @Override
    public void wakeUpDev(int position, String devId) {
        HashMap<String, Object> subMap = new HashMap<>();
        subMap.put("Cmd", "1");//0 ==>唤醒单片机 1==》唤醒单片机的同时唤醒主控 0 ==> Wake up the microcontroller; 1 ==> Wake up the microcontroller and simultaneously wake up the main controller
        HashMap<String, Object> rootMap = new HashMap<>();
        rootMap.put("ExtInfo", subMap);
        DeviceManager.getInstance().wakeUpAndSendCtrl(devId, new Gson().toJson(rootMap), new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                ToastUtils.showLong(iDevListConnectView.getContext().getString(R.string.libfunsdk_operation_success));
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                ToastUtils.showLong(iDevListConnectView.getContext().getString(R.string.libfunsdk_operation_failed));
            }
        });
    }

    @Override
    public void onSuccess(int msgId) {
        if (msgId == EUIMSG.SYS_CHANGEDEVINFO) {
            XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(getDevId());
            String devName = xmDevInfo.getDevName();
            Intent intent = new Intent(iDevListConnectView.getContext(), DevPushService.class);
            intent.putExtra("devId",getDevId());
            intent.putExtra("isUpdateDevName",true);
            intent.putExtra("devName",devName);
            iDevListConnectView.getContext().startService(intent);

            if (iDevListConnectView != null) {
                iDevListConnectView.onModifyDevNameFromServerResult(true);
            }
        } else if (msgId == EUIMSG.SYS_DELETE_DEV) {
            if (iDevListConnectView != null) {
                iDevListConnectView.onDeleteDevResult(true);
            }
        } else if (msgId == EUIMSG.SYS_GET_CURRENT_USER_DEV_LIST) {
            updateDevState();
        }
    }

    @Override
    public void onFailed(int msgId, int errorId) {
        if (msgId == EUIMSG.SYS_GET_DEV_STATE) {
            if (iDevListConnectView != null) {
                iDevListConnectView.onUpdateDevStateResult(false);
            }
        } else if (msgId == EUIMSG.SYS_CHANGEDEVINFO) {
            if (iDevListConnectView != null) {
                iDevListConnectView.onModifyDevNameFromServerResult(false);
            }
        } else if (msgId == EUIMSG.SYS_DELETE_DEV) {
            if (iDevListConnectView != null) {
                iDevListConnectView.onDeleteDevResult(false);
            }
        }
    }

    @Override
    public void onFunSDKResult(Message msg, MsgContent ex) {

    }

    /**
     * 单个设备状态获取回调
     * Callback for retrieving the status of a single device
     * @param devId
     */
    @Override
    public void onUpdateDevState(String devId) {//UpdateAllDevStateFromServer callback

        System.out.println("onUpdateDevState:" + devId +"[" + AccountManager.getInstance().getDevState(devId) + "]");
    }

    /**
     * 所有设备状态完成获取回调
     * Callback for completing the retrieval of status for all devices
     */
    @Override
    public void onUpdateCompleted() {      //UpdateAllDevStateFromServer callback
        LogUtils.debugInfo("onUpdateCompleted","onUpdateCompleted");
        if (iDevListConnectView != null) {
            iDevListConnectView.onUpdateDevStateResult(true);
        }
    }

    @Override
    public void searchShareDevList() {

    }

    @Override
    public void rejectShare(OtherShareDevUserBean otherShareDevUserBean) {
        if (otherShareDevUserBean != null) {
            curCancelShareId = otherShareDevUserBean.getShareId();
            shareManager.rejectShare(otherShareDevUserBean);
        }
    }

    @Override
    public void acceptShare(OtherShareDevUserBean otherShareDevUserBean) {
        if (otherShareDevUserBean != null) {
            curAcceptShareId = otherShareDevUserBean.getShareId();
            shareManager.acceptShare(otherShareDevUserBean);
        }
    }

    @Override
    public void onShareResult(ShareInfo shareInfo) {
        if (shareInfo == null) {
            return;
        }

        //获取他人分享的设备列表数据回调
        //Get device list data callback shared by others
        if (shareInfo.getOperating() == GET_OTHER_SHARE_DEV_USER_LIST) {
            if (shareInfo.getResultJson() != null) {
                otherShareDevUserBeans = JSON.parseArray(shareInfo.getResultJson(), OtherShareDevUserBean.class);
                dealWithShareDevList();
                List<String> devList = new ArrayList<>();
                for (OtherShareDevUserBean otherShareDevUserBean : otherShareDevUserBeans) {
                    devList.add(otherShareDevUserBean.getDevId());
                }

                manager.updateAllDevStateFromServer(devList, this);
            }
        } else if (shareInfo.getOperating() == CANCEL_SHARE || shareInfo.getOperating() == REJECT_SHARE) {
            //取消分享或者拒绝分享
            //Cancel sharing or refuse to share
            if (iDevListConnectView != null) {
                if (shareInfo.isSuccess()) {
                    dealWithCancelShareDevList(curCancelShareId);
                }

                iDevListConnectView.onDeleteDevResult(shareInfo.isSuccess());
            }
        } else if (shareInfo.getOperating() == ACCPET_SHARE) {
            //接受分享
            //Accept sharing
            if (iDevListConnectView != null) {
                if (shareInfo.isSuccess()) {
                    dealWithAcceptShareDevList(curAcceptShareId);
                }

                iDevListConnectView.onAcceptDevResult(shareInfo.isSuccess());
            }
        }
    }

    private void dealWithCancelShareDevList(String shareId) {
        if (otherShareDevUserBeans == null) {
            return;
        }

        for (int i = otherShareDevUserBeans.size() - 1; i >= 0; --i) {
            OtherShareDevUserBean otherShareDevUserBean = otherShareDevUserBeans.get(i);
            if (otherShareDevUserBean != null && StringUtils.contrast(shareId, otherShareDevUserBean.getShareId())) {
                DevDataCenter.getInstance().removeDev(otherShareDevUserBean.getDevId());
                otherShareDevUserBeans.remove(i);
                break;
            }
        }
    }

    /**
     * 处理接受分享返回的结果并同步本地分享的设备列表数据
     * Deal with the results returned by accepting sharing and synchronize the locally shared device list data
     *
     * @param shareId
     */
    private void dealWithAcceptShareDevList(String shareId) {
        if (otherShareDevUserBeans == null) {
            return;
        }

        for (int i = otherShareDevUserBeans.size() - 1; i >= 0; --i) {
            OtherShareDevUserBean otherShareDevUserBean = otherShareDevUserBeans.get(i);
            if (otherShareDevUserBean != null && StringUtils.contrast(shareId, otherShareDevUserBean.getShareId())) {
                otherShareDevUserBean.setShareState(SHARE_ACCEPT);
                XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(otherShareDevUserBean.getDevId());
                if (xmDevInfo != null) {
                    OtherShareDevUserBean oldShareInfo = xmDevInfo.getOtherShareDevUserBean();
                    if (oldShareInfo != null) {
                        oldShareInfo.setShareState(SHARE_ACCEPT);
                    }
                }
                break;
            }
        }
    }

    private void dealWithShareDevList() {
        if (otherShareDevUserBeans == null) {
            return;
        }

        for (OtherShareDevUserBean shareDevUserBean : otherShareDevUserBeans) {
            XMDevInfo xmDevInfo;
            if (!DevDataCenter.getInstance().isDevExist(shareDevUserBean.getDevId())) {
                xmDevInfo = new XMDevInfo();
                xmDevInfo.shareDevInfoToXMDevInfo(shareDevUserBean);
                DevDataCenter.getInstance().addDev(xmDevInfo);
            } else {
                xmDevInfo = DevDataCenter.getInstance().getDevInfo(shareDevUserBean.getDevId());
            }

            OtherShareDevUserBean oldShareInfo = xmDevInfo.getOtherShareDevUserBean();
            if (oldShareInfo != null) {
                oldShareInfo.setShareState(shareDevUserBean.getShareState());
            }

            HashMap<String, Object> map = new HashMap<>();
            map.put("devId", shareDevUserBean.getDevId());
            map.put("devState", xmDevInfo.getDevState());
            map.put("devName", shareDevUserBean.getDevName());
            devList.add(map);
        }
    }
}

