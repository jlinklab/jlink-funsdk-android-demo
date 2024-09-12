package demo.xm.com.xmfunsdkdemo.ui.device.aov.presenter;

import android.view.View;

import com.lib.FunSDK;
import com.lib.sdk.bean.SystemFunctionBean;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.device.aov.listener.AovSettingContract;


public class AovSettingPresenter extends XMBasePresenter<DeviceManager> implements AovSettingContract.IAovSettingPresenter {

    boolean isSupportAovLight = false;
    boolean isSupportAovWorkMode = false;
    boolean isSupportAovBatteryManager = false;

    private AovSettingContract.IAovSettingView iAovSettingView;
    private DevConfigManager devConfigManager;
    
    public AovSettingPresenter(AovSettingContract.IAovSettingView iAovSettingView) {
        this.iAovSettingView = iAovSettingView;
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void setDevId(String devId) {
        devConfigManager = manager.getDevConfigManager(devId);
        super.setDevId(devId);
    }
    /**
     *检查灯类型
     */
    @Override
    public void checkSupportLightType(){
        DeviceManager.getInstance().getDevAllAbility(getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, SystemFunctionBean result) {
                if (result != null && result.OtherFunction!=null) {
                    if (result.OtherFunction.ConsumerLightMode && result.OtherFunction.SupportCameraWhiteLight) {
                        if (result.OtherFunction.SupportDoubleLightBoxCamera) {
                            iAovSettingView.onSupportDoubleLightResult();
                        } else {
                            iAovSettingView.onSupportBlackLightResult();
                        }
                    }
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



    @Override
    public void checkSupportAovAbility(){
        DeviceManager.getInstance().getDevAllAbility(getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, SystemFunctionBean result) {
                isSupportAovLight = false;
                isSupportAovWorkMode = false;
                isSupportAovBatteryManager = false;
                if (result != null && result.OtherFunction!=null) {
                    if (result.OtherFunction.ConsumerLightMode && result.OtherFunction.SupportCameraWhiteLight) {
                        isSupportAovLight = true;
                    } else {
                        isSupportAovLight = false;
                    }
                    if (result.OtherFunction.AovMode) {
                        isSupportAovWorkMode = true;
                    } else {
                        isSupportAovWorkMode = false;
                    }
                    if (result.OtherFunction.BatteryManager) {
                        isSupportAovBatteryManager = true;
                    } else {
                        isSupportAovBatteryManager = false;
                    }
                }
                iAovSettingView.showSupportAovAbility(isSupportAovLight,isSupportAovWorkMode,isSupportAovBatteryManager);

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
                isSupportAovLight = false;
                isSupportAovWorkMode = false;
                isSupportAovBatteryManager = false;
                iAovSettingView.showSupportAovAbility(isSupportAovLight,isSupportAovWorkMode,isSupportAovBatteryManager);
            }
        });

    }
}
