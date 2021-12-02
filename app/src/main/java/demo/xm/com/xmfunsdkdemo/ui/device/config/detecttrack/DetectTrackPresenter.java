package demo.xm.com.xmfunsdkdemo.ui.device.config.detecttrack;

import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.lib.MsgContent;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.PtzCtrlInfoBean;
import com.manager.base.BaseManager;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.manager.device.config.preset.PresetManager;
import com.manager.device.media.monitor.MonitorManager;
import com.xm.activity.base.XMBasePresenter;

import java.util.HashMap;

public class DetectTrackPresenter extends XMBasePresenter<DeviceManager> implements DetectTrackContract.IDetectTrackPresenter {
    private DevConfigManager devConfigManager;
    private HashMap<String, Object> resultMap;
    private DetectTrackContract.IDetectTrackView iDetectTrackView;
    private PresetManager presetManager;

    public DetectTrackPresenter(DetectTrackContract.IDetectTrackView iDetectTrackView) {
        this.iDetectTrackView = iDetectTrackView;
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        devConfigManager = manager.getDevConfigManager(devId);
        presetManager = new PresetManager(getDevId(), new DevConfigManager.OnDevConfigResultListener() {
            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {

            }

            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                iDetectTrackView.onSetWatchPresetResult(true,0);
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                iDetectTrackView.onSetWatchPresetResult(false,errorId);
            }
        });
    }

    @Override
    public void getDetectTrack() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                //result是json数据
                if (result != null) {
                    resultMap = new Gson().fromJson(result, HashMap.class);
                    if (iDetectTrackView != null) {
                        iDetectTrackView.onGetDetectTrackResult(true, (LinkedTreeMap<String, Object>) resultMap.get("Detect.DetectTrack"), 0);
                    }
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                //获取失败，通过errorId分析具体原因
                if (iDetectTrackView != null) {
                    iDetectTrackView.onGetDetectTrackResult(false, null, errorId);
                }
            }
        });

        devConfigInfo.setJsonName(JsonConfig.CFG_DETECT_TRACK);
        devConfigInfo.setChnId(-1);
        devConfigManager.getDevConfig(devConfigInfo);
    }

    @Override
    public void setDetectTrack() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                //result是json数据
                if (iDetectTrackView != null) {
                    iDetectTrackView.onSetDetectRackResult(true, 0);
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                //获取失败，通过errorId分析具体原因
                if (iDetectTrackView != null) {
                    iDetectTrackView.onSetDetectRackResult(false, errorId);
                }
            }
        });

        devConfigInfo.setJsonName(JsonConfig.CFG_DETECT_TRACK);
        devConfigInfo.setChnId(-1);
        devConfigInfo.setJsonData(new Gson().toJson(resultMap));//设置保存的Json数据
        devConfigManager.setDevConfig(devConfigInfo);
    }

    @Override
    public void setWatchPreset() {
        //设置守望位置，对应的预置点默认是100
        //Set the lookout position, with the default preset point being 100.
        presetManager.addPreset(getChnId(), 100);
    }

    @Override
    public void devicePTZControl(int chnId, int nPTZCommand, int speed, boolean bStop) {
        if ( manager != null) {
            PtzCtrlInfoBean ptzCtrlInfoBean = new PtzCtrlInfoBean();
            ptzCtrlInfoBean.setDevId(getDevId());
            ptzCtrlInfoBean.setPtzCommandId(nPTZCommand);
            //是否停止云台转动，如果是false的话，云台会一直转动下去
            //"Whether to stop the pan/tilt movement. If it's false, the pan/tilt will continue to move indefinitely."
            ptzCtrlInfoBean.setStop(bStop);
            //通道号
            //Channel ID
            ptzCtrlInfoBean.setChnId(chnId);
            //云台操作速度（步长）
            //Panning/tilting speed (step size)
            ptzCtrlInfoBean.setSpeed(speed);
            manager.devPTZControl(ptzCtrlInfoBean, null);
        }
    }
}
