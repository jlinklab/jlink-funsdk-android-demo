package demo.xm.com.xmfunsdkdemo.ui.device.push.listener;

/**
 * @author hws
 * @class 设备推送
 * @time 2020/7/24 16:47
 */
public interface DevPushContract {
    interface IDevPushView {
        void onPushStateResult(boolean isPushOpen);
    }

    interface IDevPushPresenter {
        void checkPushLinkState();
        void openPush();
        void closePush();
    }
}
