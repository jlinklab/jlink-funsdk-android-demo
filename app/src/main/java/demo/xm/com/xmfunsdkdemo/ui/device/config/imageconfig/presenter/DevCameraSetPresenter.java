package demo.xm.com.xmfunsdkdemo.ui.device.config.imageconfig.presenter;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ToastUtils;
import com.lib.sdk.bean.JsonConfig;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.utils.LogUtils;
import com.xm.activity.base.XMBasePresenter;

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
                DeviceManager.getInstance().getDevAbility(devId, new DeviceManager.OnDevManagerListener<Boolean>() {
                    @Override
                    public void onSuccess(String s, int i, Boolean isSupportSoftPhotosensitive) {
                        if (result instanceof String) {
                            iDevCameraSetView.onUpdateView((String) result, isSupportSoftPhotosensitive);
                        } else {
                            iDevCameraSetView.onUpdateView(JSON.toJSONString(result), isSupportSoftPhotosensitive);
                        }
                    }

                    @Override
                    public void onFailed(String s, int i, String s1, int i1) {
                        LogUtils.debugInfo("zzw", "获取软光敏能力失败：s=" + s + " i=" + i + " s1=" + s1 + " i1=" + i1);
                        if (result instanceof String) {
                            iDevCameraSetView.onUpdateView("0" + (String) result, false);
                        } else {
                            iDevCameraSetView.onUpdateView("0" + JSON.toJSONString(result), false);
                        }
                    }
                }, "OtherFunction", "SupportSoftPhotosensitive");

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
}

