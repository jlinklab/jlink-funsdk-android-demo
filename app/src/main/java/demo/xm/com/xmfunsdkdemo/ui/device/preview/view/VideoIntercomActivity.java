package demo.xm.com.xmfunsdkdemo.ui.device.preview.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.hardware.camera2.CameraCharacteristics;
import android.media.Image;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.PermissionUtils;
import com.constant.SDKLogConstant;
import com.utils.LogUtils;
import com.xm.MoreClickManager;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.media.MultiWinLayout;
import com.xm.ui.media.MultiWinView;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.listener.VideoIntercomContract;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.presenter.VideoIntercomPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.push.view.DevIncomingCallActivity;
import demo.xm.com.xmfunsdkdemo.ui.widget.HelpSuspendLayout;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class VideoIntercomActivity extends DemoBaseActivity<VideoIntercomPresenter> implements
        VideoIntercomContract.IVideoIntercomView, View.OnClickListener {
    private HelpSuspendLayout rlCameraLayout;
    private RelativeLayout rlCameraCloseLayout;
    private ConstraintLayout.LayoutParams cameraLayoutParams;
    private TextureView cameraPreview;
    private MultiWinView multiWinView;
    private ViewGroup closeIntercom;//结束视频对讲（全双工）
    private ImageView micSwitch;//mic开关
    private ImageView cameraTypeSwitch;//镜头切换
    private TextView tvMicTip;
    private TextView tvCameraTypeTip;
    private ImageView cameraStateSwitch;//手机镜头开关
    private TextView tvCameraStateTip;
    private ImageView soundSwitch;//喇叭听筒开关
    private TextView tvSound;
    /**
     * 摄像头参数
     */
    private int curCameraLensType = CameraCharacteristics.LENS_FACING_FRONT;//默认前置
    /**
     * 当前麦克风是否打开
     */
    private boolean isCurMicOpening = false;
    /**
     * 当前扬声器是否打开(默认打开）
     */
    private boolean isCurSoundOpening = true;
    /**
     * 当前摄像头视频是否打开
     */
    private boolean isCurCameraVideoRecording = false;

    @Override
    public VideoIntercomPresenter getPresenter() {
        return new VideoIntercomPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_intercom);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
        VideoIntercomActivityPermissionsDispatcher.initDataWithPermissionCheck(this);
    }

    private void initView() {
        rlCameraLayout = findViewById(R.id.rl_camera_layout);
        rlCameraCloseLayout = findViewById(R.id.rl_camera_close_layout);
        cameraPreview = findViewById(R.id.camera_preview);
        multiWinView = findViewById(R.id.fl_monitor_surface);
        closeIntercom = findViewById(R.id.ll_close_talk);
        micSwitch = findViewById(R.id.iv_mic);
        cameraTypeSwitch = findViewById(R.id.iv_change_camera_type);
        tvMicTip = findViewById(R.id.tv_mic);
        tvCameraTypeTip = findViewById(R.id.tv_interval_camera);
        cameraStateSwitch = findViewById(R.id.iv_camera_state);
        tvCameraStateTip = findViewById(R.id.tv_camera_state);
        soundSwitch = findViewById(R.id.iv_sound);
        tvSound = findViewById(R.id.tv_sound);

        //默认相机框的宽度是手机宽的0.3倍，高度是 默认分辨率下的高度
        cameraLayoutParams = (ConstraintLayout.LayoutParams) rlCameraLayout.getLayoutParams();
        cameraLayoutParams.width = (int) (screenWidth * 0.3);
        float defaultRatio = 480.0f / 864.0f;
        cameraLayoutParams.height = (int) (screenWidth * 0.3 / defaultRatio);
        rlCameraLayout.requestLayout();
        rlCameraCloseLayout.requestLayout();

        closeIntercom.setOnClickListener(this);
        micSwitch.setOnClickListener(this);
        cameraTypeSwitch.setOnClickListener(this);
        cameraStateSwitch.setOnClickListener(this);
        soundSwitch.setOnClickListener(this);
    }

    /**
     * 获取摄像头权限
     * Get camera permissions
     */
    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    protected void initData() {
        presenter.initVideoChat(this, cameraPreview, curCameraLensType, false);
        presenter.initMonitor(multiWinView);
        presenter.startMonitor();
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        VideoIntercomActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void startCameraVideoResult(boolean isSuccess, int videoHeight, int videoWidth, String errorStr) {
        if (isSuccess) {
            LogUtils.debugInfo(SDKLogConstant.APP_VIDEO_ENCODE, "打开音视频成功");
            if (presenter != null) {
                if (PermissionUtils.isGranted(Manifest.permission.CAMERA)) {
                    //如果支持摄像机权限，则直接打开镜头
                    openOrCloseCamera(true);
                } else {
                    //反之则关闭
                    openOrCloseCamera(false);
                }
            }

            //音视频打开成功，直接打开扬声器
            if (presenter != null) {
                presenter.openVoice();
            }

            if (presenter != null) {
                presenter.startTalkSound();
            }

            isCurMicOpening = true;
            //默认相机框的宽度是手机宽的0.3倍，高度是 默认分辨率下的高度
            cameraLayoutParams = (ConstraintLayout.LayoutParams) rlCameraLayout.getLayoutParams();
            cameraLayoutParams.width = (int) (screenWidth * 0.3);
            float defaultRatio = videoWidth * 1.0f / videoHeight;
            cameraLayoutParams.height = (int) (screenWidth * 0.3 / defaultRatio);
            rlCameraLayout.requestLayout();
            rlCameraCloseLayout.requestLayout();

        } else {
            XMPromptDlg.onShowErrorDlg(this, errorStr, null, true);
        }
    }

    private void openOrCloseCamera(boolean isOpen) {
        if (presenter != null) {
            if (isOpen) {
                presenter.openCamera();
                cameraStateSwitch.setImageResource(R.mipmap.ic_income_call_camera_opening);
                tvCameraStateTip.setText(R.string.dev_income_call_camera_has_closed);
            } else {
                isCurCameraVideoRecording = false;
                presenter.closeCamera();
                rlCameraCloseLayout.setVisibility(View.VISIBLE);
                cameraPreview.setVisibility(View.GONE);
                cameraStateSwitch.setImageResource(R.mipmap.ic_income_call_camera_unopen);
                tvCameraStateTip.setText(R.string.dev_income_call_camera_has_closed);
            }
        }
    }

    @Override
    public void stopCameraVideoResult(boolean isSuccess, boolean isNeedFinish) {

    }

    @Override
    public void onVideoTalkFunctionNoEnable() {

    }

    @Override
    public void closeCameraResult() {
        rlCameraCloseLayout.setVisibility(View.VISIBLE);
        cameraPreview.setVisibility(View.GONE);
        isCurCameraVideoRecording = false;
        cameraStateSwitch.setImageResource(R.mipmap.ic_income_call_camera_unopen);
        tvCameraStateTip.setText(R.string.dev_income_call_camera_has_closed);
    }

    @Override
    public void openCameraResult() {
        rlCameraCloseLayout.setVisibility(View.GONE);
        cameraPreview.setVisibility(View.VISIBLE);
        isCurCameraVideoRecording = true;
        cameraStateSwitch.setImageResource(R.mipmap.ic_income_call_camera_open);
        tvCameraStateTip.setText(R.string.dev_income_call_camera_has_opened);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroyMonitor();
        presenter.release();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_close_talk:
                finish();
                break;
            case R.id.iv_mic:
                closeOrOpenMic();
                break;
            case R.id.iv_sound:
                closeOrOpenVoice();
                break;
            case R.id.iv_change_camera_type:
                curCameraLensType = curCameraLensType == CameraCharacteristics.LENS_FACING_FRONT ? CameraCharacteristics.LENS_FACING_BACK : CameraCharacteristics.LENS_FACING_FRONT;
                if (presenter != null) {
                    presenter.changeCameraLensType(curCameraLensType);
                    //切换镜头后，如果当前相机关闭，直接打开当前相机
                    if (!isCurCameraVideoRecording) {
                        openOrCloseCamera(true);
                    }
                }
                break;
            case R.id.iv_camera_state:
                if (isCurCameraVideoRecording) {
                    openOrCloseCamera(false);
                } else {
                    openOrCloseCamera(true);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 关闭或打开麦克风
     */
    public void closeOrOpenMic() {
        if (presenter != null) {
            if (isCurMicOpening) {
                presenter.closeTalkSound();
                isCurMicOpening = false;
                micSwitch.setImageResource(R.mipmap.dev_income_call_mic_close);
                tvMicTip.setText(R.string.dev_income_call_mic_has_closed);
            } else {
                presenter.startTalkSound();
                isCurMicOpening = true;
                micSwitch.setImageResource(R.mipmap.dev_income_call_mic_open);
                tvMicTip.setText(R.string.dev_income_call_mic_has_opened);
            }
        }
    }

    /**
     * 关闭或打开扬声器
     */
    public void closeOrOpenVoice() {
        if (presenter != null) {
            if (isCurSoundOpening) {
                //如果当前扬声器打开着，那就关闭扬声器
                presenter.closeVoice();
                isCurSoundOpening = false;
                soundSwitch.setImageResource(R.mipmap.dev_income_call_sound_close);
                tvSound.setText(R.string.dev_income_call_sound_has_closed);
            } else {
                //如果当前扬声器关闭，则打开扬声器
                presenter.openVoice();
                isCurSoundOpening = true;
                soundSwitch.setImageResource(R.mipmap.dev_income_call_sound_open);
                tvSound.setText(R.string.dev_income_call_sound_has_opened);
            }
        }
    }
}
