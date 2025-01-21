package demo.xm.com.xmfunsdkdemo.ui.device.push.presenter;

import android.os.Message;

import com.lib.MsgContent;
import com.manager.push.XMPushManager;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.device.push.listener.DevPushContract;

/**
 * @author hws
 * @class 设备推送
 * 报警消息推送开关是保存在手机本地的，开关状态和订阅状态不是一一对应，当APP登出账号的时候，需要取消订阅
 * @time 2020/7/24 16:45
 */
public class DevPushPresenter extends XMBasePresenter<XMPushManager> implements
        DevPushContract.IDevPushPresenter, XMPushManager.OnXMPushLinkListener {
    private DevPushContract.IDevPushView iDevPushView;

    public DevPushPresenter(DevPushContract.IDevPushView iDevPushView) {
        this.iDevPushView = iDevPushView;
    }

    @Override
    protected XMPushManager getManager() {
        return new XMPushManager(this);
    }

    @Override
    public void checkPushLinkState() {//The server gets whether to open
        manager.isAlarmLinked(getDevId());
    }

    @Override
    public void openPush() {//Local open
        manager.linkAlarm(getDevId(), 0);
    }

    @Override
    public void closePush() {//Local close
        manager.unLinkAlarm(getDevId(), 0);
    }

    /**
     * 从服务端获取订阅状态
     * Get the subscription status from the server
     *
     * @param pushType 推送类型 Push type
     * @param devId    设备序列号 Device serial number
     * @param isLinked 是否订阅 Whether subscribed
     */
    @Override
    public void onIsPushLinkedFromServer(int pushType, String devId, boolean isLinked) {//The callback to isPushOpen()
        if (iDevPushView != null) {
            iDevPushView.onPushStateResult(isLinked);
        }
    }
}
