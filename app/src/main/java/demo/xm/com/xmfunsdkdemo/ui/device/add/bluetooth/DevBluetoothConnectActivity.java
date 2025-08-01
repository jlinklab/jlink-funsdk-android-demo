package demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lib.SDKCONST;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.bluetooth.XMBleData;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.manager.bluetooth.XMBleManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.utils.LogUtils;
import com.utils.XMWifiManager;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.dialog.EditDialog;

import java.util.HashMap;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth.listener.IDevBlueToothView;
import demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth.presenter.DevBluetoothConnectPresenter;

import static com.constant.SDKLogConstant.APP_BLE;
import static com.lib.sdk.bean.bluetooth.XMBleData.CMD_CALLBACK;
import static com.lib.sdk.bean.bluetooth.XMBleData.CMD_RECEIVE;

public class DevBluetoothConnectActivity extends DemoBaseActivity<DevBluetoothConnectPresenter> implements IDevBlueToothView {
    private EditText etWiFiSSID;
    private EditText etWiFiPwd;
    private String mac;
    private String pid;
    private XMWifiManager xmWifiManager;

    @Override
    public DevBluetoothConnectPresenter getPresenter() {
        return new DevBluetoothConnectPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_set_wifi_by_bluetooth);
        etWiFiSSID = findViewById(R.id.editWifiSSID);
        etWiFiPwd = findViewById(R.id.editWifiPasswd);

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.add_dev_by_bluetooth));


        findViewById(R.id.btnWifiQuickSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWaitDialog();
                String ssid = etWiFiSSID.getText().toString().trim();
                String pwd = etWiFiPwd.getText().toString().trim();
                presenter.connectWiFi(mac, ssid, pwd);
            }
        });

        initData();
    }

    private void initData() {
        mac = getIntent().getStringExtra("mac");
        pid = getIntent().getStringExtra("pid");
        xmWifiManager = XMWifiManager.getInstance(this);

        etWiFiSSID.setText(xmWifiManager.getSSID());
    }

    @Override
    public void onConnectWiFiResult(String mac, XMBleData xmBleData) {
        if (xmBleData != null) {
            dealWithDevInfoFromBleConfig(xmBleData);
        } else {
            ToastUtils.showLong(R.string.distribution_network_failure);
        }
    }

    /**
     * 处理蓝牙配网返回的数据
     * Process the data returned by Bluetooth distribution network
     *
     * @param data
     */
    private void dealWithDevInfoFromBleConfig(XMBleData data) {
        if (data == null) {
            return;
        }

        LogUtils.debugInfo(APP_BLE, "Received data from device:" + data.getContentDataHexString());
        try {

            //如果命令ID是设备端响应ID并且当前状态是配网状态的就认为是设备配网结果回调 或者命令ID是设备端回调ID
            /*If the command ID is the response ID of the device and the current state is the distribution network status,
             it is considered as a callback result of the device distribution network or the command ID is the callback ID of the device*/
            if ((data.getCmdId() == CMD_RECEIVE) || data.getCmdId() == CMD_CALLBACK) {
                HashMap hashMap = XMBleManager.parseBleWiFiConfigResult(pid, data.getContentDataHexString());
                if (hashMap != null) {
                    showWaitDialog();
                    boolean isSuccess = (boolean) hashMap.get("isSuccess");
                    if (isSuccess) {
                        final XMDevInfo xmDevInfo = (XMDevInfo) hashMap.get("devInfo");
                        presenter.setDevId(xmDevInfo.getDevId());
                        //首先通过pid未判断为低功耗设备，若通过pid未判断为低功耗，进一步根据设备序列号检查设备是否低功耗类型
                        if(DevDataCenter.getInstance().isLowPowerDevByPid(pid)){
                            setDeviceTypeAndGetCloudCryNum(xmDevInfo,true);
                        } else {
                            //检查设备状态判断是否为低功耗设备
                            DeviceManager.getInstance().checkIsIDRDevByCfg(xmDevInfo.getDevId(),new DeviceManager.OnCheckIsIDRDevListener() {
                                @Override
                                public void onCheckIsIDRDevResult(boolean isIDRDev){
                                    setDeviceTypeAndGetCloudCryNum(xmDevInfo,isIDRDev);
                                }
                            });
                        }
                        ToastUtils.showLong("配网成功：" + xmDevInfo.getDevId());
                    } else {
                        if (hashMap.containsKey("errorId")) {
                            int errorId = (int) hashMap.get("errorId");
                            ToastUtils.showLong(getString(R.string.distribution_network_failure) + ":" + errorId);
                            if (errorId == 0x53) {//路由器的密码错误
                                if (hashMap.containsKey("resetDevFlg")) {
                                    int resetDevFlg = (int) hashMap.get("resetDevFlg");
                                    if (resetDevFlg == 0) {
                                        XMPromptDlg.onShow(this, getString(R.string.router_pwd_error_need_reset_dev_tips), null);
                                    } else if (resetDevFlg == 1) {
                                        EditDialog dialog = XMPromptDlg.onShowEditDialog(this, getString(R.string.router_pwd_error_tips), "", new EditDialog.OnEditContentListener() {
                                            @Override
                                            public void onResult(String content) {
                                                String ssid = etWiFiSSID.getText().toString().trim();
                                                presenter.connectWiFi(mac, ssid, content);
                                            }
                                        });

                                        dialog.getContentEdit().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                    }
                                }
                            } else {
//                                0x50 配网失败-未知错误（设备会一直重试，错误会一直回调，不需要APP端回响应包）
//                                0x51 未找到热点
//                                0x52 握手失败
//                                0x53 路由器密码错误
//                                0x54 客户端发送的数据解析异常
//                                0x55 配网失败-未知错误（需要APP端回响应包）
//                                0x57 配网包CRC校验错误
//                                0x58 不支持的命令类型
//                                0x59 不识别的wifi加密类型
//                                0x5A ap列表未扫描到(5G频段或者隐藏SSID)
//                                0x5B wifi无信号
//                                0x5C wifi信号非常差
//                                0x5D wifi信号差
//                                0x5E IP未配置
//                                0x5F 切换到AP模式
                            }

                        }
                    }
                }
            } else if (data.getCmdId() == CMD_RECEIVE) {
                //设备响应上报
                String hexData = data.getContentDataHexString();
                int connectResult = ConvertUtils.hexString2Int(hexData.substring(0, 2));
                //命令发送失败
                if (connectResult == 0x01) {
                    LogUtils.debugInfo(APP_BLE, "Bluetooth distribution network command was sent,and the device responded successfully");
                } else {
                    LogUtils.debugInfo(APP_BLE, "Bluetooth distribution network command was sent,end the device  responded failed");
                    ToastUtils.showLong(R.string.distribution_network_failure);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showLong(R.string.distribution_network_failure);
        }
    }

    @Override
    public void onAddDevResult(XMDevInfo xmDevInfo, boolean isSuccess, int errorId) {
        hideWaitDialog();
        if (!isSuccess) {
            showToast(getString(R.string.Add_Dev_Failed) + ":" + errorId, Toast.LENGTH_LONG);
        }
    }


    private void setDeviceTypeAndGetCloudCryNum(XMDevInfo xmDevInfo,boolean isIDRDev) {
        if (isIDRDev) {
            SDBDeviceInfo sdbDeviceInfo = xmDevInfo.getSdbDevInfo();
            sdbDeviceInfo.st_7_nType = SDKCONST.DEVICE_TYPE.IDR;
            xmDevInfo.setDevType(SDKCONST.DEVICE_TYPE.IDR);
        }

        //如果设备支持Token的话，需要获取设备特征码
        if (!StringUtils.isStringNULL(xmDevInfo.getDevToken())) {
            presenter.getCloudCryNum(xmDevInfo);
        }
    }
}
