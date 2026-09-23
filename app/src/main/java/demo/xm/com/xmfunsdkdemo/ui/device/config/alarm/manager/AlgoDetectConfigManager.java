package demo.xm.com.xmfunsdkdemo.ui.device.config.alarm.manager;

import android.os.Message;
import android.util.Log;

import com.basic.G;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.StringUtils;
import demo.xm.com.xmfunsdkdemo.ui.device.config.alarm.model.AlgoType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多算法智能报警 - 通用算法配置管理器
 * <p>
 * 通过 DevCmdGeneral 1042/1040 获取/保存 Detect 配置。
 * PIR 使用 DevGetConfigByJson/DevSetConfigByJson（SDK默认方式）。
 * <p>
 * 配置结构：
 * - IPC 设备（channel==-1，设备返回字典）：dictCfg 直接存储设备返回的 JSONObject
 * - 多通道设备（设备返回数组）：arrayCfg[0] 为当前配置 JSONObject
 * - 顶层字段：Enable、Sensitivity、ShowTrack
 * - EventHandler 子字段：TimeSection、SnapEnable、RecordEnable、SnapShotMask、RecordMask、
 *   AlarmOutEnable、AlarmOutLatch、AlarmOutTimeSection、VoiceEnable、VoiceType、VoiceTipInterval、RecordLatch、
 *   VoiceDuration、VoiceTimeSection
 * <p>
 * 线程安全：所有 dictCfg / arrayCfg 读写操作使用 synchronized 保护。
 */
public class AlgoDetectConfigManager implements IFunSDKResult {

    private static final String TAG = "AlgoDetectCfgManager";

    private static final int TIMEOUT = 15000;

    // DevCmdGeneral 命令号：获取/保存配置（通道配置用 1042/1040）
    private static final int CMD_GET_CONFIG = 1042;
    private static final int CMD_SET_CONFIG = 1040;

    // DevGetConfigByJson/DevSetConfigByJson 缓冲区大小
    private static final int BUFFER_SIZE = 1024;

    // 序列号：用于回调中区分获取与保存
    private static final int SEQ_GET_CONFIG = 1360;
    private static final int SEQ_SET_CONFIG = 1361;

    // VoiceTipType 能力获取（参照 AovAlarmLinkActivity.getAbilityVoiceTipType()，使用 DevGetConfigByJson）
    private static final String CFG_VOICE_TIP_TYPE = "Ability.VoiceTipType";

    // 顶层字段名
    private static final String KEY_NAME = "Name";
    private static final String KEY_SESSION_ID = "SessionID";
    private static final String KEY_ENABLE = "Enable";
    private static final String KEY_SENSITIVITY = "Sensitivity";
    private static final String KEY_SHOW_TRACK = "ShowTrack";
    private static final String KEY_INTERVAL = "Interval";

    // EventHandler 子字段名
    private static final String KEY_EVENT_HANDLER = "EventHandler";
    private static final String KEY_SNAP_ENABLE = "SnapEnable";
    private static final String KEY_SNAP_SHOT_MASK = "SnapShotMask";
    private static final String KEY_RECORD_ENABLE = "RecordEnable";
    private static final String KEY_RECORD_MASK = "RecordMask";
    private static final String KEY_WARNING_LIGHT = "AlarmOutEnable";
    private static final String KEY_WARNING_LIGHT_DELAY = "AlarmOutLatch";
    private static final String KEY_VOICE_ENABLE = "VoiceEnable";
    private static final String KEY_VOICE_TYPE = "VoiceType";
    private static final String KEY_VOICE_TIP_INTERVAL = "VoiceTipInterval";
    private static final String KEY_RECORD_DURATION = "RecordLatch";
    private static final String KEY_VOICE_DURATION = "VoiceDuration";
    private static final String KEY_TIME_SECTION = "TimeSection";
    private static final String KEY_VOICE_TIME_SECTION = "VoiceTimeSection";
    private static final String KEY_ALARM_OUT_TIME_SECTION = "AlarmOutTimeSection";
    private static final String KEY_PED_RULE = "PedRule";

    /**
     * 请求配置回调接口
     */
    public interface OnConfigResultListener {
        /**
         * 请求配置结果回调
         *
         * @param algoType   算法类型
         * @param success    请求是否成功
         * @param configData 成功时返回配置JSON字符串，失败时为 null
         */
        void onConfigResult(AlgoType algoType, boolean success, String configData);

        /**
         * 保存配置结果回调
         *
         * @param algoType 算法类型
         * @param success  保存是否成功
         */
        void onConfigSaved(AlgoType algoType, boolean success);

        /**
         * 配置获取/设置失败时回调，携带原始 SDK 错误信息
         *
         * @param algoType 算法类型
         * @param what    SDK 消息 what（msg.what），早期失败时为 0
         * @param errCode SDK 错误码（msg.arg1），早期失败时为 -1
         * @param errStr  SDK 错误字符串（ex.str），可能为 null
         * @param isGet   true=获取失败，false=设置失败
         */
        default void onConfigFailed(AlgoType algoType, int what, int errCode, String errStr, boolean isGet) { }
    }

    // ==================== 基本属性 ====================

    /** 当前算法类型 */
    private AlgoType algoType = AlgoType.HumanDetect;

    /** 设备序列号 */
    private String devId;

    /** 通道号（IPC: -1, 多通道设备: >= 0） */
    private int channel = -1;

    /** FunSDK 用户句柄 */
    private int userId;

    // ==================== 配置存储 ====================

    /** IPC 设备返回的配置字典（channel==-1 且设备返回字典时使用） */
    private JSONObject dictCfg;

    /** 多通道设备返回的配置数组（arrayCfg[0] 为当前配置字典） */
    private JSONArray arrayCfg;

    /** 回调监听器 */
    private OnConfigResultListener listener;

    // ==================== VoiceTipType 映射 ====================

    /** VoiceType enum → VoiceText 映射（由 Ability.VoiceTipType 填充） */
    private Map<Integer, String> mVoiceTypeTextMap = null;
    /** VoiceTipType 原始 JSON（缓存铃声列表数据，供 getVoiceText 映射查询使用） */
    private String mVoiceTipTypeJson = null;
    /** VoiceTipType 是否已加载完成 */
    private boolean mVoiceTipTypeLoaded = false;
    /** VoiceTipType 加载完成回调 */
    private OnVoiceTipTypeLoadedListener mVoiceTipTypeListener;

    /**
     * VoiceTipType 加载完成回调接口
     */
    public interface OnVoiceTipTypeLoadedListener {
        /**
         * @param success 加载是否成功
         */
        void onVoiceTipTypeLoaded(boolean success);
    }

    // ==================== 生命周期 ====================

    public AlgoDetectConfigManager() {
        userId = FunSDK.GetId(userId, this);
    }

    /**
     * 释放资源，注销 FunSDK 回调
     */
    public void release() {
        FunSDK.UnRegUser(userId);
        listener = null;
        mVoiceTipTypeListener = null;
        mVoiceTypeTextMap = null;
        mVoiceTipTypeJson = null;
        mVoiceTipTypeLoaded = false;
    }

    // ==================== VoiceTipType 加载 ====================

    /**
     * 加载 Ability.VoiceTipType 配置，获取铃声枚举到名称的映射。
     * 参照 AovAlarmLinkActivity.getAbilityVoiceTipType() 和 AlgoVoiceSelectActivity.loadVoiceData()。
     * <p>
     * 如果已加载完成，直接回调成功。
     *
     * @param devId   设备序列号
     * @param channel 通道号（IPC 传 -1）
     * @param listener 加载完成回调（可为 null）
     */
    public void loadVoiceTipType(String devId, int channel, OnVoiceTipTypeLoadedListener listener) {
        Log.d(TAG, "loadVoiceTipType: mVoiceTipTypeLoaded=" + mVoiceTipTypeLoaded
                + ", mapNull=" + (mVoiceTypeTextMap == null)
                + ", mapEmpty=" + (mVoiceTypeTextMap == null || mVoiceTypeTextMap.isEmpty()));
        // 已加载且映射表有效时，直接回调成功（避免重复请求）
        if (mVoiceTipTypeLoaded && mVoiceTypeTextMap != null && !mVoiceTypeTextMap.isEmpty()) {
            Log.d(TAG, "loadVoiceTipType: using cached data, map size=" + mVoiceTypeTextMap.size());
            if (listener != null) {
                listener.onVoiceTipTypeLoaded(true);
            }
            return;
        }
        // 映射表无效或标志位过期时，强制重新加载
        mVoiceTipTypeLoaded = false;
        this.mVoiceTipTypeListener = listener;
        Log.d(TAG, "loadVoiceTipType: requesting from device, devId=" + devId + ", channel=" + channel);
        // 参照 AovAlarmLinkActivity.getAbilityVoiceTipType()，使用 DevGetConfigByJson
        FunSDK.DevGetConfigByJson(userId, devId, CFG_VOICE_TIP_TYPE,
                BUFFER_SIZE, channel, TIMEOUT, 0);
    }

    /**
     * 获取 VoiceType 对应的显示文本
     *
     * @param voiceType 铃声枚举值
     * @return 显示文本，如果映射未加载或未找到则返回 null
     */
    public String getVoiceText(int voiceType) {
        String result = null;
        if (mVoiceTypeTextMap != null) {
            result = mVoiceTypeTextMap.get(voiceType);
        }
        Log.d(TAG, "getVoiceText: voiceType=" + voiceType + ", result=" + result
                + ", mapSize=" + (mVoiceTypeTextMap != null ? mVoiceTypeTextMap.size() : -1)
                + ", mapNull=" + (mVoiceTypeTextMap == null));
        return result;
    }

    /**
     * 获取 VoiceTipType 原始 JSON 数据（缓存铃声列表）
     */
    public String getVoiceTipTypeJson() {
        return mVoiceTipTypeJson;
    }

    // ==================== Getter / Setter ====================

    public void setAlgoType(AlgoType algoType) {
        this.algoType = algoType;
    }

    public AlgoType getAlgoType() {
        return algoType;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getChannel() {
        return channel;
    }

    // ==================== 请求接口 ====================

    /**
     * 请求获取算法配置
     *
     * @param devId    设备序列号
     * @param channel  通道号（IPC 传 -1）
     * @param listener 请求结果回调
     */
    public void requestConfig(String devId, int channel, OnConfigResultListener listener) {
        this.devId = devId;
        this.channel = channel;
        this.listener = listener;

        String cfgName = algoType.getCfgName();
        if (StringUtils.isStringNULL(cfgName)) {
            FunSDK.Log(TAG + " cfgName is empty");
            if (listener != null) {
                listener.onConfigFailed(algoType, 0, -1, null, true);
                listener.onConfigResult(algoType, false, null);
            }
            return;
        }

        if (algoType.getGetCommand() != null) {
            // 分支A/B: 使用 DevCmdGeneral (1042)
            String szCmd = cfgName;
            if (algoType.isChannelConfig() && channel >= 0) {
                szCmd = "bypass@" + cfgName + ".[" + channel + "]";
            }
            JSONObject param = new JSONObject();
            try {
                param.put(KEY_NAME, szCmd);
                param.put(KEY_SESSION_ID, "0x0000000001");
            } catch (Exception e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onConfigFailed(algoType, 0, -1, null, true);
                    listener.onConfigResult(algoType, false, null);
                }
                return;
            }
            byte[] pInParam = param.toString().getBytes();
            FunSDK.DevCmdGeneral(userId, devId, algoType.getGetCommand(), szCmd, -1, TIMEOUT,
                    pInParam, 0, SEQ_GET_CONFIG);
        } else {
            // 分支C: PIR 使用 DevGetConfigByJson (SDK默认方式)
            FunSDK.DevGetConfigByJson(userId, devId, cfgName, BUFFER_SIZE, channel, TIMEOUT, 0);
        }
    }

    /**
     * 请求保存算法配置
     *
     * @param listener 保存结果回调
     */
    public void requestSaveConfig(OnConfigResultListener listener) {
        this.listener = listener;

        String cfgName = algoType.getCfgName();
        if (StringUtils.isStringNULL(cfgName)) {
            FunSDK.Log(TAG + " cfgName is empty");
            if (listener != null) {
                listener.onConfigFailed(algoType, 0, -1, null, false);
                listener.onConfigSaved(algoType, false);
            }
            return;
        }

        // 线程安全地获取配置副本
        Object cfgValue;
        synchronized (this) {
            if (dictCfg != null) {
                cfgValue = dictCfg;
            } else if (arrayCfg != null && arrayCfg.length() > 0) {
                cfgValue = arrayCfg;
            } else {
                cfgValue = null;
            }
        }

        if (cfgValue == null) {
            FunSDK.Log(TAG + " config is empty, cannot save");
            if (listener != null) {
                listener.onConfigFailed(algoType, 0, -1, null, false);
                listener.onConfigSaved(algoType, false);
            }
            return;
        }

        if (algoType.getSetCommand() != null) {
            // 分支A/B: 使用 DevCmdGeneral (1040)
            String szCmd = cfgName;
            if (algoType.isChannelConfig() && channel >= 0) {
                szCmd = "bypass@" + cfgName + ".[" + channel + "]";
            }
            JSONObject jsonDic = new JSONObject();
            try {
                jsonDic.put(KEY_NAME, szCmd);
                jsonDic.put(KEY_SESSION_ID, "0x0000000001");
                jsonDic.put(szCmd, cfgValue);
            } catch (Exception e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onConfigSaved(algoType, false);
                }
                return;
            }
            byte[] pInParam = jsonDic.toString().getBytes();
            FunSDK.DevCmdGeneral(userId, devId, algoType.getSetCommand(), szCmd, -1, TIMEOUT,
                    pInParam, 0, SEQ_SET_CONFIG);
        } else {
            // 分支C: PIR 使用 DevSetConfigByJson (SDK默认方式)
            JSONObject jsonDic = new JSONObject();
            try {
                jsonDic.put(KEY_NAME, cfgName);
                jsonDic.put(KEY_SESSION_ID, "0x0000000001");
                jsonDic.put(cfgName, cfgValue);
            } catch (Exception e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onConfigFailed(algoType, 0, -1, null, false);
                    listener.onConfigSaved(algoType, false);
                }
                return;
            }
            FunSDK.DevSetConfigByJson(userId, devId, cfgName, jsonDic.toString(), channel, TIMEOUT, 0);
        }
    }

    // ==================== FunSDK 回调 ====================

    @Override
    public int OnFunSDKResult(Message msg, MsgContent ex) {
        if (ex == null) {
            return 0;
        }
        switch (msg.what) {
            case EUIMSG.DEV_CMD_EN: {
                // DevCmdGeneral (1042/1040/1360) 回调，通过 seq 区分获取与保存
                if (ex.seq == SEQ_GET_CONFIG) {
                    handleGetConfigCallback(msg, ex);
                } else if (ex.seq == SEQ_SET_CONFIG) {
                    handleSaveConfigCallback(msg, ex);
                }
            }
            break;
            case EUIMSG.DEV_GET_JSON: {
                // DevGetConfigByJson 回调，通过 ex.str 区分配置类型
                Log.d(TAG, "DEV_GET_JSON: ex.str=" + ex.str
                        + ", matches VoiceTipType=" + CFG_VOICE_TIP_TYPE.equals(ex.str));
                if (CFG_VOICE_TIP_TYPE.equals(ex.str)) {
                    // VoiceTipType 能力查询回调（参照 AovAlarmLinkActivity）
                    handleVoiceTipTypeCallback(msg, ex);
                } else {
                    // PIR 配置获取回调
                    handleGetConfigCallback(msg, ex);
                }
            }
            break;
            case EUIMSG.DEV_SET_JSON: {
                // DevSetConfigByJson 回调（PIR）
                handleSaveConfigCallback(msg, ex);
            }
            break;
            default:
                break;
        }
        return 0;
    }

    /**
     * 获取配置回调处理
     * IPC 返回字典，多通道返回数组取 [0]
     */
    private void handleGetConfigCallback(Message msg, MsgContent ex) {
        boolean success = msg.arg1 >= 0;
        String configData = null;

        if (success && ex.pData != null) {
            String jsonData = G.ToString(ex.pData);
            try {
                JSONObject jsonDic = new JSONObject(jsonData);
                // 优先从响应 JSON 的 "Name" 字段获取实际 cfgName
                String respCfgName = jsonDic.optString(KEY_NAME, getEffectiveCfgName());
                Object cfgValue = jsonDic.opt(respCfgName);

                if (cfgValue instanceof JSONArray) {
                    // 多通道设备返回数组
                    JSONArray arrayInfo = (JSONArray) cfgValue;
                    synchronized (this) {
                        arrayCfg = arrayInfo;
                        dictCfg = null; // 清空字典
                    }
                    if (arrayInfo.length() > 0) {
                        Object first = arrayInfo.opt(0);
                        if (first instanceof JSONObject) {
                            configData = ((JSONObject) first).toString();
                        }
                    }
                } else if (cfgValue instanceof JSONObject) {
                    // IPC 设备返回字典
                    JSONObject dictValue = (JSONObject) cfgValue;
                    synchronized (this) {
                        dictCfg = dictValue;
                        arrayCfg = null; // 清空数组
                    }
                    configData = dictValue.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!success || configData == null) {
            FunSDK.Log(TAG + " getConfigFailed algoType=" + algoType + " arg1=" + msg.arg1);
        }

        OnConfigResultListener l = listener;
        listener = null;
        if (l != null) {
            if (!success || configData == null) {
                l.onConfigFailed(algoType, msg.what, msg.arg1, ex.str, true);
            }
            l.onConfigResult(algoType, success && configData != null, configData);
        }
    }

    /**
     * 保存配置回调处理
     */
    private void handleSaveConfigCallback(Message msg, MsgContent ex) {
        boolean success = msg.arg1 >= 0;

        if (!success) {
            FunSDK.Log(TAG + " saveConfigFailed algoType=" + algoType + " arg1=" + msg.arg1);
        }

        OnConfigResultListener l = listener;
        listener = null;
        if (l != null) {
            if (!success) {
                l.onConfigFailed(algoType, msg.what, msg.arg1, ex.str, false);
            }
            l.onConfigSaved(algoType, success);
        }
    }

    /**
     * VoiceTipType 能力回调处理
     * 参照 AovAlarmLinkActivity 和 AlgoVoiceSelectActivity 的解析逻辑。
     * <p>
     * 响应格式：{"Name": "Ability.VoiceTipType", "Ability.VoiceTipType": {"VoiceTip": [...], "CustomVoice": [...]}}
     * VoiceTip 为系统铃声，CustomVoice 为自定义铃声，每项包含 VoiceEnum(int) 和 VoiceText(string)。
     */
    private void handleVoiceTipTypeCallback(Message msg, MsgContent ex) {
        boolean success = msg.arg1 >= 0;
        Log.d(TAG, "handleVoiceTipTypeCallback: success=" + success + ", arg1=" + msg.arg1
                + ", pDataNull=" + (ex.pData == null) + ", str=" + ex.str);
        if (success && ex.pData != null) {
            String jsonData = G.ToString(ex.pData);
            Log.d(TAG, "handleVoiceTipTypeCallback: jsonData=" + jsonData);
            parseVoiceTipType(jsonData);
        } else {
            FunSDK.Log(TAG + " getVoiceTipType failed arg1=" + msg.arg1);
        }
        Log.d(TAG, "handleVoiceTipTypeCallback: after parse, loaded=" + mVoiceTipTypeLoaded
                + ", mapSize=" + (mVoiceTypeTextMap != null ? mVoiceTypeTextMap.size() : -1));
        OnVoiceTipTypeLoadedListener l = mVoiceTipTypeListener;
        mVoiceTipTypeListener = null;
        if (l != null) {
            l.onVoiceTipTypeLoaded(success && mVoiceTipTypeLoaded);
        }
    }

    /**
     * 解析 VoiceTipType JSON 数据，填充 mVoiceTypeTextMap
     * 响应格式：{"Name": "Ability.VoiceTipType", "Ability.VoiceTipType": {"VoiceTip": [...], "CustomVoice": [...]}}
     */
    private void parseVoiceTipType(String jsonData) {
        if (jsonData == null || jsonData.isEmpty()) {
            Log.d(TAG, "parseVoiceTipType: jsonData is null or empty");
            return;
        }
        mVoiceTipTypeJson = jsonData;
        try {
            JSONObject jsonObj = new JSONObject(jsonData);
            // 参照 HandleConfigData.getDataObj：检查 Ret 字段
            int ret = jsonObj.optInt("Ret", -1);
            Log.d(TAG, "parseVoiceTipType: Ret=" + ret);
            if (ret < 0) {
                Log.d(TAG, "parseVoiceTipType: Ret < 0, aborting parse");
                return;
            }
            String cfgName = jsonObj.optString(KEY_NAME, CFG_VOICE_TIP_TYPE);
            Log.d(TAG, "parseVoiceTipType: cfgName from Name field=" + cfgName);
            Object value = jsonObj.opt(cfgName);
            // 容错：如果 Name 字段值与实际 key 不匹配（如带通道后缀），回退到直接使用 CFG_VOICE_TIP_TYPE
            if (value == null && !cfgName.equals(CFG_VOICE_TIP_TYPE)) {
                Log.d(TAG, "parseVoiceTipType: value null for cfgName='" + cfgName
                        + "', trying direct key=" + CFG_VOICE_TIP_TYPE);
                value = jsonObj.opt(CFG_VOICE_TIP_TYPE);
            }
            Log.d(TAG, "parseVoiceTipType: value type="
                    + (value == null ? "null" : value.getClass().getSimpleName()));

            mVoiceTypeTextMap = new HashMap<>();

            if (value instanceof JSONObject) {
                // 配置值为对象，包含 VoiceTip（系统铃声）和 CustomVoice（自定义铃声）两个数组
                JSONObject voiceObj = (JSONObject) value;
                // 解析系统铃声
                JSONArray voiceTipArray = voiceObj.optJSONArray("VoiceTip");
                Log.d(TAG, "parseVoiceTipType: VoiceTip array="
                        + (voiceTipArray == null ? "null" : voiceTipArray.length() + " items"));
                if (voiceTipArray != null) {
                    for (int i = 0; i < voiceTipArray.length(); i++) {
                        JSONObject item = voiceTipArray.optJSONObject(i);
                        if (item == null) continue;
                        int voiceEnum = item.optInt("VoiceEnum", -1);
                        String voiceText = item.optString("VoiceText", "");
                        Log.d(TAG, "parseVoiceTipType: VoiceTip[" + i + "] enum=" + voiceEnum + " text=" + voiceText);
                        if (voiceEnum >= 0 && !voiceText.isEmpty()) {
                            mVoiceTypeTextMap.put(voiceEnum, voiceText);
                        }
                    }
                }
                // 解析自定义铃声
                JSONArray customVoiceArray = voiceObj.optJSONArray("CustomVoice");
                if (customVoiceArray != null) {
                    for (int i = 0; i < customVoiceArray.length(); i++) {
                        JSONObject item = customVoiceArray.optJSONObject(i);
                        if (item == null) continue;
                        int voiceEnum = item.optInt("VoiceEnum", -1);
                        String voiceText = item.optString("VoiceText", "");
                        if (voiceEnum >= 0 && !voiceText.isEmpty()) {
                            mVoiceTypeTextMap.put(voiceEnum, voiceText);
                        }
                    }
                }
            } else if (value instanceof JSONArray) {
                // 兼容部分设备返回数组格式
                JSONArray voiceArray = (JSONArray) value;
                Log.d(TAG, "parseVoiceTipType: array format, length=" + voiceArray.length());
                for (int i = 0; i < voiceArray.length(); i++) {
                    JSONObject item = voiceArray.optJSONObject(i);
                    if (item == null) continue;
                    int voiceEnum = item.optInt("VoiceEnum", -1);
                    String voiceText = item.optString("VoiceText", "");
                    if (voiceEnum >= 0 && !voiceText.isEmpty()) {
                        mVoiceTypeTextMap.put(voiceEnum, voiceText);
                    }
                }
            }

            Log.d(TAG, "parseVoiceTipType: final map size=" + mVoiceTypeTextMap.size());
            if (mVoiceTypeTextMap.isEmpty()) {
                Log.d(TAG, "parseVoiceTipType: map is empty, not setting loaded flag");
                return;
            }
            mVoiceTipTypeLoaded = true;
            Log.d(TAG, "parseVoiceTipType: success, loaded=true, map size=" + mVoiceTypeTextMap.size());
        } catch (Exception e) {
            Log.d(TAG, "parseVoiceTipType: exception=" + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 获取配置名称
     * 通道处理已在 requestConfig/requestSaveConfig 中通过 szCmd 完成，
     * 此方法直接返回 cfgName（Detect.*Detection 或 Alarm.PIR）
     */
    private String getEffectiveCfgName() {
        return algoType.getCfgName();
    }

    /**
     * 获取当前配置只读 JSONObject（调用方需在 synchronized(this) 内调用）
     * 优先返回 dictCfg（IPC 字典模式），其次返回 arrayCfg[0]（多通道数组模式）
     */
    private JSONObject getCfgDictionary() {
        if (dictCfg != null) {
            return dictCfg;
        }
        if (arrayCfg != null && arrayCfg.length() > 0) {
            Object obj = arrayCfg.opt(0);
            if (obj instanceof JSONObject) {
                return (JSONObject) obj;
            }
        }
        return null;
    }

    /**
     * 获取 EventHandler 子字典（调用方需在 synchronized(this) 内调用）
     */
    private JSONObject getEventHandler() {
        JSONObject dicCfg = getCfgDictionary();
        if (dicCfg == null) {
            return null;
        }
        return dicCfg.optJSONObject(KEY_EVENT_HANDLER);
    }

    /**
     * 线程安全地更新顶层字段
     */
    private void updateTopLevelKey(String key, Object value) {
        synchronized (this) {
            if (dictCfg != null) {
                // IPC 字典模式
                try {
                    dictCfg.put(key, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (arrayCfg != null && arrayCfg.length() > 0) {
                // 数组模式：更新 arrayCfg[0]
                try {
                    JSONObject dicCfg = arrayCfg.getJSONObject(0);
                    dicCfg.put(key, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 线程安全地更新 EventHandler 子字段
     */
    private void updateEventHandlerKey(String key, Object value) {
        synchronized (this) {
            JSONObject dicCfg = getCfgDictionary();
            if (dicCfg == null) {
                return;
            }
            try {
                JSONObject eh = dicCfg.optJSONObject(KEY_EVENT_HANDLER);
                if (eh == null) {
                    eh = new JSONObject();
                }
                eh.put(key, value);
                dicCfg.put(KEY_EVENT_HANDLER, eh);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 线程安全地批量更新 EventHandler 子字段（单次 synchronized 保证原子性）
     */
    private void updateEventHandlerKeys(Map<String, Object> keyValues) {
        synchronized (this) {
            JSONObject dicCfg = getCfgDictionary();
            if (dicCfg == null) {
                return;
            }
            try {
                JSONObject eh = dicCfg.optJSONObject(KEY_EVENT_HANDLER);
                if (eh == null) {
                    eh = new JSONObject();
                }
                for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
                    eh.put(entry.getKey(), entry.getValue());
                }
                dicCfg.put(KEY_EVENT_HANDLER, eh);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断 Mask 字符串的整数值是否非零
     * 支持 "0x1"、"0x00"、"0x00000001" 等十六进制格式，也兼容纯数字
     */
    private boolean isMaskNonZero(Object maskValue) {
        if (maskValue == null) {
            return false;
        }
        if (maskValue instanceof String) {
            String maskStr = (String) maskValue;
            if (maskStr.isEmpty()) {
                return false;
            }
            return HandleConfigData.getIntFromHex(maskStr) != 0;
        }
        if (maskValue instanceof Number) {
            return ((Number) maskValue).intValue() != 0;
        }
        return false;
    }

    /**
     * 容错读取布尔值：支持 Boolean、Number（非0为true）、String（"true"/"1"为true）
     */
    private boolean readBool(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).intValue() != 0;
        if (value instanceof String) {
            String s = (String) value;
            return "true".equalsIgnoreCase(s) || "1".equals(s);
        }
        return false;
    }

    /**
     * 容错读取整数：支持 Integer、Number、String（含十六进制如"0x1"）
     */
    private int readInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) {
            String s = (String) value;
            if (s.startsWith("0x") || s.startsWith("0X")) {
                try { return Integer.parseInt(s.substring(2), 16); } catch (Exception e) { return 0; }
            }
            try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
        }
        return 0;
    }

    /**
     * 读取事件掩码开关：enable为true且mask非0时返回true
     */
    private boolean readEventMaskEnabled(Object enableValue, Object maskValue) {
        return readBool(enableValue) && isMaskNonZero(maskValue);
    }

    // ==================== 通用配置访问器（Getter） ====================

    /**
     * 算法开关
     */
    public boolean isEnable() {
        synchronized (this) {
            JSONObject dicCfg = getCfgDictionary();
            return dicCfg != null && readBool(dicCfg.opt(KEY_ENABLE));
        }
    }

    /**
     * 灵敏度（0=低 1=中 2=高）
     * <p>
     * 读取后 clamp 到 [algoType.getSensitivityMin(), algoType.getSensitivityMax()]，
     */
    public int getSensitivity() {
        synchronized (this) {
            JSONObject dicCfg = getCfgDictionary();
            if (dicCfg == null) return algoType.getSensitivityDefault();
            int value = dicCfg.optInt(algoType.getSensitivityField(), algoType.getSensitivityDefault());
            int min = algoType.getSensitivityMin();
            int max = algoType.getSensitivityMax();
            if (value < min) value = min;
            if (value > max) value = max;
            return value;
        }
    }

    /**
     * 目标标记框（ShowTrack），仅视频AI算法支持
     */
    public boolean isShowTrack() {
        synchronized (this) {
            JSONObject dicCfg = getCfgDictionary();
            return dicCfg != null && readBool(dicCfg.opt(KEY_SHOW_TRACK));
        }
    }

    /**
     * 抓拍图片：SnapEnable==true 且 SnapShotMask 整数值非0 时为开启
     */
    public boolean isSnapEnable() {
        synchronized (this) {
            JSONObject eh = getEventHandler();
            if (eh == null) return false;
            return readEventMaskEnabled(eh.opt(KEY_SNAP_ENABLE), eh.opt(KEY_SNAP_SHOT_MASK));
        }
    }

    /**
     * 抓拍掩码值
     */
    public int getSnapShotMask() {
        synchronized (this) {
            JSONObject eh = getEventHandler();
            if (eh == null) return 0;
            return readInt(eh.opt(KEY_SNAP_SHOT_MASK));
        }
    }

    /**
     * 抓录视频：RecordEnable==true 且 RecordMask 整数值非0 时为开启
     */
    public boolean isRecordEnable() {
        synchronized (this) {
            JSONObject eh = getEventHandler();
            if (eh == null) return false;
            return readEventMaskEnabled(eh.opt(KEY_RECORD_ENABLE), eh.opt(KEY_RECORD_MASK));
        }
    }

    /**
     * 抓录掩码值
     */
    public int getRecordMask() {
        synchronized (this) {
            JSONObject eh = getEventHandler();
            if (eh == null) return 0;
            return readInt(eh.opt(KEY_RECORD_MASK));
        }
    }

    /**
     * 警示灯开关
     */
    public boolean isWarningLightEnable() {
        synchronized (this) {
            JSONObject eh = getEventHandler();
            return eh != null && readBool(eh.opt(KEY_WARNING_LIGHT));
        }
    }

    /**
     * 警示灯延迟关闭秒数
     */
    public int getWarningLightDelay() {
        synchronized (this) {
            JSONObject eh = getEventHandler();
            return eh != null ? readInt(eh.opt(KEY_WARNING_LIGHT_DELAY)) : 0;
        }
    }

    /**
     * 设备警铃开关
     */
    public boolean isVoiceEnable() {
        synchronized (this) {
            JSONObject eh = getEventHandler();
            return eh != null && readBool(eh.opt(KEY_VOICE_ENABLE));
        }
    }

    /**
     * 铃声类型
     */
    public int getVoiceType() {
        synchronized (this) {
            JSONObject eh = getEventHandler();
            return eh != null ? readInt(eh.opt(KEY_VOICE_TYPE)) : 0;
        }
    }

    /**
     * 警铃重复间隔（0=只响一次，>0=重复间隔秒数）
     */
    public int getVoiceTipInterval() {
        synchronized (this) {
            JSONObject eh = getEventHandler();
            return eh != null ? readInt(eh.opt(KEY_VOICE_TIP_INTERVAL)) : 0;
        }
    }

    /**
     * 录制时长（仅 PIR 支持）
     */
    public int getRecordDuration() {
        synchronized (this) {
            JSONObject eh = getEventHandler();
            return eh != null ? readInt(eh.opt(KEY_RECORD_DURATION)) : 0;
        }
    }

    /**
     * 警铃持续时间（仅 PIR 支持）
     */
    public int getVoiceDuration() {
        synchronized (this) {
            JSONObject eh = getEventHandler();
            return eh != null ? readInt(eh.opt(KEY_VOICE_DURATION)) : 0;
        }
    }

    /**
     * 报警时间段列表
     */
    public List<String> getTimeSection() {
        synchronized (this) {
            JSONObject eh = getEventHandler();
            if (eh == null) {
                return null;
            }
            return jsonArrayToStringList(eh.optJSONArray(KEY_TIME_SECTION));
        }
    }

    /**
     * 报警输出时间段列表
     */
    public List<String> getAlarmOutTimeSection() {
        synchronized (this) {
            JSONObject eh = getEventHandler();
            if (eh == null) {
                return null;
            }
            return jsonArrayToStringList(eh.optJSONArray(KEY_ALARM_OUT_TIME_SECTION));
        }
    }

    /**
     * 警铃报警时间段列表
     */
    public List<String> getVoiceTimeSection() {
        synchronized (this) {
            JSONObject eh = getEventHandler();
            if (eh == null) {
                return null;
            }
            return jsonArrayToStringList(eh.optJSONArray(KEY_VOICE_TIME_SECTION));
        }
    }

    /**
     * 消息通知间隔（仅 VolumeDetection 支持）
     */
    public int getInterval() {
        synchronized (this) {
            JSONObject dicCfg = getCfgDictionary();
            return dicCfg != null ? dicCfg.optInt(KEY_INTERVAL, 0) : 0;
        }
    }

    /**
     * 获取 PedRule 规则配置（JSON 字符串）
     */
    public String getPedRuleJson() {
        synchronized (this) {
            JSONObject dicCfg = getCfgDictionary();
            if (dicCfg == null) return null;
            JSONArray pedRule = dicCfg.optJSONArray(KEY_PED_RULE);
            return pedRule != null ? pedRule.toString() : null;
        }
    }

    // ==================== 通用配置设置方法（Setter） ====================

    /**
     * 设置算法开关
     */
    public void setEnable(boolean enable) {
        updateTopLevelKey(KEY_ENABLE, enable);
    }

    /**
     * 设置灵敏度（0=低 1=中 2=高）
     * <p>
     * 保存前 clamp 到 [algoType.getSensitivityMin(), algoType.getSensitivityMax()]，
     */
    public void setSensitivity(int sensitivity) {
        int min = algoType.getSensitivityMin();
        int max = algoType.getSensitivityMax();
        if (sensitivity < min) sensitivity = min;
        if (sensitivity > max) sensitivity = max;
        updateTopLevelKey(algoType.getSensitivityField(), sensitivity);
    }

    /**
     * 设置目标标记框
     */
    public void setShowTrack(boolean showTrack) {
        updateTopLevelKey(KEY_SHOW_TRACK, showTrack);
    }

    /**
     * 设置抓拍图片开关（同时写入 SnapEnable 和 SnapShotMask）
     */
    public void setSnapEnable(boolean snapEnable) {
        Map<String, Object> keyValues = new HashMap<>();
        keyValues.put(KEY_SNAP_ENABLE, snapEnable);
        keyValues.put(KEY_SNAP_SHOT_MASK, snapEnable ? "0x1" : "0x0");
        updateEventHandlerKeys(keyValues);
    }

    /**
     * 设置抓拍掩码
     */
    public void setSnapShotMask(int mask) {
        updateEventHandlerKey(KEY_SNAP_SHOT_MASK, HandleConfigData.getHexFromInt(mask));
    }

    /**
     * 设置抓录视频开关（同时写入 RecordEnable 和 RecordMask）
     */
    public void setRecordEnable(boolean recordEnable) {
        Map<String, Object> keyValues = new HashMap<>();
        keyValues.put(KEY_RECORD_ENABLE, recordEnable);
        keyValues.put(KEY_RECORD_MASK, recordEnable ? "0x1" : "0x0");
        updateEventHandlerKeys(keyValues);
    }

    /**
     * 设置抓录掩码
     */
    public void setRecordMask(int mask) {
        updateEventHandlerKey(KEY_RECORD_MASK, HandleConfigData.getHexFromInt(mask));
    }

    /**
     * 设置警示灯开关
     */
    public void setWarningLightEnable(boolean warningLightEnable) {
        updateEventHandlerKey(KEY_WARNING_LIGHT, warningLightEnable);
    }

    /**
     * 设置警示灯延迟关闭秒数
     */
    public void setWarningLightDelay(int delay) {
        updateEventHandlerKey(KEY_WARNING_LIGHT_DELAY, delay);
    }

    /**
     * 设置设备警铃开关
     */
    public void setVoiceEnable(boolean enable) {
        updateEventHandlerKey(KEY_VOICE_ENABLE, enable);
    }

    /**
     * 设置铃声类型
     */
    public void setVoiceType(int type) {
        updateEventHandlerKey(KEY_VOICE_TYPE, type);
    }

    /**
     * 设置警铃重复间隔（0=只响一次，>0=重复间隔秒数）
     */
    public void setVoiceTipInterval(int interval) {
        updateEventHandlerKey(KEY_VOICE_TIP_INTERVAL, interval);
    }

    /**
     * 设置录制时长
     */
    public void setRecordDuration(int duration) {
        updateEventHandlerKey(KEY_RECORD_DURATION, duration);
    }

    /**
     * 设置警铃持续时间
     */
    public void setVoiceDuration(int duration) {
        updateEventHandlerKey(KEY_VOICE_DURATION, duration);
    }

    /**
     * 设置报警时间段
     */
    public void setTimeSection(List<String> timeSection) {
        updateEventHandlerKey(KEY_TIME_SECTION, stringListToJsonArray(timeSection));
    }

    /**
     * 设置报警输出时间段
     */
    public void setAlarmOutTimeSection(List<String> alarmOutTimeSection) {
        updateEventHandlerKey(KEY_ALARM_OUT_TIME_SECTION, stringListToJsonArray(alarmOutTimeSection));
    }

    /**
     * 设置警铃报警时间段
     */
    public void setVoiceTimeSection(List<String> voiceTimeSection) {
        updateEventHandlerKey(KEY_VOICE_TIME_SECTION, stringListToJsonArray(voiceTimeSection));
    }

    /**
     * 设置消息通知间隔
     */
    public void setInterval(int interval) {
        updateTopLevelKey(KEY_INTERVAL, interval);
    }

    /**
     * 设置 PedRule 规则配置（JSON 字符串）
     */
    public void setPedRuleJson(String pedRuleJson) {
        if (pedRuleJson == null || pedRuleJson.isEmpty()) return;
        synchronized (this) {
            JSONObject dicCfg = getCfgDictionary();
            if (dicCfg == null) return;
            try {
                JSONArray pedRuleArray = new JSONArray(pedRuleJson);
                dicCfg.put(KEY_PED_RULE, pedRuleArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ==================== JSON 工具方法 ====================

    /**
     * JSONArray 转 List<String>
     */
    private List<String> jsonArrayToStringList(JSONArray array) {
        if (array == null) {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object item = array.opt(i);
            if (item != null) {
                list.add(item.toString());
            } else {
                list.add(null);
            }
        }
        return list;
    }

    /**
     * List<String> 转 JSONArray
     */
    private JSONArray stringListToJsonArray(List<String> list) {
        if (list == null) {
            return null;
        }
        JSONArray array = new JSONArray();
        for (String item : list) {
            if (item != null) {
                try {
                    array.put(new JSONArray(item));
                } catch (Exception e) {
                    array.put(item);
                }
            }
        }
        return array;
    }
}
