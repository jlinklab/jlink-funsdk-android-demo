package demo.xm.com.xmfunsdkdemo.ui.device.preview.listener;

import android.content.Context;
import android.view.ViewGroup;

import com.lib.MsgContent;
import com.lib.sdk.bean.ElectCapacityBean;
import com.lib.sdk.bean.SystemFunctionBean;
import com.lib.sdk.bean.WifiRouteInfo;
import com.lib.sdk.bean.tour.TourBean;
import com.manager.device.DeviceManager;
import com.manager.device.media.MediaManager;
import com.manager.device.media.attribute.PlayerAttribute;
import com.manager.device.media.monitor.MonitorManager;
import com.xm.linke.face.FaceFeature;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.bean.music.MusicCtrlBean;

/**
 * 设备预览界面,可以控制播放,停止,码流切换,截图,录制,全屏,信息.
 * 保存图像视频,语音对话.设置预设点并控制方向
 * Device preview interface, you can control to play or stop video,switch code stream, screenshot, record, full screen playback,
 * save video, voice dialogue. Set the preset point and control the direction
 * Created by jiangping on 2018-10-23.
 */
public class DevMonitorContract {
    public interface IDevMonitorView extends MediaManager.OnFrameInfoListener {
        void onLoginResult(boolean isSuccess, int errorId);

        /**
         * 获取设备能力集结果回调
         *
         * @param systemFunctionBean 能力集
         * @param errorId            错误码
         */
        void onGetDevAbilityResult(SystemFunctionBean systemFunctionBean, int errorId);

        void onPlayState(int chnId, int state, int errorId);

        void onVideoBufferEnd(PlayerAttribute attribute, MsgContent ex);

        void onUpdateFaceFrameView(FaceFeature[] faceFeatures, int width, int height);

        void onLightCtrlResult(String jsonData);

        /**
         * 电量状态
         * Battery status
         *
         * @param electCapacityBean
         */
        void onElectCapacityResult(ElectCapacityBean electCapacityBean);

        /**
         * WiFi信号
         * WiFi signal
         *
         * @param wifiRouteInfo
         */
        void onWiFiSignalLevelResult(WifiRouteInfo wifiRouteInfo);

        /**
         * 实时码流回调
         * the callback of Real-time bitstream
         *
         * @param rate
         */
        void onVideoRateResult(String rate);

        /**
         * 喂食结果回调
         * the callback of Feed  results
         *
         * @param isSuccess 喂食是否成功
         * @param values
         * @param errorMsg
         */
        void onFeedResult(boolean isSuccess, byte[] values, String errorMsg);

        void changeChannel(int chnId);

        /**
         * 视频缩放结果回调
         *
         * @param imgScale
         */
        void onVideoScaleResult(float imgScale);

        boolean isSingleWnd();

        Context getContext();

        /**
         * 切换手动警戒结果返回
         * Switch Manual Alarm Result Return
         *
         * @param isSuccess
         * @param errorId
         */
        void onChangeManualAlarmResult(boolean isSuccess, int errorId);

        /**
         * 云台校正结果回调
         * Gimbal calibration result callback
         *
         * @param isSuccess 是否成功 Indicates if the calibration was successful
         * @param errorId   错误码 Error code
         */
        void onPtzCalibrationResult(boolean isSuccess, int errorId);

        void onShowTour(List<TourBean> tourBeans, int errorId);

        void onTourCtrlResult(boolean isSuccess, int tourCmd, int errorId);

        /**
         * 巡航结束回调
         */
        void onTourEndResult();

        void onInitTalkResult();

        void onShowWaitDialog();
        void onHideWaitDialog();



        /**
         * 显示音乐播放器弹窗
         * Show music player popup
         *
         * @param musicCtrlBean
         */
        void showMusicPop(MusicCtrlBean musicCtrlBean);
    }

    public interface IDevMonitorPresenter {
        /**
         * 登录设备
         * Login device
         */
        void loginDev();

        /**
         * 初始化实时预览
         * Initialize a live preview
         *
         * @param viewGroup
         */
        void initMonitor(int chnId, ViewGroup viewGroup);

        /**
         * 初始化实时预览
         * Initialize a live preview
         *
         * @param viewGroup
         * @param isNeedCorrectFishEye 视频信息帧中携带了需要矫正的逻辑，是否需要进行矫正处理
         */
        void initMonitor(int chnId, ViewGroup viewGroup, boolean isNeedCorrectFishEye);

        /**
         * 开启实时预览
         * Enable live preview
         */
        void startMonitor(int chnId);

        /**
         * 开启实时预览
         * Enable live preview
         *
         * @param username 设备登录名
         * @param pwd      设备密码
         */
        void startMonitor(int chnId, String username, String pwd);

        /**
         * 停止实时预览
         * Stop live preview
         */
        void stopMonitor(int chnId);

        /**
         * 销毁实时预览
         * Destruction Live preview
         */
        void destroyMonitor(int chnId);

        /**
         * 销毁所有实时预览
         */
        void destroyAllMonitor();

        /**
         * 获取播放状态
         * Get playback status
         *
         * @return
         */
        int getPlayState(int chnId);

        /**
         * 抓图
         * Capture picture
         */
        void capture(int chnId);

        /**
         * 开始视频剪切
         * Start video clipping
         */
        void startRecord(int chnId);

        /**
         * 停止视频剪切
         * Stop video clipping
         */
        void stopRecord(int chnId);

        /**
         * 当前是否处于视频剪切中
         * Whether the video is currently being clipped
         *
         * @return
         */
        boolean isRecording(int chnId);

        /**
         * 打开音频
         * Turn on audio
         */
        void openVoice(int chnId);

        /**
         * 关闭音频
         * Turn off audio
         */
        void closeVoice(int chnId);

        /**
         * 初始化对讲
         * @param context
         * @param chnId
         */
        void initTalk(Context context,int chnId);
        /**
         * 开始单向对讲
         * Start the one-way intercom
         *
         * @param chnId          通道号
         * @param isTalkAboutChn 针对设备对讲还是通道对讲，默认是设备对讲
         */
        void startSingleIntercomAndSpeak(int chnId, boolean isTalkAboutChn);

        /**
         * 停止单向对讲说话开始听
         * Stop talking one way and start listening
         */
        void stopSingleIntercomAndHear(int chnId);

        /**
         * 开始双向对讲
         * Start two-way intercom
         *
         * @param chnId
         * @param isTalkAboutChn 针对设备对讲还是通道对讲，默认是设备对讲
         */
        void startDoubleIntercom(int chnId, boolean isTalkAboutChn);

        /**
         * 当前是否处于对讲
         * Whether the current intercom is on
         *
         * @param chnId
         * @return
         */
        boolean isTalking(int chnId);

        /**
         * 停止对讲
         * Stop intercom
         */
        void stopIntercom(int chnId);

        /**
         * 切换主副码流
         * Switch between primary and secondary streams
         *
         * @return 返回改变后的码流类型
         */
        int changeStream(int chnId);

        /**
         * 获取当前播放的码流类型
         * Gets the stream type currently being played
         *
         * @return
         */
        int getStreamType(int chnId);

        /**
         * 设置单个视频是否满屏显示
         * Sets whether a single video should be displayed full screen
         *
         * @param isFullScreen
         */
        void setVideoFullScreen(int chnId, boolean isFullScreen);

        boolean isVideoFullScreen(int chnId);

        /**
         * 设备端抓图并保存到设备端本地存储
         * The device captures the image and saves it to the local storage of the device
         */
        void capturePicFromDevAndSave(int chnId);

        /**
         * 设备端抓图并回传给APP，图片不保存到设备端
         * Capture an image on the device and return it to the app (the image is not saved on the device).
         */
        void capturePicFromDevAndToApp(int chnId);

        /**
         * 设备云台控制
         * Equipment head control
         *
         * @param chnId       通道号
         * @param nPTZCommand 云台命令
         * @param speed       运动步长 *默认值4，范围1 ~ 8, 设备程序会把该步长转换为相应的速度值，1的速度最慢，8的速度最快。推荐水平三档：2、4、8 垂直三档：1、2、4
         * @param bStop       是否停止
         */
        void devicePTZControl(int chnId, int nPTZCommand, int speed, boolean bStop);

        /**
         * 获取巡航线
         *
         * @param chnId 通道号
         */
        void getTour(int chnId);

        /**
         * 添加巡航点
         *
         * @param chnId
         * @param presetId
         */
        void addTour(int chnId, int presetId);

        /**
         * 删除巡航点
         *
         * @param chnId
         * @param presetId
         */
        void deleteTour(int chnId, int presetId);

        /**
         * 开始巡航
         *
         * @param chnId
         */
        void startTour(int chnId);

        /**
         * 停止巡航
         *
         * @param chnId
         */
        void stopTour(int chnId);

        void addPreset(int chnId, int presetId);

        void turnToPreset(int chnId, int presetID);

        /**
         * 是否要开启实时预览实时性，只有局域网模式下才支持，开启后为了确保实时性可能会出现丢帧情况
         * Whether to enable real-time preview real-time, only LAN mode is supported,
         * in order to ensure real-time may appear frame loss situation
         *
         * @param enable
         */
        void setRealTimeEnable(boolean enable);

        /**
         * 只支持水平
         * Horizontal display only
         *
         * @return
         */
        boolean isOnlyHorizontal();

        /**
         * 支持垂直
         * Vertical display support
         *
         * @return
         */
        boolean isOnlyVertically();

        /**
         * 是否支持灯光控制
         * Whether lighting control is supported
         *
         * @return
         */
        boolean isSupportLightCtrl();

        /**
         * 获取灯光控制配置
         * Get the lighting control configuration
         */
        void getLightCtrlConfig();

        /**
         * 设置变声类型
         * Set the sound change type
         *
         * @param speakerType
         */
        void setSpeakerType(int chnId, int speakerType);

        /**
         * 视频旋转
         * Video rotation
         *
         * @param chnId 通道号
         * @param
         */
        void setVideoFlip(int chnId);

        /**
         * 喂狗
         * Pet feeding
         *
         * @param portions 食物份数（Number of servings）
         */
        void feedTheDog(int portions);

        /**
         * @param portions
         */
        void feedTheFish(int portions);

        /**
         * 切换双目模式
         *
         * @param mode
         */
        void changeTwoLensesScreen(int chnId, int mode);

        /**
         * 获取双目屏幕模式
         *
         * @return
         */
        int getTwoLensesScreen(int chnId);


        /**
         * 获取irCut配置
         */
        void getIrCutInfo();

        /**
         * 设置irCut反序
         *
         * @param isOpen
         */
        void setIrCutInfo(boolean isOpen, DeviceManager.OnDevManagerListener onDevManagerListener);

        /**
         * 当前视频是否缩放了
         *
         * @return
         */
        boolean isVideoScale();

        /**
         * 恢复出厂设置
         */
        void factoryReset(DeviceManager.OnDevManagerListener onDevManagerListener);

        /**
         * 切换手动警戒开启/关闭
         */
        boolean changeManualAlarmSwitch();

        /**
         * 云台校正
         */
        void ptzCalibration();

        /**
         * 获取当前选中的播放器
         *
         * @param chnId
         * @return
         */
        MonitorManager getCurSelMonitorManager(int chnId);

        MonitorManager getMonitorManager(String devId);

        /**
         * 分割画面
         *
         * @param playView 播放布局
         */
        void splitScreen(ViewGroup playView);

        /**
         * 合并画面
         */
        void mergeScreen();

        /**
         * 更改播放布局
         *
         * @param playViewOne 第一个播放布局
         * @param playViewTwo 第二个播放布局
         */
        void changePlayView(ViewGroup playViewOne, ViewGroup playViewTwo);

        /**
         * 画中画切换
         */
        void swapPlayHandle();
    }
}

