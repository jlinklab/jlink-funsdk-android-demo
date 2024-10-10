package demo.xm.com.xmfunsdkdemo.ui.device.alarm.presenter;

import android.content.Intent;
import android.widget.Toast;

import com.lib.FunSDK;
import com.lib.SDKCONST;
import com.lib.sdk.bean.AlarmInfoBean;
import com.lib.sdk.bean.CameraParamBean;
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

import java.util.ArrayList;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.ui.device.alarm.listener.WhiteLightContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.filetransfer.view.FileTransferActivity;
import demo.xm.com.xmfunsdkdemo.ui.entity.WorkModeBean;

/**
 * 声光报警：白光灯界面,可改变控制模式
 */
public class WhiteLightPresenter extends XMBasePresenter<DeviceManager> implements WhiteLightContract.IWhiteLightPresenter{

    private WhiteLightContract.IWhiteLightView iWhiteLightView;
    private DevConfigManager mDevConfigManager;


    protected WhiteLightBean mWhiteLight;
    public CameraParamBean mCameraParamBean;
    private IntelliAlertAlarmBean mIntelliAlertAlarmBean;
    private DevVolumeBean mDevHornVolumeBean;
    private List<VoiceTipBean> mVoiceTipBeanList;
    private LP4GLedParameterBean mLp4GLedParameterBean;
    //是否支持庭院灯
    private boolean mSupportGardenLight;
    //是否支持设置亮度
    private boolean mSupportSetBrightness;
    //是否支持红蓝灯
    private boolean mSupportSetAlarmLed;
    private WorkModeBean workModeBean;

    //是否支持灯光开关配置
    private boolean mSupportLightSwitch = true;



    public WhiteLightPresenter(WhiteLightContract.IWhiteLightView iWhiteLightView) {
        this.iWhiteLightView = iWhiteLightView;
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
    public void getVoiceLightAlarmConfig() {
        iWhiteLightView.onShowWaitDialog();
        getWhiteLight();
        // 如果设备支持工作模式，先获取工作模式能力集，再获取能力集
        if (FunSDK.GetDevAbility(getDevId(), "OtherFunction/SupportLPWorkModeSwitchV2") > 0) {
            getWorkModeBean();
        } else {
            //获取SystemFunction信息
            getSystemFunction();
        }

    }


    /**
     * 获取摄像机参数
     */
    private void getCameraParam(){
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                if(result instanceof ArrayList){
                    ArrayList<CameraParamBean> list = (ArrayList<CameraParamBean>)result;
                    if(list!=null && list.size()>0){
                        mCameraParamBean = list.get(0);
                    }
                    if (mCameraParamBean != null) {
                        long selectValue = Long.parseLong(mCameraParamBean.DayNightColor.substring(2), 16);
                        iWhiteLightView.showColorWhiteLight(selectValue);
                        iWhiteLightView.onHideWaitDialog();
                    } else {
                        getDataFail();
                    }
                } else {
                    getDataFail();
                }

            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName(JsonConfig.CAMERA_PARAM);
        mainConfigInfo.setChnId(getChnId());
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }
    /**
     * 获取白光灯工作模式设置
     */
    private void getWhiteLight(){
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<WhiteLightBean>() {
            @Override
            public void onSuccess(String devId, int msgId, WhiteLightBean whiteLightBean) {
                mWhiteLight = whiteLightBean;
                if (mWhiteLight != null) {
                    iWhiteLightView.showWorkMode(mWhiteLight);
                    getCameraParam();
                } else {
                    getDataFail();
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName(JsonConfig.WHITE_LIGHT);
        mainConfigInfo.setChnId(getChnId());
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }

    /**
     * 获取4G白光红外切换配置
     */
    private void getLP4GLedParameterBean(){
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                HandleConfigData<LP4GLedParameterBean> handleConfigData = new HandleConfigData<>();
                if (handleConfigData.getDataObj(result, LP4GLedParameterBean.class)) {
                    mLp4GLedParameterBean = handleConfigData.getObj();
                    //庭院灯能力集是基于灯光能力集的，所以要优先判断庭院灯能力集
                    if (mSupportGardenLight) {
                        //庭院灯照明开关
                        //type代表开关状态：等于2表示开，等于1表示关
                        iWhiteLightView.initGardenLightSwitch(mLp4GLedParameterBean,mSupportSetBrightness);
                    } else {
                        //支持低功耗设备灯光能力
                        iWhiteLightView.initDoubleLightSwitch(workModeBean, mLp4GLedParameterBean);
                    }
                }
                iWhiteLightView.onHideWaitDialog();
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName(JsonConfig.CFG_LP_4G_LED_PARAMETER);
        mainConfigInfo.setChnId(getChnId());
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }

    /**
     * 获取智能警戒配置
     */
    private void getIntelliAlertAlarmBean(){
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                //单品智能警戒
                HandleConfigData<IntelliAlertAlarmBean> handleConfigData = new HandleConfigData<>();
                if (handleConfigData.getDataObj(result, IntelliAlertAlarmBean.class)) {
                    mIntelliAlertAlarmBean = handleConfigData.getObj();
                    iWhiteLightView.initSmartAlarmData(false,mIntelliAlertAlarmBean, mSupportGardenLight,mSupportSetAlarmLed,true);
                    getAbilityVoiceTipType();
                }
                iWhiteLightView.onHideWaitDialog();
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
     * 获取低功耗设备工作模式
     */
    private void getWorkModeBean(){
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                //单品智能警戒
                HandleConfigData<WorkModeBean> handleConfigData = new HandleConfigData<>();
                if (handleConfigData.getDataObj(result, WorkModeBean.class)) {
                    workModeBean = handleConfigData.getObj();
                    iWhiteLightView.showPirAlarmTitle(workModeBean,mSupportGardenLight);

                }
                getSystemFunction();
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName("LPDev.WorkMode");
        mainConfigInfo.setChnId(getChnId());
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }
    /**
     * 获取获取报警声种类
     */
    private void getAbilityVoiceTipType(){
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

                    iWhiteLightView.initRingtone(mVoiceTipBeanList, mIntelliAlertAlarmBean,voiceType);

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
     * 获取PIR报警设置
     */
    private void getAlarmInfoBean(){
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                //单品智能警戒

                if(result instanceof ArrayList){
                    ArrayList<AlarmInfoBean> list = (ArrayList<AlarmInfoBean>)result;
                    if(list!=null && list.size()>0){
                        iWhiteLightView.showPIRAlarmInfo(list.get(0),workModeBean,mSupportGardenLight);
                    }
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName(JsonConfig.ALARM_PIR);
        mainConfigInfo.setChnId(getChnId());
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }



    private void getDevVolumeBean(){
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                if(result instanceof ArrayList){
                    ArrayList<DevVolumeBean> list = (ArrayList<DevVolumeBean>)result;
                    if(list!=null && list.size()>0){
                        mDevHornVolumeBean = list.get(0);
                        iWhiteLightView.initVolume(mDevHornVolumeBean);
                    }
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




    private void getSystemFunction(){
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int operationType, String result) {
                if (result != null) {
                    HandleConfigData handleConfigData = new HandleConfigData();
                    if (handleConfigData.getDataObj(result, SystemFunctionBean.class)) {
                        SystemFunctionBean systemFunctionBean = (SystemFunctionBean) handleConfigData.getObj();
                        if (systemFunctionBean.OtherFunction.LP4GSupportDoubleLightSwitch
                                || systemFunctionBean.OtherFunction.SupportLowPowerDoubleLightToLightingSwitch) {
                            mSupportGardenLight = systemFunctionBean.OtherFunction.SupportLowPowerDoubleLightToLightingSwitch;
                            mSupportSetBrightness = systemFunctionBean.OtherFunction.SupportLowPowerSetBrightness;
                            mSupportSetAlarmLed = systemFunctionBean.OtherFunction.SupportLowPowerSetAlarmLed;
                            getLP4GLedParameterBean();
                        }
                        if (systemFunctionBean.AlarmFunction.IntellAlertAlarm) {
                            //单品设备
                            getIntelliAlertAlarmBean();
                            if (systemFunctionBean.OtherFunction.SupportSetVolume) {
                                getDevVolumeBean();
                            }
                            if (systemFunctionBean.OtherFunction.SupportPirAlarm) {
                                getAlarmInfoBean();
                            }
                        }
                    }
                } else {
                    getDataFail();
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                getDataFail();
            }
        });

        devConfigInfo.setJsonName(JsonConfig.SYSTEM_FUNCTION);
        devConfigInfo.setChnId(getChnId());
        mDevConfigManager.getDevConfig(devConfigInfo);
    }

    /**
     * 获取白光灯工作模式设置数据
     */
    @Override
    public WhiteLightBean getWhiteLightBean() {
        return mWhiteLight;
    }

    /**
     * 保存白光灯工作模式设置
     */
    @Override
    public void saveWhiteLight() {

        iWhiteLightView.onShowWaitDialog();

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
        devConfigInfo.setJsonName(JsonConfig.WHITE_LIGHT);
        devConfigInfo.setChnId(getChnId());
        devConfigInfo.setJsonData(HandleConfigData.getSendData(JsonConfig.WHITE_LIGHT, "0x01", mWhiteLight));
        mDevConfigManager.setDevConfig(devConfigInfo);
    }

    private void saveSmartAlarm() {
        if (mIntelliAlertAlarmBean != null) {
            iWhiteLightView.onShowWaitDialog();
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


    /**
     * 设置报警音量
     */
    public void saveVolume() {
        if (mDevHornVolumeBean != null) {
            iWhiteLightView.onShowWaitDialog();
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

    /**
     * 保存庭院灯配置
     */
    public void saveGardenConfig() {
        if (mLp4GLedParameterBean != null) {
            iWhiteLightView.onShowWaitDialog();


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
            devConfigInfo.setJsonName(JsonConfig.CFG_LP_4G_LED_PARAMETER);
            devConfigInfo.setChnId(getChnId());
            devConfigInfo.setJsonData(HandleConfigData.getSendData(JsonConfig.CFG_LP_4G_LED_PARAMETER, "0x01", mLp4GLedParameterBean));
            mDevConfigManager.setDevConfig(devConfigInfo);
        }
    }

    private void getDataFail(){
        iWhiteLightView.onHideWaitDialog();
        Toast.makeText(iWhiteLightView.getActivity().getApplicationContext(), FunSDK.TS("no_data"), Toast.LENGTH_SHORT).show();
    }


    private void saveDataFail(){
        iWhiteLightView.onHideWaitDialog();
        Toast.makeText(iWhiteLightView.getActivity().getApplicationContext(), FunSDK.TS("Save_Failed"), Toast.LENGTH_SHORT).show();
    }


    private void saveDataSuccess(){
        iWhiteLightView.onHideWaitDialog();
    }


    /**
     * 保存智能报警间隔配置
     */
    @Override
    public void saveSmartAlarmDurationConfig(int duration){
        if (mIntelliAlertAlarmBean != null) {
            mIntelliAlertAlarmBean.Duration = duration;
            saveSmartAlarm();
        }
    }

    /**
     * 保存智能报警开关配置
     */
    @Override
    public void saveSmartAlarmSwitchConfig(boolean isOpen){
        if (mIntelliAlertAlarmBean != null) {
            mIntelliAlertAlarmBean.Enable = isOpen;
            saveSmartAlarm();
        }
    }


    /**
     * 保存报警灯光开关配置
     */
    @Override
    public void saveSmartAlarmLightSwitchConfig(boolean isOpen){
        if (mIntelliAlertAlarmBean != null && mIntelliAlertAlarmBean.EventHandler != null) {
            mIntelliAlertAlarmBean.EventHandler.AlarmOutEnable = isOpen;
            saveSmartAlarm();
        }
    }


    /**
     * 保存图像设置配置
     */
    @Override
    public void saveDayNightColorConfig(String dayNightColor){
        if (mCameraParamBean != null) {
            iWhiteLightView.onShowWaitDialog();
            mCameraParamBean.DayNightColor = dayNightColor;
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
            devConfigInfo.setJsonName(JsonConfig.CAMERA_PARAM);
            devConfigInfo.setChnId(getChnId());
            devConfigInfo.setJsonData(HandleConfigData.getSendData(JsonConfig.CAMERA_PARAM, "0x01", mCameraParamBean));
            mDevConfigManager.setDevConfig(devConfigInfo);
        } else {
            Toast.makeText(iWhiteLightView.getActivity().getApplicationContext(), FunSDK.TS("Data_exception"), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 保存报警铃声配置
     */
    @Override
    public void saveIntelliAlertAlarmVoiceType(int value){
        if (mIntelliAlertAlarmBean != null) {
            mIntelliAlertAlarmBean.EventHandler.VoiceType = value;
            saveSmartAlarm();
            if ((int) value == -2 || (int) value == 550) {
                //选择自定义报警音
                Intent intent = new Intent(iWhiteLightView.getActivity(), FileTransferActivity.class);
                intent.putExtra("devId", getDevId());
                intent.putExtra("chnId", getChnId());
                iWhiteLightView.getActivity().startActivity(intent);

            }
        }
    }

    /**
     * 是否为基站设备
     *
     * @param devType
     * @return
     */
    public static boolean isWBS(int devType) {
        switch (devType) {
            case SDKCONST.DEVICE_TYPE.EE_DEV_WBS:
                return true;
            default:
                if (isWBS_IOT(devType)) {
                    return true;
                }
                return false;
        }
    }

    /**
     * 是否为基站IOT设备
     *
     * @param devType
     * @return
     */
    public static boolean isWBS_IOT(int devType) {
        int type = 0x0000FF00;
        if (devType == SDKCONST.DEVICE_TYPE.EE_DEV_WBS_IOT) {
            return true;
        } else if (isNVR(devType) && (devType & type) > 0) {
            return true;
        }
        return false;
    }

    public static boolean isNVR(int devType) {
        if (devType == SDKCONST.DEVICE_TYPE.EE_DEV_WBS) {
            return true;
        }
        int type = 0x0F000000;
        if ((devType & type) == 0x02000000 || (devType & type) == 0x03000000) {
            return true;
        }
        return false;
    }

    /**
     * 保存灯光设置
     */

    @Override
    public void saveDoubleLightSwitch(int value) {
        if (mLp4GLedParameterBean != null) {
            iWhiteLightView.onShowWaitDialog();
            mLp4GLedParameterBean.setType(value);
            //低功耗设备
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
            devConfigInfo.setJsonName(JsonConfig.CFG_LP_4G_LED_PARAMETER);
            devConfigInfo.setChnId(getChnId());
            devConfigInfo.setJsonData(HandleConfigData.getSendData(JsonConfig.CFG_LP_4G_LED_PARAMETER, "0x01", mLp4GLedParameterBean));
            mDevConfigManager.setDevConfig(devConfigInfo);
        }
    }


    /**
     * 保存庭院灯照明开关配置
     */
    @Override
    public void saveGardenLightSwitch(boolean isOpenGardenLightSwitch){
        mLp4GLedParameterBean.setType(isOpenGardenLightSwitch ? 2 : 1);
        saveGardenConfig();
    }


    /**
     * 是否支持设置灯光亮度
     */
    @Override
    public boolean isSupportSetBrightness(){
        return mSupportSetBrightness;
    }

    /**
     *保存报警音量配置
     */
    @Override
    public void saveDevHornVolume(int volume){
        mDevHornVolumeBean.setRightVolume(volume);
        mDevHornVolumeBean.setLeftVolume(volume);
    }

    /**
     *保存灯光亮度配置
     */
    @Override
    public void saveBrightness(int brightness){
        mLp4GLedParameterBean.setBrightness(brightness);
    }


    /**
     * 是否支持灯光开关配置
     */
    public boolean isSupportLightSwitch() {
        return mSupportLightSwitch;
    }

    public void setSupportLightSwitch(boolean supportLightSwitch) {
        this.mSupportLightSwitch = supportLightSwitch;
    }
}

