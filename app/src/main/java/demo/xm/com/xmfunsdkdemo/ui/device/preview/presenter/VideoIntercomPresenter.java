package demo.xm.com.xmfunsdkdemo.ui.device.preview.presenter;

import static com.constant.SDKLogConstant.APP_VIDEO_ENCODE;
import static com.lib.EUIMSG.DEV_START_AV_TALK;
import static com.lib.SDKCONST.EEncoderPreSet.E_ENCODER_PRESET_SUPERFAST;

import android.content.Context;
import android.os.Message;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.constant.DeviceConstant;
import com.lib.EFUN_ERROR;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.decode.DecoderPramBean;
import com.lib.sdk.bean.encode.EncodeAudioBean;
import com.lib.sdk.bean.encode.EncodeCameraBean;
import com.lib.sdk.bean.encode.EncodeVideoBean;
import com.lib.sdk.struct.SDK_FishEyeFrame;
import com.manager.base.BaseManager;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.manager.device.media.MediaManager;
import com.manager.device.media.attribute.PlayerAttribute;
import com.manager.device.media.encode.VideoEncodeManager;
import com.manager.device.media.monitor.MonitorManager;
import com.utils.LogUtils;
import com.xm.activity.base.XMBasePresenter;
import com.xm.base.code.ErrorCodeManager;

import demo.xm.com.xmfunsdkdemo.ui.device.preview.listener.VideoIntercomContract;

public class VideoIntercomPresenter extends XMBasePresenter implements VideoIntercomContract.IVideoIntercomPresenter,
        MediaManager.OnFrameInfoListener, MediaManager.OnMediaManagerListener {
    private VideoEncodeManager videoEncodeManager;

    /**
     * 手机摄像头是否要竖屏显示
     */
    private boolean isSupportPortraitCamera = true;

    private DecoderPramBean decoderPramBean;

    private EncodeCameraBean encodeCameraBean;

    protected DevConfigManager devConfigManager;

    protected MonitorManager monitorManager;

    private VideoIntercomContract.IVideoIntercomView iVideoIntercomView;

    public VideoIntercomPresenter(VideoIntercomContract.IVideoIntercomView iVideoIntercomView) {
        this.iVideoIntercomView = iVideoIntercomView;
    }

    @Override
    protected BaseManager getManager() {
        return null;
    }

    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        devConfigManager = DevConfigManager.create(devId);
    }


    private void startVideoTalk(Context context, TextureView textureView, int cameraLensType, boolean isAudioCall) {
        if (videoEncodeManager == null) {
            videoEncodeManager = new VideoEncodeManager(context, new VideoEncodeManager.OnVideoEncodeListener() {
                @Override
                public void onCameraDataResult(byte[] bytes, int i, int i1) {

                }

                @Override
                public void onStartCameraResult(boolean isSuccess, int videoWidth, int videoHeight, int fps) {
                    if (iVideoIntercomView != null) {
                        if (isSuccess) {
                            if (videoWidth != 0) {
                                iVideoIntercomView.startCameraVideoResult(true, videoHeight, videoWidth, null);
                            }
                        }
                    }
                }

                @Override
                public void onStopCameraResult(boolean isSuccess) {
                    if (iVideoIntercomView != null) {
                        //sdk返回关闭的话，一般是打开失败/媒体链接断开时/主动关闭的时候，这三个时候实际上都做了逻辑处理了，所以就不需要主动挂断退出
                        iVideoIntercomView.stopCameraVideoResult(isSuccess, false);
                    }
                }

                @Override
                public void onCameraCloseResult() {
                    if (iVideoIntercomView != null) {
                        iVideoIntercomView.closeCameraResult();
                    }
                }

                @Override
                public void onCameraOpenResult() {
                    if (iVideoIntercomView != null) {
                        iVideoIntercomView.openCameraResult();
                    }
                }

                @Override
                public int OnFunSDKResult(Message message, MsgContent msgContent) {
                    if (message.what == DEV_START_AV_TALK) {
                        if (message.arg1 == EFUN_ERROR.EE_DEV_NO_ENABLE) {
                            //如果返回的是 设备功能未启用，直接返回上层弹窗
                            LogUtils.debugInfo(APP_VIDEO_ENCODE, "try start video talk failed,error reason: " + message.arg1);
                            if (iVideoIntercomView != null) {
                                iVideoIntercomView.onVideoTalkFunctionNoEnable();
                            }
                        } else if (message.arg1 < 0) {
                            LogUtils.debugInfo(APP_VIDEO_ENCODE, "尝试开启音视频失败");
                            if (iVideoIntercomView != null) {
                                iVideoIntercomView.startCameraVideoResult(false, 0, 0, ErrorCodeManager.getSDKStrErrorByNO(message.arg1));
                            }
                        }
                    }
                    return 0;
                }
            }, getDevId());
        }
        encodeCameraBean = new EncodeCameraBean();
        EncodeVideoBean encodeVideoBean = new EncodeVideoBean();
        EncodeAudioBean encodeAudioBean = new EncodeAudioBean();
        if (decoderPramBean != null) {

            if (decoderPramBean.Video != null) {
                if (decoderPramBean.Video.get(0).Res != null && !decoderPramBean.Video.get(0).Res.isEmpty()) {
                    int videoWidth = decoderPramBean.Video.get(0).Res.get(0).W;
                    int videoHeight = decoderPramBean.Video.get(0).Res.get(0).H;
                    if (videoHeight <= videoWidth) {
                        isSupportPortraitCamera = false;
                    }
                    videoEncodeManager.initCameraSize(videoWidth, videoHeight);
                    encodeVideoBean.setFps(decoderPramBean.Video.get(0).Res.get(0).FPS);
                    encodeVideoBean.setSrcwidth(videoWidth);
                    encodeVideoBean.setSrcheight(videoHeight);
                    encodeVideoBean.setDstwidth(videoWidth);
                    encodeVideoBean.setDstheight(videoHeight);
                }

                if (decoderPramBean.Video.get(0).Enc != null) {
                    encodeVideoBean.setEncodetype(decoderPramBean.Video.get(0).Enc);
                }
            }
            if (decoderPramBean.Audio != null) {
                //音频编码类型
                if (decoderPramBean.Audio.get(0).Enc != null) {
                    encodeAudioBean.setEncodetype(decoderPramBean.Audio.get(0).Enc);
                }

                //音频格式
                if (decoderPramBean.Audio.get(0).SB != null && !decoderPramBean.Audio.get(0).SB.isEmpty()) {
                    encodeAudioBean.setBit(decoderPramBean.Audio.get(0).SB.get(0));
                }
                //音频采样率
                if (decoderPramBean.Audio.get(0).SR != null && !decoderPramBean.Audio.get(0).SR.isEmpty()) {
                    encodeAudioBean.setSampleRate(decoderPramBean.Audio.get(0).SR.get(0));
                }
            }
        }
        //编码器的预设配置:值越大编码速度越慢，图片质量越高,详见枚举EEncoderPreSet
        encodeVideoBean.setPreset(E_ENCODER_PRESET_SUPERFAST);

        encodeCameraBean.setVideoinfo(encodeVideoBean);
        encodeCameraBean.setAudioinfo(encodeAudioBean);

        videoEncodeManager.startVideoTalk(textureView, encodeCameraBean, cameraLensType, isSupportPortraitCamera, isAudioCall);

    }

    /**
     * 初始化音视频对讲
     *
     * @param context
     * @param textureView    摄像头采集视频显示布局
     * @param cameraLensType 摄像头镜头 0：前置 1：后置
     * @param isAudioCall    是否音频对讲
     */
    public void initVideoChat(Context context, TextureView textureView, int cameraLensType, boolean isAudioCall) {

        if (isAudioCall) {
            //如果是音频对讲，此时视频解码参数不需要去获取，直接就开始音视频对讲即可
            startVideoTalk(context, textureView, cameraLensType, true);
            return;
        }

        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String jsonData) {
                HandleConfigData handleConfigData = new HandleConfigData();
                if (handleConfigData.getDataObj(jsonData, DecoderPramBean.class)) {
                    decoderPramBean = (DecoderPramBean) handleConfigData.getObj();
                }
                if (iVideoIntercomView != null) {
                    startVideoTalk(context, textureView, cameraLensType, false);
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                LogUtils.debugInfo(APP_VIDEO_ENCODE, "获取DecoderPram失败,使用默认分辨率开启音视频对讲");
                if (iVideoIntercomView != null) {
                    startVideoTalk(context, textureView, cameraLensType, false);
                }
            }
        });

        devConfigInfo.setCmdId(1360);
        devConfigInfo.setJsonName("DecoderPram");
        if (devConfigManager != null) {
            devConfigManager.setDevCmd(devConfigInfo);
        }
    }

    public void openCamera() {
        if (videoEncodeManager != null) {
            videoEncodeManager.openCamera();
            videoEncodeManager.controlDevAvTalk(videoEncodeManager.createControlCommand(SDKCONST.AVTalkControl.kAVTalkCtrlVideoContinue), 0);
        }
    }

    public void closeCamera() {
        if (videoEncodeManager != null) {
            videoEncodeManager.closeCamera();
            videoEncodeManager.controlDevAvTalk(videoEncodeManager.createControlCommand(SDKCONST.AVTalkControl.kAVTalkCtrlVideoPause), 0);
        }
    }

    public void openVoice() {
        if (monitorManager != null) {
            LogUtils.debugInfo("APP_TALK", "打开视频伴音");
            monitorManager.openVoiceBySound();
        }
    }

    public void closeTalkSound() {
        if (videoEncodeManager != null) {
            videoEncodeManager.stopAudioRecord();
        }
    }

    public void startTalkSound() {
        if (videoEncodeManager != null) {
            videoEncodeManager.startAudioRecord();
        }
    }

    @Override
    public void initMonitor(ViewGroup playView) {
        monitorManager = DeviceManager.getInstance().createMonitorPlayer(playView, getDevId());
        monitorManager.setVideoFullScreen(false);
        monitorManager.setOnFrameInfoListener(this);
        monitorManager.setOnMediaManagerListener(this);
    }

    @Override
    public void startMonitor() {
        if (monitorManager != null) {
            monitorManager.startMonitor();
        }
    }

    @Override
    public void stopMonitor() {
        if (monitorManager != null) {
            monitorManager.stopPlay();
        }
    }

    @Override
    public void destroyMonitor() {
        if (monitorManager != null) {
            monitorManager.destroyPlay();
        }
    }

    @Override
    public void release() {
        if (videoEncodeManager != null) {
            videoEncodeManager.release();
        }
        DeviceManager.getInstance().logoutDev(getDevId());
    }

    @Override
    public void onFrameInfo(PlayerAttribute playerAttribute, SDK_FishEyeFrame sdk_fishEyeFrame) {

    }

    @Override
    public void onMediaPlayState(PlayerAttribute playerAttribute, int i) {

    }

    @Override
    public void onFailed(PlayerAttribute playerAttribute, int i, int i1) {

    }

    @Override
    public void onShowRateAndTime(PlayerAttribute playerAttribute, boolean b, String s, long l) {

    }

    @Override
    public void onVideoBufferEnd(PlayerAttribute playerAttribute, MsgContent msgContent) {

    }

    @Override
    public void onPlayStateClick(View view) {

    }

    public void changeCameraLensType(int cameraType) {
        if (videoEncodeManager != null) {
            videoEncodeManager.changeCameraLensType(cameraType);
        }
    }

    public void closeVoice() {
        if (monitorManager != null) {
            LogUtils.debugInfo("APP_TALK", "关闭视频伴音");
            monitorManager.closeVoiceBySound();

        }
    }
}

