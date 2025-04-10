package demo.xm.com.xmfunsdkdemo.ui.device.config.imageconfig.view;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.lib.SDKCONST;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.StringUtils;
import com.manager.device.DeviceManager;
import com.manager.device.media.monitor.MonitorManager;
import com.xm.base.code.ErrorCodeManager;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.DevConfigState;
import demo.xm.com.xmfunsdkdemo.ui.device.config.imageconfig.listener.DevCameraSetContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.imageconfig.presenter.DevCameraSetPresenter;
import demo.xm.com.xmfunsdkdemo.ui.entity.DevCameraConfig;
import io.reactivex.annotations.Nullable;

/**
 * 图像配置界面,改变图像的画质,画像字符叠加，日夜模式
 */
public class DevCameraSetActivity extends BaseConfigActivity<DevCameraSetPresenter>
        implements DevCameraSetContract.IDevCameraSetView, View.OnClickListener {

    /**
     * 两个作用：1.获取：处理图像配置的json数据，并显示到控件上。2.保存：根据控件信息生成json格式数据以用来保存
     */
    private DevCameraConfig mCamera;
    /**
     * AE灵敏度:摄像机所处环境明暗发生快速变化时，摄像机是否需要调整以适应新环境，灵敏度越高切换时间越快，反之越长。
     * 当对着类似经常有车开过的场景时，可以适当降低灵敏度以避免车开过引起的画面闪烁、IR来回切换。
     */
    private Spinner spDefinition;
    /**
     * 上下翻转：
     */
    private ImageButton btnFlip;
    /**
     * 左右翻转
     */
    private ImageButton btnMirror;
    /**
     * 背光补偿：能提供在非常强的背景光线前面目标的理想的曝光；即指能看到逆光的物体，但是物体背后的事物看不清楚；现在主要考虑目标在视野中间位置情况。
     * 此功能打开以后自动曝光算法会优先考虑中间部分的亮度，侧重点会放在中心物体，曝光是全局处理，提升亮度看清中心关注的目标，但周围很可能会过曝；根据侧重点选择背光补偿的开关。
     */
    private ImageButton btnBLCMode;

    /**
     * 日夜模式切换
     */
    private Spinner spDayNightMode;
    private RelativeLayout rlDayNightMode;

    private String[] stringDayNightModes;
    private Integer[] dayNightValues;
    /**
     * 灵敏度选项：6：最好，5：很好，4：好，3：一般，2：较差，1：很差
     */
    private static final Integer[] defValues = {6, 5, 4, 3, 2, 1};
    private ListSelectItem lsiWDR;
    private MonitorManager monitorManager;
    private FrameLayout playView;
    @Override
    public DevCameraSetPresenter getPresenter() {
        return new DevCameraSetPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_setup_camera);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_setup_image));
        titleBar.setRightBtnResource(R.mipmap.icon_save_normal,R.mipmap.icon_save_pressed);
        titleBar.setLeftClick(this);
        titleBar.setRightIvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                tryToSaveConfig();
            }
        });

        mCamera = new DevCameraConfig(JsonConfig.CAMERA_PARAM);
        mCamera.setState(DevConfigState.DEV_CONFIG_UNLOAD);
        spDefinition = findViewById(R.id.sp_dev_set_camera_definition);
        String[] stringDefinitions = getResources().getStringArray(R.array.device_setup_camera_definition_values);
        ArrayAdapter<String> adapterDefinition = new ArrayAdapter<>(this, R.layout.right_spinner_item, stringDefinitions);
        adapterDefinition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDefinition.setAdapter(adapterDefinition);
        spDefinition.setTag(defValues);

        btnFlip = findViewById(R.id.btn_dev_set_camera_flip);
        btnFlip.setOnClickListener(this);
        btnMirror = findViewById(R.id.btn_dev_set_camera_mirror);
        btnMirror.setOnClickListener(this);

        btnBLCMode = findViewById(R.id.btn_dev_set_camera_BLCMode);
        btnBLCMode.setOnClickListener(this);

        spDayNightMode = findViewById(R.id.sp_dev_set_camera_day_night_mode);
        rlDayNightMode = findViewById(R.id.rl_camera_daynight_mode);

        lsiWDR = findViewById(R.id.lsi_wdr);
        lsiWDR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                boolean isOpen = !(lsiWDR.getRightValue() == SDKCONST.Switch.Open);
                lsiWDR.setRightImage(isOpen ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
                presenter.setWDRConfig(isOpen);
            }
        });

        playView = findViewById(R.id.fl_monitor_surface);
    }

    @Override
    public void error(String errorMsg) {
        if (StringUtils.isStringNULL(errorMsg)) {
            finish();
        } else {
            ToastUtils.showShort(errorMsg);
        }
    }

    private void initData() {
        showWaitDialog();
        presenter.getCameraInfo();
    }

    @Override
    public void onUpdateView(String result, boolean isSupportSoftPhotosensitive) {
        hideWaitDialog();
        if (result.charAt(0) != '0' && !StringUtils.isStringNULL(result) && mCamera.onParse(result)) {
            rlDayNightMode.setVisibility(View.VISIBLE);
            mCamera.setState(DevConfigState.DEV_CONFIG_VIEW_VISIABLE);
            spDefinition.setSelection(mCamera.getmAeSensitivity());
            btnFlip.setSelected(mCamera.ismPictureFlip());
            btnMirror.setSelected(mCamera.ismPictureMirror());
            btnBLCMode.setSelected(mCamera.ismBlcMode());
            if (isSupportSoftPhotosensitive) {
                stringDayNightModes = new String[]{getString(R.string.device_setup_daynight_auto_color),
                        getString(R.string.device_setup_daynight_setting_color), getString(R.string.device_setup_daynight_setting_white_black),
                        getString(R.string.device_setup_daynight_setting_whiteLamp_auto), getString(R.string.device_setup_daynight_setting_irLamp_auto)};
                dayNightValues = new Integer[]{0, 1, 2, 4, 5};
            } else {
                stringDayNightModes = new String[]{getString(R.string.device_setup_daynight_auto_color),
                        getString(R.string.device_setup_daynight_setting_color), getString(R.string.device_setup_daynight_setting_white_black)};
                dayNightValues = new Integer[]{0, 1, 2};
            }
            ArrayAdapter<String> adapterDayNight = new ArrayAdapter<>(this, R.layout.right_spinner_item, stringDayNightModes);
            adapterDayNight.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spDayNightMode.setAdapter(adapterDayNight);
            spDayNightMode.setTag(dayNightValues);
            for (int i = 0; i < dayNightValues.length; i++) {
                if (dayNightValues[i] == mCamera.getmDayNightColor()) {
                    spDayNightMode.setSelection(i);
                    break;
                }
            }
        } else {
            mCamera.setState(DevConfigState.DEV_CONFIG_VIEW_INVISIABLE);
            rlDayNightMode.setVisibility(View.GONE);
        }
    }

    @Override
    public void onWDRConfig(boolean isSupport, boolean isOpen) {
        if (isSupport) {
            findViewById(R.id.ll_wdr_layout).setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) playView.getLayoutParams();
            params.height = screenWidth * 9 / 16;
            lsiWDR.setRightImage(isOpen ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
            monitorManager = DeviceManager.getInstance().createMonitorPlayer(playView,presenter.getDevId());
            monitorManager.startMonitor();
        }else {
            lsiWDR.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSetWDRConfigResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        if (isSuccess) {
            showToast(getString(R.string.set_dev_config_success), Toast.LENGTH_SHORT);
        } else {
            showToast(getString(R.string.set_dev_config_failed) + ":" + ErrorCodeManager.getSDKStrErrorByNO(errorId), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onSaveResult(int state) {
        hideWaitDialog();
        if (state == DevConfigState.DEV_CONFIG_UPDATE_SUCCESS) {
            showToast(getString(R.string.set_dev_config_success), Toast.LENGTH_SHORT);
            finish();
        } else {
            showToast(getString(R.string.set_dev_config_failed), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_dev_set_camera_flip:
                btnFlip.setSelected(!btnFlip.isSelected());
                break;
            case R.id.btn_dev_set_camera_mirror:
                btnMirror.setSelected(!btnMirror.isSelected());
                break;
            case R.id.btn_dev_set_camera_BLCMode:
                btnBLCMode.setSelected(!btnBLCMode.isSelected());
                break;
            default:
                break;
        }
    }

    /**
     * 根据控件信息去保存数据
     */
    private void tryToSaveConfig() {
        showWaitDialog();
        mCamera.setmAeSensitivity(defValues[spDefinition.getSelectedItemPosition()]);
        mCamera.setmPictureFlip(btnFlip.isSelected());
        mCamera.setmPictureMirror(btnMirror.isSelected());
        mCamera.setmBlcMode(btnBLCMode.isSelected());
        mCamera.setmDayNightColor(dayNightValues[spDayNightMode.getSelectedItemPosition()]);
        presenter.setCameraInfo(mCamera.getSendMsg());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (monitorManager != null) {
            monitorManager.release();
        }
    }
}
