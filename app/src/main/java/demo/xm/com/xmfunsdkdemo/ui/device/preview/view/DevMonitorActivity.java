package demo.xm.com.xmfunsdkdemo.ui.device.preview.view;

import static com.manager.db.Define.LOGIN_NONE;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_PlAY;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_SAVE_PIC_FILE_S;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_SAVE_RECORD_FILE_S;
import static com.manager.device.media.audio.XMAudioManager.SPEAKER_TYPE_MAN;
import static com.manager.device.media.audio.XMAudioManager.SPEAKER_TYPE_NORMAL;
import static com.manager.device.media.audio.XMAudioManager.SPEAKER_TYPE_WOMAN;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ToastUtils;
import com.lib.EFUN_ERROR;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.ElectCapacityBean;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.SystemFunctionBean;
import com.lib.sdk.bean.WifiRouteInfo;
import com.lib.sdk.bean.tour.TourBean;
import com.manager.ScreenOrientationManager;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.manager.device.config.PwdErrorManager;
import com.manager.device.config.preset.PresetManager;
import com.manager.device.idr.IDRManager;
import com.utils.XUtils;
import com.xm.linke.face.FaceFeature;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.manager.AutoHideManager;
import com.xm.ui.media.MultiWinLayout;
import com.xm.ui.widget.BtnColorBK;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.RippleButton;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.dialog.EditDialog;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.ptzview.PtzView;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.activity.DeviceConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.view.ActivityGuideDeviceLanAlarm;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.view.DoubleLightActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.view.DoubleLightBoxActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.view.GardenDoubleLightActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.view.MusicLightActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.view.WhiteLightActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.cameralink.view.CameraLinkSetActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.detecttrack.DetectTrackActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.simpleconfig.view.DevSimpleConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.picture.view.DevPictureActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.listener.DevMonitorContract;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.presenter.DevMonitorPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.record.view.DevRecordActivity;
import demo.xm.com.xmfunsdkdemo.utils.SPUtil;
import io.reactivex.annotations.Nullable;

import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_MEDIA_DISCONNECT;

import static demo.xm.com.xmfunsdkdemo.base.FunError.EE_DVR_ACCOUNT_PWD_NOT_VALID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 设备预览界面,可以控制播放,停止,码流切换,截图,录制,全屏,信息.
 * 保存图像视频,语音对话.设置预设点并控制方向
 * <p>
 * The device preview interface allows control of playback, pause, stream switching, screenshot, recording, fullscreen, and information.
 * Save images and videos, engage in voice conversations, set presets, and control directions.
 */
public class DevMonitorActivity extends DemoBaseActivity<DevMonitorPresenter> implements DevMonitorContract.IDevMonitorView, OnClickListener {
    private static final String TAG = "DevMonitorActivity";
    private MultiWinLayout playWndLayout;
    private RecyclerView rvMonitorFun;
    private ViewGroup wndLayout;
    private MonitorFunAdapter monitorFunAdapter;
    private ScreenOrientationManager screenOrientationManager;//Screen rotation manager
    private ViewGroup[] playViews;
    private int portraitWidth;
    private int portraitHeight;
    private int chnCount;
    private PresetViewHolder presetViewHolder;
    //电量WiFi状态显示，只有低功耗设备才会显示
    //Display battery and WiFi status, shown only on low-power devices.
    private TextView tvBatteryState;
    private TextView tvWiFiState;
    //实时码流
    //"Real-time stream"
    private TextView tvRate;
    // 是否打开指哪看哪
    //  Is point-to-view enabled
    private boolean isOpenPointPtz;

    //伴音
    // Background audio
    private static final int FUN_VOICE = 0;
    //抓图
    // Capture image
    private static final int FUN_CAPTURE = 1;
    //剪切
    // Clip
    private static final int FUN_RECORD = 2;
    // PTZ control
    // 云台控制
    private static final int FUN_PTZ = 3;
    //Gimbal Calibration
    //云台校正
    private static final int FUN_PTZ_CALIBRATION = 4;

    //  Intercom
    // 对讲
    private static final int FUN_INTERCOM = 5;

    // Playback
    // 回放
    private static final int FUN_PLAYBACK = 6;

    // Stream switching
    // 码流切换
    private static final int FUN_CHANGE_STREAM = 7;

    // Preset point
    // 预置点
    private static final int FUN_PRESET = 8;

    // Full screen
    // 全屏
    private static final int FUN_LANDSCAPE = 9;

    // Full stream
    // 满屏
    private static final int FUN_FULL_STREAM = 10;

    // LAN alarm
    // 局域网报警
    private static final int FUN_LAN_ALARM = 11;

    // Device image
    // 设备端图片
    private static final int FUN_DEV_PIC = 12;

    // Capture and save on the device side
    // 设备端抓图并保存
    private static final int FUN_DEV_CAPTURE_SAVE = 13;
    //设备远程抓图传回给APP（设备端不保存图片）
    private static final int FUN_DEV_CAPTURE_TO_APP = 14;
    // Real-time preview timeliness
    // 实时预览实时性
    private static final int FUN_REAL_PLAY = 15;

    // Simple data interaction
    // 简单数据交互
    private static final int FUN_SIMPLE_DATA = 16;

    // Video frame rotation
    // 视频画面旋转
    private static final int FUN_VIDEO_ROTATE = 17;

    // Feeding
    // 喂食
    private static final int FUN_FEET = 18;

    // irCut
    // irCut
    private static final int FUN_IRCUT = 19;

    // Restore factory settings
    // 恢复出厂设置
    private static final int FUN_RESET = 20;

    // Point-to-view
    // 指哪看哪
    private static final int FUN_POINT = 21;

    // Manual alarm
    // 手动警戒
    private static final int FUN_MANUAL_ALARM = 22;

    private static final int FUN_ALARM_BY_VOICE_LIGHT = 23;// 声光报警
    //Camera linkage
    //相机联动
    private static final int FUN_CAMERA_LINK = 24;

    //Mobile Tracking (Humanoid Tracking)
    //移动追踪（人形跟随）
    private static final int FUN_DETECT_TRACK = 25;

    //cruise
    //巡航
    private static final int FUN_CRUISE = 26;
    private List<HashMap<String, Object>> monitorFunList = new ArrayList<>();//预览页面的功能列表
    private AutoHideManager autoHideManager;//自动隐藏控件
    private Dialog tourDlg;
    private SystemFunctionBean systemFunctionBean;//设备能力集

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_camera);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_preview));
        titleBar.setRightBtnResource(R.mipmap.icon_setting_normal, R.mipmap.icon_setting_pressed);
        titleBar.setLeftClick(this);
        titleBar.setRightIvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                Intent intent = new Intent();
                intent.setClass(DevMonitorActivity.this, DeviceConfigActivity.class);
                intent.putExtra("devId", presenter.getDevId());
                DevMonitorActivity.this.startActivity(intent);
            }
        });
        titleBar.setBottomTip(DevMonitorActivity.class.getName());
        playWndLayout = findViewById(R.id.layoutPlayWnd);
        rvMonitorFun = findViewById(R.id.rv_monitor_fun);
        wndLayout = findViewById(R.id.wnd_layout);


        tvBatteryState = findViewById(R.id.tv_battery_state);
        tvWiFiState = findViewById(R.id.tv_wifi_state);
        tvRate = findViewById(R.id.tv_rate);
        initPresetLayout();

        autoHideManager = new AutoHideManager();
        autoHideManager.addView(findViewById(R.id.ll_dev_state));
        autoHideManager.show();
    }

    /**
     * 初始化播放窗口布局
     * "Initialize the layout of playback windows."
     */
    private void initPlayWnd() {
        //窗口个数按照1、4、9、16...整数的平方规则来设置
        //"The number of windows is set according to the rule of integer squares, such as 1, 4, 9, 16..."
        int wndCount = (int) Math.sqrt(chnCount);
        if (wndCount < Math.sqrt(chnCount)) {
            wndCount = (int) (Math.pow(wndCount + 1, 2));
        } else {
            wndCount = (int) Math.pow(wndCount, 2);
        }

        playViews = playWndLayout.setViewCount(wndCount);
        playWndLayout.setOnMultiWndListener(new MultiWinLayout.OnMultiWndListener() {
            @Override
            public boolean isDisableToChangeWndSize(int i) {
                return false;
            }

            @Override
            public boolean onTouchEvent(int wndId, MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onSingleWnd(int i, MotionEvent motionEvent, boolean b) {
                presenter.setChnId(i);
                return false;
            }

            @Override
            public boolean onSelectedWnd(int i, MotionEvent motionEvent, boolean b) {
                return false;
            }

            @Override
            public boolean onSingleTapUp(int i, MotionEvent motionEvent, boolean b) {
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(int i, MotionEvent motionEvent, boolean b) {
                if (autoHideManager.isVisible()) {
                    autoHideManager.hide();
                } else {
                    autoHideManager.show();
                }

                if (isOpenPointPtz) {
                    // 上下分屏的时候 打开指哪看哪才会触发
                    // "When in split-screen mode, triggering 'point-to-view' will activate the view corresponding to the direction pointed at."

                    // 点击相对x坐标
                    // "Click on the relative X coordinate."
                    float x = motionEvent.getX();
                    // 点击相对y坐标
                    // "Click on the relative Y coordinate."
                    float y = motionEvent.getY();
                    int xOffset = 0;
                    int yOffset = 0;

                    // 按照上半视频中心点划分距离 总共步长范围为 -4096 到 4096
                    // "Divide the distance based on the center point of the upper half of the video. The total step range is from -4096 to 4096."
                    xOffset = (int) (x * 8192 / playWndLayout.getWidth() - 4096);

                    // 按照上半视频中心点划分距离 总共步长范围为 -4096 到 4096
                    // "Divide the distance based on the center point of the upper half of the video. The total step range is from -4096 to 4096."
                    yOffset = -(int) (y * 8192 / (playWndLayout.getHeight() / 2) - 4096);
                    Log.d("dzc", "xoffset:" + xOffset + "  yoffset:" + yOffset);
                    if (xOffset >= -4096 && xOffset <= 4096 && yOffset >= -4096 && yOffset <= 4096) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("Name", "OPPtzLocate");
                        JSONObject object = new JSONObject();
                        object.put("xoffset", xOffset);
                        object.put("yoffset", yOffset);
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.add(object);
                        jsonObject.put("OPPtzLocate", jsonArray);

                        presenter.setPointPtz(jsonObject.toString());


                    }


                }
                return false;
            }


            @Override
            public boolean onDoubleTap(View view, MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onLongPress(View view, MotionEvent motionEvent) {

            }

            @Override
            public void onFling(View view, MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
//                float x = motionEvent1.getX() - motionEvent.getX();
//                float y = motionEvent1.getY() - motionEvent.getY();
//                int chnId = view.getId();
//                System.out.println("onFling:" + presenter.getTwoLensesScreen(0) + " " + x + " " + y);
//                if (Math.abs(x) > Math.abs(y)) {
//                    if (Math.abs(x) > 100) {
//                        if (x > 0) {
//                            // 向右滑动
//                            if (presenter.getTwoLensesScreen(0) == VRSoftDefine.XMTwoLensesScreen.ScreenRightOnly) {
//                                presenter.changeTwoLensesScreen(0, VRSoftDefine.XMTwoLensesScreen.ScreenDouble);
//                            } else {
//                                presenter.changeTwoLensesScreen(0, VRSoftDefine.XMTwoLensesScreen.ScreenLeftOnly);
//                            }
//
//                            if (--chnId < 0) {
//                                chnId = 0;
//                            }
//                        } else {
//                            //向左滑动
//                            if (presenter.getTwoLensesScreen(0) == VRSoftDefine.XMTwoLensesScreen.ScreenLeftOnly) {
//                                presenter.changeTwoLensesScreen(0, VRSoftDefine.XMTwoLensesScreen.ScreenDouble);
//                            } else {
//                                presenter.changeTwoLensesScreen(0, VRSoftDefine.XMTwoLensesScreen.ScreenRightOnly);
//                            }
//
//
//                            if (++chnId >= chnCount) {
//                                chnId = chnCount - 1;
//                            }
//                        }
//
//                        //是否为多窗口设备，并且当前是单窗口没有缩放显示，才支持切换窗口
//                        if (chnCount > 1 && playWndLayout.getSelectedId() != chnId && playWndLayout.isSingleWnd() && !presenter.isVideoScale()) {
//                            playWndLayout.changeChannel(chnId);
//                        }
//                    }
//
//                } else {
//                    if (y > 0) {
//                        // 向下滑动
//                        if (presenter.getTwoLensesScreen(0) == VRSoftDefine.XMTwoLensesScreen.ScreenRightOnly) {
//                            presenter.changeTwoLensesScreen(0, VRSoftDefine.XMTwoLensesScreen.ScreenDouble);
//                        } else {
//                            presenter.changeTwoLensesScreen(0, VRSoftDefine.XMTwoLensesScreen.ScreenLeftOnly);
//                        }
//                    } else {
//                        // 下上滑动
//                        if (presenter.getTwoLensesScreen(0) == VRSoftDefine.XMTwoLensesScreen.ScreenLeftOnly) {
//                            presenter.changeTwoLensesScreen(0, VRSoftDefine.XMTwoLensesScreen.ScreenDouble);
//                        } else {
//                            presenter.changeTwoLensesScreen(0, VRSoftDefine.XMTwoLensesScreen.ScreenRightOnly);
//                        }
//                    }
//                }
            }
        });
    }

    /**
     * 初始化预置点页面
     */
    private void initPresetLayout() {
        presetViewHolder = new PresetViewHolder();
        presetViewHolder.initView(LayoutInflater.from(getContext()).inflate(R.layout.view_preset_layout, null));
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        chnCount = intent.getIntExtra("chnCount", 1);
        titleBar.setTitleText(getString(R.string.channel) + ":" + playWndLayout.getSelectedId());
        presenter.setChnId(0);

        initPlayWnd();

        for (int i = 0; i < chnCount && i < playViews.length; ++i) {
            presenter.initMonitor(i, playViews[i]);
        }

        monitorFunAdapter = new MonitorFunAdapter();
        rvMonitorFun.setLayoutManager(new GridLayoutManager(this, 4));
        rvMonitorFun.setAdapter(monitorFunAdapter);

        screenOrientationManager = ScreenOrientationManager.getInstance();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeClickReceiver, filter);
    }


    /**
     * onResume->manager.loginDev（）->iDevMonitorView.onLoginResult（）-> true ?  startMonitor(): manager.loginDev or toast
     * onResume->manager.loginDev（）-> initData()-> manager.getDevAbility() -> getDevWiFiSignalLevel() ->iDevMonitorView.onWiFiSignalLevelResult() -> Setting the wifi Level
     **/
    @Override
    protected void onResume() {
        super.onResume();
        if (!isHomePress) {
            showWaitDialog();
            presenter.loginDev();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (int i = 0; i < chnCount && i < playViews.length; ++i) {
            presenter.stopMonitor(i);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (int i = 0; i < chnCount && i < playViews.length; ++i) {
            presenter.destroyMonitor(i);
        }

        if (screenOrientationManager != null) {
            screenOrientationManager.release(this);
        }

        //如果是低功耗设备，退出预览的时候需要发起休眠命令
        if (DevDataCenter.getInstance().isLowPowerDev(presenter.getDevId())) {
            IDRManager.sleepNow(this, presenter.getDevId());
        }

        presenter.release();

        if (mHomeClickReceiver != null) {
            unregisterReceiver(mHomeClickReceiver);
        }
    }

    @Override
    public DevMonitorPresenter getPresenter() {
        return new DevMonitorPresenter(this);
    }

    /**
     * 登录回调
     *
     * @param isSuccess
     * @param errorId
     */
    @Override
    public void onLoginResult(boolean isSuccess, int errorId) {
        if (isSuccess) {
            for (int i = 0; i < chnCount && i < playViews.length; ++i) {
                presenter.startMonitor(i);
            }
        } else {
            //密码错误后，会弹出密码输入框，需输入正确的密码重新登录
            if (errorId == EFUN_ERROR.EE_DVR_PASSWORD_NOT_VALID || errorId == EE_DVR_ACCOUNT_PWD_NOT_VALID) {
                XMDevInfo devInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
                //参考下方弹出密码输入框 输入正确的密码，如果不参加以下的弹框，可以按照以下方式操作：
                //第一步让用户输入正确密码，拿到正确密码后，直接调用 FunSDK.DevSetLocalPwd(devId, userName, passWord); 其中 devId是设备序列号、userName是设备登录名（默认是admin），password是要传入的正确密码
                //第二步再次调用 presenter.loginDev();
                // To reference the pop-up password input box below, if you don't want to participate in the pop-up dialog, you can follow the steps below:
                // Step 1: Ask the user to enter the correct password. Once you have the correct password, directly call FunSDK.DevSetLocalPwd(devId, userName, passWord), where devId is the device serial number, userName is the device login name (default is admin), and passWord is the correct password you want to pass.
                // Step 2: Call presenter.loginDev() again.
                XMPromptDlg.onShowPasswordErrorDialog(this, devInfo.getSdbDevInfo(), 0, new PwdErrorManager.OnRepeatSendMsgListener() {
                    @Override
                    public void onSendMsg(int msgId) {
                        showWaitDialog();
                        presenter.loginDev();
                    }
                });
            } else if (errorId < 0) {
                showToast(getString(R.string.open_video_f) + errorId, Toast.LENGTH_LONG);
            }
        }
    }

    /**
     * 获取设备能力集结果回调
     *
     * @param systemFunctionBean 能力集
     * @param errorId            错误码
     */
    @Override
    public void onGetDevAbilityResult(SystemFunctionBean systemFunctionBean, int errorId) {
        this.systemFunctionBean = systemFunctionBean;
        monitorFunList.clear();
        hideWaitDialog();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_VOICE);
        hashMap.put("itemName", getString(R.string.device_setup_encode_audio));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_CAPTURE);
        hashMap.put("itemName", getString(R.string.capture));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_RECORD);
        hashMap.put("itemName", getString(R.string.cut_video));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_PTZ);
        hashMap.put("itemName", getString(R.string.ptz_ctrl));
        monitorFunList.add(hashMap);

        if (systemFunctionBean != null) {
            //是否支持云台校正
            if (systemFunctionBean.OtherFunction.SupportPtzAutoAdjust) {
                hashMap = new HashMap<>();
                hashMap.put("itemId", FUN_PTZ_CALIBRATION);
                hashMap.put("itemName", getString(R.string.pzt_calibration));
                monitorFunList.add(hashMap);
            }
        }

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_INTERCOM);
        hashMap.put("itemName", getString(R.string.one_way_intercom));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_PLAYBACK);
        hashMap.put("itemName", getString(R.string.playback));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_CHANGE_STREAM);
        hashMap.put("itemName", getString(R.string.main_stream));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_PRESET);
        hashMap.put("itemName", getString(R.string.preset));
        monitorFunList.add(hashMap);

        if (systemFunctionBean != null) {
            //是否支持巡航
            if (systemFunctionBean.OtherFunction.SupportPTZTour) {
                hashMap = new HashMap<>();
                hashMap.put("itemId", FUN_CRUISE);
                hashMap.put("itemName", getString(R.string.cruise));
                monitorFunList.add(hashMap);
            }
        }

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_LANDSCAPE);
        hashMap.put("itemName", getString(R.string.full_stream));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_FULL_STREAM);
        hashMap.put("itemName", getString(R.string.video_fit_center));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_LAN_ALARM);
        hashMap.put("itemName", getString(R.string.start_lan_alarm));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_DEV_PIC);
        hashMap.put("itemName", getString(R.string.search_dev_pic));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_DEV_CAPTURE_SAVE);
        hashMap.put("itemName", getString(R.string.capture_pic_from_dev_and_save));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_DEV_CAPTURE_TO_APP);
        hashMap.put("itemName", getString(R.string.capture_pic_from_dev_and_to_app));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_REAL_PLAY);
        hashMap.put("itemName", getString(R.string.realtime_play));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_SIMPLE_DATA);
        hashMap.put("itemName", getString(R.string.other_config));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_VIDEO_ROTATE);
        hashMap.put("itemName", getString(R.string.video_flip));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_FEET);
        hashMap.put("itemName", getString(R.string.feeding));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_IRCUT);
        hashMap.put("itemName", getString(R.string.ircut));
        monitorFunList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("itemId", FUN_RESET);
        hashMap.put("itemName", getString(R.string.factory_reset));
        monitorFunList.add(hashMap);


        if (systemFunctionBean != null) {
            //是否支持指哪看那和相机联动配置功能
            if (systemFunctionBean.OtherFunction.SupportGunBallTwoSensorPtzLocate) {
                hashMap = new HashMap<>();
                hashMap.put("itemId", FUN_POINT);
                hashMap.put("itemName", getString(R.string.look_where_you_point));
                monitorFunList.add(hashMap);

                hashMap = new HashMap<>();
                hashMap.put("itemId", FUN_CAMERA_LINK);
                hashMap.put("itemName", getString(R.string.camera_link_set));
                monitorFunList.add(hashMap);
            }

            //是否支持手动警报功能
            if (systemFunctionBean.AlarmFunction.ManuIntellAlertAlarm) {
                hashMap = new HashMap<>();
                hashMap.put("itemId", FUN_MANUAL_ALARM);
                hashMap.put("itemName", getString(R.string.manual_alarm));
                monitorFunList.add(hashMap);
            }

            //是否支持声光警戒功能
            if (getSupportLightType(systemFunctionBean) != NO_LIGHT_CAMERA) {
                hashMap = new HashMap<>();
                hashMap.put("itemId", FUN_ALARM_BY_VOICE_LIGHT);
                hashMap.put("itemName", getString(R.string.alarm_by_voice_light));
                monitorFunList.add(hashMap);
            }

            //是否支持移动追踪
            if (systemFunctionBean.AlarmFunction.MotionHumanDection) {
                hashMap = new HashMap<>();
                hashMap.put("itemId", FUN_DETECT_TRACK);
                hashMap.put("itemName", getString(R.string.detect_track));
                monitorFunList.add(hashMap);
            }
        } else {
            showToast(getString(R.string.get_dev_ability_failed) + ":" + errorId, Toast.LENGTH_LONG);
        }
    }


    //不支持灯光
    public static final int NO_LIGHT_CAMERA = -1;
    //支持双光
    public static final int DOUBLE_LIGHT_CAMERA = 0;
    //支持双光枪机
    public static final int DOUBLE_LIGHT_BOX_CAMERA = 1;
    //支持音乐灯
    public static final int MUSIC_LIGHT_CAMERA = 2;
    //支持庭院双光灯
    public static final int GARDEN_DOUBLE_LIGHT_CAMERA = 3;
    //支持白光灯
    public static final int WHITE_LIGHT_CAMERA = 4;
    //支持低功耗设备灯光能力 / 支持单品智能警戒
    public static final int LOW_POWER_WHITE_LIGHT_CAMERA = 5;

    /**
     * 支持的灯光类型
     */
    private int getSupportLightType(SystemFunctionBean systemFunctionBean) {
        if (systemFunctionBean == null) {
            return NO_LIGHT_CAMERA;
        }

        if (systemFunctionBean.OtherFunction.SupportCameraWhiteLight) {
            Log.d(TAG, "支持基础白光灯");
            if (systemFunctionBean.OtherFunction.SupportDoubleLightBulb) {
                Log.d(TAG, "支持双光      " + presenter.getDevId());
                return DOUBLE_LIGHT_CAMERA;
            } else if (systemFunctionBean.OtherFunction.SupportDoubleLightBoxCamera) {
                Log.d(TAG, "支持双光枪机   " + presenter.getDevId());
                return DOUBLE_LIGHT_BOX_CAMERA;
            } else if (systemFunctionBean.OtherFunction.SupportMusicLightBulb) {
                Log.d(TAG, "支持音乐灯     " + presenter.getDevId());
                return MUSIC_LIGHT_CAMERA;
            } else if (systemFunctionBean.OtherFunction.SupportBoxCameraBulb) {
                Log.d(TAG, "支持庭院双光灯     " + presenter.getDevId());
                return GARDEN_DOUBLE_LIGHT_CAMERA;
            } else {
                Log.d(TAG, "支持白光灯     " + presenter.getDevId());
                return WHITE_LIGHT_CAMERA;
            }
        } else if (systemFunctionBean.OtherFunction.LP4GSupportDoubleLightSwitch
                || systemFunctionBean.AlarmFunction.IntellAlertAlarm
                || systemFunctionBean.OtherFunction.SupportLowPowerDoubleLightToLightingSwitch) {
            //支持低功耗设备灯光能力 / 支持单品智能警戒
            Log.d(TAG, "支持低功耗设备灯光能力 / 支持单品智能警戒 / 庭院灯照明开关   " + presenter.getDevId());
            return LOW_POWER_WHITE_LIGHT_CAMERA;

        }
        return NO_LIGHT_CAMERA;
    }


    /**
     * 播放状态回调
     *
     * @param chnId   通道号
     * @param state   状态
     * @param errorId 错误码
     */
    @Override
    public void onPlayState(int chnId, int state, int errorId) {
        if (state == E_STATE_PlAY) {
            if (errorId == EFUN_ERROR.EE_DVR_PASSWORD_NOT_VALID) {
                XMDevInfo devInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
                XMPromptDlg.onShowPasswordErrorDialog(this, devInfo.getSdbDevInfo(), 0, new PwdErrorManager.OnRepeatSendMsgListener() {
                    @Override
                    public void onSendMsg(int msgId) {
                        showWaitDialog();
                        presenter.startMonitor(chnId);
                    }
                });
            } else if (errorId < 0) {
                showToast(getString(R.string.open_video_f) + errorId, Toast.LENGTH_LONG);
            } else {
                monitorFunAdapter.changeBtnState(FUN_CHANGE_STREAM, presenter.getStreamType(chnId) == SDKCONST.StreamType.Main);
            }
        }

        if (state == E_STATE_SAVE_RECORD_FILE_S) {
            showToast(getString(R.string.record_s), Toast.LENGTH_LONG);
        } else if (state == E_STATE_SAVE_PIC_FILE_S) {
            showToast(getString(R.string.capture_s), Toast.LENGTH_LONG);
        } else if (state == E_STATE_MEDIA_DISCONNECT) {
            showToast(getString(R.string.media_disconnect), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onUpdateFaceFrameView(final FaceFeature[] faceFeatures, final int width, final int height) {
    }

    @Override
    public void onLightCtrlResult(String jsonData) {
        XMPromptDlg.onShow(this, jsonData, null);
    }

    /**
     * 电量、SD卡存储等状态回调
     * devStorageStatus 0:没有存储 1:有存储 2:存储设备插入 -2:存储设备未知
     * electable 显示是否在充电 0:未充电; 1:正在充电;2:充电满；3:未知
     * percent 电量百分比
     * level 电量等级
     *
     * @param electCapacityBean
     */
    @Override
    public void onElectCapacityResult(ElectCapacityBean electCapacityBean) {
        if (tvBatteryState.getVisibility() == View.GONE) {
            tvBatteryState.setVisibility(View.VISIBLE);
        }

        tvBatteryState.setText(String.format(getString(R.string.battery_state_show), electCapacityBean.percent, electCapacityBean.devStorageStatus, electCapacityBean.electable));
    }

    /**
     * WiFi信号回调
     *
     * @param wifiRouteInfo
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onWiFiSignalLevelResult(WifiRouteInfo wifiRouteInfo) {
        if (wifiRouteInfo != null) {
            if (tvWiFiState.getVisibility() == View.GONE) {
                tvWiFiState.setVisibility(View.VISIBLE);
            }

            tvWiFiState.setText(String.format(getString(R.string.wifi_state_show), wifiRouteInfo.getSignalLevel()));
        }
    }

    /**
     * 视频码流回调
     *
     * @param rate
     */
    @Override
    public void onVideoRateResult(String rate) {
        if (tvRate.getVisibility() == View.GONE) {
            tvRate.setVisibility(View.VISIBLE);
        }

        tvRate.setText(rate);
    }

    /**
     * 喂食结果回调
     *
     * @param isSuccess 喂食是否成功
     * @param values
     * @param errorMsg
     */
    @Override
    public void onFeedResult(boolean isSuccess, byte[] values, String errorMsg) {
        ToastUtils.showLong(isSuccess ? getString(R.string.libfunsdk_operation_success) : getString(R.string.libfunsdk_operation_failed) + ":" + errorMsg);
    }

    @Override
    public void changeChannel(int chnId) {
        playWndLayout.changeChannel(chnId);
    }

    @Override
    public boolean isSingleWnd() {
        return playWndLayout.isSingleWnd();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onClick(View view) {

    }

    public class MonitorFunAdapter extends RecyclerView.Adapter<MonitorFunAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_monitor_fun, null));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HashMap<String, Object> hashMap = monitorFunList.get(position);
            if (hashMap != null) {
                String itemName = (String) hashMap.get("itemName");
                int itemId = (int) hashMap.get("itemId");
                holder.btnMonitorFun.setText(itemName);
                holder.btnMonitorFun.setTag(itemId);
                if (FUN_FULL_STREAM == itemId) {
                    holder.btnMonitorFun.setSelected(!presenter.isVideoFullScreen(playWndLayout.getSelectedId()));
                }
            }
        }

        @Override
        public int getItemCount() {
            return monitorFunList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            BtnColorBK btnMonitorFun;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                btnMonitorFun = itemView.findViewById(R.id.btn_monitor_fun);
                btnMonitorFun.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        HashMap<String, Object> hashMap = monitorFunList.get(position);
                        if (hashMap != null) {
                            int itemId = (int) hashMap.get("itemId");
                            boolean isBtnChange = dealWithMonitorFunction(itemId, btnMonitorFun.isSelected());
                            if (isBtnChange) {
                                btnMonitorFun.setSelected(!btnMonitorFun.isSelected());
                            }
                        }
                    }
                });

            }
        }

        public void changeBtnState(int itemId, boolean isSelected) {
            BtnColorBK btnMonitorFun = rvMonitorFun.findViewWithTag(itemId);
            if (btnMonitorFun != null) {
                btnMonitorFun.setSelected(isSelected);
            }
        }
    }

    private boolean dealWithMonitorFunction(int itemId, boolean isSelected) {
        switch (itemId) {
            case FUN_VOICE://开启和关闭音频
                if (isSelected) {
                    presenter.closeVoice(playWndLayout.getSelectedId());
                } else {
                    presenter.openVoice(playWndLayout.getSelectedId());
                }
                return true;
            case FUN_CAPTURE://视频抓图
                presenter.capture(playWndLayout.getSelectedId());
                break;
            case FUN_RECORD://视频剪切
                if (isSelected) {
                    presenter.stopRecord(playWndLayout.getSelectedId());
                } else {
                    presenter.startRecord(playWndLayout.getSelectedId());
                }
                return true;
            case FUN_PTZ://云台控制
            {
                PtzView contentLayout = (PtzView) LayoutInflater.from(this).inflate(R.layout.view_ptz, null);
                if (presenter.isOnlyHorizontal() && !presenter.isOnlyVertically()) {//Horizontal display
                    contentLayout.setOnlyHorizontal(true);
                    contentLayout.setNormalBgSrcId(R.mipmap.ic_only_h_ptz_ctrl_nor);
                    contentLayout.setSelectedLeftBgSrcId(R.mipmap.ic_only_h_ptz_ctrl_left);
                    contentLayout.setSelectedRightBgSrcId(R.mipmap.ic_only_h_ptz_ctrl_right);
                } else if (presenter.isOnlyVertically() && !presenter.isOnlyHorizontal()) {//Vertically display
                    contentLayout.setOnlyVertically(true);
                    contentLayout.setNormalBgSrcId(R.mipmap.ic_only_v_ptz_ctrl_nor);
                    contentLayout.setSelectedDownBgSrcId(R.mipmap.ic_only_v_ptz_ctrl_down);
                    contentLayout.setSelectedUpBgSrcId(R.mipmap.ic_only_v_ptz_ctrl_top);
                }

                contentLayout.setOnPtzViewListener(new PtzView.OnPtzViewListener() {
                    @Override
                    public void onPtzDirection(int direction, boolean stop) {
                        presenter.devicePTZControl(playWndLayout.getSelectedId(), direction, 4, stop);
                    }
                });
                XMPromptDlg.onShow(this, contentLayout);
                break;
            }
            case FUN_PTZ_CALIBRATION://云台校正
                showWaitDialog();
                presenter.ptzCalibration();
                break;
            case FUN_INTERCOM: //单向对讲，按下去说话，放开后听到设备端的声音，对话框消失后 对讲结束
            {
                View layout = LayoutInflater.from(this).inflate(R.layout.view_intercom, null);
                RippleButton rippleButton = layout.findViewById(R.id.btn_talk);

                ListSelectItem lsiDoubleTalkSwitch = layout.findViewById(R.id.lsi_double_talk_switch);
                ListSelectItem lsiTalkBroadcast = layout.findViewById(R.id.lsi_double_talk_broadcast);
                lsiTalkBroadcast.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lsiTalkBroadcast.setRightImage(lsiTalkBroadcast.getRightValue() == SDKCONST.Switch.Close ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
                    }
                });
                lsiTalkBroadcast.setVisibility(chnCount > 1 ? View.VISIBLE : View.GONE);//如果是多通道设备，那么支持广播对讲功能
                rippleButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                //判断双向对讲配置是否开启
                                if (lsiDoubleTalkSwitch.getRightValue() == SDKCONST.Switch.Open) {
                                    //双向对讲
                                    if (presenter.isTalking(playWndLayout.getSelectedId())) {
                                        //当前对讲开启中，则需要关闭对讲
                                        presenter.stopIntercom(playWndLayout.getSelectedId());// Pause the intercom in the middle of a conversation
                                        rippleButton.clearState();
                                    } else {
                                        //当前对讲未开启，则需要开启对讲 (如果设备的通道数(chnCount)大于1，比如NVR设备的话，那么要使用通道对讲)
                                        boolean isTalkBroadcast = lsiTalkBroadcast.getRightValue() == SDKCONST.Switch.Open;
                                        presenter.startDoubleIntercom(playWndLayout.getSelectedId(), isTalkBroadcast); //Turn on the two-way intercom
                                        rippleButton.setUpGestureEnable(false);
                                        //对讲开启的时候，视频伴音会随之关闭
                                        monitorFunAdapter.changeBtnState(FUN_VOICE, false);
                                    }

                                } else {
                                    //(如果设备的通道数(chnCount)大于1，比如NVR设备的话，那么要使用通道对讲)
                                    boolean isTalkBroadcast = lsiTalkBroadcast.getRightValue() == SDKCONST.Switch.Open;
                                    presenter.startSingleIntercomAndSpeak(playWndLayout.getSelectedId(), isTalkBroadcast);//Enable a one-way intercom
                                    rippleButton.setUpGestureEnable(true);
                                    //对讲开启的时候，视频伴音会随之关闭
                                    monitorFunAdapter.changeBtnState(FUN_VOICE, false);
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:
                                if (lsiDoubleTalkSwitch.getRightValue() == SDKCONST.Switch.Close) {
                                    presenter.stopSingleIntercomAndHear(playWndLayout.getSelectedId());
                                }
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

                lsiDoubleTalkSwitch.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lsiDoubleTalkSwitch.setRightImage(lsiDoubleTalkSwitch.getRightValue() == SDKCONST.Switch.Close ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
                        if (lsiDoubleTalkSwitch.getRightValue() == SDKCONST.Switch.Open) {
                            rippleButton.setTabText(getString(R.string.click_to_open_two_way_talk));
                        } else {
                            rippleButton.setTabText(getString(R.string.long_press_open_one_way_talk));
                        }

                        presenter.stopIntercom(playWndLayout.getSelectedId());
                    }
                });

                ListSelectItem lsiChooseVoice = layout.findViewById(R.id.lsi_choose_voice);
                lsiChooseVoice.getExtraSpinner().initData(new String[]{getString(R.string.speaker_type_normal), getString(R.string.speaker_type_man), getString(R.string.speaker_type_woman)}, new Integer[]{SPEAKER_TYPE_NORMAL, SPEAKER_TYPE_MAN, SPEAKER_TYPE_WOMAN});
                lsiChooseVoice.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lsiChooseVoice.toggleExtraView();
                    }
                });
                lsiChooseVoice.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener<Integer>() {
                    @Override
                    public void onItemClick(int position, String key, Integer value) {
                        lsiChooseVoice.toggleExtraView(true);
                        lsiChooseVoice.setRightText(key);
                        presenter.setSpeakerType(playWndLayout.getSelectedId(), value);
                    }
                });

                XMPromptDlg.onShow(this, layout, true, new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        presenter.stopIntercom(playWndLayout.getSelectedId());
                    }
                });
            }
            break;
            case FUN_PLAYBACK://录像回放
                turnToActivity(DevRecordActivity.class, new Object[][]{{"devId", presenter.getDevId()}, {"chnId", playWndLayout.getSelectedId()}});
                break;
            case FUN_CHANGE_STREAM://主副码流切换      The primary stream is clearer and the secondary stream is blurry
                presenter.changeStream(playWndLayout.getSelectedId());
                break;
            case FUN_PRESET://预置点
                presetViewHolder.showPresetDlg = XMPromptDlg.onShow(getContext(), presetViewHolder.presetLayout, (int) (screenWidth * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT, true, null);
                break;
            case FUN_CRUISE://巡航
            {
                showWaitDialog();
                presenter.getTour(playWndLayout.getSelectedId());
                break;
            }
            case FUN_LANDSCAPE://全屏
                screenOrientationManager.landscapeScreen(this, true);
                break;
            case FUN_FULL_STREAM://满屏显示 视频画面按比例显示
                if (isSelected) {
                    presenter.setVideoFullScreen(playWndLayout.getSelectedId(), true);
                } else {
                    presenter.setVideoFullScreen(playWndLayout.getSelectedId(), false);
                }
                return true;
            case FUN_LAN_ALARM: //局域网报警
                turnToActivity(ActivityGuideDeviceLanAlarm.class);
                break;
            case FUN_DEV_PIC://设备端图片
                turnToActivity(DevPictureActivity.class);
                break;
            case FUN_DEV_CAPTURE_SAVE://设备端抓图并保存到设备端本地存储，不会将图片回传给APP
                XMPromptDlg.onShow(this, getString(R.string.not_all_dev_support), new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.capturePicFromDevAndSave(playWndLayout.getSelectedId());
                    }
                });
                break;
            case FUN_DEV_CAPTURE_TO_APP://设备端抓图并回传给APP，但是不会将图片保存到设备本地
                presenter.capturePicFromDevAndToApp(playWndLayout.getSelectedId());
                break;
            case FUN_REAL_PLAY://实时预览实时性（局域网IP访问才生效）
                showWaitDialog();
                presenter.setRealTimeEnable(isSelected);

                for (int i = 0; i < chnCount && i < playViews.length; ++i) {
                    presenter.stopMonitor(i);
                }

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < chnCount && i < playViews.length; ++i) {
                            presenter.startMonitor(i);
                        }
                        hideWaitDialog();
                    }
                }, 1000);
                return true;
            case FUN_SIMPLE_DATA://简单数据交互
                turnToActivity(DevSimpleConfigActivity.class);
                break;
            case FUN_VIDEO_ROTATE://视频画面旋转
                presenter.setVideoFlip(playWndLayout.getSelectedId());
                break;
            case FUN_FEET://喂食
                XMPromptDlg.onShowEditDialog(this, getString(R.string.food_portions), "1", new EditDialog.OnEditContentListener() {
                    @Override
                    public void onResult(String content) {
                        if (StringUtils.isStringNULL(content)) {
                            ToastUtils.showLong(R.string.input_food_portions);
                        } else {
                            XMPromptDlg.onShow(DevMonitorActivity.this, getString(R.string.please_sel_feet_type), getString(R.string.feed_the_dog), getString(R.string.feed_the_fish), new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    presenter.feedTheDog(Integer.parseInt(content));
                                }
                            }, new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    presenter.feedTheFish(Integer.parseInt(content));
                                }
                            });

                        }
                    }
                });
                break;
            case FUN_IRCUT://irCut反序
            {
                View layout = LayoutInflater.from(this).inflate(R.layout.view_ircut, null);
                ListSelectItem lsiIrCut = layout.findViewById(R.id.lsi_ircut);
                lsiIrCut.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (presenter.getCameraParamBean() != null) {
                            boolean open = lsiIrCut.getSwitchState() == SDKCONST.Switch.Open;
                            lsiIrCut.setSwitchState(!open ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
                            presenter.setIrCutInfo(!open, new DeviceManager.OnDevManagerListener() {
                                @Override
                                public void onSuccess(String s, int i, Object o) {

                                }

                                @Override
                                public void onFailed(String s, int i, String s1, int i1) {

                                }
                            });
                        }
                    }
                });

                XMPromptDlg.onShow(this, layout, true, new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                });

                if (presenter.getCameraParamBean() != null) {
                    lsiIrCut.setSwitchState(presenter.getCameraParamBean().IrcutSwap);
                } else {
                    presenter.getIrCutInfo();
                    showToast("获取irCut配置失败，请重试！", Toast.LENGTH_LONG);
                }

            }
            break;
            case FUN_RESET://恢复出厂设置
                XMPromptDlg.onShow(DevMonitorActivity.this, getString(R.string.factory_reset), getString(R.string.only_factory_reset), getString(R.string.factory_reset_and_delete_dev), new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.factoryReset(new DeviceManager.OnDevManagerListener() {
                            @Override
                            public void onSuccess(String devId, int operationType, Object result) {
                                showToast(getString(R.string.libfunsdk_operation_success), Toast.LENGTH_LONG);
                            }

                            @Override
                            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                                showToast(getString(R.string.libfunsdk_operation_failed) + ":" + errorId, Toast.LENGTH_LONG);
                            }
                        });
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.factoryReset(new DeviceManager.OnDevManagerListener() {
                            @Override
                            public void onSuccess(String devId, int operationType, Object result) {
                                showToast(getString(R.string.libfunsdk_operation_success), Toast.LENGTH_LONG);
                                //未使用AccountManager(包括XMAccountManager或LocalAccountManager)登录（包括账号登录和本地临时登录），只能将设备信息临时缓存，重启应用后无法查到设备信息。
                                if (DevDataCenter.getInstance().getLoginType() == LOGIN_NONE) {
                                    DevDataCenter.getInstance().removeDev(devId);
                                    showToast(getString(R.string.delete_s), Toast.LENGTH_LONG);
                                    finish();
                                } else {
                                    AccountManager.getInstance().deleteDev(presenter.getDevId(), new BaseAccountManager.OnAccountManagerListener() {
                                        @Override
                                        public void onSuccess(int msgId) {
                                            showToast(getString(R.string.delete_s), Toast.LENGTH_LONG);
                                            finish();
                                        }

                                        @Override
                                        public void onFailed(int msgId, int errorId) {
                                            showToast(getString(R.string.delete_f) + ":" + errorId, Toast.LENGTH_LONG);
                                        }

                                        @Override
                                        public void onFunSDKResult(Message message, MsgContent msgContent) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                                showToast(getString(R.string.libfunsdk_operation_failed) + ":" + errorId, Toast.LENGTH_LONG);
                            }
                        });
                    }
                });
                break;
            case FUN_POINT://指哪看那
                //如果支持指哪看哪的功能，需要将预览画布的触摸事件设置成false，传递给上层控件
                isOpenPointPtz = !isOpenPointPtz;
                if (isOpenPointPtz) {
                    XMPromptDlg.onShow(DevMonitorActivity.this, getString(R.string.camera_point_ptz_tips), null);
                }

                ViewGroup.LayoutParams layoutParams = wndLayout.getLayoutParams();
                layoutParams.height = XUtils.getScreenWidth(DevMonitorActivity.this) * 18 / 16;
                wndLayout.setLayoutParams(layoutParams);
                presenter.setPlayViewTouchable(0, false);
                return true;
            case FUN_MANUAL_ALARM://手动警戒
                boolean isManualAlarmOpen = presenter.changeManualAlarmSwitch();
                monitorFunAdapter.changeBtnState(itemId, isManualAlarmOpen);
                break;
            case FUN_ALARM_BY_VOICE_LIGHT: //声光报警
                switch (getSupportLightType(systemFunctionBean)) {
                    case DOUBLE_LIGHT_BOX_CAMERA:
                        turnToActivity(DoubleLightBoxActivity.class);
                        break;
                    case LOW_POWER_WHITE_LIGHT_CAMERA:
                        Intent intent = new Intent(DevMonitorActivity.this, WhiteLightActivity.class);
                        intent.putExtra("devId", presenter.getDevId());
                        intent.putExtra("supportLightSwitch", false);
                        startActivity(intent);
                        break;

                    case WHITE_LIGHT_CAMERA:
                        Intent whiteLightIntent = new Intent(DevMonitorActivity.this, WhiteLightActivity.class);
                        whiteLightIntent.putExtra("devId", presenter.getDevId());
                        whiteLightIntent.putExtra("supportLightSwitch", true);
                        startActivity(whiteLightIntent);
                        break;
                    case DOUBLE_LIGHT_CAMERA:
                        Intent doubleLightIntent = new Intent(DevMonitorActivity.this, DoubleLightActivity.class);
                        doubleLightIntent.putExtra("devId", presenter.getDevId());
                        doubleLightIntent.putExtra("supportLightSwitch", true);
                        startActivity(doubleLightIntent);
                        break;
                    case MUSIC_LIGHT_CAMERA:
                        Intent musicLightIntent = new Intent(DevMonitorActivity.this, MusicLightActivity.class);
                        musicLightIntent.putExtra("devId", presenter.getDevId());
                        musicLightIntent.putExtra("supportLightSwitch", true);
                        startActivity(musicLightIntent);
                        break;
                    case GARDEN_DOUBLE_LIGHT_CAMERA:
                        Intent GardenDoubleLightIntent = new Intent(DevMonitorActivity.this, GardenDoubleLightActivity.class);
                        GardenDoubleLightIntent.putExtra("devId", presenter.getDevId());
                        startActivity(GardenDoubleLightIntent);
                        break;
                    default:
                        break;
                }
                break;
            case FUN_CAMERA_LINK://相机联动
                turnToActivity(CameraLinkSetActivity.class);
                break;
            case FUN_DETECT_TRACK://移动追踪
                turnToActivity(DetectTrackActivity.class);
                break;
            default:
                Toast.makeText(DevMonitorActivity.this, getString(R.string.not_support_tip), Toast.LENGTH_LONG).show();
                break;
        }

        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            titleBar.setVisibility(View.GONE);
            rvMonitorFun.setVisibility(View.GONE);
            portraitWidth = wndLayout.getWidth();
            portraitHeight = wndLayout.getHeight();
            ViewGroup.LayoutParams layoutParams = wndLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            wndLayout.requestLayout();
            presenter.setPlayViewTouchable(0, true);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            titleBar.setVisibility(View.VISIBLE);
            rvMonitorFun.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = wndLayout.getLayoutParams();
            layoutParams.width = portraitWidth;
            layoutParams.height = portraitHeight;
            wndLayout.requestLayout();
            //如果支持指哪看哪的功能，需要将预览画布的触摸事件设置成false，传递给上层控件
            DeviceManager.getInstance().getDevAbility(presenter.getDevId(), new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String s, int i, Object o) {
                    if ((boolean) o) {
                        presenter.setPlayViewTouchable(0, false);
                    }
                }

                @Override
                public void onFailed(String s, int i, String s1, int i1) {

                }
            }, "OtherFunction", "SupportGunBallTwoSensorPtzLocate");
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenOrientationManager.portraitScreen(this, true);
        } else {
            finish();
        }
    }

    class PresetViewHolder {
        /**
         * 预置点操作页面
         */
        View presetLayout;
        /**
         * 键盘数字按钮0-9
         */
        BtnColorBK[] btnNumbers = new BtnColorBK[10];
        /**
         * 添加预置点
         */
        BtnColorBK btnAddPreset;
        /**
         * 调用预置点
         */
        BtnColorBK btnCtrlPreset;
        BtnColorBK btnCancel;
        BtnColorBK btnTurnToPresetList;//跳转到预置点列表
        ImageView ivBack;
        ImageView ivClear;
        EditText etInput;
        Dialog showPresetDlg;

        void initView(View rootLayout) {
            presetLayout = rootLayout;
            btnNumbers[0] = presetLayout.findViewById(R.id.btn_keypad_0);
            btnNumbers[1] = presetLayout.findViewById(R.id.btn_keypad_1);
            btnNumbers[2] = presetLayout.findViewById(R.id.btn_keypad_2);
            btnNumbers[3] = presetLayout.findViewById(R.id.btn_keypad_3);
            btnNumbers[4] = presetLayout.findViewById(R.id.btn_keypad_4);
            btnNumbers[5] = presetLayout.findViewById(R.id.btn_keypad_5);
            btnNumbers[6] = presetLayout.findViewById(R.id.btn_keypad_6);
            btnNumbers[7] = presetLayout.findViewById(R.id.btn_keypad_7);
            btnNumbers[8] = presetLayout.findViewById(R.id.btn_keypad_8);
            btnNumbers[9] = presetLayout.findViewById(R.id.btn_keypad_9);

            for (int i = 0; i < btnNumbers.length; i++) {
                btnNumbers[i].setOnClickListener(numberClickLs);
                btnNumbers[i].setTag(i);
            }

            btnAddPreset = presetLayout.findViewById(R.id.btn_add_preset);
            btnCtrlPreset = presetLayout.findViewById(R.id.btn_ctrl_preset);
            btnTurnToPresetList = presetLayout.findViewById(R.id.btn_turn_to_preset_list);
            ivBack = presetLayout.findViewById(R.id.iv_back);
            ivClear = presetLayout.findViewById(R.id.iv_clear);
            btnCancel = presetLayout.findViewById(R.id.btn_cancel);

            etInput = presetLayout.findViewById(R.id.et_preset_input);
            etInput.setInputType(InputType.TYPE_NULL);
            etInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s != null && !StringUtils.isStringNULL(s.toString())) {
                        int number = Integer.parseInt(s.toString());
                        int index = etInput.getSelectionEnd();
                        if (number == 0 || number > 255) {
                            Editable editable = etInput.getEditableText();
                            editable.delete(index - 1, index);
                            Toast.makeText(getContext(), FunSDK.TS("TR_Preset_Range"), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = etInput.getSelectionEnd();
                    Editable editable = etInput.getEditableText();
                    if (editable.length() > 0 && index > 0) {
                        editable.delete(index - 1, index);
                    }
                }
            });

            ivClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etInput.setText("");
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (showPresetDlg != null) {
                        showPresetDlg.dismiss();
                    }
                }
            });


            btnCtrlPreset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String inputContent = etInput.getText().toString();
                    if (!StringUtils.isStringNULL(inputContent)) {
                        presenter.turnToPreset(0, Integer.parseInt(inputContent));
                    } else {
                        Toast.makeText(getContext(), R.string.TR_Input_Preset_Empty_Tip, Toast.LENGTH_LONG).show();
                    }
                }
            });

            btnAddPreset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String inputContent = etInput.getText().toString();
                    if (!StringUtils.isStringNULL(inputContent)) {
                        presenter.addPreset(0, Integer.parseInt(inputContent));
                    } else {
                        Toast.makeText(getContext(), R.string.TR_Input_Preset_Empty_Tip, Toast.LENGTH_LONG).show();
                    }
                }
            });

            btnTurnToPresetList.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    turnToActivity(PresetListActivity.class);
                }
            });
        }

        View.OnClickListener numberClickLs = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = etInput.getSelectionEnd();
                Editable editable = etInput.getEditableText();
                editable.insert(index, v.getTag() + "");
            }
        };

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isHomePress) {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null);
            if (intent != null) {
                startActivityForResult(intent, 0);
            }
        }
    }

    @Override
    public void onChangeManualAlarmResult(boolean isSuccess, int errorId) {
        if (isSuccess) {
            showToast(getString(R.string.libfunsdk_operation_success), Toast.LENGTH_LONG);
        } else {
            showToast(getString(R.string.libfunsdk_operation_failed) + ":" + errorId, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onPtzCalibrationResult(boolean isSuccess, int errorId) {
        if (isSuccess) {
            showToast(getString(R.string.libfunsdk_operation_success), Toast.LENGTH_LONG);
        } else {
            showToast(getString(R.string.libfunsdk_operation_failed) + ":" + errorId, Toast.LENGTH_LONG);
        }

        hideWaitDialog();
    }

    /**
     * 新建预置点ID取值252、253、254，将这三个预置点作为巡航点
     *
     * @param tourBeans
     * @param errorId
     */
    @Override
    public void onShowTour(List<TourBean> tourBeans, int errorId) {
        hideWaitDialog();
        if (errorId == 0) {
            LinearLayout contentLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_criuse, null);
            BtnColorBK btnOne = contentLayout.findViewById(R.id.btn_keypad_1);
            BtnColorBK btnTwo = contentLayout.findViewById(R.id.btn_keypad_2);
            BtnColorBK btnThree = contentLayout.findViewById(R.id.btn_keypad_3);
            BtnColorBK btnStartCruise = contentLayout.findViewById(R.id.btn_start_cruise);
            BtnColorBK btnStopCruise = contentLayout.findViewById(R.id.btn_stop_cruise);

            if (tourBeans != null && !tourBeans.isEmpty()) {
                for (TourBean tourBean : tourBeans) {
                    if (tourBean != null) {
                        if (tourBean.Id == 252) {
                            btnOne.setText("1");
                        } else if (tourBean.Id == 253) {
                            btnTwo.setText("2");
                        } else if (tourBean.Id == 254) {
                            btnThree.setText("3");
                        }
                    }
                }
            }

            btnOne.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dealWithTourCtrl(252);
                }
            });

            btnOne.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dealWithDeleteTour(252);
                    return true;
                }
            });

            btnTwo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dealWithTourCtrl(253);
                }
            });

            btnTwo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dealWithDeleteTour(253);
                    return true;
                }
            });

            btnThree.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dealWithTourCtrl(254);
                }
            });

            btnThree.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dealWithDeleteTour(254);
                    return true;
                }
            });

            btnStartCruise.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.startTour(0);
                }
            });

            btnStopCruise.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.stopTour(0);
                }
            });

            PtzView ptzView = contentLayout.findViewById(R.id.ptz_view);
            ptzView.setOnPtzViewListener(new PtzView.OnPtzViewListener() {
                @Override
                public void onPtzDirection(int direction, boolean stop) {
                    presenter.devicePTZControl(playWndLayout.getSelectedId(), direction, 4, stop);
                }
            });


            tourDlg = XMPromptDlg.onShow(this, contentLayout);
        }
    }

    @Override
    public void onTourCtrlResult(boolean isSuccess, int tourCmd, int errorId) {
        if (isSuccess) {
            Toast.makeText(this, getString(R.string.libfunsdk_operation_success), Toast.LENGTH_LONG).show();
            if (tourCmd == PresetManager.PRESET_ADD_TOUR || tourCmd == PresetManager.PRESET_DELETE_TOUR) {
                tourDlg.dismiss();
                presenter.getTour(0);
            }
        } else {
            Toast.makeText(this, getString(R.string.libfunsdk_operation_failed) + ":" + errorId, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTourEndResult() {
        Toast.makeText(this, R.string.tour_end, Toast.LENGTH_LONG).show();
    }

    /**
     * 处理巡航点操作
     */
    private void dealWithTourCtrl(int presetId) {
        List<TourBean> tourBeans = presenter.getTourBeans(0);
        if (tourBeans != null) {
            for (TourBean tourBean : tourBeans) {
                if (tourBean != null) {
                    if (tourBean.Id == presetId) {
                        presenter.turnToPreset(0, tourBean.Id);
                        return;
                    }
                }
            }
        }

        XMPromptDlg.onShow(DevMonitorActivity.this, "是否设置巡航点?", new OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.addTour(0, presetId);
            }
        }, null);
    }

    /**
     * 处理删除巡航点操作
     *
     * @param presetId
     */
    private void dealWithDeleteTour(int presetId) {
        List<TourBean> tourBeans = presenter.getTourBeans(0);
        if (tourBeans != null) {
            for (TourBean tourBean : tourBeans) {
                if (tourBean != null) {
                    if (tourBean.Id == presetId) {//判断巡航点是否设置了
                        XMPromptDlg.onShow(DevMonitorActivity.this, "确定删除该巡航点?", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                presenter.deleteTour(0, presetId);
                            }
                        }, null);
                        return;
                    }
                }
            }
        }
    }

    private boolean isHomePress;//按了Home键，退到后台
    /**
     * 监听home键的按下
     */
    private BroadcastReceiver mHomeClickReceiver = new BroadcastReceiver() {
        static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        static final String SYSTEM_DIALOG_REASON_RECENTAPPS = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            switch (action) {
                case Intent.ACTION_CLOSE_SYSTEM_DIALOGS:
                    String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                    if (SYSTEM_DIALOG_REASON_RECENTAPPS.equals(reason)) {
                        break;
                    }

                    if (!SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason) && !"fs_gesture".equals(reason)) {
                        return;
                    }

                    isHomePress = true;

                    break;
                default:
                    return;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            showWaitDialog();
            presenter.loginDev();
        } else {
            finish();
        }
    }
}

