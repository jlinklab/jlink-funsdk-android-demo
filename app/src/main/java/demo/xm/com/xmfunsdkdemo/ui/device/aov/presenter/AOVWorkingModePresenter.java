package demo.xm.com.xmfunsdkdemo.ui.device.aov.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lib.FunSDK;
import com.lib.sdk.bean.SystemFunctionBean;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.manager.device.idr.IDRDevBatteryManager;
import com.manager.device.idr.IDRManager;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.device.aov.listener.AOVWorkingModeContract;
import demo.xm.com.xmfunsdkdemo.ui.entity.AovWorkModeBean;

public class AOVWorkingModePresenter extends XMBasePresenter<DeviceManager> implements AOVWorkingModeContract.IAOVWorkingModePresenter{


    public static final String BALANCE_MODE = "Balance";
    public static final String PERFORMANCE_MODE = "Performance";
    public static final String CUSTOM_MODE = "Custom";

    private AovWorkModeBean workbean;
    private int powerThreshold = 0;
    private int percent = 0;

    private IDRManager mIDRModel;

    private DevConfigManager mDevConfigManager;



    private AOVWorkingModeContract.IAOVWorkingModeView iAOVWorkingModeView;


    public AOVWorkingModePresenter(AOVWorkingModeContract.IAOVWorkingModeView iAOVWorkingModeView){
        this.iAOVWorkingModeView = iAOVWorkingModeView;
    }


    @Override
    public void setDevId(String devId) {
        mDevConfigManager = manager.getDevConfigManager(devId);
        super.setDevId(devId);
    }

    private IDRDevBatteryManager.OnBatteryLevelListener mOnBatteryLevelListener = new IDRDevBatteryManager.OnBatteryLevelListener() {
        @Override
        public void onBatteryLevel(int devStorageStatus, int electable, int level, int percent) {
            if (AOVWorkingModePresenter.this.percent != percent) {
                AOVWorkingModePresenter.this.percent = percent;
                if (powerThreshold != 0 && percent != 0) {
                    iAOVWorkingModeView.showTips(powerThreshold > AOVWorkingModePresenter.this.percent, powerThreshold);
                }
            }
        }
    };



    @Override
    public void getDevBattery(Context context) {
        mIDRModel = new IDRManager(context, getDevId());
        mIDRModel.reReceiveBatteryInfo(mOnBatteryLevelListener);
    }
    @Override
    public void releaseIDRModel() {
        mIDRModel.clear();
    }

    public void checkIsSupportDoubleLightBoxCamera(){
        DeviceManager.getInstance().getDevAllAbility(getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, SystemFunctionBean result) {
                if (result != null && result.OtherFunction!=null && result.OtherFunction.SupportDoubleLightBoxCamera) {
                    iAOVWorkingModeView.onSupportDoubleLightBoxCameraResult(true);
                } else {
                    iAOVWorkingModeView.onSupportDoubleLightBoxCameraResult(false);
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
                iAOVWorkingModeView.onSupportDoubleLightBoxCameraResult(false);
            }
        });
    }

    /**
     * 获取设备配置
     */
    public void getAovWorkModel() {
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                iAOVWorkingModeView.getConfigBack(true);
                try {
                    JsonObject jsonObject =
                            new Gson().fromJson(result, JsonObject.class);
                    JsonObject asJsonObject = jsonObject.getAsJsonObject("Dev.AovWorkMode");
                    workbean =
                            AovWorkModeBean.ParseAvoWorkModeByJson(new Gson().toJson(asJsonObject));
                    iAOVWorkingModeView.showWorkModel(workbean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName("Dev.AovWorkMode");
        mainConfigInfo.setChnId(-1);
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }

    private void getDataFail(){
        iAOVWorkingModeView.onHideWaitDialog();
        Toast.makeText(iAOVWorkingModeView.getActivity().getApplicationContext(), FunSDK.TS("no_data"), Toast.LENGTH_SHORT).show();

    }
    @Override
    public void getLowElectrMode() {

        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                try {
                    JSONObject jsonObject = new Gson().fromJson(result, JSONObject.class);
                    JSONObject asJsonObject = jsonObject.getJSONObject("Dev.LowElectrMode");
                    powerThreshold = asJsonObject.getIntValue("PowerThreshold");
                    if (powerThreshold != 0 && percent != 0) {
                        if (powerThreshold != 0 && percent != 0) {
                            iAOVWorkingModeView.showTips(powerThreshold > percent, powerThreshold);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName("Dev.LowElectrMode");
        mainConfigInfo.setChnId(-1);
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }

    /**
     * 保存设备配置
     */
    @Override
    public void setAovWorkModel() {
        JsonObject jsonObject = new Gson().fromJson(new Gson().toJson(workbean), JsonObject.class);
        JsonObject senObj = new JsonObject();
        senObj.addProperty("Name", "Dev.AovWorkMode");
        senObj.addProperty("SessionID", "0x02");
        senObj.add("Dev.AovWorkMode", jsonObject);
        Log.e("dzc", senObj.toString());

        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                iAOVWorkingModeView.saveSuccess(true);
                saveDataSuccess();
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iAOVWorkingModeView.saveSuccess(false);
                saveDataFail();
            }
        });
        devConfigInfo.setJsonName("Dev.AovWorkMode");
        devConfigInfo.setChnId(-1);
        devConfigInfo.setJsonData(senObj.toString());
        mDevConfigManager.setDevConfig(devConfigInfo);

    }


    private void saveDataFail(){
        iAOVWorkingModeView.onHideWaitDialog();
        Toast.makeText(iAOVWorkingModeView.getActivity().getApplicationContext(), FunSDK.TS("Save_Failed"), Toast.LENGTH_SHORT).show();
    }


    private void saveDataSuccess(){
        iAOVWorkingModeView.onHideWaitDialog();
    }



    public void checkIsSupportAovAlarmHold(){
        DeviceManager.getInstance().getDevAllAbility(getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, SystemFunctionBean result) {
                if (result != null && result.OtherFunction!=null && result.OtherFunction.AovAlarmHold) {
                    iAOVWorkingModeView.showAovInterval(true);

                } else {
                    iAOVWorkingModeView.showAovInterval(false);
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
                iAOVWorkingModeView.showAovInterval(false);
            }
        });
    }



    public AovWorkModeBean getWorkBean() {
        return workbean;
    }

    public void setPercent(int percent){
        this.percent = percent;
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }
}