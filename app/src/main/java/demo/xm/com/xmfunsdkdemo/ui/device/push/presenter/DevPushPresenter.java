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
    public boolean isPushOpen() {//The server gets whether to open
        manager.isAlarmLinked(getDevId());
        return false;
    }

    @Override
    public boolean openPush() {//Local open
        manager.linkAlarm(getDevId(), 0);
        return true;
    }

    @Override
    public boolean closePush() {//Local close
        manager.unLinkAlarm(getDevId(), 0);
        return true;
    }

    @Override
    public void onPushInit(int i, int i1) {

    }

    @Override
    public void onPushLink(int i, String s, int i1, int i2) {

    }

    @Override
    public void onPushUnLink(int i, String s, int i1, int i2) {

    }

    @Override
    public void onIsPushLinkedFromServer(int i, String s, boolean isLinked) {//The callback to isPushOpen()
        if (iDevPushView != null) {
            iDevPushView.onPushStateResult(isLinked);
        }
    }

    @Override
    public void onAlarmInfo(int i, String s, Message message, MsgContent msgContent) {

    }

    @Override
    public void onLinkDisconnect(int i, String s) {

    }

    @Override
    public void onWeChatState(String s, int i, int i1) {

    }

    @Override
    public void onThirdPushState(String s, int i, int i1, int i2) {

    }

    @Override
    public void onAllUnLinkResult(boolean isSuccess) {

    }

    @Override
    public void onFunSDKResult(Message message, MsgContent msgContent) {

    }
}
