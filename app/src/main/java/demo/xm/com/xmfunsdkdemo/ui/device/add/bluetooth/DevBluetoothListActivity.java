package demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lib.SDKCONST;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.bluetooth.XMBleData;
import com.lib.sdk.bean.bluetooth.XMBleInfo;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.manager.bluetooth.IXMBleManagerListener;
import com.manager.bluetooth.XMBleManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.utils.LogUtils;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;

import java.util.HashMap;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.InputWiFiInfoActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth.adapter.DevBluetoothListAdapter;
import demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth.listener.IDevBlueToothView;
import demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth.presenter.DevBluetoothConnectPresenter;
import demo.xm.com.xmfunsdkdemo.utils.SPUtil;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.constant.SDKLogConstant.APP_BLE;
import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.lib.sdk.bean.bluetooth.XMBleData.CMD_CALLBACK;
import static com.lib.sdk.bean.bluetooth.XMBleData.CMD_RECEIVE;
import static com.utils.BleDistributionUtil.combineFlameHeader;
import static com.utils.BleDistributionUtil.createCheckCode;
import static com.utils.BleUtils.CmdId.APP_RESPONSE;
import static com.utils.BleUtils.DataType.NO_ENCRY_BINARY;
import static com.utils.BleUtils.FunId.DMS_BY_BLE;

/**
 * 蓝牙设备列表
 */
@RuntimePermissions
public class DevBluetoothListActivity extends DemoBaseActivity<DevBluetoothConnectPresenter> implements IDevBlueToothView {
    private static final int REQUEST_INPUT_WIFI_INFO_CODE = 0x01;//获取WiFi热点和密码回调
    private static final int REQUEST_LOCATION_SOURCE_SETTINGS = 0x02;
    private RecyclerView rvDevBluetoothList;
    private Button startBleNetwork;//开始蓝牙配对
    private Button stopBleNetwork;//停止蓝牙配对
    private Button searchBleDevs;//搜索蓝牙设备
    private CheckBox cbOnlyNearDev;//仅显示近距离设备
    private DevBluetoothListAdapter devBluetoothListAdapter;
    /**
     * 路由器热点名称
     */
    private String ssid;
    /**
     * 路由器密码
     */
    private String password;
    /**
     * 蓝牙WiFi配对成功存储
     */
    private HashMap<String, Boolean> isBleNetworkSuccess = new HashMap<>();
    /**
     * 是否要停止蓝牙配对
     */
    private boolean isStopBlePairing;
    /**
     * 是否批量操作
     */
    private boolean isMassCtrl;

    @Override
    public DevBluetoothConnectPresenter getPresenter() {
        return new DevBluetoothConnectPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_bluetooth_list);
        rvDevBluetoothList = findViewById(R.id.rv_dev_bluetooth_list);
        rvDevBluetoothList.setLayoutManager(new LinearLayoutManager(this));

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.add_dev_by_bluetooth));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });

        startBleNetwork = findViewById(R.id.btn_start_ble_network);
        startBleNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemCount = devBluetoothListAdapter.getItemCount();
                if (itemCount == 0) {
                    ToastUtils.showLong(R.string.have_no_search_ble_devs);
                } else {
                    Intent intent = new Intent(DevBluetoothListActivity.this, InputWiFiInfoActivity.class);
                    startActivityForResult(intent, REQUEST_INPUT_WIFI_INFO_CODE);
                }

                isMassCtrl = true;
            }
        });

        stopBleNetwork = findViewById(R.id.btn_stop_ble_network);
        stopBleNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBleDevs.setVisibility(View.VISIBLE);
                stopBleNetwork.setVisibility(View.GONE);
                isStopBlePairing = true;
            }
        });

        searchBleDevs = findViewById(R.id.btn_search_ble_dev);
        searchBleDevs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                devBluetoothListAdapter.clearData();
                presenter.setOnlySearchNearDev(cbOnlyNearDev.isChecked());
                presenter.searchDevByBluetooth(DevBluetoothListActivity.this);
                searchBleDevs.setVisibility(View.GONE);
                startBleNetwork.setVisibility(View.VISIBLE);
            }
        });

        cbOnlyNearDev = findViewById(R.id.cb_only_near_dev);
        cbOnlyNearDev.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (devBluetoothListAdapter != null) {
                    devBluetoothListAdapter.clearData();
                    presenter.setOnlySearchNearDev(isChecked);
                    SPUtil.getInstance(DevBluetoothListActivity.this).setSettingParam("isOnlySearchNearDev", isChecked);
                }
            }
        });

        boolean isOnlySearchNearDev = SPUtil.getInstance(DevBluetoothListActivity.this).getSettingParam("isOnlySearchNearDev", false);
        cbOnlyNearDev.setChecked(isOnlySearchNearDev);
        DevBluetoothListActivityPermissionsDispatcher.initDataWithPermissionCheck(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION})
    void initData() {
        devBluetoothListAdapter = new DevBluetoothListAdapter(this);
        rvDevBluetoothList.setAdapter(devBluetoothListAdapter);

        checkBLEPermission();
    }


    /**
     * 检查蓝牙权限是否开启
     */
    private void checkBLEPermission() {
        //判断是否开启GPS
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean ok = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!ok) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, REQUEST_LOCATION_SOURCE_SETTINGS);
            return;
        }

        if (!checkBleToothOpen()) {
            openBleTooth();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //当前版本是Android12及以上 检查蓝牙扫描权限
            PermissionUtils.permission(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT).callback(new PermissionUtils.SimpleCallback() {
                @Override
                public void onGranted() {
                    presenter.setOnlySearchNearDev(cbOnlyNearDev.isChecked());
                    presenter.searchDevByBluetooth(DevBluetoothListActivity.this);
                }

                @Override
                public void onDenied() {

                }
            }).request();
        } else {
            //当前版本是Android12以下 检查蓝牙权限
            if (PermissionUtils.isGranted(Manifest.permission.BLUETOOTH_ADMIN)) {
                presenter.setOnlySearchNearDev(cbOnlyNearDev.isChecked());
                presenter.searchDevByBluetooth(DevBluetoothListActivity.this);
            }
        }
    }

    /**
     * 检查蓝牙是否打开
     *
     * @return
     */
    private boolean checkBleToothOpen() {
        //判断是否打开蓝牙/支持蓝牙
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        return blueAdapter != null && blueAdapter.isEnabled();
    }

    /**
     * 打开蓝牙
     *
     * @return
     */
    private boolean openBleTooth() {
        //判断是否打开蓝牙/支持蓝牙
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!blueAdapter.isEnabled()) {
            return blueAdapter.enable();
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stopSearchDevByBlueTooth();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 蓝牙搜索返回结果
     *
     * @param xmBleInfo 蓝牙搜索包数据
     */
    @Override
    public void onSearchDevBluetoothResult(XMBleInfo xmBleInfo) {
        devBluetoothListAdapter.setData(xmBleInfo);
    }

    /**
     * 连接蓝牙设备结果回调
     *
     * @param mac        mac地址
     * @param resultCode 返回结果
     */
    @Override
    public void onConnectDebBleResult(String mac, int resultCode) {
        hideWaitDialog();
        if (resultCode == REQUEST_SUCCESS) {
            ToastUtils.showLong(R.string.connect_ble_success);
            XMPromptDlg.onShow(this, "请选择是否进行WiFi配网，还是直接蓝牙交互？", "蓝牙交互", "WiFi配网", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    turnToActivity(DevBluetoothCtrlActivity.class, new String[][]{{"bleMac", mac}});
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            devBluetoothListAdapter.setProgress(mac, 50);
                            presenter.connectWiFi(mac, ssid, password);
                        }
                    }, 100);
                }
            });
        } else {
            ToastUtils.showLong(getString(R.string.connect_ble_failed) + ":" + resultCode);
            startNextBelNetwork();
        }
    }

    @Override
    public RecyclerView getRecyclerView() {
        return rvDevBluetoothList;
    }

    /**
     * 蓝牙配网结果回调
     *
     * @param mac       mac地址
     * @param xmBleData 蓝牙配网结果数据
     */
    @Override
    public void onConnectWiFiResult(String mac, XMBleData xmBleData) {
        if (xmBleData != null) {
            isBleNetworkSuccess.put(mac, true);
            dealWithDevInfoFromBleConfig(mac, xmBleData);
        } else {
            isBleNetworkSuccess.put(mac, false);
            ToastUtils.showLong(R.string.distribution_network_failure);
        }
    }

    @Override
    public void onDevBleItemSelected(XMBleInfo xmBleInfo) {
        //如果已经批量配网了，就不支持当个配网
        if (stopBleNetwork.getVisibility() == View.VISIBLE) {
            return;
        }

        Intent intent = new Intent(DevBluetoothListActivity.this, InputWiFiInfoActivity.class);
        intent.putExtra("mac", xmBleInfo.getMac());
        startActivityForResult(intent, REQUEST_INPUT_WIFI_INFO_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DevBluetoothListActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INPUT_WIFI_INFO_CODE) {
            if (resultCode == RESULT_OK) {
                ssid = data.getStringExtra("ssid");
                password = data.getStringExtra("password");
                String mac = data.getStringExtra("mac");
                isBleNetworkSuccess.clear();
                startBleNetwork.setVisibility(View.GONE);
                stopBleNetwork.setVisibility(View.VISIBLE);
                isStopBlePairing = false;
                if (!StringUtils.isStringNULL(mac)) {
                    startNextBelNetwork(mac);
                } else {
                    startNextBelNetwork();
                }
            }
        } else if (requestCode == REQUEST_LOCATION_SOURCE_SETTINGS) {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            //判断是否开启了GPS
            boolean ok = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            //若开启，申请权限
            if (ok) {
                checkBLEPermission();
            }
        }

    }

    /**
     * 开始下一个设备蓝牙配网
     */
    private void startNextBelNetwork(String mac) {
        if (isStopBlePairing) {
            return;
        }

        if (!StringUtils.isStringNULL(mac)) {
            presenter.connectBle(mac);
        } else {
            searchBleDevs.setVisibility(View.VISIBLE);
            stopBleNetwork.setVisibility(View.GONE);
            isStopBlePairing = true;
        }
    }

    private void startNextBelNetwork() {
        if (isMassCtrl) {
            XMBleInfo xmBleInfo = devBluetoothListAdapter.getData(isBleNetworkSuccess.size());
            if (xmBleInfo != null) {
                startNextBelNetwork(xmBleInfo.getMac());
            }
        }
    }

    /**
     * 处理蓝牙配网返回的数据
     *
     * @param data
     */
    private void dealWithDevInfoFromBleConfig(String mac, XMBleData data) {
        if (data == null) {
            return;
        }

        LogUtils.debugInfo(APP_BLE, "Received data from device:" + data.getContentDataHexString());
        try {

            /**
             * V1.0---->如果命令ID是设备端响应ID并且当前状态是配网状态的就认为是设备配网结果回调 或者命令ID是设备端回调ID
             * V2.0及以上版本---->根据不同的命令Id判断设备是回调还是响应
             */
            if (((data.getCmdId() == CMD_RECEIVE) && data.getVersion() == 1) || data.getCmdId() == CMD_CALLBACK) {
                XMBleInfo xmBleInfo = devBluetoothListAdapter.getData(mac);
                HashMap hashMap = XMBleManager.parseBleWiFiConfigResult(xmBleInfo.getProductId(), data.getContentDataHexString());
                if (hashMap != null) {
                    boolean isSuccess = (boolean) hashMap.get("isSuccess");
                    if (data.getVersion() > 1) {
                        //设备端返回配网结果后，APP段需要回一个应答包，如果一直不回复，设备重新进入配网状态
                        presenter.responseReceiveConnectWiFiResult(mac, isSuccess);
                    }
                    if (isSuccess) {
                        XMDevInfo xmDevInfo = (XMDevInfo) hashMap.get("devInfo");
                        if (xmDevInfo == null) {
                            return;
                        }

                        //判断是否为低功耗设备
                        if (DevDataCenter.getInstance().isLowPowerDevByPid(xmDevInfo.getPid())) {
                            SDBDeviceInfo sdbDeviceInfo = xmDevInfo.getSdbDevInfo();
                            sdbDeviceInfo.st_7_nType = SDKCONST.DEVICE_TYPE.IDR;
                            xmDevInfo.setDevType(SDKCONST.DEVICE_TYPE.IDR);
                        }


                        presenter.setDevId(xmDevInfo.getDevId());

                        //如果设备支持Token的话，需要获取设备特征码
                        if (!StringUtils.isStringNULL(xmDevInfo.getDevToken())) {
                            showWaitDialog();
                            presenter.getCloudCryNum(xmDevInfo);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                devBluetoothListAdapter.setProgress(mac, 100);
                                ToastUtils.showLong("配网成功：" + xmDevInfo.getDevId());
                            }
                        });

                        startNextBelNetwork();
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
            if (errorId != -99992) {
                showToast(getString(R.string.Add_Dev_Failed) + ":" + errorId, Toast.LENGTH_LONG);
                return;
            }
        }
    }
}
