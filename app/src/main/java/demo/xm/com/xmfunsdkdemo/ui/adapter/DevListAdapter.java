package demo.xm.com.xmfunsdkdemo.ui.adapter;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.constant.DeviceConstant;
import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.share.OtherShareDevUserBean;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.config.mqtt.DevStateNotifyMqttManager;
import com.manager.device.config.shadow.DevShadowManager;
import com.manager.device.config.shadow.OnDevShadowManagerListener;
import com.manager.device.config.shadow.ShadowConfigEnum;
import com.manager.device.config.websocket.DevStateNotifyManager;
import com.utils.LogUtils;
import com.xm.ui.widget.ListSelectItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;

import static com.constant.SDKLogConstant.APP_WEB_SOCKET;
import static com.manager.account.share.ShareInfo.SHARE_ACCEPT;
import static com.manager.account.share.ShareInfo.SHARE_NOT_YET_ACCEPT;
import static com.manager.account.share.ShareInfo.SHARE_REJECT;

/**
 * Created by hws on 2018-10-10.
 */

public class DevListAdapter extends RecyclerView.Adapter<DevListAdapter.ViewHolder> implements
        DevStateNotifyMqttManager.OnDevStateNotifyListener {
    private RecyclerView recyclerView;
    private List<HashMap<String, Object>> data;
    private HashMap<String, Bundle> isSupportInterDevLink = new HashMap<>();//缓存是否支持设备之间联动
    private DevStateNotifyMqttManager devStateNotifyManager;
    public static final String[] DEV_STATE = new String[]{
            FunSDK.TS("Offline"),
            FunSDK.TS("Online"),
            FunSDK.TS("Sleep"),
            FunSDK.TS("WakeUp"),
            FunSDK.TS("Wake"),
            FunSDK.TS("Not awakened"),
            FunSDK.TS("Ready to sleep")
    };

    public DevListAdapter(Application application, RecyclerView recyclerView, ArrayList<HashMap<String, Object>> data, OnItemDevClickListener ls) {
        this.recyclerView = recyclerView;
        this.data = data;
        this.onItemDevClickListener = ls;
        devStateNotifyManager = DevStateNotifyMqttManager.getInstance(application);
        devStateNotifyManager.addNotifyListener(this);
        devStateNotifyManager.connectMqtt();//连接MQTT
        DevShadowManager.getInstance().addDevShadowListener(onDevShadowManagerListener);
    }

    private OnDevShadowManagerListener onDevShadowManagerListener = new OnDevShadowManagerListener() {
        @Override
        public void onDevShadowConfigResult(String devId, String configData, int errorId) {
            if (errorId >= 0 && configData != null) {
                //解析影子服务器返回的数据，如果有NetWork.LANLinkageBindInfo返回，表示支持联动
                HashMap hashMap = JSON.parseObject(configData, HashMap.class);
                JSONObject jsonObject = (JSONObject) hashMap.get("data");
                if (jsonObject != null && jsonObject.containsKey(ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName())) {
                    jsonObject = jsonObject.getJSONObject(ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
                    Integer otherDevBindAttr = 0;
                    String linkSn = null;
                    String linkPin = null;
                    boolean isBind = false;
                    if (jsonObject != null) {
                        if (jsonObject.containsKey("BindAttr")) {
                            otherDevBindAttr = jsonObject.getInteger("BindAttr");
                        }

                        if (jsonObject.containsKey("BindList")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("BindList");
                            if (jsonArray != null && jsonArray.size() > 0) {
                                jsonObject = jsonArray.getJSONObject(0);
                                if (jsonObject != null && jsonObject.containsKey("SN")) {
                                    linkSn = jsonObject.getString("SN");
                                    linkPin = jsonObject.getString("PIN");
                                    if (!StringUtils.isStringNULL(linkSn) && !StringUtils.contrast(linkSn, "NoBound")) {
                                        isBind = true;
                                    }
                                }
                            }
                        }

                        Bundle bundle = new Bundle();
                        bundle.putInt("bindAttr", otherDevBindAttr);
                        bundle.putString("bindSn", linkSn);
                        bundle.putString("pin", linkPin);
                        bundle.putBoolean("isBind", isBind);
                        isSupportInterDevLink.put(devId, bundle);
                        if (recyclerView != null) {
                            Button btnInterDevLinkage = recyclerView.findViewWithTag("inter_dev_linkage:" + devId);
                            if (btnInterDevLinkage != null) {
                                btnInterDevLinkage.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }

        }

        @Override
        public void onSetDevShadowConfigResult(String devId, boolean isSuccess, int errorId) {
        }

        @Override
        public void onLinkShadow(String s, int i) {

        }

        @Override
        public void onUnLinkShadow(String s, int i) {

        }

        @Override
        public void onLinkShadowDisconnect() {

        }
    };

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_dev_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String devId = (String) data.get(position).get("devId");
        String devName = (String) data.get(position).get("devName");
        holder.lsiDevInfo.setTip(devId);
        holder.lsiDevInfo.setRightText(DEV_STATE[(int) data.get(position).get("devState")]);

        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(devId);
        String strShareState = "";
        if (xmDevInfo != null) {
            if (xmDevInfo.isShareDev()) {
                Context context = holder.lsiDevInfo.getContext();
                strShareState = context.getString(R.string.from_share_dev);
                OtherShareDevUserBean otherShareDevUserBean = xmDevInfo.getOtherShareDevUserBean();
                if (otherShareDevUserBean != null) {
                    int iShareState = otherShareDevUserBean.getShareState();
                    if (iShareState == SHARE_ACCEPT) {
                        strShareState = strShareState + "[" + context.getString(R.string.share_accept) + "]";
                    } else if (iShareState == SHARE_NOT_YET_ACCEPT) {
                        strShareState = strShareState + "[" + context.getString(R.string.share_not_yet_accept) + "]";
                    } else if (iShareState == SHARE_REJECT) {
                        strShareState = strShareState + "[" + context.getString(R.string.share_reject) + "]";
                    }
                }
            }
        }
        holder.lsiDevInfo.setTitle(devName + " " + strShareState);
        holder.lsiDevInfo.setTag(devId + ":state");
        holder.lsiDevInfo.setTag(devId);
        holder.btnInterDevLinkage.setTag("inter_dev_linkage:" + devId);
        //如果是来自分享的设备，需要隐藏分享管理
        //the device from shared,need hide the share management
        if (!DevDataCenter.getInstance().isLoginByAccount() || (xmDevInfo != null && xmDevInfo.isShareDev())) {
            holder.btnTurnToShareManage.setVisibility(View.GONE);
        } else {
            holder.btnTurnToShareManage.setVisibility(View.VISIBLE);
        }

        //如果有缓存的话，直接判断，没有的话需要从影子服务器获取
        if (isSupportInterDevLink.containsKey(devId)) {
            holder.btnInterDevLinkage.setVisibility(View.VISIBLE);
        } else {
            holder.btnInterDevLinkage.setVisibility(View.GONE);
            DevShadowManager.getInstance().getDevCfgsFromShadowService(devId, ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public void setData(ArrayList<HashMap<String, Object>> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void release() {
        if (devStateNotifyManager != null) {
            devStateNotifyManager.release();
        }

        DevShadowManager.getInstance().removeDevShadowListener(onDevShadowManagerListener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ListSelectItem lsiDevInfo;
        Button btnTurnToAlarmMsg;//跳转到报警消息  jump to alarm message
        Button btnTurnToPushSet;//跳转到推送设置   jump to push Settings
        Button btnTurnToCloudService;//跳转到云服务 jump to the cloud service
        Button btnModifyDevName;//修改设备名称  change the device name
        Button btnTurnToShareManage;//跳转到分享管理  go to Share Management
        Button btnLocalDevUserPwd;//本地设备登录名和密码 local device login name and password
        Button btnUnlock;//开锁
        Button btnPingTest;//Ping
        Button btnSdPlayback;//SD卡录像回放 SD Playback
        Button btnInterDevLinkage;//门锁和其他摇头机之间的联动，该功能通过影子服务来判断是否支持 "The linkage between the door lock and other pan-tilt cameras is determined by the shadow service to ascertain its support."

        public ViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemDevClickListener != null) {
                        int index = getAdapterPosition();
                        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(index).get("devId"));
                        onItemDevClickListener.onItemClick(index, xmDevInfo);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onItemDevClickListener != null) {
                        int index = getAdapterPosition();
                        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(index).get("devId"));
                        return onItemDevClickListener.onLongItemClick(index, xmDevInfo);
                    }
                    return false;
                }
            });

            lsiDevInfo = itemView.findViewById(R.id.lsi_dev_info);
            btnTurnToAlarmMsg = itemView.findViewById(R.id.btn_turn_to_alarm_msg);
            btnTurnToAlarmMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDevClickListener != null) {
                        int index = getAdapterPosition();
                        onItemDevClickListener.onTurnToAlarmMsg(index);
                    }
                }
            });

            btnTurnToAlarmMsg.setVisibility(DevDataCenter.getInstance().isLoginByAccount() ? View.VISIBLE : View.GONE);

            btnTurnToPushSet = itemView.findViewById(R.id.btn_turn_to_push_set);
            btnTurnToPushSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onTurnToPushSet(getAdapterPosition());
                    }
                }
            });

            btnTurnToPushSet.setVisibility(DevDataCenter.getInstance().isLoginByAccount() ? View.VISIBLE : View.GONE);

            btnTurnToCloudService = itemView.findViewById(R.id.btn_turn_to_cloud_service);
            btnTurnToCloudService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onTurnToCloudService(getAdapterPosition());
                    }
                }
            });

            btnTurnToCloudService.setVisibility(DevDataCenter.getInstance().isLoginByAccount() ? View.VISIBLE : View.GONE);
            btnModifyDevName = itemView.findViewById(R.id.btn_modify_dev_name);
            btnModifyDevName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onModifyDevName(getAdapterPosition(), xmDevInfo);
                    }
                }
            });

            btnTurnToShareManage = itemView.findViewById(R.id.btn_share_dev);
            btnTurnToShareManage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onShareDevManage(getAdapterPosition(), xmDevInfo);
                    }
                }
            });
            btnTurnToShareManage.setVisibility(DevDataCenter.getInstance().isLoginByAccount() ? View.VISIBLE : View.GONE);

            btnLocalDevUserPwd = itemView.findViewById(R.id.btn_edit_user_password);
            btnLocalDevUserPwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onTurnToEditLocalDevUserPwd(getAdapterPosition(), xmDevInfo);
                    }
                }
            });

            //唤醒设备（包括主控)

            btnUnlock = itemView.findViewById(R.id.btn_un_lock);
            btnUnlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onWakeUpDev(getAdapterPosition(), xmDevInfo);
                    }
                }
            });

            //SD卡录像回放
            btnSdPlayback = itemView.findViewById(R.id.btn_sd_playback);
            btnSdPlayback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onTurnToSdPlayback(getAdapterPosition(), xmDevInfo);
                    }
                }
            });

            //设备之间联动
            btnInterDevLinkage = itemView.findViewById(R.id.btn_inter_device_linkage);
            btnInterDevLinkage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    Bundle bundle = isSupportInterDevLink.get(xmDevInfo.getDevId());
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onTurnToInterDevLinkage(getAdapterPosition(), xmDevInfo, bundle);
                    }
                }
            });

            //Ping

            btnPingTest = itemView.findViewById(R.id.btn_ping);
            btnPingTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo((String) data.get(getAdapterPosition()).get("devId"));
                    if (onItemDevClickListener != null) {
                        onItemDevClickListener.onPingTest(getAdapterPosition(), xmDevInfo);
                    }
                }
            });
        }
    }

    /**
     * WeBSocket 已经连接
     */
    @Override
    public void onConnected() {
        //订阅MQTT 传入设备列表
        devStateNotifyManager.subscribeDevIdsByMqtt(DevDataCenter.getInstance().getDevList());
    }

    /**
     * WeBSocket 已经断开
     */
    @Override
    public void onDisconnected() {

    }

    /**
     * WeBSocket 已经重连成功
     */
    @Override
    public void onReconnected() {
        //订阅MQTT 传入设备列表
        devStateNotifyManager.subscribeDevIdsByMqtt(DevDataCenter.getInstance().getDevList());
    }

    /**
     * WeBSocket 正在连接中
     */
    @Override
    public void onReconnecting() {

    }

    /**
     * 设备错误状态上报
     */
    @Override
    public void onDevErrorState(String devId, String errorId, Object errorMsg, JSONObject originalData) {

    }

    /**
     * 设备功能状态上报
     */
    @Override
    public void onDevFunState(String devId, String funId, Object value, JSONObject originalData) {

    }

    /**
     * 设备休眠状态上报
     */
    @Override
    public void onDevSleepState(String devId, Object value, JSONObject originalData) {
        LogUtils.debugInfo(APP_WEB_SOCKET, originalData.toJSONString());
        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(devId);
        if (xmDevInfo == null) {
            return;
        }

        if (DeviceConstant.DevStateEnum.PREPARE_SLEEP.getDevState().equals(value)) {//准备休眠中
            xmDevInfo.setDevState(XMDevInfo.PREPARE_SLEEP);
        } else if (DeviceConstant.DevStateEnum.LIGHT_SLEEP.getDevState().equals(value)) {//浅度休眠
            xmDevInfo.setDevState(XMDevInfo.SLEEP);
        } else if (DeviceConstant.DevStateEnum.DEEP_SLEEP.getDevState().equals(value)) {//深度休眠
            xmDevInfo.setDevState(XMDevInfo.SLEEP_UNWAKE);
        } else if (DeviceConstant.DevStateEnum.WAKE_UP.getDevState().equals(value)) {//已唤醒
            xmDevInfo.setDevState(XMDevInfo.WAKE_UP);
        }

        if (recyclerView != null) {
            ListSelectItem lsiDevInfo = recyclerView.findViewWithTag(devId + ":state");
            if (lsiDevInfo != null) {
                lsiDevInfo.setRightText(DEV_STATE[xmDevInfo.getDevState()]);
            }
        }
    }

    private OnItemDevClickListener onItemDevClickListener;

    public interface OnItemDevClickListener {
        void onItemClick(int position, XMDevInfo xmDevInfo);

        boolean onLongItemClick(int position, XMDevInfo xmDevInfo);

        void onTurnToAlarmMsg(int position);

        void onTurnToCloudService(int position);

        void onTurnToPushSet(int position);

        void onModifyDevName(int position, XMDevInfo xmDevInfo);

        /**
         * 分享管理
         * share management
         *
         * @param position
         * @param xmDevInfo
         */
        void onShareDevManage(int position, XMDevInfo xmDevInfo);

        /**
         * 编辑本地设备登录名和密码
         * edit the login name and password of the local device
         *
         * @param position
         * @param xmDevInfo
         */
        void onTurnToEditLocalDevUserPwd(int position, XMDevInfo xmDevInfo);

        /**
         * 唤醒设备(包括主控)
         *
         * @param position
         * @param xmDevInfo
         */
        void onWakeUpDev(int position, XMDevInfo xmDevInfo);

        /**
         * 跳转到SD卡录像回放页面
         *
         * @param position
         * @param xmDevInfo
         */
        void onTurnToSdPlayback(int position, XMDevInfo xmDevInfo);

        /**
         * Ping
         *
         * @param position
         * @param xmDevInfo
         */
        void onPingTest(int position, XMDevInfo xmDevInfo);

        void onTurnToInterDevLinkage(int position, XMDevInfo xmDevInfo, Bundle bundle);
    }
}
