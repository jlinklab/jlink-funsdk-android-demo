package demo.xm.com.xmfunsdkdemo.ui.device.alarm.presenter;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.basic.G;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.lib.FunSDK;
import com.lib.SDKCONST;
import com.lib.sdk.bean.AlarmInfoBean;
import com.lib.sdk.bean.DevVolumeBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.IntelliAlertAlarmBean;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.LP4GLedParameterBean;
import com.lib.sdk.bean.SystemFunctionBean;
import com.lib.sdk.bean.VoiceTipBean;
import com.lib.sdk.bean.VoiceTipTypeBean;
import com.lib.sdk.bean.WhiteLightBean;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.listener.DoubleLightBoxContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.filetransfer.view.FileTransferActivity;
import demo.xm.com.xmfunsdkdemo.ui.entity.WorkModeBean;

/**
 * 声光报警：双光枪机界面,可改变控制模式
 */
public class DoubleLightBoxPresenter extends XMBasePresenter<DeviceManager> implements DoubleLightBoxContract.IDoubleLightBoxPresenter {
    private DoubleLightBoxContract.IDoubleLightBoxView iDoubleLightBoxView;
    private DevConfigManager mDevConfigManager;
    protected WhiteLightBean mWhiteLight;
    private DevVolumeBean mDevHornVolumeBean;
    private List<VoiceTipBean> mVoiceTipBeanList;
    private IntelliAlertAlarmBean mIntelliAlertAlarmBean;
    private boolean isAOVDev;//AOV设备
    private WorkModeBean workModeBean;//工作模式

    public DoubleLightBoxPresenter(DoubleLightBoxContract.IDoubleLightBoxView iDoubleLightBoxView) {
        this.iDoubleLightBoxView = iDoubleLightBoxView;
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void setDevId(String devId) {
        mDevConfigManager = manager.getDevConfigManager(devId);
        super.setDevId(devId);
    }

    /**
     * 获取声光报警配置
     */
    @Override
    public void getAlarmByVoiceLightConfig() {
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<WhiteLightBean>() {
            @Override
            public void onSuccess(String devId, int msgId, WhiteLightBean result) {
                System.out.println("result:" + result);
                if (result != null) {
                    mWhiteLight = result;
                    iDoubleLightBoxView.onUpdateView(true, mWhiteLight);
                } else {
                    mWhiteLight = null;
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                mWhiteLight = null;
                iDoubleLightBoxView.onUpdateView(false, null);
            }
        });
        mainConfigInfo.setJsonName(JsonConfig.WHITE_LIGHT);
        mainConfigInfo.setChnId(-1);
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }

    public void getSystemFunction() {
        //获取能力集
        manager.getDevAllAbility(getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            @Override
            public void onSuccess(String s, int i, SystemFunctionBean result) {
                if (result != null) {
                    if (result.AlarmFunction.IntellAlertAlarm) {
                        //单品设备
                        getIntelliAlertAlarmBean();
                        if (result.OtherFunction.SupportSetVolume) {
                            getDevVolumeBean();
                        }
                    }

                    isAOVDev = result.OtherFunction.AovMode;

                    if (result.OtherFunction.SupportListCameraDayLightModes) {
                        getCameraDayLightModes();
                        return;
                    }
                } else {
                    getDataFail();
                }

                iDoubleLightBoxView.initWhiteLightSwitch(new String[]{
                                iDoubleLightBoxView.getActivity().getString(R.string.full_color_vision),
                                iDoubleLightBoxView.getActivity().getString(R.string.general_night_vision),
                                iDoubleLightBoxView.getActivity().getString(R.string.double_light_vision)},
                        new Integer[]{0, 1, 2});
            }

            @Override
            public void onFailed(String s, int i, String s1, int errorId) {
                getDataFail();
                iDoubleLightBoxView.initWhiteLightSwitch(new String[]{
                                iDoubleLightBoxView.getActivity().getString(R.string.full_color_vision),
                                iDoubleLightBoxView.getActivity().getString(R.string.general_night_vision),
                                iDoubleLightBoxView.getActivity().getString(R.string.double_light_vision)},
                        new Integer[]{0, 1, 2});
            }
        });
    }

    /**
     * 获取工作模式
     * ModeType //工作模式（0：低功耗模式（超级省电模式）、1：智能模式（电量高时为常电模式，电量低切换为低功耗模式））
     * WorkStateNow //当前实际模式，在智能模式下和ModeType字段的值可能不一样（0：低功耗模式、1：常电模式）
     * PowerThreshold //智能模式下切换到低功耗模式的电池电量阈值（不允许修改）
     */
    private void getWorkMode() {
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String jsonData) {
                if (jsonData != null) {
                    JsonElement jsonElement = JsonParser.parseString(jsonData);
                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        if (jsonObject.has("LPDev.WorkMode")) {
                            jsonElement = jsonObject.get("LPDev.WorkMode");
                            workModeBean = new Gson().fromJson(jsonElement,WorkModeBean.class);
                        }
                    }
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName("LPDev.WorkMode");
        mainConfigInfo.setChnId(-1);
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }
    /**
     * 获取设备灯光支持的模式
     */
    private void getCameraDayLightModes() {
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String jsonData) {
                if (jsonData != null) {
                    boolean supportNightVision = false;
                    boolean supportFullColor = false;
                    boolean supportDoubleLight = false;
                    HashMap<String, Object> resultMap = new Gson().fromJson(jsonData, HashMap.class);
                    if (resultMap != null && resultMap.containsKey("CameraDayLightModes")) {
                        List<LinkedTreeMap<String, Double>> cameraDayLightModes = (List<LinkedTreeMap<String, Double>>) resultMap.get("CameraDayLightModes");
                        for (LinkedTreeMap<String, Double> cameraDayLightMode : cameraDayLightModes) {
                            Double valueDouble = cameraDayLightMode.get("value");
                            int value = valueDouble.intValue();
                            if (value == 5) {//红外夜视(智能红外)
                                supportNightVision = true;
                            } else if (value == 4) {//星光全彩(智能可变光)
                                supportFullColor = true;
                            } else if (value == 3) {//双光警戒(智能警戒)
                                supportDoubleLight = true;
                            }
                        }

                        if (supportFullColor && supportNightVision && supportDoubleLight) {
                            //超级省电模式不显示双光警戒
                            if (workModeBean != null && workModeBean.getModeType() == 0) {
                                iDoubleLightBoxView.initWhiteLightSwitch(new String[]{
                                                iDoubleLightBoxView.getActivity().getString(R.string.full_color_vision),
                                                iDoubleLightBoxView.getActivity().getString(R.string.general_night_vision)},
                                        new Integer[]{0, 1});
                            }else {
                                iDoubleLightBoxView.initWhiteLightSwitch(new String[]{
                                                iDoubleLightBoxView.getActivity().getString(R.string.full_color_vision),
                                                iDoubleLightBoxView.getActivity().getString(R.string.general_night_vision),
                                                iDoubleLightBoxView.getActivity().getString(R.string.double_light_vision)},
                                        new Integer[]{0, 1, 2});
                            }
                        } else {
                            iDoubleLightBoxView.initWhiteLightSwitch(new String[]{
                                            iDoubleLightBoxView.getActivity().getString(R.string.general_night_vision),
                                            iDoubleLightBoxView.getActivity().getString(R.string.full_color_vision)},
                                    new Integer[]{1, 0});
                        }
                    }
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName("CameraDayLightModes");
        mainConfigInfo.setChnId(-1);
        mainConfigInfo.setCmdId(1360);
        mDevConfigManager.setDevCmd(mainConfigInfo);
    }

    private void getDevVolumeBean() {
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                if (result instanceof ArrayList) {
                    ArrayList<DevVolumeBean> list = (ArrayList<DevVolumeBean>) result;
                    if (list != null && list.size() > 0) {
                        mDevHornVolumeBean = list.get(0);
                        iDoubleLightBoxView.initVolume(mDevHornVolumeBean);
                    }
                } else if (result instanceof DevVolumeBean) {
                    mDevHornVolumeBean = (DevVolumeBean) result;
                    iDoubleLightBoxView.initVolume(mDevHornVolumeBean);
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName(JsonConfig.CFG_DEV_HORN_VOLUME);
        mainConfigInfo.setChnId(getChnId());
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }

    /**
     * 获取智能警戒配置
     */
    private void getIntelliAlertAlarmBean() {
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                //单品智能警戒
                HandleConfigData<IntelliAlertAlarmBean> handleConfigData = new HandleConfigData<>();
                if (handleConfigData.getDataObj(result, IntelliAlertAlarmBean.class)) {
                    mIntelliAlertAlarmBean = handleConfigData.getObj();
                    iDoubleLightBoxView.initSmartAlarmData(mIntelliAlertAlarmBean);
                    getAbilityVoiceTipType();
                }
                iDoubleLightBoxView.onHideWaitDialog();
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName(JsonConfig.ALARM_INTEL_ALERT_ALARM);
        mainConfigInfo.setChnId(getChnId());
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }

    /**
     * 获取获取报警声种类
     */
    private void getAbilityVoiceTipType() {
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                //单品智能警戒
                HandleConfigData<VoiceTipTypeBean> handleConfigData = new HandleConfigData<>();
                if (handleConfigData.getDataObj(result, VoiceTipTypeBean.class)) {
                    VoiceTipTypeBean voiceTipTypeBean = handleConfigData.getObj();
                    mVoiceTipBeanList = voiceTipTypeBean.getVoiceTips();

                    String voiceType = null;
                    if (mVoiceTipBeanList != null) {
                        for (VoiceTipBean bean : mVoiceTipBeanList) {
                            if (bean.getVoiceEnum() == mIntelliAlertAlarmBean.EventHandler.VoiceType) {
                                voiceType = bean.getVoiceText();
                            }
                        }
                    }

                    iDoubleLightBoxView.initRingtone(mVoiceTipBeanList, mIntelliAlertAlarmBean, voiceType);

                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName(JsonConfig.CFG_VOICE_TIP_TYPE);
        mainConfigInfo.setChnId(getChnId());
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }

    /**
     * 保存声光报警配置
     */
    @Override
    public void saveAlarmByVoiceLightConfig() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                iDoubleLightBoxView.onSaveResult(true);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDoubleLightBoxView.onSaveResult(false);
            }
        });
        devConfigInfo.setJsonName(JsonConfig.WHITE_LIGHT);
        devConfigInfo.setChnId(-1);
        devConfigInfo.setJsonData(HandleConfigData.getSendData(JsonConfig.WHITE_LIGHT, "0x01", mWhiteLight));
        mDevConfigManager.setDevConfig(devConfigInfo);
    }

    @Override
    public void saveIntelliAlertAlarmVoiceType(int value) {
        if (mIntelliAlertAlarmBean != null) {
            mIntelliAlertAlarmBean.EventHandler.VoiceType = value;
            saveSmartAlarm();
            if ((int) value == -2 || (int) value == 550) {
                //选择自定义报警音
                Intent intent = new Intent(iDoubleLightBoxView.getActivity(), FileTransferActivity.class);
                intent.putExtra("devId", getDevId());
                intent.putExtra("chnId", getChnId());
                iDoubleLightBoxView.getActivity().startActivity(intent);
            }
        }
    }

    @Override
    public void saveSmartAlarmDurationConfig(int duration) {
        if (mIntelliAlertAlarmBean != null) {
            mIntelliAlertAlarmBean.Duration = duration;
            saveSmartAlarm();
        }
    }

    @Override
    public void saveSmartAlarmSwitchConfig(boolean isOpen) {
        if (mIntelliAlertAlarmBean != null) {
            mIntelliAlertAlarmBean.Enable = isOpen;
            saveSmartAlarm();
        }
    }

    @Override
    public void saveSmartAlarmLightSwitchConfig(boolean isOpen) {
        if (mIntelliAlertAlarmBean != null && mIntelliAlertAlarmBean.EventHandler != null) {
            mIntelliAlertAlarmBean.EventHandler.AlarmOutEnable = isOpen;
            saveSmartAlarm();
        }
    }

    @Override
    public void saveDevHornVolume(int volume) {
        mDevHornVolumeBean.setRightVolume(volume);
        mDevHornVolumeBean.setLeftVolume(volume);
    }

    @Override
    public void saveVolume() {
        if (mDevHornVolumeBean != null) {
            iDoubleLightBoxView.onShowWaitDialog();
            List<DevVolumeBean> devVolumeBeans = new ArrayList<>();
            devVolumeBeans.add(mDevHornVolumeBean);


            DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String devId, int msgId, Object result) {
                    saveDataSuccess();
                }

                @Override
                public void onFailed(String devId, int msgId, String s1, int errorId) {
                    saveDataFail();
                }
            });
            devConfigInfo.setJsonName(JsonConfig.CFG_DEV_HORN_VOLUME);
            devConfigInfo.setChnId(getChnId());
            devConfigInfo.setJsonData(HandleConfigData.getSendData(JsonConfig.CFG_DEV_HORN_VOLUME, "0x01", devVolumeBeans));
            mDevConfigManager.setDevConfig(devConfigInfo);
        }
    }

    private void saveSmartAlarm() {
        if (mIntelliAlertAlarmBean != null) {
            iDoubleLightBoxView.onShowWaitDialog();
            DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String devId, int msgId, Object result) {
                    saveDataSuccess();
                }

                @Override
                public void onFailed(String devId, int msgId, String s1, int errorId) {
                    saveDataFail();
                }
            });
            devConfigInfo.setJsonName(JsonConfig.ALARM_INTEL_ALERT_ALARM);
            devConfigInfo.setChnId(getChnId());
            devConfigInfo.setJsonData(HandleConfigData.getSendData(JsonConfig.ALARM_INTEL_ALERT_ALARM, "0x01", mIntelliAlertAlarmBean));
            mDevConfigManager.setDevConfig(devConfigInfo);
        }
    }

    @Override
    public WhiteLightBean getWhiteLightBean() {
        return mWhiteLight;
    }

    private void getDataFail() {
        iDoubleLightBoxView.onHideWaitDialog();
        Toast.makeText(iDoubleLightBoxView.getActivity(), FunSDK.TS("no_data"), Toast.LENGTH_SHORT).show();
    }


    private void saveDataFail() {
        iDoubleLightBoxView.onHideWaitDialog();
        Toast.makeText(iDoubleLightBoxView.getActivity(), FunSDK.TS("Save_Failed"), Toast.LENGTH_SHORT).show();
    }

    private void saveDataSuccess() {
        iDoubleLightBoxView.onHideWaitDialog();
    }
}

