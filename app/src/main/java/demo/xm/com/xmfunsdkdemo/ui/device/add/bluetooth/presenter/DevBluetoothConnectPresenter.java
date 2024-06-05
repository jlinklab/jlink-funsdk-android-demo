package demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth.presenter;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.basic.G;
import com.blankj.utilcode.util.ConvertUtils;
import com.google.gson.JsonObject;
import com.lib.EFUN_ERROR;
import com.lib.EUIMSG;
import com.lib.MsgContent;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.bluetooth.XMBleData;
import com.lib.sdk.bean.bluetooth.XMBleInfo;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.account.XMAccountManager;
import com.manager.base.BaseManager;
import com.manager.bluetooth.XMBleManager;
import com.manager.bluetooth.IXMBleManagerListener;
import com.manager.bluetooth.XMBleManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.utils.LogUtils;
import com.utils.XMWifiManager;
import com.xm.activity.base.XMBasePresenter;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth.listener.IDevBlueToothView;

import static com.constant.SDKLogConstant.APP_BLE;
import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.lib.sdk.bean.JsonConfig.GET_CLOUD_CRY_NUM;
import static com.manager.db.Define.LOGIN_NONE;
import static com.utils.BleDistributionUtil.combineFlameHeader;
import static com.utils.BleDistributionUtil.createCheckCode;
import static com.utils.BleUtils.CmdId.APP_RESPONSE;
import static com.utils.BleUtils.DataType.NO_ENCRY_BINARY;
import static com.utils.BleUtils.FunId.DMS_BY_BLE;

import org.json.JSONException;
import org.json.JSONObject;

public class DevBluetoothConnectPresenter extends XMBasePresenter<AccountManager> {
    private IDevBlueToothView iDevBlueToothView;
    private boolean isOnlySearchNearDev;

    public DevBluetoothConnectPresenter(IDevBlueToothView iDevBlueToothView) {
        this.iDevBlueToothView = iDevBlueToothView;
    }

    @Override
    protected AccountManager getManager() {
        return AccountManager.getInstance();
    }

    /**
     * @param context             上下文
     */
    public void searchDevByBluetooth(Context context) {
        XMBleManager.getInstance().setTimeOut(600000).startScan(context, iBleManagerListener);
    }

    public void stopSearchDevByBlueTooth() {
        XMBleManager.getInstance().stopScan();
    }

    public void connectBle(String mac) {
        XMBleManager.getInstance().connect(mac, iBleManagerListener);
    }

    public void connectWiFi(String mac, String ssid, String pwd) {
        XMBleManager.getInstance().connectWiFi(mac, ssid, pwd, "", iBleManagerListener);
    }

    /**
     * 应答接收连接WiFi结果
     *
     * @param isSuccess
     */
    public void responseReceiveConnectWiFiResult(String mac, boolean isSuccess) {
        StringBuilder flameContent = new StringBuilder();
        flameContent.append(isSuccess ? "01" : "00");//1表示成功 0表示失败
        StringBuilder sendData = new StringBuilder();
        String flameHeaderStr = combineFlameHeader("02", APP_RESPONSE, DMS_BY_BLE, NO_ENCRY_BINARY);//帧头
        sendData.append(flameHeaderStr);
        sendData.append(String.format("%04x", flameContent.length() / 2));/**帧内容长度**/
        sendData.append(flameContent);

        String checkCode = createCheckCode(sendData.toString());
        LogUtils.debugInfo(APP_BLE, "checkCode:" + checkCode);
        sendData.append(checkCode);

        String hexData = sendData.toString().toUpperCase();
        LogUtils.debugInfo(APP_BLE, "hexData:" + hexData);
        XMBleManager.getInstance().write(mac, ConvertUtils.hexString2Bytes(sendData.toString().toUpperCase()), iBleManagerListener);
    }

    /**
     * 将设备添加到账号下
     *
     * @param xmDevInfo
     */
    public void addDevToAccount(XMDevInfo xmDevInfo) {
        //未使用AccountManager(包括XMAccountManager或LocalAccountManager)登录（包括账号登录和本地临时登录），只能将设备信息临时缓存，重启应用后无法查到设备信息。
        if (DevDataCenter.getInstance().getLoginType() == LOGIN_NONE) {
            DevDataCenter.getInstance().addDev(xmDevInfo);
            DeviceManager.getInstance().setLocalDevLoginInfo(xmDevInfo.getDevId(),xmDevInfo.getDevUserName(),xmDevInfo.getDevPassword(),xmDevInfo.getDevToken());
            if (iDevBlueToothView != null) {
                iDevBlueToothView.onAddDevResult(xmDevInfo, true, 0);
            }
        }else {
            manager.addDev(xmDevInfo, true, new BaseAccountManager.OnAccountManagerListener() {
                @Override
                public void onSuccess(int msgId) {
                    if (msgId == EUIMSG.SYS_ADD_DEVICE) {
                        if (iDevBlueToothView != null) {
                            iDevBlueToothView.onAddDevResult(xmDevInfo, true, 0);
                        }
                    }
                }

                @Override
                public void onFailed(int msgId, int errorId) {
                    if (msgId == EUIMSG.SYS_ADD_DEVICE) {
                        if (iDevBlueToothView != null) {
                            iDevBlueToothView.onAddDevResult(xmDevInfo, false, errorId);
                        }
                    }
                }

                @Override
                public void onFunSDKResult(Message msg, MsgContent ex) {

                }
            });
        }
    }

    boolean isNeedGetCloudTryNum = true;//是否需要再次获取校验码

    /**
     * 获取设备配网成功后的校验码，这个校验码用来给服务器校验能否能进行弱绑定，将原来已经添加到其他账号的设备重新添加当前账号
     */
    public void getCloudCryNum(XMDevInfo xmDevInfo) {
        DevConfigManager devConfigManager = DeviceManager.getInstance().getDevConfigManager(xmDevInfo.getDevId());
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                if (result instanceof String) {
                    try {
                        JSONObject jsonObject = new JSONObject((String) result);
                        jsonObject = jsonObject.optJSONObject(GET_CLOUD_CRY_NUM);
                        if (jsonObject != null) {
                            String cloudCryNum = jsonObject.optString("CloudCryNum");
                            xmDevInfo.setCloudCryNum(cloudCryNum);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                addDevToAccount(xmDevInfo);
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                System.out.println("errorId:" + errorId);
                if (isNeedGetCloudTryNum && (errorId != EFUN_ERROR.EE_DVR_PASSWORD_NOT_VALID)) {
                    //如果获取随机用户名密码超时的话，可以延时1s重试一次
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getCloudCryNum(xmDevInfo);
                        }
                    }, 1000);

                    isNeedGetCloudTryNum = false;
                    return;
                } else {
                    //如果获取不到设备特征码，直接添加设备到账号
                    addDevToAccount(xmDevInfo);
                }
            }
        });

        devConfigInfo.setJsonName(GET_CLOUD_CRY_NUM);
        devConfigInfo.setCmdId(1020);
        devConfigManager.setDevCmd(devConfigInfo);
    }

    private IXMBleManagerListener iBleManagerListener = new IXMBleManagerListener() {
        @Override
        public void onConnectWiFiResult(String mac, int code) {
            super.onConnectWiFiResult(mac, code);
        }

        @Override
        public void onConnectBleResult(String mac, int code) {
            if (iDevBlueToothView != null) {
                iDevBlueToothView.onConnectDebBleResult(mac, code);
            }

            if (code == REQUEST_SUCCESS) {
                XMBleManager.getInstance().notify(mac, iBleManagerListener);
            }
        }

        @Override
        public void onBleScanResult(XMBleInfo xmBleInfo) {
            //如果只显示近距离的设备，那么信号小于-65的直接丢弃
            if (isOnlySearchNearDev && xmBleInfo.getRssi() < -65) {
                return;
            }

            if (iDevBlueToothView != null) {
                iDevBlueToothView.onSearchDevBluetoothResult(xmBleInfo);
            }
        }

        @Override
        public void onResponse(String mac, XMBleData data, int code) {
            if (iDevBlueToothView != null) {
                iDevBlueToothView.onConnectWiFiResult(mac, data);
            }
        }
    };

    public void setOnlySearchNearDev(boolean onlySearchNearDev) {
        isOnlySearchNearDev = onlySearchNearDev;
    }
}
