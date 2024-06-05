package demo.xm.com.xmfunsdkdemo.ui.device.add.manul.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.FunDevType;
import demo.xm.com.xmfunsdkdemo.ui.device.add.manul.listener.DevManulConnectContract;
import demo.xm.com.xmfunsdkdemo.ui.device.add.manul.presenter.DevManulConnectPresenter;
import demo.xm.com.xmfunsdkdemo.ui.user.login.view.UserLoginActivity;
import io.reactivex.annotations.Nullable;

/**
 * 用户添加设备界面,根据设备类型及序列号添加设备,还可以选择局域网中的设备
 * User add device interface, according to the device type and serial number to add devices, but also can select the local area network device
 * Created by jiangping on 2018-10-23.
 */
public class DevManulConnectActivity extends DemoBaseActivity<DevManulConnectPresenter>
        implements DevManulConnectContract.IDevManulConnectView, OnItemClickListener, OnItemSelectedListener, OnClickListener {
    private Spinner devTypeSpinner = null;
    private FunDevType devTypeCurr = null;

    private EditText devSNEdit;
    private Button devAddBtn;
    private ImageButton scanQrCodeBtn;

    private final FunDevType[] devTypesSupport = {
            FunDevType.EE_DEV_NORMAL_MONITOR,
            FunDevType.EE_DEV_INTELLIGENTSOCKET,
            FunDevType.EE_DEV_SCENELAMP,
            FunDevType.EE_DEV_LAMPHOLDER,
            FunDevType.EE_DEV_CARMATE,
            FunDevType.EE_DEV_BIGEYE,
            FunDevType.EE_DEV_SMALLEYE,
            FunDevType.EE_DEV_BOUTIQUEROTOT,
            FunDevType.EE_DEV_SPORTCAMERA,
            FunDevType.EE_DEV_SMALLRAINDROPS_FISHEYE,
            FunDevType.EE_DEV_LAMP_FISHEYE,
            FunDevType.EE_DEV_MINIONS,
            FunDevType.EE_DEV_MUSICBOX,
            FunDevType.EE_DEV_SPEAKER,
            FunDevType.EE_DEV_LINKCENTER,
            FunDevType.EE_DEV_DASH_CAMERA,
            FunDevType.EE_DEV_POWER_STRIP,
            FunDevType.EE_DEV_FISH_FUN,
            FunDevType.EE_DEV_UFO,
            FunDevType.EE_DEV_IDR,
            FunDevType.EE_DEV_BULLET,
            FunDevType.EE_DEV_DRUM,
            FunDevType.EE_DEV_CAMERA
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_add_by_user);

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.guide_module_title_device_add));
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(getClass().getName());

        devTypeSpinner = findViewById(R.id.spinnerDeviceType);
        String[] spinnerStrs = new String[devTypesSupport.length];
        for (int i = 0; i < devTypesSupport.length; i++) {
            spinnerStrs[i] = getResources().getString(devTypesSupport[i].getTypeStrId());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerStrs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        devTypeSpinner.setAdapter(adapter);
        devTypeSpinner.setSelection(0);
        devTypeCurr = devTypesSupport[0];
        devTypeSpinner.setOnItemSelectedListener(this);

        devSNEdit = findViewById(R.id.editDeviceSN);
        devAddBtn = findViewById(R.id.devAddBtn);
        devAddBtn.setOnClickListener(this);

        scanQrCodeBtn = findViewById(R.id.btnScanCode);
        scanQrCodeBtn.setOnClickListener(this);


    }

    @Override
    public void onUpdateView() {

    }

    @Override
    public DevManulConnectPresenter getPresenter() {
        return new DevManulConnectPresenter(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (position >= 0 && position < devTypesSupport.length) {
            devTypeCurr = devTypesSupport[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private int getSpinnerIndexByDeviceType(FunDevType type) {
        for (int i = 0; i < devTypesSupport.length; i++) {
            if (type == devTypesSupport[i]) {
                return i;
            }
        }
        return 0;
    }

    private void startLogin() {
        Intent intent = new Intent();
        intent.setClass(this, UserLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startScanQrCode() {
//        Intent intent = new Intent();
//        intent.setClass(this, CaptureActivity.class);
//        startActivityForResult(intent, 1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.devAddBtn: {

            }
            break;
            case R.id.btnScanCode: {
                startScanQrCode();
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        if (requestCode == 1
                && responseCode == 1) {
            // Demo, 扫描二维码的结果
            if (null != data) {
                String deviceSn = data.getStringExtra("SN");
                if (null != deviceSn && null != devSNEdit) {
                    devSNEdit.setText(deviceSn);
                }
            }
        }
        super.onActivityResult(requestCode, responseCode, data);
    }
}
