package demo.xm.com.xmfunsdkdemo.ui.device.config.powersavingmode.view;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.xm.activity.base.XMBasePresenter;
import com.xm.base.code.ErrorCodeManager;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.listselectitem.extra.view.ExtraSpinner;

import java.util.HashMap;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.powersavingmode.listener.PowerSavingModeContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.powersavingmode.presenter.PowerSavingModePresenter;

/**
 * 省电模式
 */
public class PowerSavingModeActivity extends DemoBaseActivity<PowerSavingModePresenter> implements PowerSavingModeContract.IPowerSavingModeView {
    private ListSelectItem lsiPowerSavingMode;//省电模式
    private ListSelectItem lsiDelayTime;//延时时间
    private ExtraSpinner<Integer> extraSpinner;
    private SeekBar sbDelayTime;//延时时间拖动条

    @Override
    public PowerSavingModePresenter getPresenter() {
        return new PowerSavingModePresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_saving_mode);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        lsiPowerSavingMode = findViewById(R.id.lsi_power_saving_mode);
        extraSpinner = lsiPowerSavingMode.getExtraSpinner();
        lsiPowerSavingMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lsiPowerSavingMode.toggleExtraView();
            }
        });
        extraSpinner.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener<Integer>() {
            @Override
            public void onItemClick(int position, String key, Integer mode) {
                //如果是触发模式的话，需要显示延时时间设置
                if (mode == 0) {
                    lsiDelayTime.setVisibility(View.VISIBLE);
                } else {
                    lsiDelayTime.setVisibility(View.GONE);
                }

                HashMap sleepModeInfo = presenter.getSleepModeInfo();
                ((LinkedTreeMap) ((LinkedTreeMap) sleepModeInfo.get("System.Sleep")).get("LightSleep")).put("Mode", mode);
                presenter.saveConfig();
                lsiPowerSavingMode.toggleExtraView(true);
                lsiPowerSavingMode.setRightText(key);
            }
        });

        lsiDelayTime = findViewById(R.id.lsi_time_seekbar);
        sbDelayTime = lsiDelayTime.getExtraSeekbar();
        sbDelayTime.setMax(30 * 60);//最大是30分钟 单位 秒
        sbDelayTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lsiDelayTime.setRightText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                HashMap sleepModeInfo = presenter.getSleepModeInfo();
                ((LinkedTreeMap) ((List) ((LinkedTreeMap) ((LinkedTreeMap) sleepModeInfo.get("System.Sleep")).get("LightSleep")).get("Param")).get(0)).put("Delay", seekBar.getProgress());
                ((LinkedTreeMap) ((List) ((LinkedTreeMap) ((LinkedTreeMap) sleepModeInfo.get("System.Sleep")).get("LightSleep")).get("Param")).get(0)).put("Value", 0);
                presenter.saveConfig();
                lsiDelayTime.toggleExtraView(true);
            }
        });

        lsiDelayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lsiDelayTime.toggleExtraView();
            }
        });

        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
    }

    /**
     * 0 - 触发模式 - 锁端触发后短时间内进入浅睡模式（该时间可以设置，参考文档下面的Delay），过了这个时间点后设备会进入深水睡状态无法唤醒
     * <p>
     * 1 - 图传模式 - 锁端被唤醒后上传一张图片成功后立马休眠进入深睡
     * <p>
     * 2 - 监控模式 - 设备处于浅睡状态，支持远程唤醒
     */
    private void initData() {
        String[] keys = new String[]{getString(R.string.trigger_mode),
                getString(R.string.image_transmission_mode),
                getString(R.string.monitor_mode)};

        Integer[] values = new Integer[]{0, 1, 2};
        extraSpinner.initData(keys, values);
    }

    @Override
    public void onGetConfigResult(HashMap<Object, Object> result, int errorId) {
        if (errorId >= 0) {
            LinkedTreeMap sleepMap = (LinkedTreeMap) result.get("System.Sleep");
            LinkedTreeMap lightSleepMap = (LinkedTreeMap) sleepMap.get("LightSleep");
            Double mode = (Double) lightSleepMap.get("Mode");
            extraSpinner.setValue(mode.intValue());
            lsiPowerSavingMode.setRightText(extraSpinner.getSelectedName());

            List<LinkedTreeMap> params = (List<LinkedTreeMap>) lightSleepMap.get("Param");
            if (params != null) {
                LinkedTreeMap linkedTreeMap = params.get(0);
                if (linkedTreeMap != null) {
                    Double delayTime = (Double) linkedTreeMap.get("Delay");
                    sbDelayTime.setProgress(delayTime.intValue());
                }
            }

            //如果是触发模式的话，需要将延时时间显示出来
            if (mode == 0) {
                lsiDelayTime.setVisibility(View.VISIBLE);
            } else {
                lsiDelayTime.setVisibility(View.GONE);
            }
        } else {
            showToast(getString(R.string.libfunsdk_operation_failed) + ":" + ErrorCodeManager.getSDKStrErrorByNO(errorId), Toast.LENGTH_LONG);
        }
    }
}
