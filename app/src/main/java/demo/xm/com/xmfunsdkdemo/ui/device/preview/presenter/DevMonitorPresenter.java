package demo.xm.com.xmfunsdkdemo.ui.device.preview.presenter;

import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.google.gson.Gson;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.CameraParamBean;
import com.lib.sdk.bean.ElectCapacityBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.OPPTZControlBean;
import com.lib.sdk.bean.PtzCtrlInfoBean;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.SystemFunctionBean;
import com.lib.sdk.bean.WhiteLightBean;
import com.lib.sdk.bean.WifiRouteInfo;
import com.lib.sdk.bean.preset.ConfigGetPreset;
import com.lib.sdk.bean.tour.PTZTourBean;
import com.lib.sdk.bean.tour.TourBean;
import com.manager.account.code.AccountCode;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.manager.device.config.DevReportManager;
import com.manager.device.config.SerialPortsInfo;
import com.manager.device.config.preset.IPresetManager;
import com.manager.device.media.MediaManager;
import com.manager.device.media.TalkManager;
import com.manager.device.media.attribute.PlayerAttribute;
import com.manager.device.media.monitor.MonitorManager;
import com.utils.BleDistributionUtil;
import com.utils.FileUtils;
import com.video.opengl.GLSurfaceView20;
import com.video.opengl.OnPlayViewTouchListener;
import com.xm.activity.base.XMBasePresenter;
import com.xm.base.code.ErrorCodeManager;
import com.xm.ui.dialog.XMPromptDlg;
import com.xmgl.vrsoft.VRSoftDefine;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.listener.DevMonitorContract;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.listener.PresetListContract;
import demo.xm.com.xmfunsdkdemo.utils.SPUtil;

import static com.lib.EFUN_ATTR.EDA_DEV_TANSPORT_COM_WRITE;
import static com.lib.EUIMSG.DEV_CMD_EN;
import static com.lib.EUIMSG.DEV_PTZ_CONTROL;
import static com.lib.sdk.bean.JsonConfig.CAMERA_PARAM;
import static com.lib.sdk.bean.JsonConfig.WHITE_LIGHT;
import static com.manager.device.media.monitor.MonitorManager.TALK_TYPE_BROADCAST;
import static com.manager.device.media.monitor.MonitorManager.TALK_TYPE_CHN;
import static com.manager.device.media.monitor.MonitorManager.TALK_TYPE_DEV;

/**
 * 设备预览界面,可以控制播放,停止,码流切换,截图,录制,全屏,信息.
 * 保存图像视频,语音对话.设置预设点并控制方向
 * Device preview interface, you can control to play or stop video,switch code stream, screenshot, record, full screen playback,
 * save video, voice dialogue. Set the preset point and control the direction
 * Created by jiangping on 2018-10-23.
 */
public class DevMonitorPresenter extends XMBasePresenter<DeviceManager> implements DevMonitorContract.IDevMonitorPresenter,
        MediaManager.OnMediaManagerYUVListener, DevReportManager.OnDevReportListener {
    private HashMap<Integer, MonitorManager> monitorManagers;//Multi-screen display of the manager
    private DevMonitorContract.IDevMonitorView iDevMonitorView;
    /**
     * 播放状态
     * State of play
     */
    private int playState;
    /**
     * 设备配置管理类
     * Device Configuration Management class
     */
    private DevConfigManager devConfigManager;
    /**
     * 预置点管理类
     * Preset point management classes
     */
    private IPresetManager presetManager;
    /**
     * 是否要开启实时预览实时性，只有局域网模式下才支持，开启后为了确保实时性可能会出现丢帧情况
     * Whether to enable real-time preview real-time, only LAN mode is supported, in order to ensure real-time may appear frame loss situation
     */
    private boolean isRealTimeEnable;
    /**
     * 设备状态上报管理类
     * Equipment status reporting management class
     */
    private DevReportManager devReportManager;
    /**
     * 对讲变声类型：0 正常 1 男 2 女
     * Intercom voice change type: 0 normal 1 male 2 female
     */
    private int speakerType;
    /**
     * 旋转类型：0 不旋转 1 旋转90度 2 旋转180度 3 旋转270度
     * Rotation type: 0 no rotation 1 rotation 90 degrees 2 rotation 180 degrees 3 rotation 270 degrees
     */
    private int videoFlip;
    /**
     * 播放比例
     * ratio
     */
    public float videoRatio = 0;
    private MotionEvent downEvent;
    /**
     * 手动警戒是否打开
     * Is manual alert enabled
     */
    private boolean isManualAlarmOpen;
    private List<PTZTourBean> ptzTourBeans;
    private PresetListPresenter presetListPresenter;
    private List<ConfigGetPreset> configGetPresets;
    private boolean isFirstGetVideoStream = true;//打开视频后首次获取到码流数据
    private SensorChangePresenter sensorChangePresenter;
    private boolean isDelayChangeStream = false;//针对多目设备获取到镜头信息后再进行码流切换
    private MonitorManager splitMonitorManager;//被分割后的播放器

    public DevMonitorPresenter(DevMonitorContract.IDevMonitorView iDevMonitorView) {
        this.iDevMonitorView = iDevMonitorView;
        monitorManagers = new HashMap<>();
        //初始化电量上报
        devReportManager = new DevReportManager(null, SDKCONST.UploadDataType.SDK_ELECT_STATE, this);
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    /**
     * 登录设备
     */
    @Override
    public void loginDev() {// This will be called after onresume()
        if (DevDataCenter.getInstance().isLowPowerDev(getDevId())) {
            manager.loginDevByLowPower(getDevId(), new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String s, int i, Object abilityKey) {
                    initData();
                    if (iDevMonitorView != null) {
                        iDevMonitorView.onLoginResult(true, 0);
                    }
                }

                @Override
                public void onFailed(String s, int i, String s1, int errorId) {
                    if (iDevMonitorView != null) {
                        iDevMonitorView.onLoginResult(false, errorId);
                    }
                }
            });
        } else {
            manager.loginDev(getDevId(), new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String s, int i, Object abilityKey) {
                    initData();
                    if (iDevMonitorView != null) {
                        iDevMonitorView.onLoginResult(true, 0);
                    }
                }

                @Override
                public void onFailed(String s, int i, String s1, int errorId) {
                    if (iDevMonitorView != null) {
                        iDevMonitorView.onLoginResult(false, errorId);
                    }
                }
            });
        }
    }

    /**
     * 设置设备Id（序列号或IP+Port）
     *
     * @param devId
     */
    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        TransComManager.getInstance().initDevConfigManager(devId);
        devConfigManager = manager.getDevConfigManager(devId);
        presetManager = manager.createPresetManager(devId, new DevConfigManager.OnDevConfigResultListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {

            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {

            }

            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {
                if (msg.what == DEV_CMD_EN) {
                    if (msg.arg1 < 0) {
                        iDevMonitorView.onTourCtrlResult(false, ex.seq, msg.arg1);
                    } else {
                        iDevMonitorView.onTourCtrlResult(true, ex.seq, 0);
                    }
                } else if (msg.what == DEV_PTZ_CONTROL) {
                    if (msg.arg1 == OPPTZControlBean.PTZ_TOUR_END_RSP_ID) {
                        iDevMonitorView.onTourEndResult();
                    }
                }
            }
        });

        getIrCutInfo();

        presetListPresenter = new PresetListPresenter(new PresetListContract.IPresetListView() {
            @Override
            public void onGetPresetListResult(List<ConfigGetPreset> presetList) {
                DevMonitorPresenter.this.configGetPresets = presetList;
                DevConfigManager devConfigManager = manager.getDevConfigManager(getDevId());
                if (devConfigManager != null) {
                    DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
                        @Override
                        public void onSuccess(String devId, int operationType, String jsonData) {
                            HandleConfigData handleConfigData = new HandleConfigData();
                            if (handleConfigData.getDataObj(jsonData, PTZTourBean.class)) {
                                ptzTourBeans = (List<PTZTourBean>) handleConfigData.getObj();
                            }


                            if (iDevMonitorView != null) {
                                if (ptzTourBeans != null && ptzTourBeans.size() > getChnId()) {
                                    PTZTourBean ptzTourBean = ptzTourBeans.get(getChnId());
                                    iDevMonitorView.onShowTour(ptzTourBean.Tour, 0);
                                } else {
                                    iDevMonitorView.onShowTour(null, 0);
                                }
                            }
                        }

                        @Override
                        public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                            if (iDevMonitorView != null) {
                                iDevMonitorView.onShowTour(null, errorId);
                            }
                        }
                    });

                    devConfigInfo.setJsonName(PTZTourBean.JSON_NAME);
                    devConfigInfo.setChnId(getChnId());
                    devConfigManager.getDevConfig(devConfigInfo);
                }
            }

            @Override
            public void onDeletePresetResult(boolean isSuccess, int errorId) {

            }

            @Override
            public void onModifyPresetNameResult(boolean isSuccess, int errorId) {

            }
        });

        presetListPresenter.setDevId(getDevId());
    }

    public List<TourBean> getTourBeans(int chnId) {
        return ptzTourBeans != null && ptzTourBeans.size() > chnId ? ptzTourBeans.get(chnId).Tour : null;
    }

    private void initData() {
        //获取能力集
        manager.getInstance().getDevAllAbility(getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            @Override
            public void onSuccess(String s, int i, SystemFunctionBean result) {
                if (result != null && result.NetServerFunction.WifiRouteSignalLevel) {
                    //获取WiFi信号,先要判断能力集是否支持
                    getDevWiFiSignalLevel();
                }

                iDevMonitorView.onGetDevAbilityResult(result, 0);
            }

            @Override
            public void onFailed(String s, int i, String s1, int errorId) {
                iDevMonitorView.onGetDevAbilityResult(null, errorId);
            }
        });

        XMDevInfo devInfo = DevDataCenter.getInstance().getDevInfo(getDevId());
        //如果是低功耗设备则支持电量状态上报
        if (devInfo != null && isIDR(devInfo.getDevType())) {
            devReportManager.startReceive(getDevId());
        }
    }

    /**
     * 获取WiFi信号
     */
    private void getDevWiFiSignalLevel() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String s, int i, String jsonData) {
                if (jsonData != null) {
                    HandleConfigData<WifiRouteInfo> handleConfigData = new HandleConfigData<>();
                    if (handleConfigData.getDataObj(jsonData, WifiRouteInfo.class)) {
                        WifiRouteInfo wifiRouteInfo = handleConfigData.getObj();
                        if (iDevMonitorView != null) {
                            iDevMonitorView.onWiFiSignalLevelResult(wifiRouteInfo);
                        }
                    }
                }
            }

            @Override
            public void onFailed(String s, int i, String s1, int i1) {

            }
        });


        devConfigInfo.setCmdId(1020);
        devConfigInfo.setJsonName(JsonConfig.WIFI_ROUTE_INFO);
        devConfigInfo.setChnId(-1);
        devConfigManager.setDevCmd(devConfigInfo);
    }

    /**
     * 判断哪些设备类型属于低功耗的
     * Determine which device types belong to low power consumption
     *
     * @param devType 设备类型 devType Device type
     * @return
     */
    private boolean isIDR(int devType) {
        switch (devType) {
            case SDKCONST.DEVICE_TYPE.IDR:
            case SDKCONST.DEVICE_TYPE.PEEPHOLE:
            case SDKCONST.DEVICE_TYPE.DEV_CZ_IDR:
            case SDKCONST.DEVICE_TYPE.EE_DEV_DOORLOCK:
            case SDKCONST.DEVICE_TYPE.EE_DEV_DOORLOCK_V2:
            case SDKCONST.DEVICE_TYPE.EE_DEV_LOW_POWER:
            case SDKCONST.DEVICE_TYPE.EE_DEV_DOORLOCK_PEEPHOLE:
                return true;
            default:
                return false;
        }
    }

    /**
     * 设置指哪看哪的命令方法
     *
     * @param json
     */
    public void setPointPtz(String json) {

        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String s, int i, Object o) {
                Toast.makeText(iDevMonitorView.getContext(), R.string.libfunsdk_operation_success, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailed(String s, int i, String s1, int i1) {
                Toast.makeText(iDevMonitorView.getContext(), R.string.libfunsdk_operation_failed, Toast.LENGTH_LONG).show();

            }
        });
        devConfigInfo.setChnId(0);
        devConfigInfo.setTimeOut(8000);//设置超时时间
        devConfigInfo.setJsonName("OPPtzLocate");
        devConfigInfo.setJsonData(json);
        devConfigInfo.setCmdId(3032);
        devConfigManager.setDevCmd(devConfigInfo);

    }

    /**
     * 初始化实时预览
     * Initialize a live preview
     *
     * @param chnId
     * @param viewGroup
     */
    @Override
    public void initMonitor(int chnId, ViewGroup viewGroup) {
        initMonitor(chnId, viewGroup, true);
    }

    @Override
    public void initMonitor(int chnId, ViewGroup viewGroup, boolean isNeedCorrectFishEye) {
        MonitorManager mediaManager;
        if (!monitorManagers.containsKey(chnId)) {
            //创建播放器 Create a player
            mediaManager = manager.createMonitorPlayer(viewGroup, getDevId());
            //是否开启硬解码，默认是关闭的 Is hardware decoding enabled? By default, it's disabled.
            mediaManager.setHardDecode(false);
            //通道ID Channel ID
            mediaManager.setChnId(chnId);
            //设置是否满屏显示 Set whether to display in full screen
            mediaManager.setVideoFullScreen(true);
            mediaManager.setNeedCorrectFishEye(isNeedCorrectFishEye);
            monitorManagers.put(chnId, mediaManager);

            // 是否要保存原始媒体数据
            boolean isSaveOriginalMediaData = SPUtil.getInstance(iDevMonitorView.getContext()).getSettingParam("IS_SAVE_ORIGINAL_MEDIA_DATA", false);
            if (isSaveOriginalMediaData) {
                mediaManager.initStorePlayingMediaData(SDKDemoApplication.PATH_ORIGINAL_MEDIA_DATA);
            }

            // 是否要保存对讲数据
            boolean isSaveTalkData = SPUtil.getInstance(iDevMonitorView.getContext()).getSettingParam("IS_SAVE_TALK_DATA", false);
            if (isSaveTalkData) {
                mediaManager.initStoreTalkData(SDKDemoApplication.PATH_ORIGINAL_TALK_DATA);
            }
        } else {
            mediaManager = monitorManagers.get(chnId);
        }

        //设置设备列表缩略图保存路径，必须要在视频出图之前调用,传入的路径建议使用私有目录，如果使用外部存储的话，在Android 11及以上系统上需要MANAGE_EXTERNAL_STORAGE这个权限
//        mediaManager.setSaveThumbnailPath(SDKDemoApplication.PATH_PHOTO_TEMP);
        //设置媒体播放监听（包括播放状态回调、实时码流、时间戳回调等）
        mediaManager.setOnMediaManagerListener(this);
        mediaManager.setOnFrameInfoListener(iDevMonitorView);

        mediaManager.setOnPlayViewTouchListener(new OnPlayViewTouchListener() {
            @Override
            public void onScale(float fingerScale, float imgScale, View view, MotionEvent motionEvent) {
                if (iDevMonitorView != null) {
                    iDevMonitorView.onVideoScaleResult(imgScale);
                }

                // 处理多通道设备，窗口切换功能
                // Handle multi-channel devices and window switching functionality
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    downEvent = motionEvent;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    // 如果是多指操作的 就不支持窗口切换，防止触控冲突
                    // If multi-finger operation, window switching is not supported to prevent touch conflicts
                    if (motionEvent.getPointerCount() > 1) {
                        downEvent = null;
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (downEvent != null) {
                        float x = motionEvent.getX() - downEvent.getX();
                        float y = motionEvent.getY() - downEvent.getY();
                        int chnId = view.getId();
                        if (Math.abs(x) > Math.abs(y)) {
                            if (Math.abs(x) > 100) {
                                if (x > 0) {
                                    // 向右滑动
                                    // Swipe right
                                    if (--chnId < 0) {
                                        chnId = 0;
                                    }
                                } else {
                                    //向左滑动
                                    // Swipe left
                                    if (++chnId >= monitorManagers.size()) {
                                        chnId = monitorManagers.size() - 1;
                                    }
                                }

                                //是否为多窗口设备，并且当前是单窗口没有缩放显示，才支持切换窗口
                                // Whether it is a multi-window device, and the current display is single window without zooming, then window switching is supported
                                if (monitorManagers.size() > 1 && view.getId() != chnId && iDevMonitorView.isSingleWnd() && !isVideoScale()) {
                                    iDevMonitorView.changeChannel(chnId);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onTrans(float v, float v1) {
            }

            @Override
            public void onBoundary(boolean b, boolean b1) {
            }
        });
    }

    /**
     * 开始实时预览
     * Start live preview
     *
     * @param chnId 通道号
     */
    @Override
    public void startMonitor(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            //设置码流类型（主码流、副码流）
            // Set stream type (main stream, sub stream)
            mediaManager.setStreamType(SDKCONST.StreamType.Main);
            //是否要开启实时预览实时性，只有局域网模式下才支持，开启后为了确保实时性可能会出现丢帧情况
            // Whether to enable real-time preview timeliness, only supported in LAN mode, enabling may cause frame loss to ensure real-time performance
            mediaManager.setRealTimeEnable(isRealTimeEnable);
            //开始实时预览
            // Start real-time preview
            mediaManager.startMonitor();//Start Monitoring
        }
    }


    /**
     * 开始实时预览
     * Start live preview
     *
     * @param chnId    通道号
     * @param username 设备登录名
     * @param pwd      设备密码
     */
    @Override
    public void startMonitor(int chnId, String username, String pwd) {
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager == null) {
            return;
        }

        // 判断是否为低功耗设备（需要唤醒设备，如果是深度休眠的设备，需要到设备端手动唤醒）
        // Check if it's a low-power device (needs to wake up the device, if it's in deep sleep, it needs to be manually awakened on the device side)
        if (DevDataCenter.getInstance().isLowPowerDev(getDevId())) {
            //如果是低功耗设备，先要唤醒设备再登录设备
            // If it's a low-power device, wake up the device first and then log in to the device
            manager.loginDevByLowPower(getDevId(), new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String devId, int operationType, Object result) {
                    startMonitor(chnId);
                }

                @Override
                public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                    if (iDevMonitorView != null) {
                        iDevMonitorView.onPlayState(chnId, 0, errorId);
                    }
                }
            });
        } else {
            //登录设备
            // Log in to the device
            manager.loginDev(getDevId(), new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String devId, int operationType, Object result) {
                    startMonitor(chnId);
                }

                @Override
                public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                    if (iDevMonitorView != null) {
                        iDevMonitorView.onPlayState(chnId, 0, errorId);
                    }
                }
            });
        }

    }

    /**
     * 停止实时预览
     * Stop live preview
     *
     * @param chnId
     */
    @Override
    public void stopMonitor(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            mediaManager.stopPlay();
        }
    }

    /**
     * 销毁实时预览
     * Destroy real-time preview
     *
     * @param chnId
     */
    @Override
    public void destroyMonitor(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            mediaManager.destroyPlay();
            mediaManager = null;
        }

        monitorManagers.remove(chnId);
    }

    @Override
    public void destroyAllMonitor() {
        for (Map.Entry map : monitorManagers.entrySet()) {
            MonitorManager mediaManager = (MonitorManager) map.getValue();
            if (mediaManager != null) {
                mediaManager.destroyPlay();
                mediaManager = null;
            }
        }

        monitorManagers.clear();
    }

    /**
     * 获取当前播放状态
     * Gets the current playback state
     *
     * @param chnId
     * @return
     */
    @Override
    public int getPlayState(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return 0;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            return playState;
        }

        return 0;
    }

    /**
     * 实时预览抓图
     * Real-time preview capture
     *
     * @param chnId 通道号
     */
    @Override
    public void capture(int chnId) {//Capture the image
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            // 传的是抓图保存的路径地址
            // The path address for saving the captured image is passed
            mediaManager.capture(SDKDemoApplication.PATH_PHOTO);
        }

    }

    /**
     * 开始实时预览视频剪切
     * Start preview video clipping in real time
     *
     * @param chnId 通道号
     */
    @Override
    public void startRecord(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null && !mediaManager.isRecord()) {
            // 传的是视频剪切保存的路径地址
            // The path address for saving the video clip is passed
            mediaManager.startRecord(SDKDemoApplication.PATH_VIDEO);
        }

    }

    /**
     * 停止实时预览剪切
     *
     * @param chnId 通道号
     */
    @Override
    public void stopRecord(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null && mediaManager.isRecord()) {
            mediaManager.stopRecord();
        }
    }

    /**
     * 判断当前是否正在视频剪切中
     *
     * @param chnId 通道号
     * @return
     */
    @Override
    public boolean isRecording(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return false;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            return mediaManager.isRecord();
        }

        return false;
    }

    /**
     * 打开预览伴音（音频）
     *
     * @param chnId 通道号
     */
    @Override
    public void openVoice(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            //在对讲的情况下，开启的是对讲音量并且要关闭视频伴音
            if (mediaManager.isTalking()) {
                mediaManager.getTalkManager().doubleDirectionSound(SDKCONST.Switch.Open);
                mediaManager.closeVoiceBySound();
            } else {
                mediaManager.openVoiceBySound();
            }
        }
    }

    /**
     * 关闭视频伴音（音频）
     *
     * @param chnId 通道号
     */
    @Override
    public void closeVoice(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            //在对讲的情况下，关闭的是对讲音量
            if (mediaManager.isTalking()) {
                mediaManager.getTalkManager().doubleDirectionSound(SDKCONST.Switch.Close);
            } else {
                mediaManager.closeVoiceBySound();
            }
        }
    }

    /**
     * 开始单向对讲并开始说话
     * Start the one-way intercom and start talking
     *
     * @param chnId           通道号
     * @param isTalkBroadcast 针对设备对讲还是通道对讲，默认是设备对讲
     */
    @Override
    public void startSingleIntercomAndSpeak(int chnId, boolean isTalkBroadcast) {
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            //设置对讲类型，是针对设备对讲还是通道对讲，IPC设备默认选择设备对讲，NVR设备可以选择设备或者通道前端对讲
            // Set the intercom type, whether it is for device intercom or channel intercom. IPC devices default to device intercom, while NVR devices can choose device or channel front-end intercom
            if (isTalkBroadcast) {
                mediaManager.setTalkType(TALK_TYPE_BROADCAST);
            } else {
                mediaManager.setTalkType(monitorManagers.size() > 1 ? TALK_TYPE_CHN : TALK_TYPE_DEV);
            }
            //开启单向对讲
            // Start one-way intercom
            mediaManager.startTalkByHalfDuplex(iDevMonitorView.getContext());
            //设置对讲变声类型
            // Set the intercom voice changing type
            TalkManager talkManager = mediaManager.getTalkManager();
            talkManager.setSpeakerType(speakerType);
        }

    }

    /**
     * 停止单向说话，开始接听
     * Stop talking one way and start listening
     *
     * @param chnId
     */
    @Override
    public void stopSingleIntercomAndHear(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            mediaManager.stopTalkByHalfDuplex();
        }
    }

    /**
     * 开始双向对讲（同时说话和接听设备端的声音）
     * Start two-way talkback (talking and listening at the same time)
     *
     * @param chnId           通道号
     * @param isTalkBroadcast 是否广播对讲(针对多通道设备)
     */
    @Override
    public void startDoubleIntercom(int chnId, boolean isTalkBroadcast) {
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            //设置对讲类型，是针对设备对讲还是通道对讲，IPC设备默认选择设备对讲，NVR设备可以选择设备或者通道前端对讲
            // Set the intercom type, whether it is for device intercom or channel intercom. IPC devices default to device intercom, while NVR devices can choose device or channel front-end intercom
            if (isTalkBroadcast) {
                mediaManager.setTalkType(TALK_TYPE_BROADCAST);
            } else {
                mediaManager.setTalkType(monitorManagers.size() > 1 ? TALK_TYPE_CHN : TALK_TYPE_DEV);
            }

            //开始双向对讲
            // Start two-way intercom
            mediaManager.startTalkByDoubleDirection(iDevMonitorView.getContext(), true);
            //设置对讲变声类型
            // Set the intercom voice changing type
            TalkManager talkManager = mediaManager.getTalkManager();
            talkManager.setSpeakerType(speakerType);
        }
    }

    /**
     * 当前是否处于对讲状态
     * Whether the current intercom state
     *
     * @param chnId 通道号
     * @return
     */
    @Override
    public boolean isTalking(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return false;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            return mediaManager.isTalking();
        }

        return false;
    }

    /**
     * 停止对讲
     * Stop the intercom
     *
     * @param chnId 通道号
     */
    @Override
    public void stopIntercom(int chnId) { //Pause the intercom when the dialog box disappears
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            mediaManager.destroyTalk();
        }
    }

    /**
     * 切换码流
     * Switched bitstream
     *
     * @param chnId 通道号
     * @return
     */
    @Override
    public int changeStream(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return SDKCONST.StreamType.Extra;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            mediaManager.setStreamType(mediaManager.getStreamType() == SDKCONST.StreamType.Extra ? SDKCONST.StreamType.Main : SDKCONST.StreamType.Extra);
            mediaManager.stopPlay();
            mediaManager.startMonitor();
            return mediaManager.getStreamType();
        }

        return SDKCONST.StreamType.Extra;
    }

    /**
     * 获取码流类型
     *
     * @param chnId 通道号
     * @return
     */
    @Override
    public int getStreamType(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return SDKCONST.StreamType.Main;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            return mediaManager.getStreamType();
        }

        return SDKCONST.StreamType.Main;
    }

    /**
     * 设置视频是否满屏显示
     * Sets whether the video should be full screen
     *
     * @param isFullScreen
     */
    @Override
    public void setVideoFullScreen(int chnId, boolean isFullScreen) {
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            mediaManager.setVideoFullScreen(isFullScreen);
        }
    }

    /**
     * 当前是否为全屏
     *
     * @param chnId
     * @return
     */
    @Override
    public boolean isVideoFullScreen(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return false;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            return mediaManager.isVideoFullScreen();
        }

        return false;
    }

    /**
     * 从设备端抓图并且保存到设备端
     *
     * @param chnId
     */
    @Override
    public void capturePicFromDevAndSave(int chnId) {
        manager.captureFromDevAndSaveToDev(getDevId(), chnId, new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                Toast.makeText(iDevMonitorView.getContext(), R.string.remote_capture_s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                Toast.makeText(iDevMonitorView.getContext(), R.string.remote_capture_f, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void capturePicFromDevAndToApp(int chnId) {
        String saveFilePath = SDKDemoApplication.PATH_PHOTO_TEMP + File.separator + "capture" + System.currentTimeMillis() + ".jpg";
        manager.captureFromDevAndToApp(getDevId(), chnId, saveFilePath, new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                ImageView imageView = new ImageView(iDevMonitorView.getContext());
                imageView.setImageBitmap(ImageUtils.getBitmap(saveFilePath));
                XMPromptDlg.onShow(iDevMonitorView.getContext(), imageView);
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                Toast.makeText(iDevMonitorView.getContext(), R.string.remote_capture_f, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 获取巡航线
     */
    @Override
    public void getTour(int chnId) {
        presetListPresenter.updatePresetList();
    }

    @Override
    public void addTour(int chnId, int presetId) {
        if (presetManager != null) {
            presetManager.controlTour(chnId, presetId, 0, OPPTZControlBean.ADD_TOUR, 3, 1);
        }
    }

    @Override
    public void deleteTour(int chnId, int presetId) {
        if (presetManager != null) {
            presetManager.controlTour(chnId, presetId, 0, OPPTZControlBean.DELETE_TOUR, 3, 1);
        }
    }

    @Override
    public void startTour(int chnId) {
        if (presetManager != null) {
            presetManager.controlTour(chnId, 0, 0, OPPTZControlBean.START_TOUR, 3, 1);
        }
    }

    @Override
    public void stopTour(int chnId) {
        if (presetManager != null) {
            presetManager.controlTour(chnId, 0, 0, OPPTZControlBean.STOP_TOUR, 3, 1);
        }
    }

    /**
     * 添加预置点
     *
     * @param chnId    通道号
     * @param presetId 预置点
     */
    @Override
    public void addPreset(int chnId, int presetId) {//Preset point Settings
        if (presetManager != null) {
            presetManager.addPreset(chnId, presetId);
        }
    }

    /**
     * 跳转到预置点位置
     *
     * @param chnId    通道号
     * @param presetId 预置点号
     */
    @Override
    public void turnToPreset(int chnId, int presetId) {//Calls to preset points
        if (presetManager != null) {
            presetManager.turnPreset(chnId, presetId);
        }
    }

    /**
     * 是否要开启实时预览实时性，只有局域网模式下才支持，开启后为了确保实时性可能会出现丢帧情况
     *
     * @param enable
     */
    @Override
    public void setRealTimeEnable(boolean enable) {
        this.isRealTimeEnable = enable;
    }

    /**
     * 是否只支持水平云台操作
     *
     * @return
     */
    @Override
    public boolean isOnlyHorizontal() {
        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(getDevId());
        if (xmDevInfo != null) {
            SystemFunctionBean systemFunctionBean = xmDevInfo.getSystemFunctionBean();
            if (systemFunctionBean != null) {
                return systemFunctionBean.OtherFunction.SupportPTZDirectionHorizontalControl;
            }
        }

        return false;
    }

    /**
     * 是否只支持上下云台操作
     *
     * @return
     */
    @Override
    public boolean isOnlyVertically() {
        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(getDevId());
        if (xmDevInfo != null) {
            SystemFunctionBean systemFunctionBean = xmDevInfo.getSystemFunctionBean();
            if (systemFunctionBean != null) {
                return systemFunctionBean.OtherFunction.SupportPTZDirectionVerticalControl;
            }
        }

        return false;
    }

    /**
     * 是否支持灯光配置
     * Whether the lighting configuration is supported
     *
     * @return
     */
    @Override
    public boolean isSupportLightCtrl() {//Whether to support light control
        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(getDevId());
        if (xmDevInfo != null) {
            SystemFunctionBean systemFunctionBean = xmDevInfo.getSystemFunctionBean();
            if (systemFunctionBean != null) {
                boolean isSupport = systemFunctionBean.OtherFunction.SupportCameraWhiteLight || systemFunctionBean.OtherFunction.SupportDoubleLightBoxCamera || systemFunctionBean.OtherFunction.SupportDoubleLightBulb || systemFunctionBean.OtherFunction.SupportMusicLightBulb;
                return isSupport;
            }
        }

        return false;
    }

    /**
     * 获取灯光控制配置
     * Get the lighting control configuration
     */
    @Override
    public void getLightCtrlConfig() {//Control the lights
        DevConfigManager devConfigManager = manager.getDevConfigManager(getDevId());
        if (devConfigManager != null) {
            DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<WhiteLightBean>() {
                @Override
                public void onSuccess(String s, int i, WhiteLightBean whiteLightBean) {
                    if (iDevMonitorView != null) {
                        iDevMonitorView.onLightCtrlResult(JSON.toJSONString(whiteLightBean));
                    }
                }

                @Override
                public void onFailed(String s, int i, String s1, int i1) {

                }
            });

            devConfigInfo.setJsonName(WHITE_LIGHT);
            devConfigInfo.setChnId(-1);
            devConfigManager.getDevConfig(devConfigInfo);
        }
    }

    /**
     * 设置变声
     * Variable tone setting
     *
     * @param chnId       通道号
     * @param speakerType 变声类型
     */
    @Override
    public void setSpeakerType(int chnId, int speakerType) {//Set the type of sound change
        this.speakerType = speakerType;
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            TalkManager talkManager = mediaManager.getTalkManager();
            if (talkManager != null) {
                talkManager.setSpeakerType(speakerType);
            }
        }
    }

    /**
     * 设置视频翻转
     * Set the flip of the video
     *
     * @param chnId 通道号
     */
    @Override
    public void setVideoFlip(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            videoFlip++;
            // 旋转类型：0 不旋转 1 旋转90度 2 旋转180度 3 旋转270度
            // Rotation types: 0 - No rotation, 1 - Rotate 90 degrees, 2 - Rotate 180 degrees, 3 - Rotate 270 degrees
            videoFlip %= 3;
            mediaManager.setVideoFlip(videoFlip);
        }
    }

    /**
     * 喂狗
     * Number of servings to feed
     *
     * @param portions 喂食食物份数
     */
    @Override
    public void feedTheDog(int portions) {
        String hexData = "55AA0006000803020004000000" + String.format("%02x", portions);
        System.out.println("hexData:" + hexData);
        String checkCode = BleDistributionUtil.createCheckCode(hexData);
        byte[] resultData = ConvertUtils.hexString2Bytes(hexData + checkCode);
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String s, int i, Object value) {
                if (iDevMonitorView != null) {
                    if (value instanceof byte[]) {
                        iDevMonitorView.onFeedResult(true, (byte[]) value, "");
                    } else {
                        iDevMonitorView.onFeedResult(true, null, "");
                    }
                }
            }

            @Override
            public void onFailed(String s, int errorId, String s1, int i1) {
                if (iDevMonitorView != null) {
                    iDevMonitorView.onFeedResult(false,
                            null,
                            ErrorCodeManager.parseErrorCode(errorId, iDevMonitorView.getContext().getString(R.string.libfunsdk_operation_failed), AccountCode.class));
                }
            }
        });

        //开启串口
        // Open serial ports
        TransComManager.getInstance().openSerialPorts(SDKCONST.SDK_CommTypes.SDK_COMM_TYPES_RS232);
        SerialPortsInfo serialPortsInfo = new SerialPortsInfo();
        serialPortsInfo.setOperationType(EDA_DEV_TANSPORT_COM_WRITE);
        serialPortsInfo.setSerialPortsType(SDKCONST.SDK_CommTypes.SDK_COMM_TYPES_RS232);
        serialPortsInfo.setSerialPortsData(resultData);
        devConfigInfo.setSerialPortsInfo(serialPortsInfo);
        //发送串口数据
        // Send serial port data
        TransComManager.getInstance().sendSerialPortsData(devConfigInfo);
    }

    /**
     * 喂鱼
     *
     * @param portions
     */
    @Override
    public void feedTheFish(int portions) {
        String hexData = "55AA01100004" + String.format("%02x", portions) + "000000";
        System.out.println("hexData:" + hexData);
        String checkCode = BleDistributionUtil.createCheckCode(hexData);
        byte[] resultData = ConvertUtils.hexString2Bytes(hexData + checkCode);
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String s, int i, Object value) {
                if (iDevMonitorView != null) {
                    if (value instanceof byte[]) {
                        iDevMonitorView.onFeedResult(true, (byte[]) value, "");
                    } else {
                        iDevMonitorView.onFeedResult(true, null, "");
                    }
                }
            }

            @Override
            public void onFailed(String s, int errorId, String s1, int i1) {
                if (iDevMonitorView != null) {
                    iDevMonitorView.onFeedResult(false,
                            null,
                            ErrorCodeManager.parseErrorCode(errorId, iDevMonitorView.getContext().getString(R.string.libfunsdk_operation_failed), AccountCode.class));
                }
            }
        });

        //开启串口
        // Open serial ports
        TransComManager.getInstance().openSerialPorts(SDKCONST.SDK_CommTypes.SDK_COMM_TYPES_RS232);
        SerialPortsInfo serialPortsInfo = new SerialPortsInfo();
        serialPortsInfo.setOperationType(EDA_DEV_TANSPORT_COM_WRITE);
        serialPortsInfo.setSerialPortsType(SDKCONST.SDK_CommTypes.SDK_COMM_TYPES_RS232);
        serialPortsInfo.setSerialPortsData(resultData);
        devConfigInfo.setSerialPortsInfo(serialPortsInfo);
        //发送串口数据
        // Send serial port data
        TransComManager.getInstance().sendSerialPortsData(devConfigInfo);
    }

    /**
     * 切换双目拼接画面
     *
     * @param chnId 通道号
     * @param mode  模式
     */
    @Override
    public void changeTwoLensesScreen(int chnId, int mode) {
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager monitorManager = monitorManagers.get(chnId);
        monitorManager.setTwoLensesScreen(mode);
    }

    /**
     * 获取双目拼接模式
     * Get stereo stitching mode
     *
     * @param chnId 通道号
     *              Channel ID
     * @return
     */
    @Override
    public int getTwoLensesScreen(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return VRSoftDefine.XMTwoLensesScreen.ScreenDouble;
        }

        MonitorManager monitorManager = monitorManagers.get(chnId);
        return monitorManager.getTwoLensesScreen();
    }

    private CameraParamBean cameraParamBean;

    public CameraParamBean getCameraParamBean() {
        return cameraParamBean;
    }

    @Override
    public void getIrCutInfo() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<CameraParamBean>() {
            @Override
            public void onSuccess(String s, int i, CameraParamBean cameraParamBean) {
                DevMonitorPresenter.this.cameraParamBean = cameraParamBean;
            }

            @Override
            public void onFailed(String s, int i, String s1, int i1) {

            }
        });

        devConfigInfo.setJsonName(CAMERA_PARAM);
        devConfigInfo.setChnId(0);

        devConfigManager.getDevConfig(devConfigInfo);
    }

    @Override
    public void setIrCutInfo(boolean isOpen, DeviceManager.OnDevManagerListener onDevManagerListener) {
        if (DevMonitorPresenter.this.cameraParamBean == null) {
            return;
        }

        DevMonitorPresenter.this.cameraParamBean.IrcutSwap = isOpen ? SDKCONST.Switch.Open : SDKCONST.Switch.Close;
        DevConfigInfo devConfigInfo = DevConfigInfo.create(onDevManagerListener);

        devConfigInfo.setJsonData(HandleConfigData.getSendData(HandleConfigData.getFullName(CAMERA_PARAM, 0), "0x08", DevMonitorPresenter.this.cameraParamBean));
        devConfigInfo.setChnId(0);
        devConfigInfo.setJsonName(CAMERA_PARAM);

        devConfigManager.setDevConfig(devConfigInfo);
    }

    /**
     * 设备云台控制
     * Equipment head control
     *
     * @param chnId       通道号
     *                    Channel ID
     * @param nPTZCommand 云台命令
     *                    Pan-tilt command
     * @param speed       运动步长 *默认值4，范围1 ~ 8, 设备程序会把该步长转换为相应的速度值，1的速度最慢，8的速度最快。推荐水平三档：2、4、8 垂直三档：1、2、4
     *                    "Motion step size *Default value 4, range 1 ~ 8. The device program will convert this step size to the corresponding speed value, where 1 is the slowest speed and 8 is the fastest. Recommended three levels for horizontal: 2, 4, 8; vertical: 1, 2, 4."
     * @param bStop       是否停止
     *                    "Is it stopped
     */
    @Override
    public void devicePTZControl(int chnId, int nPTZCommand, int speed, boolean bStop) {//Cloud console, operation button callback
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager mediaManager = monitorManagers.get(chnId);
        if (mediaManager != null) {
            PtzCtrlInfoBean ptzCtrlInfoBean = new PtzCtrlInfoBean();
            ptzCtrlInfoBean.setDevId(mediaManager.getDevId());
            ptzCtrlInfoBean.setPtzCommandId(nPTZCommand);
            //是否停止云台转动，如果是false的话，云台会一直转动下去
            //"Whether to stop the pan/tilt movement. If it's false, the pan/tilt will continue to move indefinitely."
            ptzCtrlInfoBean.setStop(bStop);
            //通道号
            //Channel ID
            ptzCtrlInfoBean.setChnId(mediaManager.getChnId());
            //云台操作速度（步长）
            //Panning/tilting speed (step size)
            ptzCtrlInfoBean.setSpeed(speed);
            manager.devPTZControl(ptzCtrlInfoBean, null);
        }
    }

    /**
     * YUV数据回调
     * YUV data callbacks
     *
     * @param attribute 播放信息 Play Info
     * @param width     视频宽 Video Width
     * @param height    视频高 Video Height
     * @param data      yuv数据 YUV Data
     */
    @Override
    public void onResultYUVData(PlayerAttribute attribute, final int width, final int height, final byte[] data) {
        // 该回调的前置条件：mediaManager.setPlayMode(MEDIA_DATA_TYPE_YUV_NOT_DISPLAY);
        // The preconditions of this callback:mediaManager.setPlayMode(MEDIA_DATA_TYPE_YUV_NOT_DISPLAY);
    }

    /**
     * 播放状态回调
     * Play status callback
     *
     * @param attribute
     * @param state
     */
    @Override
    public void onMediaPlayState(PlayerAttribute attribute, int state) {//mediaplay status
        this.playState = state;
        if (iDevMonitorView != null) {
            iDevMonitorView.onPlayState(attribute.getChnnel(), state, 0);
        }
    }


    /**
     * 播放失败回调
     * Play the failure callback
     *
     * @param attribute
     * @param msgId
     * @param errorId
     */
    @Override
    public void onFailed(PlayerAttribute attribute, int msgId, int errorId) {
        if (iDevMonitorView != null) {
            iDevMonitorView.onPlayState(attribute.getChnnel(), attribute.getPlayState(), errorId);
        }
    }

    /**
     * 码流、时间戳回调
     * Bitstream, timestamp callback
     *
     * @param attribute
     * @param isShowTime
     * @param time
     * @param rate
     */
    @Override
    public void onShowRateAndTime(PlayerAttribute attribute, boolean isShowTime, String time, long rate) {
        if (iDevMonitorView != null && attribute.getChnnel() == getChnId()) {
            iDevMonitorView.onVideoRateResult(FileUtils.FormetFileSize(rate) + "/S");
        }
    }

    /**
     * 视频码流数据缓冲结束，开始显示画面
     *
     * @param attribute
     * @param ex
     */
    @Override
    public void onVideoBufferEnd(PlayerAttribute attribute, MsgContent ex) {
        videoRatio = attribute.getVideoScale();
        iDevMonitorView.onVideoBufferEnd(attribute, ex);
    }

    @Override
    public void onPlayStateClick(View view) {

    }

    /**
     * 设备主动上报回调
     * The device actively reports the callback
     *
     * @param devId     设备序列号
     * @param stateType 状态类型
     * @param stateData 状态数据
     */
    @Override
    public void onReport(String devId, String stateType, String stateData) {
        if (!StringUtils.isStringNULL(stateData)) {
            JSONObject jsonObject = JSON.parseObject(stateData);
            if (jsonObject.containsKey("Dev.ElectCapacity")) {
                ElectCapacityBean electCapacityBean = jsonObject.getObject("Dev.ElectCapacity", ElectCapacityBean.class);
                if (iDevMonitorView != null) {
                    iDevMonitorView.onElectCapacityResult(electCapacityBean);
                }
            }
        }
    }

    /**
     * 当前视频是否缩放了
     *
     * @return
     */
    @Override
    public boolean isVideoScale() {
        MonitorManager monitorManager = monitorManagers.get(getChnId());
        if (monitorManager != null) {
            SurfaceView surfaceView = monitorManager.getSurfaceView();
            if (surfaceView instanceof GLSurfaceView20) {
                return ((GLSurfaceView20) surfaceView).bitmapScale != 1;
            }
        }


        return false;
    }

    /**
     * 释放主动上报资源
     * Release proactive reporting resources
     */
    public void release() {
        if (devReportManager != null) {
            devReportManager.release();
        }

        TransComManager.getInstance().release();
    }

    public void setPlayViewTouchable(int chnId, boolean touchable) {
        if (!monitorManagers.containsKey(chnId)) {
            return;
        }

        MonitorManager monitorManager = monitorManagers.get(chnId);
        //多目设备预览设置成false，单画面预览设置成true
        // For multi-camera device preview, set to false; for single-screen preview, set to true.
        monitorManager.setTouchable(touchable);
    }

    /**
     * 恢复出厂设置
     * Restore factory settings
     */
    @Override
    public void factoryReset(DeviceManager.OnDevManagerListener onDevManagerListener) {
        manager.resetDevConfig(getDevId(), onDevManagerListener);
    }

    /**
     * 切换手动警戒开启/关闭
     * Toggle manual alarm on/off
     *
     * @return 返回的是开启还是关闭 Whether it's turned on or off
     */
    @Override
    public boolean changeManualAlarmSwitch() {
        isManualAlarmOpen = !isManualAlarmOpen;
        //将开启/关闭 bool状态转成16进制字符串
        // Convert the bool state of opening/closing to hexadecimal string.
        String manualAlarmSwitch = isManualAlarmOpen ? "0x00000001" : "0x00000000";
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                //result是json数据
                //result is JSON data
                iDevMonitorView.onChangeManualAlarmResult(true, 0);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                //获取失败，通过errorId分析具体原因
                //Failed to retrieve, analyze the specific reason through errorId
                iDevMonitorView.onChangeManualAlarmResult(false, errorId);
            }
        });

        devConfigInfo.setJsonName("OPRemoteCtrl");
        devConfigInfo.setChnId(-1);
        devConfigInfo.setCmdId(4000);

        HashMap<String, Object> sendMap = new HashMap<>();
        HashMap<String, Object> remoteCtrlMap = new HashMap<>();
        remoteCtrlMap.put("Type", "ManuIntelAlarm");
        remoteCtrlMap.put("msg", manualAlarmSwitch);
        remoteCtrlMap.put("P1", "0x00000000");
        remoteCtrlMap.put("P2", "0x00000000");

        sendMap.put("Name", "OPRemoteCtrl");
        sendMap.put("OPRemoteCtrl", remoteCtrlMap);
        sendMap.put("SessionID", "0x0000001b");

        //设置保存的Json数据
        //Set the saved JSON data.
        devConfigInfo.setJsonData(new Gson().toJson(sendMap));
        devConfigManager.setDevCmd(devConfigInfo);

        return isManualAlarmOpen;
    }

    @Override
    public void ptzCalibration() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                iDevMonitorView.onPtzCalibrationResult(true, 0);
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                iDevMonitorView.onPtzCalibrationResult(false, errorId);
            }
        });

        devConfigInfo.setChnId(-1);
        devConfigInfo.setJsonName(JsonConfig.OP_PTZ_AUTO_ADJUST);
        devConfigInfo.setCmdId(1450);
        devConfigInfo.setTimeOut(60000);//超时1分钟
        devConfigManager.setDevCmd(devConfigInfo);
    }

    @Override
    public MonitorManager getCurSelMonitorManager(int chnId) {
        if (!monitorManagers.containsKey(chnId)) {
            return null;
        }

        return monitorManagers.get(chnId);
    }

    @Override
    public MonitorManager getMonitorManager(String devId) {
        for (Map.Entry map : monitorManagers.entrySet()) {
            MonitorManager monitorManager = (MonitorManager) map.getValue();
            if (StringUtils.contrast(monitorManager.getDevId(), devId)) {
                return monitorManager;
            }
        }

        return null;
    }

    /**
     * 分割画面，将当前画面分割成上下两部分
     *
     * @param playView 播放布局
     */
    @Override
    public void splitScreen(ViewGroup playView) {
        //创建新的播放器来显示分割出来的画面
        initMonitor(1, playView);
        splitMonitorManager = monitorManagers.get(1);
        splitMonitorManager.setDevId(getDevId());

        //分割画面并将主画面的播放句柄传给新画面的播放器
        MonitorManager monitorManager = monitorManagers.get(0);


        //将画面的下半部分分割给新的播放器,splitType 画面分割类型 0--》不分割 1--》上下分割 2--》左右分割
        splitMonitorManager.splitScreen(monitorManager.getPlayHandle(), createSplitJson(0.0f, 1.0f, 1.0f, 0.5f), 1);
        PlayerAttribute playerAttribute = splitMonitorManager.getPlayerAttribute();
        playerAttribute.setVideoWidth(monitorManager.getPlayerAttribute().getVideoWidth());
        playerAttribute.setVideoHeight(monitorManager.getPlayerAttribute().getVideoHeight());
        playerAttribute.setVideoScale(monitorManager.getVideoScale());
        splitMonitorManager.setVideoFullScreen(false);
        //将画面的上半部分留给老的播放器,splitType 画面分割类型 0--》不分割 1--》上下分割 2--》左右分割
        monitorManager.splitScreen(monitorManager.getPlayHandle(), createSplitJson(0.0f, 1.0f, 0.5f, 0.0f), 1);
        monitorManager.setVideoFullScreen(false);
    }

    /**
     * 合并画面，将分割后的画面合并成一个画面
     */
    @Override
    public void mergeScreen() {
        MonitorManager monitorManager = monitorManagers.get(0);
        //将新播放器的画面合并到老的播放器，并将新播发器释放
        splitMonitorManager.mergeScreen(null);
        splitMonitorManager = null;
        monitorManagers.remove(1);
        //将新播放器的画面合并到老的播放器，需要将完整的画面数据传给老播放器
        monitorManager.mergeScreen(createSplitJson(0.0f, 1.0f, 1.0f, 0.0f));
    }

    @Override
    public void changePlayView(ViewGroup[] playViews) {
        MonitorManager monitorManager = getCurSelMonitorManager(0);
        if (monitorManager != null) {
            monitorManager.changePlayView(playViews[0],createSplitJson(0.0f, 1.0f, 0.5f, 0.0f));
        }

        if (splitMonitorManager != null) {
            splitMonitorManager.changePlayView(playViews[1],createSplitJson(0.0f, 1.0f, 1.0f, 0.5f));
        }
    }

    /**
     * 创建包含play_view的JSON字符串
     *
     * @param left   左边界
     * @param right  右边界
     * @param top    上边界
     * @param bottom 下边界
     * @return 生成的JSON字符串
     */
    public static String createSplitJson(float left, float right, float top, float bottom) {
        // 创建Gson实例
        Gson gson = new Gson();

        // 创建coord_vertices的Map
        Map<String, Float> coordVerticesMap = new HashMap<>();
        coordVerticesMap.put("left", left);
        coordVerticesMap.put("right", right);
        coordVerticesMap.put("top", top);
        coordVerticesMap.put("bottom", bottom);

        // 创建play_view的Map
        Map<String, Object> playViewContentMap = new HashMap<>();
        playViewContentMap.put("render_enable", true);
        playViewContentMap.put("coord_vertices", coordVerticesMap);

        // 创建外部Map
        Map<String, Object> playViewMap = new HashMap<>();
        playViewMap.put("play_view", playViewContentMap);

        // 返回JSON字符串
        return gson.toJson(playViewMap);
    }
}

