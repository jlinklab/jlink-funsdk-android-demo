package demo.xm.com.xmfunsdkdemo.ui.device.config.serialport.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ConvertUtils;
import com.xm.ui.widget.ItemSetLayout;

import java.nio.charset.StandardCharsets;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.serialport.contract.SerialPortContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.serialport.preseenter.SerialPortPresenter;
import demo.xm.com.xmfunsdkdemo.utils.TypeConversion;
import io.reactivex.annotations.Nullable;

/**
 * 设备串口透传
 *
 * @author hws
 * @class describe
 * @time 2021/1/4 19:22
 */
public class SerialPortActivity extends BaseConfigActivity<SerialPortPresenter> implements SerialPortContract.ISerialPortView {
    private ItemSetLayout islSendData;
    private ItemSetLayout lslReceiveData;
    private EditText etSendData;
    private Button btnOpen;
    private Button btnClose;
    private Button btnSendData;
    private EditText etInputData;
    private TextView tvReceiveData;
    private StringBuffer receiveData;
    private CheckBox cbHex;//16进制
    private CheckBox cbString;//字符串

    @Override
    public SerialPortPresenter getPresenter() {
        return new SerialPortPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_opt_transmission));
        titleBar.setLeftClick(this);

        islSendData = findViewById(R.id.isl_send_data);
        lslReceiveData = findViewById(R.id.isl_receive_data);

        btnOpen = findViewById(R.id.btn_open);
        btnClose = findViewById(R.id.btn_close);
        btnSendData = findViewById(R.id.btn_send_data);

        btnClose.setEnabled(false);
        btnSendData.setEnabled(false);

        etInputData = islSendData.findViewById(R.id.et_input_data);
        tvReceiveData = lslReceiveData.findViewById(R.id.et_receive_data);

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.openSerialPort();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.closeSerialPort();
            }
        });

        btnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果是16进制数据，需要特殊处理一下传给设备
                if (cbHex.isChecked()) {
                    String hexData = etInputData.getText().toString();
                    byte[] resultData = ConvertUtils.hexString2Bytes(hexData);
                    presenter.writeSerialPortData(resultData);
                } else if (cbString.isChecked()) {
                    presenter.writeSerialPortData(etInputData.getText().toString().getBytes(StandardCharsets.UTF_8));
                }

            }
        });

        cbHex = findViewById(R.id.cb_hex);
        cbString = findViewById(R.id.cb_string);
        cbHex.setChecked(true);
        cbHex.setClickable(false);
        cbHex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbString.setChecked(false);
                    cbHex.setClickable(false);
                    cbString.setClickable(true);
                }
            }
        });

        cbString.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbHex.setChecked(false);
                    cbString.setClickable(false);
                    cbHex.setClickable(true);
                }
            }
        });
    }

    private void initData() {

    }

    @Override
    public void onOpenSerialPortResult(boolean isOpen) {
        if (isOpen) {
            showToast(getString(R.string.open_serial_port_s), Toast.LENGTH_LONG);
            btnOpen.setEnabled(false);
            btnClose.setEnabled(true);
            btnSendData.setEnabled(true);
            receiveData = new StringBuffer();
        } else {
            showToast(getString(R.string.close_serial_port_s), Toast.LENGTH_LONG);
            btnOpen.setEnabled(true);
            btnClose.setEnabled(false);
            btnSendData.setEnabled(false);
        }
    }

    @Override
    public void onSerialPortResult(byte[] data) {
        String info;
        if (cbHex.isChecked()) {
            info = TypeConversion.bytes2HexString(data, data.length);
        } else {
            info = new String(data);
        }
        receiveData.append(info);
        receiveData.append("\n");
        tvReceiveData.setText(receiveData.toString());
    }
}
