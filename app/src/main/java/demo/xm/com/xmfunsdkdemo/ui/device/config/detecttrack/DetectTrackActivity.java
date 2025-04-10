package demo.xm.com.xmfunsdkdemo.ui.device.config.detecttrack;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.lib.SDKCONST;
import com.lib.sdk.bean.PtzCtrlInfoBean;
import com.manager.device.DeviceManager;
import com.manager.device.media.monitor.MonitorManager;
import com.xm.base.code.ErrorCodeManager;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.listselectitem.extra.view.ExtraSpinner;
import com.xm.ui.widget.ptzview.PtzView;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;

/**
 * 移动追踪
 */
public class DetectTrackActivity extends BaseConfigActivity<DetectTrackPresenter> implements DetectTrackContract.IDetectTrackView {
    private ListSelectItem lsiEnable;//移动追踪开关
    private ListSelectItem lsiWatchTime;//移动追踪 守望时间
    private ExtraSpinner spWatchTime;
    private ListSelectItem lsiSensitivity;//移动追踪灵敏度
    private ExtraSpinner spSensitivity;
    private FrameLayout playView;
    private MonitorManager monitorManager;
    private LinkedTreeMap<String, Object> dataMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_track);
        initView();
        initData();
    }

    private void initView() {
        lsiEnable = findViewById(R.id.lsi_enable);
        lsiEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                int enable = lsiEnable.getRightValue() == SDKCONST.Switch.Open ? SDKCONST.Switch.Close : SDKCONST.Switch.Open;
                lsiEnable.setRightImage(enable);
                dataMap.put("Enable", enable);
                presenter.setDetectTrack();
            }
        });
        lsiSensitivity = findViewById(R.id.lsi_sensitivity);
        lsiWatchTime = findViewById(R.id.lsi_watch_time);

        spWatchTime = lsiWatchTime.getExtraSpinner();
        spSensitivity = lsiSensitivity.getExtraSpinner();

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.detect_track));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });

        playView = findViewById(R.id.fl_monitor_surface);
        findViewById(R.id.btn_set_watch_preset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                presenter.setWatchPreset();
            }
        });

        PtzView ptzView = findViewById(R.id.ptz_view);
        ptzView.setOnPtzViewListener(new PtzView.OnPtzViewListener() {
            @Override
            public void onPtzDirection(int direction, boolean stop) {
                presenter.devicePTZControl(0, direction, 4, stop);
            }
        });
    }

    private void initData() {
        spWatchTime.initData(new String[]{
                "3s",
                "5s",
                "10s",
                "15s",
                "30s",
                "60s",
                "180s",
                "300s",
                "600s",
                "900s",
                "1800s"}, new Integer[]{3, 5, 10, 15, 30, 60, 180, 300, 600, 900, 1800});
        spWatchTime.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
            @Override
            public void onItemClick(int position, String key, Object value) {
                lsiWatchTime.toggleExtraView(true);
                lsiWatchTime.setRightText(key);
                dataMap.put("ReturnTime", value);
                showWaitDialog();
                presenter.setDetectTrack();
            }
        });
        lsiWatchTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lsiWatchTime.toggleExtraView();
            }
        });

        spSensitivity.initData(new String[]{
                getString(R.string.low),
                getString(R.string.middle),
                getString(R.string.high)}, new Integer[]{0, 1, 2});
        spSensitivity.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
            @Override
            public void onItemClick(int position, String key, Object value) {
                lsiSensitivity.toggleExtraView(true);
                lsiSensitivity.setRightText(key);
                dataMap.put("Sensitivity", value);
                showWaitDialog();
                presenter.setDetectTrack();
            }
        });
        lsiSensitivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lsiSensitivity.toggleExtraView();
            }
        });

        showWaitDialog();
        presenter.getDetectTrack();

        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) playView.getLayoutParams();
        params.height = screenWidth * 9 / 16;
        monitorManager = DeviceManager.getInstance().createMonitorPlayer(playView, presenter.getDevId());
        monitorManager.startMonitor();
    }

    @Override
    public DetectTrackPresenter getPresenter() {
        return new DetectTrackPresenter(this);
    }

    @Override
    public void onGetDetectTrackResult(boolean isSuccess, LinkedTreeMap<String, Object> resultMap, int errorId) {
        hideWaitDialog();
        if (isSuccess) {
            if (resultMap != null) {
                dataMap = resultMap;
                lsiEnable.setRightImage(((Double) resultMap.get("Enable")).intValue());
                spWatchTime.setValue(((Double) resultMap.get("ReturnTime")).intValue());
                lsiWatchTime.setRightText(spWatchTime.getSelectedName());
                spSensitivity.setValue(((Double) resultMap.get("Sensitivity")).intValue());
                lsiSensitivity.setRightText(spSensitivity.getSelectedName());
            }
        } else {
            showToast(getString(R.string.get_dev_config_failed) + ":" + ErrorCodeManager.getSDKStrErrorByNO(errorId), Toast.LENGTH_LONG);
            finish();
        }
    }

    @Override
    public void onSetDetectRackResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        if (isSuccess) {
            showToast(getString(R.string.set_dev_config_success), Toast.LENGTH_LONG);
        } else {
            showToast(getString(R.string.set_dev_config_failed) + ":" + ErrorCodeManager.getSDKStrErrorByNO(errorId), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onSetWatchPresetResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        if (isSuccess) {
            showToast(getString(R.string.libfunsdk_operation_success), Toast.LENGTH_LONG);
        } else {
            showToast(getString(R.string.libfunsdk_operation_failed) + ":" + ErrorCodeManager.getSDKStrErrorByNO(errorId), Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (monitorManager != null) {
            monitorManager.destroyPlay();
        }
    }
}

