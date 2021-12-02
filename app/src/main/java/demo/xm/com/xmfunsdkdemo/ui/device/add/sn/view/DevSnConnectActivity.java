package demo.xm.com.xmfunsdkdemo.ui.device.add.sn.view;

import static com.manager.account.share.ShareInfo.SHARE_ACCEPT;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.share.DevShareQrCodeInfo;
import com.lib.sdk.bean.share.OtherShareDevUserBean;
import com.manager.account.share.ShareInfo;
import com.manager.account.share.ShareManager;
import com.manager.db.Define;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.utils.XUtils;
import com.xm.activity.base.XMBaseActivity;
import com.xm.base.code.ErrorCodeManager;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.listselectitem.extra.view.ExtraSpinner;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.activity.scanqrcode.CaptureActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.FunDevType;
import demo.xm.com.xmfunsdkdemo.ui.device.add.sn.listener.DevSnConnectContract;
import demo.xm.com.xmfunsdkdemo.ui.device.add.sn.presenter.DevSnConnectPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.view.DevMonitorActivity;
import io.reactivex.annotations.Nullable;

/**
 * 序列号连接设备,根据设备类型的序列号及账号密码,或者IP设备端口及账号密码,登录设备.
 * Connect the device with the serial number, and login to the device according to the serial number
 * and account password of the device type, or the IP device port and account password.
 * Created by jiangping on 2018-10-23.
 */
public class DevSnConnectActivity extends DemoBaseActivity<DevSnConnectPresenter> implements DevSnConnectContract.IDevSnConnectView, OnItemSelectedListener, OnClickListener {
    private Spinner devTypeSpinner = null;
    private Spinner devIpTypeSpinner = null;

    private FunDevType devTypeCurr = null;
    private FunDevType devIpTypeCurr = null;

    private EditText devSNEdit;
    private EditText devLoginNameEdit;
    private EditText devLoginPasswdEdit;
    private Button devLoginBtn;

    private EditText devIPEdit;
    private EditText devicePortmedia;
    private EditText devIpLoginNameEdit;
    private EditText devIpLoginPasswdEdit;
    private Button devIpLoginBtn;
    private ImageButton scanQrCodeBtn;

    private ListSelectItem lsiDevType;
    private ExtraSpinner spDevType;
    private EditText devLoginTokenEdit;
    private EditText devPidEdit;
    private final FunDevType[] devTypesSupport = {FunDevType.EE_DEV_NORMAL_MONITOR, FunDevType.EE_DEV_INTELLIGENTSOCKET, FunDevType.EE_DEV_SMALLEYE};
    private int devType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_sn_login);

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.guide_module_title_device_sn));
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(getClass().getName());

        devTypeSpinner = findViewById(R.id.spinnerDeviceType);
        devIpTypeSpinner = findViewById(R.id.spinnerDeviceIpType);
        String[] strsSpinner = new String[devTypesSupport.length];
        for (int i = 0; i < devTypesSupport.length; i++) {
            strsSpinner[i] = getResources().getString(devTypesSupport[i].getTypeStrId());
        }
        ArrayAdapter<String> strsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strsSpinner);
        strsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        devTypeSpinner.setAdapter(strsAdapter);
        devTypeSpinner.setSelection(0);
        devTypeCurr = devTypesSupport[0];
        devTypeSpinner.setOnItemSelectedListener(this);

        devIpTypeSpinner.setAdapter(strsAdapter);
        devIpTypeSpinner.setSelection(0);
        devIpTypeCurr = devTypesSupport[0];
        devIpTypeSpinner.setOnItemSelectedListener(this);

        devSNEdit = findViewById(R.id.editDeviceSN);
        devLoginNameEdit = findViewById(R.id.editDeviceLoginName);
        devLoginPasswdEdit = findViewById(R.id.editDeviceLoginPasswd);
        devLoginBtn = findViewById(R.id.devLoginBtn);
        devLoginBtn.setOnClickListener(this);

        devIPEdit = findViewById(R.id.editDeviceIP);
        devicePortmedia = findViewById(R.id.editDevicePort);
        devIpLoginNameEdit = findViewById(R.id.editDeviceIpLoginName);
        devIpLoginPasswdEdit = findViewById(R.id.editDeviceIpLoginPasswd);
        devIpLoginBtn = findViewById(R.id.devLoginBtnIP);
        devIpLoginBtn.setOnClickListener(this);

        devSNEdit.setText("");
        devIPEdit.setText("");

        scanQrCodeBtn = findViewById(R.id.btnScanCode);
        scanQrCodeBtn.setOnClickListener(this);

        lsiDevType = findViewById(R.id.lsi_dev_type);//Scalable TAB controls
        spDevType = lsiDevType.getExtraSpinner();
        spDevType.initData(new String[]{getString(R.string.normal_ipc), getString(R.string.low_power_dev)}, new Integer[]{0, 21});
        spDevType.setValue(0);
        lsiDevType.setRightText(spDevType.getSelectedName());
        spDevType.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
            @Override
            public void onItemClick(int position, String key, Object value) {
                lsiDevType.setRightText(key);
                lsiDevType.toggleExtraView(true);
            }
        });

        lsiDevType.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                lsiDevType.toggleExtraView();
            }
        });

        devLoginTokenEdit = findViewById(R.id.editDeviceLoginToken);
        devPidEdit = findViewById(R.id.editDevicePid);
    }

    @Override
    public DevSnConnectPresenter getPresenter() {
        return new DevSnConnectPresenter(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (position >= 0 && position < devTypesSupport.length) {
            devTypeCurr = devTypesSupport[position];
            devIpTypeCurr = devTypesSupport[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    // 扫描二维码
    /*Scan the QR code*/
    private void startScanQrCode() {
        Intent intent = new Intent();
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.devLoginBtn: {
                String devId = devSNEdit.getText().toString().trim().toLowerCase();
                presenter.addDev(
                        devId,
                        devLoginNameEdit.getText().toString().trim(),
                        devLoginPasswdEdit.getText().toString().trim(),
                        devLoginTokenEdit.getText().toString().trim(),
                        (Integer) spDevType.getSelectedValue(), devPidEdit.getText().toString());
            }
            break;
            case R.id.devLoginBtnIP: {
                //requestDeviceIpStatus();
            }
            break;
            case R.id.btnScanCode: {
                startScanQrCode();
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        if (requestCode == 1 && responseCode == RESULT_OK) {
            if (null != data) {
                String result = data.getStringExtra("result");
                if (XUtils.isSn(result)) {
                    //设备序列号
                    if (null != devSNEdit) {
                        devSNEdit.setText(result);
                    }
                } else if (result.startsWith("sn:")) {
                    String[] devInfos = result.split(";");

                    //设备序列号
                    if (null != devSNEdit) {
                        devSNEdit.setText(devInfos[0].split(":")[1]);
                    }

                    //设备登录Token
                    if (null != devLoginTokenEdit) {
                        devLoginTokenEdit.setText(devInfos[1].split(":")[1]);
                    }
                } else {
                    try {
                        if (result.startsWith("{")) {
                            //如果不是纯序列号的话，需要解析一下是否为分享的Json数据
                            DevShareQrCodeInfo devShareQrCodeInfo = JSON.parseObject(result, DevShareQrCodeInfo.class);
                            if (devShareQrCodeInfo != null) {
                                long nowTimes = System.currentTimeMillis() / 1000;
                                long oldTimes = devShareQrCodeInfo.getShareTimes();
                                //判断分享的时间和当前扫描的时间差是否超过30分钟
                                if ((nowTimes - oldTimes) > 30 * 60) {
                                    Toast.makeText(this, R.string.sharing_code_has_expired, Toast.LENGTH_LONG).show();
                                    finish();
                                    return;
                                }

                                showWaitDialog();
                                devSNEdit.setText(devShareQrCodeInfo.getDevId());
                                //扫描并添加分享设备
                                ShareManager shareManager = ShareManager.getInstance(this);
                                shareManager.addDevFromShared(
                                        devShareQrCodeInfo.getDevId(),
                                        devShareQrCodeInfo.getUserId(),
                                        devShareQrCodeInfo.getLoginName(),
                                        devShareQrCodeInfo.getPwd(),
                                        devShareQrCodeInfo.getDevType(),
                                        devShareQrCodeInfo.getPermissions());
                                shareManager.addShareManagerListener(new ShareManager.OnShareManagerListener() {
                                    @Override
                                    public void onShareResult(ShareInfo shareInfo) {
                                        if (shareInfo != null && shareInfo.isSuccess()) {
                                            OtherShareDevUserBean otherShareDevUser = new OtherShareDevUserBean();
                                            otherShareDevUser.setDevId(devShareQrCodeInfo.getDevId());
                                            otherShareDevUser.setDevType(devShareQrCodeInfo.getDevType() + "");
                                            otherShareDevUser.setPassword(devShareQrCodeInfo.getPwd());
                                            otherShareDevUser.setUsername(devShareQrCodeInfo.getLoginName());
                                            otherShareDevUser.setShareState(SHARE_ACCEPT);
                                            otherShareDevUser.setDevName(devShareQrCodeInfo.getDevId());//设备名称，这里默认是设备序列号
                                            otherShareDevUser.setDevPermissions(devShareQrCodeInfo.getPermissions());
                                            XMDevInfo xmDevInfo = new XMDevInfo();
                                            xmDevInfo.shareDevInfoToXMDevInfo(otherShareDevUser);
                                            DevDataCenter.getInstance().addDev(xmDevInfo);
                                            onAddDevResult(true, 0);
                                        } else {
                                            Toast.makeText(DevSnConnectActivity.this, R.string.add_share_qr_code_error, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            String[] splitResults = result.split(",");
                            if (splitResults != null && splitResults.length >= 4) {
                                if (null != devSNEdit) {
                                    devSNEdit.setText(splitResults[0]);
                                }

                                if (XUtils.isInteger(splitResults[3])) {
                                    //解析二维码中的设备类型
                                    int devType = Integer.parseInt(splitResults[3]);
                                    if (DevDataCenter.getInstance().isLowPowerDev(devType)) {
                                        spDevType.setValue(21);//低功耗设备
                                        lsiDevType.setRightText(spDevType.getSelectedName());
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    @Override
    public void onAddDevResult(boolean isSuccess, int errorId) {
        String errorMsg = isSuccess ? getString(R.string.add_s) : ErrorCodeManager.getSDKStrErrorByNO(errorId);
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
        if (isSuccess) {
            presenter.setDevId(devSNEdit.getText().toString());
            turnToActivity(DevMonitorActivity.class);
        }
    }
}
