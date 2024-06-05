package demo.xm.com.xmfunsdkdemo.ui.device.push.view;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.Message;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.basic.G;
import com.lib.Mps.SMCInitInfo;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.alarm.AlarmInfo;
import com.manager.account.XMAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.image.BaseImageManager;
import com.manager.image.CloudImageManager;
import com.manager.push.XMPushManager;
import com.utils.XUtils;
import com.xm.ui.dialog.XMPromptDlg;

import static com.manager.push.XMPushManager.MSG_TYPE_FAMILY_CALL;
import static com.manager.push.XMPushManager.MSG_TYPE_VIDEO_TALK;
import static com.manager.push.XMPushManager.PUSH_TYPE_XM;
import static com.manager.push.XMPushManager.TYPE_DOOR_BELL;
import static com.manager.push.XMPushManager.TYPE_FORCE_DISMANTLE;
import static com.manager.push.XMPushManager.TYPE_LOCAL_ALARM;
import static com.manager.push.XMPushManager.TYPE_REMOTE_CALL_ALARM;

import static demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication.PATH_PHOTO;
import static demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication.PATH_PHOTO_TEMP;

import demo.xm.com.xmfunsdkdemo.R;

/**
 * @author hws
 * @class
 * @time 2020/8/13 18:53
 */
public class DevPushService extends Service {
    private XMPushManager xmPushManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (DevDataCenter.getInstance().isLoginByAccount()) {
            initPush();
            Toast.makeText(getApplicationContext(), R.string.start_push_service, Toast.LENGTH_LONG).show();
        } else {
            stopSelf();
            System.out.println(getString(R.string.start_push_service_error_tips));
        }
    }

    private void initPush() {
        xmPushManager = new XMPushManager(xmPushLinkResult);
        String pushToken = XUtils.getPushToken(this);
        if (!StringUtils.isStringNULL(pushToken)) {
            SMCInitInfo info = new SMCInitInfo();
            G.SetValue(info.st_0_user, XMAccountManager.getInstance().getAccountName());
            G.SetValue(info.st_1_password, XMAccountManager.getInstance().getPassword());
            G.SetValue(info.st_2_token, pushToken);
            xmPushManager.initFunSDKPush(this, info, PUSH_TYPE_XM);
        }
    }

    /**
     * 用URL初始化报警消息订阅
     * 使用Url初始化报警推送,主要修改: appType设置”Third:url”
     */
    private void initPushByUrl() {
        xmPushManager = new XMPushManager(xmPushLinkResult);
        String pushToken = XUtils.getPushToken(this);
        if (!StringUtils.isStringNULL(pushToken)) {
            SMCInitInfo info = new SMCInitInfo();
            G.SetValue(info.st_0_user, XMAccountManager.getInstance().getAccountName());
            G.SetValue(info.st_1_password, XMAccountManager.getInstance().getPassword());
            G.SetValue(info.st_2_token, pushToken);
            G.SetValue(info.st_5_appType, "Third:http://xxxxx xxxx");
            //(例：Third:http(s)://xmey.net:80/xxx/xxx 或 Third:ip:80 中间用“:”做分隔符)
            xmPushManager.initFunSDKPush(this, info, PUSH_TYPE_XM);
        }
    }

    private XMPushManager.OnXMPushLinkListener xmPushLinkResult = new XMPushManager.OnXMPushLinkListener() {
        @Override
        public void onPushInit(int pushType, int errorId) {
            if (errorId >= 0) {
                System.out.println("推送初始化成功:" + pushType);
            } else {
                System.out.println("推送初始化失败:" + errorId);
            }
        }

        @Override
        public void onPushLink(int pushType, String devId, int seq, int errorId) {
            if (errorId >= 0) {
                System.out.println("推送订阅成功:" + devId);
            } else {
                System.out.println("推送订阅失败:" + devId + ":" + errorId);
            }
        }

        @Override
        public void onPushUnLink(int pushType, String devId, int seq, int errorId) {
            if (errorId >= 0) {
                System.out.println("取消订阅成功:" + devId);
            } else {
                System.out.println("取消订阅失败:" + devId + ":" + errorId);
            }
        }

        @Override
        public void onIsPushLinkedFromServer(int pushType, String devId, boolean isLinked) {

        }

        @Override
        public void onAlarmInfo(int pushType, String devId, Message message, MsgContent msgContent) {
            String pushMsg = G.ToString(msgContent.pData);
            System.out.println("接收到报警消息:" + pushMsg);
            parseAlarmInfo(devId, pushMsg);
        }

        @Override
        public void onLinkDisconnect(int pushType, String devId) {

        }

        @Override
        public void onWeChatState(String devId, int state, int errorId) {

        }

        @Override
        public void onThirdPushState(String info, int pushType, int state, int errorId) {

        }

        @Override
        public void onAllUnLinkResult(boolean isSuccess) {

        }

        @Override
        public void onFunSDKResult(Message message, MsgContent msgContent) {

        }
    };

    /**
     * 解析报警消息
     * Parse alarm message
     *
     * @param devId   设备序列号 Device serial number
     * @param pushMsg 推送消息 Push message
     */
    private void parseAlarmInfo(String devId, String pushMsg) {
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.onParse(pushMsg);
        Toast.makeText(getApplicationContext(), getString(R.string.received_alarm_message) +
                XMPushManager.getAlarmName(getApplicationContext(), alarmInfo.getEvent()) + ":" +
                alarmInfo.getStartTime(), Toast.LENGTH_LONG).show();
        //如果是来电消息才需要弹出来电页面
        // Show incoming call page only if it's a call message
        if (isCallAlarm(alarmInfo.getEvent(), alarmInfo.getMsgType(), devId)) {
            Intent intent = new Intent(DevPushService.this, DevIncomingCallActivity.class);
            intent.putExtra("devId", devId);
            intent.putExtra("alarmTime", alarmInfo.getStartTime());
            intent.putExtra("alarm_msg_type",alarmInfo.getMsgType());
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /**
     * 是否是来电通知
     * Whether it is a call notification
     *
     * @param alarmEvent 报警事件 Alarm event
     * @param msgType    报警类型 Alarm type
     * @param devId      设备序列号 Device serial number
     * @return
     */
    private boolean isCallAlarm(String alarmEvent, String msgType, String devId) {
        switch (alarmEvent) {
            case TYPE_LOCAL_ALARM:
                return DevDataCenter.getInstance().isLowPowerDev(devId);
            case TYPE_DOOR_BELL:
            case TYPE_FORCE_DISMANTLE:
            case TYPE_REMOTE_CALL_ALARM: //监控设备呼叫
                return true;
            case MSG_TYPE_VIDEO_TALK: // 带屏摇头机来电呼叫
                if (StringUtils.contrastIgnoreCase(msgType, MSG_TYPE_FAMILY_CALL)) {
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }
}
