package demo.xm.com.xmfunsdkdemo.ui.device.config.advance.view;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.dialog.EditDialog;

import java.util.ArrayList;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.advance.data.GPIOInfo;
import demo.xm.com.xmfunsdkdemo.ui.device.config.advance.listener.DevAdvanceContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.advance.presenter.DevAdvancePresenter;
import io.reactivex.annotations.Nullable;

/**
 * 高级配置界面,该功能主要是一些更加专业化的设置项,包括GPIO口控制等
 * Created by jiangping on 2018-10-23.
 */
public class DevAdvanceActivity extends BaseConfigActivity<DevAdvancePresenter> implements DevAdvanceContract.IDevAdvanceView, AdapterView.OnItemSelectedListener {
    private Spinner gpio1;
    private Spinner gpio2;
    private Button btnModifyOSD;//修改设备名水印
    private TextView tvOSD;//存放水印信息的TextView，该TextView需要设置成invisible

    @Override
    public DevAdvancePresenter getPresenter() {
        return new DevAdvancePresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_setup_expert);

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_setup_expert));
        titleBar.setRightBtnResource(R.mipmap.icon_save_normal, R.mipmap.icon_save_pressed);
        titleBar.setLeftClick(this);
        titleBar.setRightIvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                showWaitDialog();
                presenter.saveGPIO();
            }
        });

        gpio1 = findViewById(R.id.sp_gpio_1);
        String[] gpioValue = getResources().getStringArray(R.array.device_setup_gpio_values);
        ArrayAdapter<String> adapterGpio = new ArrayAdapter<>(this, R.layout.right_spinner_item, gpioValue);
        adapterGpio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Integer[] defValues1 = {0, 1, 2};

        gpio1.setAdapter(adapterGpio);
        gpio1.setTag(defValues1);
        gpio1.setOnItemSelectedListener(this);

        gpio2 = findViewById(R.id.sp_gpio_2);
        gpio2.setAdapter(adapterGpio);
        gpio2.setTag(defValues1);
        gpio2.setOnItemSelectedListener(this);

        btnModifyOSD = findViewById(R.id.btn_modify_dev_name_osd);
        btnModifyOSD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                XMPromptDlg.onShowEditDialog(DevAdvanceActivity.this, getString(R.string.modify_dev_name_osd),"", new EditDialog.OnEditContentListener() {
                    @Override
                    public void onResult(String devName) {
                        tvOSD.setText(devName);

                        //调整TextView宽度，让内容能显示完整/To adjust the width of the TextView so that the content can be fully displayed
                        float fontWidth = tvOSD.getPaint().measureText(devName);
                        int reach = (int) fontWidth % 8;
                        if (reach != 0) {
                            tvOSD.setWidth((int) (fontWidth + 8 - reach));
                        } else {
                            tvOSD.setWidth((int) fontWidth);
                        }
                        presenter.modifyDevNameForOSD(devName,tvOSD);
                    }
                });
            }
        });

        tvOSD = findViewById(R.id.tv_osd);
        initData();
    }

    private void initData() {
        presenter.getGPIO();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == gpio1) {
            presenter.setGPIO(0, i);
        } else if (adapterView == gpio2) {
            presenter.setGPIO(1, i);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onGetGPIOResult(ArrayList<GPIOInfo> gpioInfoArrayList) {
        if (gpioInfoArrayList != null) {
            GPIOInfo gpioInfo = gpioInfoArrayList.get(0);
            gpio1.setSelection(gpioInfo.getStatus());

            gpioInfo = gpioInfoArrayList.get(1);
            gpio2.setSelection(gpioInfo.getStatus());
        }
    }

    @Override
    public void onSaveGPIOResult(boolean isSuccess) {
        hideWaitDialog();
        showToast(isSuccess ? getString(R.string.set_dev_config_success) : getString(R.string.set_dev_config_failed), Toast.LENGTH_LONG);
    }

    /**
     * 修改设备名水印回调/Callback for modifying device name watermark
     *
     * @param isSuccess
     */
    @Override
    public void onModifyDevNameOsd(boolean isSuccess, int errorId) {
        hideWaitDialog();
        showToast(isSuccess ? getString(R.string.libfunsdk_operation_success) : getString(R.string.libfunsdk_operation_failed) + ":" + errorId, Toast.LENGTH_LONG);
    }
}
