package demo.xm.com.xmfunsdkdemo.ui.device.aov.presenter;


import android.widget.Toast;

import com.lib.FunSDK;
import com.lib.sdk.bean.CameraParamExBean;
import com.lib.sdk.bean.FbExtraStateCtrlBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.SystemFunctionBean;
import com.lib.sdk.bean.WhiteLightBean;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.ui.device.aov.listener.AOVBlackLightSettingContract;


public class AOVBlackLightSettingPresenter extends XMBasePresenter<DeviceManager> implements AOVBlackLightSettingContract.IAOVBlackLightSettingPresenter{

    private int minute, hours;
    private int timeSelect;

    private WhiteLightBean mWhiteLight;
    private CameraParamExBean mCameraParamEx;
    private boolean mSupportSetBrightness;//是否支持设置灯光亮度
    private boolean mSoftLedThr;//是否支持自动灯光模式下的灵敏度设置
    private FbExtraStateCtrlBean mFbExtraStateInfo;



    private DevConfigManager mDevConfigManager;

    private AOVBlackLightSettingContract.IAOVBlackLightSettingView iAOVBlackLightSettingView;


    public AOVBlackLightSettingPresenter(AOVBlackLightSettingContract.IAOVBlackLightSettingView iAOVBlackLightSettingView){
        this.iAOVBlackLightSettingView = iAOVBlackLightSettingView;
    }

    
    
    

    @Override
    public void setDevId(String devId) {
        mDevConfigManager = manager.getDevConfigManager(devId);
        super.setDevId(devId);
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }







    public void saveWorkPeriod(int hour,int min,boolean isStart){
        if(isStart){
            iAOVBlackLightSettingView.onShowWaitDialog();
            mWhiteLight.getWorkPeriod().setSHour(hour);
            mWhiteLight.getWorkPeriod().setSMinute(min);
            saveLightConfig();
        } else {
            iAOVBlackLightSettingView.onShowWaitDialog();
            mWhiteLight.getWorkPeriod().setEHour(hour);
            mWhiteLight.getWorkPeriod().setEMinute(min);
            saveLightConfig();
        }
    }


    private void saveLightConfig() {
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
        devConfigInfo.setChnId(-1);
        devConfigInfo.setJsonData(HandleConfigData.getSendData(JsonConfig.WHITE_LIGHT, "0x01", mWhiteLight));
        mDevConfigManager.setDevConfig(devConfigInfo);
    }

    private void saveCameraParamEx() {
        if (mCameraParamEx != null) {
            iAOVBlackLightSettingView.onShowWaitDialog();

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
            devConfigInfo.setJsonName(JsonConfig.CAMERA_PARAMEX);
            devConfigInfo.setChnId(0);
            devConfigInfo.setJsonData(HandleConfigData.getSendData(HandleConfigData.getFullName(JsonConfig.CAMERA_PARAMEX, 0), "", mCameraParamEx));
            mDevConfigManager.setDevConfig(devConfigInfo);


        }
    }
    /**
     * 保存状态灯和提示音
     */
    private void saveLampAndSoundConfig() {
        if(mFbExtraStateInfo != null){
            iAOVBlackLightSettingView.onShowWaitDialog();

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
            devConfigInfo.setJsonName(JsonConfig.CFG_FbExtraStateCtrl);
            devConfigInfo.setChnId(-1);
            devConfigInfo.setJsonData(HandleConfigData.getSendData(JsonConfig.CFG_FbExtraStateCtrl, "0x01", mFbExtraStateInfo));
            mDevConfigManager.setDevConfig(devConfigInfo);



        }

    }



    public void getWhiteLight() {
        iAOVBlackLightSettingView.onShowWaitDialog();
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<WhiteLightBean>() {
            @Override
            public void onSuccess(String devId, int msgId, WhiteLightBean result) {
                iAOVBlackLightSettingView.onHideWaitDialog();
                mWhiteLight = result;
                if (mWhiteLight != null) {
                        checkDevAbility();
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName(JsonConfig.WHITE_LIGHT);
        mainConfigInfo.setChnId(-1);
        mDevConfigManager.getDevConfig(mainConfigInfo);


    }


    public void checkDevAbility(){
        DeviceManager.getInstance().getDevAllAbility(getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, SystemFunctionBean result) {
                if (result != null && result.OtherFunction!=null) {
                    mSupportSetBrightness = result.OtherFunction.SupportSetBrightness;
                    mSoftLedThr = result.OtherFunction.SoftLedThr;
                    iAOVBlackLightSettingView.updateView(mWhiteLight,mSupportSetBrightness,mSoftLedThr);
                } else {
                    iAOVBlackLightSettingView.updateView(mWhiteLight,mSupportSetBrightness,mSoftLedThr);
                }
            }

            /**
             * 失败回调
             *
             * @param devId    设备序列号
             * @param msgId    消息ID
             * @param jsonName
             * @param errorId  错误码
             */
            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                //获取失败，通过errorId分析具体原因
                iAOVBlackLightSettingView.updateView(mWhiteLight,mSupportSetBrightness,mSoftLedThr);
            }
        });

    }

    public void checkIsSupportMicroFillLight(){
        DeviceManager.getInstance().getDevAllAbility(getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, SystemFunctionBean result) {
                if (result != null && result.OtherFunction!=null && result.OtherFunction.MicroFillLight) {
                    iAOVBlackLightSettingView.showMicroFillLight(true);

                } else {
                    iAOVBlackLightSettingView.showMicroFillLight(false);
                }
            }

            /**
             * 失败回调
             *
             * @param devId    设备序列号
             * @param msgId    消息ID
             * @param jsonName
             * @param errorId  错误码
             */
            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                //获取失败，通过errorId分析具体原因
                iAOVBlackLightSettingView.showMicroFillLight(false);
            }
        });
    }

    public void checkIsSupportStatusLed(){
        DeviceManager.getInstance().getDevAllAbility(getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, SystemFunctionBean result) {
                if (result != null && result.OtherFunction!=null && result.OtherFunction.SupportStatusLed) {
                    getFbExtraStateCtrl();
                    iAOVBlackLightSettingView.showIndicator(true);

                } else {
                    iAOVBlackLightSettingView.showIndicator(false);
                }
            }

            /**
             * 失败回调
             *
             * @param devId    设备序列号
             * @param msgId    消息ID
             * @param jsonName
             * @param errorId  错误码
             */
            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                //获取失败，通过errorId分析具体原因
                iAOVBlackLightSettingView.showIndicator(false);
            }
        });
    }



    /**
     * 检测白光灯自动开关灯判断阈值
     */
    public void checkIsSupportSoftLedThr(){
        DeviceManager.getInstance().getDevAllAbility(getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, SystemFunctionBean result) {
                if (result != null && result.OtherFunction!=null && result.OtherFunction.SoftLedThr) {
                    getCameraParamEx();
                }
            }

            /**
             * 失败回调
             *
             * @param devId    设备序列号
             * @param msgId    消息ID
             * @param jsonName
             * @param errorId  错误码
             */
            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                //获取失败，通过errorId分析具体原因
            }
        });

    }

    public void getFbExtraStateCtrl(){
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                iAOVBlackLightSettingView.onHideWaitDialog();
                HandleConfigData handleConfigData = new HandleConfigData();
                if (handleConfigData.getDataObj(result, FbExtraStateCtrlBean.class)) {
                    // 个别设备获取的内容仍为空，故判断
                    if (handleConfigData.getObj() instanceof FbExtraStateCtrlBean) {
                        mFbExtraStateInfo = (FbExtraStateCtrlBean) handleConfigData.getObj();
                        if(mFbExtraStateInfo!= null){
                            iAOVBlackLightSettingView.showIndicatorLightState(mFbExtraStateInfo.getIson());
                        }
                    }
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName(JsonConfig.CFG_FbExtraStateCtrl);
        mainConfigInfo.setChnId(-1);
        mDevConfigManager.getDevConfig(mainConfigInfo);


    }

    public void getCameraParamEx(){

        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                iAOVBlackLightSettingView.onHideWaitDialog();
                HandleConfigData handleConfigData = new HandleConfigData();
                if (handleConfigData.getDataObj(result, CameraParamExBean.class)) {
                    if(handleConfigData.getObj() instanceof CameraParamExBean){
                        mCameraParamEx = (CameraParamExBean) handleConfigData.getObj();
                    } else if(handleConfigData.getObj() instanceof List){
                        List<CameraParamExBean> list = (List<CameraParamExBean>)handleConfigData.getObj();
                        if(list.size() > 0){
                            mCameraParamEx = list.get(0);
                        }
                    }

                    if (mCameraParamEx != null) {
                        iAOVBlackLightSettingView.showSensitive(mCameraParamEx.SoftLedThr);
                        if(mCameraParamEx.MicroFillLight != null){
                            iAOVBlackLightSettingView.showMicroLight(mCameraParamEx.MicroFillLight);
                        }
                    }
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName(JsonConfig.CAMERA_PARAMEX);
        mainConfigInfo.setChnId(-1);
        mDevConfigManager.getDevConfig(mainConfigInfo);


    }

    private void saveDataFail(){
        iAOVBlackLightSettingView.onHideWaitDialog();
        Toast.makeText(iAOVBlackLightSettingView.getActivity().getApplicationContext(), FunSDK.TS("Save_Failed"), Toast.LENGTH_SHORT).show();
    }


    private void saveDataSuccess(){
        iAOVBlackLightSettingView.onHideWaitDialog();
        Toast.makeText(iAOVBlackLightSettingView.getActivity().getApplicationContext(), FunSDK.TS("Save_Success"), Toast.LENGTH_SHORT).show();
    }




    public boolean isSupportSetBrightness(){
        return mSupportSetBrightness;
    }


   

    private void getDataFail(){
        iAOVBlackLightSettingView.onHideWaitDialog();
        Toast.makeText(iAOVBlackLightSettingView.getActivity().getApplicationContext(), FunSDK.TS("no_data"), Toast.LENGTH_SHORT).show();

    }

    @Override
    public WhiteLightBean getWhiteLightBean() {
        return mWhiteLight;
    }

    public boolean isSoftLedThr() {
        return mSoftLedThr;
    }


    public void saveWorkMode(String workMode) {
        iAOVBlackLightSettingView.onShowWaitDialog();
        mWhiteLight.setWorkMode(workMode);
        saveLightConfig();
    }


    public void saveBrightness(int brightness) {
        iAOVBlackLightSettingView.onShowWaitDialog();
        mWhiteLight.setBrightness(brightness);
        saveLightConfig();
    }

    public CameraParamExBean getCameraParamExBean() {
        return mCameraParamEx;
    }


    public void saveSensitive(int value) {
        iAOVBlackLightSettingView.onShowWaitDialog();
        mCameraParamEx.SoftLedThr = value;
        saveCameraParamEx();
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getTimeSelect() {
        return timeSelect;
    }


    public FbExtraStateCtrlBean getFbExtraStateInfo() {
        return mFbExtraStateInfo;
    }

    public void setTimeData(int timeSelect,int hours,int minute){
        this.timeSelect = timeSelect;
        this.hours = hours;
        this.minute = minute;
    }


    @Override
    public void saveIndicatorLight(int switchState) {
        mFbExtraStateInfo.setIson(switchState);
        saveLampAndSoundConfig();
    }

    @Override
    public void saveMicroLight(int switchState) {
        mCameraParamEx.MicroFillLight = switchState;
        saveCameraParamEx();
    }
}
