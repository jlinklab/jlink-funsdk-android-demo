package demo.xm.com.xmfunsdkdemo.ui.device.config.serialport.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xm.ui.widget.ItemSetLayout;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.serialport.contract.SerialPortContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.serialport.preseenter.SerialPortPresenter;
import io.reactivex.annotations.Nullable;

/**
 * 设备串口透传
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
                presenter.writeSerialPortData(etInputData.getText().toString().getBytes());
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
        }else {
            showToast(getString(R.string.close_serial_port_s), Toast.LENGTH_LONG);
            btnOpen.setEnabled(true);
            btnClose.setEnabled(false);
            btnSendData.setEnabled(false);
        }
    }

    @Override
    public void onSerialPortResult(String data) {
        receiveData.append(data);
        receiveData.append("\n");
        tvReceiveData.setText(receiveData.toString());
    }
}
