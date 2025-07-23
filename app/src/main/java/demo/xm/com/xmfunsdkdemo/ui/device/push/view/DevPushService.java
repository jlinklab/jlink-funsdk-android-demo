package demo.xm.com.xmfunsdkdemo.ui.device.push.view;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.manager.push.XMPushManager.MSG_TYPE_FAMILY_CALL;
import static com.manager.push.XMPushManager.MSG_TYPE_VIDEO_TALK;
import static com.manager.push.XMPushManager.PUSH_TYPE_XM;
import static com.manager.push.XMPushManager.TYPE_DOOR_BELL;
import static com.manager.push.XMPushManager.TYPE_FORCE_DISMANTLE;
import static com.manager.push.XMPushManager.TYPE_LOCAL_ALARM;
import static com.manager.push.XMPushManager.TYPE_REMOTE_CALL_ALARM;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.basic.G;
import com.google.gson.Gson;
import com.lib.Mps.SMCInitInfoV2;
import com.lib.MsgContent;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.alarm.AlarmGroup;
import com.lib.sdk.bean.alarm.AlarmInfo;
import com.lib.sdk.bean.push.NotifyPushInfo;
import com.manager.account.XMAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.device.alarm.DevAlarmInfoManager;
import com.manager.push.XMPushManager;
import com.utils.XUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication;
import demo.xm.com.xmfunsdkdemo.ui.entity.AlarmTranslationIconBean;

/**
 * @author hws
 * @class
 * @time 2020/8/13 18:53
 */
public class DevPushService extends Service implements DevAlarmInfoManager.OnAlarmInfoListener {

    public static final String TAG = "DevPushService";
    private XMPushManager xmPushManager;
    private DevAlarmInfoManager devAlarmInfoManager;

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
            initAlarmInfo();
            Toast.makeText(getApplicationContext(), R.string.start_push_service, Toast.LENGTH_LONG).show();
        } else {
            stopSelf();
            System.out.println(getString(R.string.start_push_service_error_tips));
        }
    }

    /**
     * 初始化推送
     */
    private void initPush() {
        xmPushManager = new XMPushManager(xmPushLinkResult);
        String pushToken = XUtils.getPushToken(this);
        if (!StringUtils.isStringNULL(pushToken)) {
            SMCInitInfoV2 info = new SMCInitInfoV2();
            G.SetValue(info.st_0_user, XMAccountManager.getInstance().getAccountName());
            G.SetValue(info.st_1_password, XMAccountManager.getInstance().getPassword());
            G.SetValue(info.st_3_token, pushToken);
            xmPushManager.initFunSDKPush(this, info, PUSH_TYPE_XM);
        }
    }

    /**
     * 获取报警消息翻译内容和图标
     * Get alarm message translation and icon
     * 该功能暂时不对外，只是报警翻译内容，不影响整体功能
     * This feature is temporarily unavailable to the public. It only handles alarm message translations and does not affect the overall functionality.
     */
    private void initAlarmInfo() {
        // 使用Map直接传参
        Map<String, Object> dataMap = new HashMap<>();

        // 添加参数到Map
        dataMap.put("msg", "get_translate_icon");
        dataMap.put("timeout", 8000);


        ArrayList ltList = new ArrayList();
        ltList.add("ZH");
        dataMap.put("app", XUtils.getPackageName(this));
        dataMap.put("st", "AND");
        dataMap.put("appvs", XUtils.getVersion(this));
        dataMap.put("lt", ltList);
        devAlarmInfoManager = new DevAlarmInfoManager(this);
        devAlarmInfoManager.getAlarmMsgTranslationAndIcon(new Gson().toJson(dataMap), 0);
    }

    /**
     * 用URL初始化报警消息订阅
     * Initialize alarm message subscription with URL
     * 使用Url初始化报警推送,主要修改: appType设置”Third:url”
     * Initialize alarm push using URL, main modification: set appType to "Third:url"
     * <p>
     * 该功能只针对有私有的推送服务才需要参考
     * This feature is only applicable to those with a private push service.
     */
    private void initPushByUrl() {
        xmPushManager = new XMPushManager(xmPushLinkResult);
        String pushToken = XUtils.getPushToken(this);
        if (!StringUtils.isStringNULL(pushToken)) {
            SMCInitInfoV2 info = new SMCInitInfoV2();
            G.SetValue(info.st_0_user, XMAccountManager.getInstance().getAccountName());
            G.SetValue(info.st_1_password, XMAccountManager.getInstance().getPassword());
            G.SetValue(info.st_3_token, pushToken);
            G.SetValue(info.st_6_szAppType, "Third:http://xxxxx xxxx");
            //(例：Third:http(s)://xmey.net:80/xxx/xxx 或 Third:ip:80 中间用“:”做分隔符)
            xmPushManager.initFunSDKPush(this, info, PUSH_TYPE_XM);
        }
    }

    private XMPushManager.OnXMPushLinkListener xmPushLinkResult = new XMPushManager.OnXMPushLinkListener() {
        /**
         * 订阅初始化回调
         * Subscription initialization callback
         * @param pushType 推送类型
         * @param errorId 错误码
         */
        @Override
        public void onPushInit(int pushType, int errorId) {
            if (errorId >= 0) {
                //Push initialization successful
                System.out.println("推送初始化成功:" + pushType);
                //订阅客服推送
                xmPushManager.linkCustomerService();
            } else {
                // Push initialization failed
                System.out.println("推送初始化失败:" + errorId);
            }
        }

        /**
         * 订阅结果回调
         * Subscription result callback
         * @param pushType 推送类型
         * @param devId 设备序列号 Device serial number
         * @param seq
         * @param errorId 错误码
         */
        @Override
        public void onPushLink(int pushType, String devId, int seq, int errorId) {
            if (errorId >= 0) {
                //"Push subscription successful"
                System.out.println("推送订阅成功:" + devId);
            } else {
                //"Push subscription failed"
                System.out.println("推送订阅失败:" + devId + ":" + errorId);
            }
        }

        /**
         * 取消订阅结果回调
         * "Unsubscription result callback"
         * @param pushType 推送类型
         * @param devId 设备序列号 Device serial number
         * @param seq
         * @param errorId 错误码
         */
        @Override
        public void onPushUnLink(int pushType, String devId, int seq, int errorId) {
            if (errorId >= 0) {
                //"Unsubscription successful"
                System.out.println("取消订阅成功:" + devId);
            } else {
                //"Unsubscription failed"
                System.out.println("取消订阅失败:" + devId + ":" + errorId);
            }
        }

        /**
         * 报警推送回调
         * Alarm push callback
         * @param pushType 推送类型 Push type
         * @param devId 设备序列号 Device serial number
         * @param message
         * @param msgContent
         */
        @Override
        public void onAlarmInfo(int pushType, String devId, Message message, MsgContent msgContent) {
            String pushMsg = G.ToString(msgContent.pData);
            System.out.println("接收到报警消息:" + pushMsg);
            parseAlarmInfo(devId, pushMsg);
        }


        /**
         * 客服推送订阅结果回调
         *
         * @param pushType 推送类型
         * @param isSuccess 是否成功
         * @param errorId 错误码
         */
        @Override
        public void onLinkCustomerServiceResult(int pushType, boolean isSuccess, int errorId) {
            Log.d(TAG, "onLinkCustomerServiceResult: " + pushType + " " + isSuccess + " " + errorId);
        }




        /**
         * 客服推送订阅结果回调
         *
         * @param pushType 推送类型
         * @param isSuccess 是否成功
         * @param errorId 错误码
         */

        @Override
        public void onUnLinkCustomerServiceResult(int pushType, boolean isSuccess, int errorId) {
            Log.d(TAG, "onUnLinkCustomerServiceResult: " + pushType + " " + isSuccess + " " + errorId);
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
        if(XUtils.isSn(devId)){
            AlarmInfo alarmInfo = new AlarmInfo();
            alarmInfo.onParse(pushMsg);
            Toast.makeText(getApplicationContext(), getString(R.string.received_alarm_message) +
                    alarmInfo.getDevName() + ":" +
                    XMPushManager.getAlarmName(getApplicationContext(), alarmInfo.getEvent()) + ":" +
                    alarmInfo.getStartTime(), Toast.LENGTH_LONG).show();
            //如果是来电消息才需要弹出来电页面
            // Show incoming call page only if it's a call message
            if (isCallAlarm(alarmInfo.getEvent(), alarmInfo.getMsgType(), devId)) {
                Intent intent = new Intent(DevPushService.this, DevIncomingCallActivity.class);
                intent.putExtra("devId", devId);
                intent.putExtra("alarmTime", alarmInfo.getStartTime());
                intent.putExtra("alarm_msg_type", alarmInfo.getMsgType());
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else {
            JSONObject jsonObject = JSON.parseObject(pushMsg);
            if (jsonObject != null && jsonObject.containsKey("CustomInfo")) {
                String customInfoJson = jsonObject.getString("CustomInfo");
                NotifyPushInfo notifyPushInfo = JSON.parseObject(customInfoJson, NotifyPushInfo.class);
                if (!TextUtils.isEmpty(notifyPushInfo.getTitle()) && notifyPushInfo.getTitle().contains("[KF]")) {
                    //客服消息
                    Toast.makeText(getApplicationContext(), getString(R.string.received_customer_service_message) +
                            notifyPushInfo.getContent(), Toast.LENGTH_LONG).show();
                }
            }
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

    @Override
    public void onSearchResult(List<AlarmGroup> list) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess, Message msg, MsgContent ex, List<AlarmInfo> deleteAlarmInfos) {

    }

    @Override
    public void onAlarmMsgTranslationAndIconResult(boolean isSuccess, Message msg, MsgContent ex, String resultJson) {
        System.out.println("resultJson:" + resultJson);
        AlarmTranslationIconBean alarmTranslationIconBean = new AlarmTranslationIconBean();
        ((SDKDemoApplication) getApplication()).setAlarmTranslationIconBean(alarmTranslationIconBean);
        JSONObject jsonObject = JSON.parseObject(resultJson);
        if (jsonObject != null && jsonObject.containsKey("ifs")) {
            JSONObject ifsObj = jsonObject.getJSONObject("ifs");
            if (ifsObj != null) {
                for (Map.Entry entry : ifsObj.entrySet()) {
                    HashMap<String, AlarmTranslationIconBean.AlarmLanIconInfo> alarmLanIconInfoHashMap = new HashMap<>();
                    JSONArray list = (JSONArray) entry.getValue();
                    String lanKey = (String) entry.getKey();
                    for (int i = 0; i < list.size(); ++i) {

                        AlarmTranslationIconBean.AlarmLanIconInfo alarmLanIconInfo = new AlarmTranslationIconBean.AlarmLanIconInfo();
                        JSONObject obj = (JSONObject) list.get(i);
                        String eventKey = obj.getString("et");
                        alarmLanIconInfo.setEt(obj.getString("et"));
                        alarmLanIconInfo.setTl(obj.getString("tl"));
                        alarmLanIconInfo.setUrl(obj.getString("url"));
                        alarmLanIconInfoHashMap.put(eventKey, alarmLanIconInfo);
                    }

                    alarmTranslationIconBean.getLanguageInfo().put(lanKey, alarmLanIconInfoHashMap);
                }
            }
        }
    }
}
