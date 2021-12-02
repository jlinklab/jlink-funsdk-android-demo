package demo.xm.com.xmfunsdkdemo.ui.device.config.imageconfig.presenter;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ToastUtils;
import com.lib.SDKCONST;
import com.lib.sdk.bean.CameraParamExBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.SystemFunctionBean;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.utils.LogUtils;
import com.xm.activity.base.XMBasePresenter;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.DevConfigState;
import demo.xm.com.xmfunsdkdemo.ui.device.config.imageconfig.listener.DevCameraSetContract;

/**
 * 图像配置界面,改变图像的画质,画像字符叠加，日夜模式
 * Created by jiangping on 2018-10-23.
 */
public class DevCameraSetPresenter extends XMBasePresenter<DeviceManager> implements DevCameraSetContract.IDevCameraSetPresenter {
    private DevCameraSetContract.IDevCameraSetView iDevCameraSetView;
    private DevConfigManager devConfigManager;
    private CameraParamExBean cameraParamExBean;

    public DevCameraSetPresenter(DevCameraSetContract.IDevCameraSetView iDevCameraSetView) {
        this.iDevCameraSetView = iDevCameraSetView;
    }

    @Override
    public void setDevId(String devId) {
        devConfigManager = manager.getDevConfigManager(devId);
        super.setDevId(devId);
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void getCameraInfo() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                /**
                 * 获取设备是否支持软光敏
                 */
                DeviceManager.getInstance().getDevAllAbility(devId, new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
                    @Override
                    public void onSuccess(String devId, int operationType, SystemFunctionBean systemFunctionBean) {
                        if (result instanceof String) {
                            iDevCameraSetView.onUpdateView((String) result, systemFunctionBean.OtherFunction.SupportSoftPhotosensitive);
                        } else {
                            iDevCameraSetView.onUpdateView(JSON.toJSONString(result), systemFunctionBean.OtherFunction.SupportSoftPhotosensitive);
                        }

                        //是否支持宽动态
                        if (systemFunctionBean.OtherFunction.SupportBT) {
                            getCameraInfoEx();
                        }
                    }

                    @Override
                    public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                        if (result instanceof String) {
                            iDevCameraSetView.onUpdateView("0" + (String) result, false);
                        } else {
                            iDevCameraSetView.onUpdateView("0" + JSON.toJSONString(result), false);
                        }
                    }
                });
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                LogUtils.debugInfo("zzw", "获取图像配置数据失败：devId=" + devId + " msgId=" + msgId + " s1=" + s1 + " errorId=" + errorId);
                ToastUtils.showShort(R.string.device_setup_camera_get_failed);
                iDevCameraSetView.error(null);
            }
        });

        devConfigInfo.setJsonName(JsonConfig.CAMERA_PARAM);
        devConfigInfo.setChnId(-1);
        devConfigManager.getDevConfig(devConfigInfo);
    }

    /**
     * 获取摄像机拓展参数
     */
    private void getCameraInfoEx() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                if (result != null) {
                    HandleConfigData handleConfigData = new HandleConfigData();
                    if (handleConfigData.getDataObj(result, CameraParamExBean.class)) {
                        cameraParamExBean = (CameraParamExBean) handleConfigData.getObj();
                        if (cameraParamExBean != null) {
                            iDevCameraSetView.onWDRConfig(true, cameraParamExBean.BroadTrends.AutoGain == SDKCONST.Switch.Open);
                            return;
                        }
                    }
                }

                iDevCameraSetView.onWDRConfig(false, false);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevCameraSetView.onWDRConfig(false, false);
            }
        });

        devConfigInfo.setJsonName(JsonConfig.CAMERA_PARAMEX);
        devConfigInfo.setChnId(0);//通道Id，第一通道是0 第二通道是1 依此类推，如果查所有通道的传-1
        devConfigManager.getDevConfig(devConfigInfo);
    }

    @Override
    public void setCameraInfo(String jsonData) {
        if (jsonData == null) {
            return;
        }
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                iDevCameraSetView.onSaveResult(DevConfigState.DEV_CONFIG_UPDATE_SUCCESS);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevCameraSetView.onSaveResult(DevConfigState.DEV_CONFIG_UPDATE_FAILED);
            }
        });

        devConfigInfo.setJsonName(JsonConfig.CAMERA_PARAM);
        devConfigInfo.setChnId(0);
        devConfigInfo.setJsonData(jsonData);
        devConfigManager.setDevConfig(devConfigInfo);
    }

    @Override
    public void setWDRConfig(boolean isOpen) {
        if (cameraParamExBean == null) {
            return;
        }

        cameraParamExBean.BroadTrends.AutoGain = isOpen ? SDKCONST.Switch.Open : SDKCONST.Switch.Close;
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                iDevCameraSetView.onSetWDRConfigResult(true, 0);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevCameraSetView.onSetWDRConfigResult(false, errorId);
            }
        });

        devConfigInfo.setJsonName(JsonConfig.CAMERA_PARAMEX);
        devConfigInfo.setChnId(0);//通道Id，第一通道是0 第二通道是1 依此类推

        HandleConfigData handleConfigData = new HandleConfigData();
        String sendJson = handleConfigData.getSendData(HandleConfigData.getFullName(JsonConfig.CAMERA_PARAMEX, 0), cameraParamExBean);
        devConfigInfo.setJsonData(sendJson);//保存的Json数据
        devConfigManager.setDevConfig(devConfigInfo);
    }
}

