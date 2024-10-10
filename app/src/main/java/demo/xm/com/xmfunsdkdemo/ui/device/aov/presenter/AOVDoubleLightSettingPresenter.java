package demo.xm.com.xmfunsdkdemo.ui.device.aov.presenter;

import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

import java.util.ArrayList;

import demo.xm.com.xmfunsdkdemo.ui.device.aov.listener.AOVDoubleLightSettingContract;


public class AOVDoubleLightSettingPresenter extends XMBasePresenter<DeviceManager> implements AOVDoubleLightSettingContract.IAOVDoubleLightSettingPresenter{



    private ArrayList<Integer> modeList = new ArrayList<>();
    private WhiteLightBean mWhiteLight;
    private FbExtraStateCtrlBean mFbExtraStateInfo;
    private CameraParamExBean mCameraParamEx;

    private DevConfigManager mDevConfigManager;

    private AOVDoubleLightSettingContract.IAOVDoubleLightSettingView iAOVDoubleLightSettingView;


    public AOVDoubleLightSettingPresenter(AOVDoubleLightSettingContract.IAOVDoubleLightSettingView iAOVDoubleLightSettingView){
        this.iAOVDoubleLightSettingView = iAOVDoubleLightSettingView;
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

    public void getWhiteLight() {
        iAOVDoubleLightSettingView.onShowWaitDialog();
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<WhiteLightBean>() {
            @Override
            public void onSuccess(String devId, int msgId, WhiteLightBean result) {
                iAOVDoubleLightSettingView.onHideWaitDialog();
                mWhiteLight = result;
                if (mWhiteLight != null) {
                    iAOVDoubleLightSettingView.showWorkMode(mWhiteLight.getWorkMode());
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
                    getCameraParamEx();
                    iAOVDoubleLightSettingView.showMicroFillLight(true);

                } else {
                    iAOVDoubleLightSettingView.showMicroFillLight(false);
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
                iAOVDoubleLightSettingView.showMicroFillLight(false);
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
                    iAOVDoubleLightSettingView.showStatusLed(true);

                } else {
                    iAOVDoubleLightSettingView.showStatusLed(false);
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
                iAOVDoubleLightSettingView.showStatusLed(false);
            }
        });
    }

    /**
     * 获取设备灯光支持的模式
     */
    public void getCameraDayLightModes() {
        iAOVDoubleLightSettingView.onShowWaitDialog();
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                String jsonStr = null;
                if (result instanceof String) {
                    jsonStr = (String) result;
                }else {
                    jsonStr =  JSON.toJSONString(result);
                }

                if (jsonStr != null) {
                    JSONObject parse = (JSONObject) JSONObject.parse(jsonStr);
                    JSONArray cameraDayLightModes = parse.getJSONArray("CameraDayLightModes");
                    if (cameraDayLightModes!=null){
                        for (int i = 0; i < cameraDayLightModes.size(); i++) {
                            JSONObject jsonObject = (JSONObject) cameraDayLightModes.get(i);
                            Integer value = jsonObject.getInteger("value");
                            modeList.add(value);
                        }
                    }

                }
                boolean supportNightVision = false;
                boolean supportFullColor = false;
                boolean supportDoubleLight = false;
                for (int i = 0; i < modeList.size(); i++) {
                    if (modeList.get(i) == 5) {
                        supportNightVision = true;
                    } else if (modeList.get(i) == 4) {
                        supportFullColor = true;
                    } else if (modeList.get(i) == 3) {
                        supportDoubleLight = true;
                    }
                }
                iAOVDoubleLightSettingView.showCameraDayLightModes(supportNightVision,supportFullColor,supportDoubleLight);


                getWhiteLight();

            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });

        devConfigInfo.setJsonName("CameraDayLightModes");
        devConfigInfo.setChnId(-1);
        devConfigInfo.setCmdId(1360);
        mDevConfigManager.setDevCmd(devConfigInfo);

    }

    public void saveConfig() {
        iAOVDoubleLightSettingView.onShowWaitDialog();
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

    /**
     * 保存状态灯和提示音
     */
    public void saveLampAndSoundConfig() {
        if(mFbExtraStateInfo != null){
            iAOVDoubleLightSettingView.onShowWaitDialog();
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

    public void saveCameraParamEx() {
        iAOVDoubleLightSettingView.onShowWaitDialog();
        if (mCameraParamEx != null) {
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
            devConfigInfo.setChnId(-1);
            devConfigInfo.setJsonData(HandleConfigData.getSendData(JsonConfig.CAMERA_PARAMEX, "0x01", mCameraParamEx));
            mDevConfigManager.setDevConfig(devConfigInfo);


        }
    }

    private void saveDataFail(){
        iAOVDoubleLightSettingView.onHideWaitDialog();
        Toast.makeText(iAOVDoubleLightSettingView.getActivity().getApplicationContext(), FunSDK.TS("Save_Failed"), Toast.LENGTH_SHORT).show();
    }


    private void saveDataSuccess(){
        iAOVDoubleLightSettingView.onHideWaitDialog();
        Toast.makeText(iAOVDoubleLightSettingView.getActivity().getApplicationContext(), FunSDK.TS("Save_Success"), Toast.LENGTH_SHORT).show();
    }




    public void getFbExtraStateCtrl(){
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                iAOVDoubleLightSettingView.onHideWaitDialog();
                HandleConfigData handleConfigData = new HandleConfigData();
                if (handleConfigData.getDataObj(result, FbExtraStateCtrlBean.class)) {
                    // 个别设备获取的内容仍为空，故判断
                    if (handleConfigData.getObj() instanceof FbExtraStateCtrlBean) {
                        mFbExtraStateInfo = (FbExtraStateCtrlBean) handleConfigData.getObj();
                        if(mFbExtraStateInfo!= null){
                            iAOVDoubleLightSettingView.showIndicatorLight(mFbExtraStateInfo.getIson());

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
                iAOVDoubleLightSettingView.onHideWaitDialog();
                HandleConfigData handleConfigData = new HandleConfigData();
                if (handleConfigData.getDataObj(result, CameraParamExBean.class)) {
                    mCameraParamEx = (CameraParamExBean) handleConfigData.getObj();
                    if (mCameraParamEx != null) {
                        if(mCameraParamEx.MicroFillLight != null){
                            iAOVDoubleLightSettingView.showMicroLight(mCameraParamEx.MicroFillLight);

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

    private void getDataFail(){
        iAOVDoubleLightSettingView.onHideWaitDialog();
        Toast.makeText(iAOVDoubleLightSettingView.getActivity().getApplicationContext(), FunSDK.TS("no_data"), Toast.LENGTH_SHORT).show();

    }

    public void saveWorkMode(String mode){
        if (mWhiteLight != null) {

            mWhiteLight.setWorkMode(mode);
            saveConfig();
        }
    }

    public void saveIndicatorLight(int switchState){
        if (mFbExtraStateInfo != null) {
            mFbExtraStateInfo.setIson(switchState);
            saveLampAndSoundConfig();
        }
    }

    public void saveMicroLight(int switchState){
        if(mCameraParamEx != null && mCameraParamEx.MicroFillLight!= null){
            mCameraParamEx.MicroFillLight = switchState;
            saveCameraParamEx();
        }
    }
}
