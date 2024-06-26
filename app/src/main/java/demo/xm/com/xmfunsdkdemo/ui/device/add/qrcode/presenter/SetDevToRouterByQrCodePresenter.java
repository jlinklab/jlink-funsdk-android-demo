package demo.xm.com.xmfunsdkdemo.ui.device.add.qrcode.presenter;

import static com.lib.sdk.bean.JsonConfig.GET_CLOUD_CRY_NUM;
import static com.lib.sdk.bean.JsonConfig.GET_RANDOM_USER;
import static com.manager.db.Define.LOGIN_NONE;

import android.graphics.Bitmap;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;

import com.basic.G;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lib.EFUN_ERROR;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.struct.CONFIG_IPAddress;
import com.lib.sdk.struct.SDK_CONFIG_NET_COMMON_V2;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.account.XMAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.utils.LogUtils;
import com.utils.XMWifiManager;
import com.utils.XUtils;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.dialog.XMPromptDlg;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.add.qrcode.contract.SetDevToRouterByQrCodeContract;

/**
 * 通过二维码方式添加设备
 * Add devices by means of QR code
 *
 * @author hws
 * @class
 * @time 2020/8/11 17:20
 */
public class SetDevToRouterByQrCodePresenter extends XMBasePresenter<DeviceManager> implements SetDevToRouterByQrCodeContract.ISetDevToRouterByQrCodePresenter {
    private SetDevToRouterByQrCodeContract.ISetDevToRouterByQrCodeView iSetDevToRouterByQrCodeView;
    private XMWifiManager xmWifiManager;
    private boolean isSuccess;//配网是否成功

    public SetDevToRouterByQrCodePresenter(SetDevToRouterByQrCodeContract.ISetDevToRouterByQrCodeView iSetDevToRouterByQrCodeView) {
        this.iSetDevToRouterByQrCodeView = iSetDevToRouterByQrCodeView;
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void initWiFi() {
        xmWifiManager = XMWifiManager.getInstance(iSetDevToRouterByQrCodeView.getContext());
    }

    /**
     * 获取当前连接的WiFi 热点名称
     * Get the name of the currently connected WiFi hotspot
     *
     * @return
     */
    @Override
    public String getConnectWiFiSsid() {
        return XUtils.initSSID(xmWifiManager.getSSID());
    }

    /**
     * 获取带有配网信息的二维码，并开始将设备配置到路由器
     * Get the QR code with the distribution network information and start configuring the device to the router
     *
     * @param wifiPwd
     * @return
     */
    @Override
    public Bitmap startSetDevToRouterByQrCode(String ssid, String wifiPwd) {
        DhcpInfo dhcpInfo = xmWifiManager.getDhcpInfo();
        if (ssid == null) {
            ssid = getConnectWiFiSsid();
        }

        int pwdType = TextUtils.isEmpty(wifiPwd) ? 0 : 1;
        String ipAddress = dhcpInfo != null ? Formatter.formatIpAddress(dhcpInfo.ipAddress) : "";
        String macAddress = XMWifiManager.getWiFiMacAddress().replace(":", "");
        Bitmap bitmap = manager.initDevToRouterByQrCode(ssid, wifiPwd, pwdType, macAddress, ipAddress, new DeviceManager.OnDevWiFiSetListener() {
            @Override
            public void onDevWiFiSetState(int result) {
                System.out.println("result:" + result);
                if (result < 0) {
                    iSetDevToRouterByQrCodeView.onSetDevToRouterResult(false, null);
                    //获取失败后，要延时一段时间 再次尝试主动获取配网结果
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isInit) {
                                manager.startDevToRouterByQrCode();
                            }
                        }
                    }, 2000);

                }
            }

            @Override
            public void onDevWiFiSetResult(XMDevInfo xmDevInfo) {
                //A time-consuming operation that needs to be executed in a sub thread
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.start_lan_check));
                        SDK_CONFIG_NET_COMMON_V2 v2 = new SDK_CONFIG_NET_COMMON_V2();
                        byte[] data = G.ObjToBytes(v2);
                        //LAN search device,3000 is timeout
                        int iRet = FunSDK.DevLANSearch(xmDevInfo.getDevId(), 10000, data);
                        //1 represents that the device is within the local area network, otherwise it is not
                        if (iRet == 1) {
                            iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.check_lan_s_and_check_port));
                            //Detect TCP services for LAN devices ,15 * 1000 is timeout
                            iRet = FunSDK.DevIsDetectTCPService(data, 15 * 1000);
                            //0: Connection failed 1: Connection succeeded
                            if (iRet == 1) {
                                iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.check_port_s));
                                // //After successful connection, proceed with device addition
                            }else {
                                iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.check_port_f) + iRet);
                            }
                        } else {
                            iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.check_lan_f) + iRet);
                        }

                        checkRandomUserPwdNeedGet(xmDevInfo);
                    }
                }.start();
            }
        });

        manager.startDevToRouterByQrCode();
        return bitmap;
    }

    /**
     * 检测随机用户名和密码是否需要获取
     *
     * @param xmDevInfo
     */
    private void checkRandomUserPwdNeedGet(XMDevInfo xmDevInfo) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //Here we can handle some of the processes after the successful distribution network，For example, accessing some configuration information of the device
                //配网成功后 停止二维码配网
                manager.stopDevToRouterByQrCode();
                if (!isInit) {
                    //如果二维码配网的时候，能获取到Token就不需要通过获取随机用户名和密码方式拿到Token
//                    if (StringUtils.isStringNULL(xmDevInfo.getDevToken())) {
//                        getDevRandomUserPwd(xmDevInfo);
//                    } else {
//                        getCloudCryNum(xmDevInfo);
//                    }
                    getDevRandomUserPwd(xmDevInfo);
                    iSetDevToRouterByQrCodeView.onSetDevToRouterResult(true, xmDevInfo);
                    isInit = true;
                }
            }
        });
    }

    private void addDevice(XMDevInfo xmDevInfo, boolean isUnbindDevUnderOther) {
        //如果支持Token，并且不是账号登录的话，设备Token更新后将无法用Token去登录设备了
        iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.start_add_dev_to_account));
        //未使用AccountManager(包括XMAccountManager或LocalAccountManager)登录（包括账号登录和本地临时登录），只能将设备信息临时缓存，重启应用后无法查到设备信息。
        if (DevDataCenter.getInstance().getLoginType() == LOGIN_NONE) {
            DevDataCenter.getInstance().addDev(xmDevInfo);
            DeviceManager.getInstance().setLocalDevLoginInfo(xmDevInfo.getDevId(), xmDevInfo.getDevUserName(), xmDevInfo.getDevPassword(), xmDevInfo.getDevToken());
            iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.add_dev_to_account_s));
            setDevId(xmDevInfo.getDevId());
            iSetDevToRouterByQrCodeView.onAddDevToAccountResult(true, 0);
        } else {
            //配网成功后，将设备添加到账号下
            AccountManager.getInstance().addDev(xmDevInfo, isUnbindDevUnderOther, new BaseAccountManager.OnAccountManagerListener() {
                @Override
                public void onSuccess(int msgId) {
                    iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.add_dev_to_account_s));
                    setDevId(xmDevInfo.getDevId());
                    iSetDevToRouterByQrCodeView.onAddDevToAccountResult(true, 0);
                }

                @Override
                public void onFailed(int msgId, int errorId) {
                    setDevId(xmDevInfo.getDevId());
                    iSetDevToRouterByQrCodeView.onAddDevToAccountResult(false, errorId);
                    iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.add_dev_to_account_f) + errorId);
                }

                @Override
                public void onFunSDKResult(Message msg, MsgContent ex) {

                }
            });
        }
    }

    boolean isInit = false;
    boolean isNeedGetDevRandomUserPwdAgain = true;//是否需要再次获取随机用户名密码（用于在配网成功后，因设备的34567端口还没有建立起来，App通过IP方式去访问设备会失败，所以需要重试）
    boolean isNeedGetCloudTryNum = true;//是否需要再次获取校验码

    /**
     * 获取设备的随机用户名和密码
     */
    private void getDevRandomUserPwd(XMDevInfo xmDevInfo) {
        iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.start_get_random_user_pwd));
        DeviceManager.getInstance().getDevRandomUserPwd(xmDevInfo, new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.get_random_user_pwd_s));
                iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.start_get_dev_token));
                //获取设备登录Token信息：先要登录设备，然后通过DevGetLocalEncToken来获取
                DeviceManager.getInstance().loginDev(devId, new DeviceManager.OnDevManagerListener() {
                    @Override
                    public void onSuccess(String devId, int operationType, Object result) {
                        iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.get_dev_token_s));
                        //获取设备登录Token信息
                        String devToken = FunSDK.DevGetLocalEncToken(devId);
                        if (!StringUtils.isStringNULL(devToken)) {
                            xmDevInfo.setDevToken(devToken);
                            System.out.println("devToken:" + devToken);
                            getCloudCryNum(xmDevInfo);
                        } else {
                            //如果不支持Token的话直接添加设备
                            addDevice(xmDevInfo, true);
                        }
                    }

                    @Override
                    public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                        System.out.println("login:" + errorId);
                        iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.get_dev_token_f) + errorId);
                        addDevice(xmDevInfo, true);
                    }
                });
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                System.out.println("errorId:" + errorId);
                iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.get_dev_random_user_pwd_f) + errorId);
                if (isNeedGetDevRandomUserPwdAgain && (errorId == -10005 || errorId == -100000)) {
                    //如果获取随机用户名密码超时的话，可以延时1s重试一次
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getDevRandomUserPwd(xmDevInfo);
                        }
                    }, 1000);

                    isNeedGetDevRandomUserPwdAgain = false;
                    return;
                }else {
                    //如果Token不为空 就需要获取设备的特征码，否则无法缇娜加到账号下
                    if (xmDevInfo.getDevToken() != null) {
                        getCloudCryNum(xmDevInfo);
                    }else {
                        addDevice(xmDevInfo,true);
                    }
                }
            }
        });
    }

    /**
     * 获取设备配网成功后的校验码，这个校验码用来给服务器校验能否能进行弱绑定，将原来已经添加到其他账号的设备重新添加当前账号
     */
    private void getCloudCryNum(XMDevInfo xmDevInfo) {
        iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.start_get_dev_verification_code));
        DevConfigManager devConfigManager = DeviceManager.getInstance().getDevConfigManager(xmDevInfo.getDevId());
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.get_dev_verification_s));
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

                addDevice(xmDevInfo, true);
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                System.out.println("errorId:" + errorId);
                iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.get_dev_verification_f) + errorId);
                if (isNeedGetCloudTryNum && (errorId == -10005 || errorId == -100000 || errorId == -400009)) {
                    //如果获取设备特征码的话，可以延时1s重试一次
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getCloudCryNum(xmDevInfo);
                        }
                    }, 1000);

                    isNeedGetCloudTryNum = false;
                    return;
                } else {
                    addDevice(xmDevInfo, true);
                }
            }
        });

        devConfigInfo.setJsonName(GET_CLOUD_CRY_NUM);
        devConfigInfo.setCmdId(1020);
        devConfigManager.setDevCmd(devConfigInfo);
    }

    @Override
    public Bitmap startSetDevToRouterByQrCodeSimple(String ssid, String wifiPwd, int pwdType) {
        Bitmap bitmap = manager.initDevToRouterByQrCode(ssid, wifiPwd, pwdType, "020000000000", "192.168.10.1", new DeviceManager.OnDevWiFiSetListener() {
            @Override
            public void onDevWiFiSetState(int result) {
                System.out.println("result:" + result);
                if (result < 0) {
                    iSetDevToRouterByQrCodeView.onSetDevToRouterResult(false, null);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            manager.startDevToRouterByQrCode();
                        }
                    }, 2000);

                }
            }

            @Override
            public void onDevWiFiSetResult(XMDevInfo xmDevInfo) {
                //未使用AccountManager(包括XMAccountManager或LocalAccountManager)登录（包括账号登录和本地临时登录），只能将设备信息临时缓存，重启应用后无法查到设备信息。
                if (DevDataCenter.getInstance().getLoginType() == LOGIN_NONE) {
                    DevDataCenter.getInstance().addDev(xmDevInfo);
                    DeviceManager.getInstance().setLocalDevLoginInfo(xmDevInfo.getDevId(), xmDevInfo.getDevUserName(), xmDevInfo.getDevPassword(), xmDevInfo.getDevToken());
                    setDevId(xmDevInfo.getDevId());
                    iSetDevToRouterByQrCodeView.onAddDevToAccountResult(true, 0);
                } else {
                    AccountManager.getInstance().addDev(xmDevInfo, new BaseAccountManager.OnAccountManagerListener() {
                        @Override
                        public void onSuccess(int msgId) {
                            setDevId(xmDevInfo.getDevId());
                            iSetDevToRouterByQrCodeView.onAddDevToAccountResult(true, 0);
                        }

                        @Override
                        public void onFailed(int msgId, int errorId) {
                            iSetDevToRouterByQrCodeView.onAddDevToAccountResult(false, errorId);
                        }

                        @Override
                        public void onFunSDKResult(Message msg, MsgContent ex) {

                        }
                    });
                }

                iSetDevToRouterByQrCodeView.onSetDevToRouterResult(true, xmDevInfo);
            }
        });

        manager.startDevToRouterByQrCode();
        return bitmap;
    }

    /**
     * 停止配网
     */
    @Override
    public void stopSetDevToRouterByQrCode() {
        manager.unInitDevToRouterByQrCode();
    }

    @Override
    public void syncDevTimeZone() {
        //设置时区
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        float zoneOffset = (float) cal.get(java.util.Calendar.ZONE_OFFSET);
        float zone = (float) (zoneOffset / 60.0 / 60.0 / 1000.0);// 时区，东时区数字为正，西时区为负
        manager.syncDevTimeZone(getDevId(), (int) (-zone * 60), new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.sync_dev_timezone_s));
                syncDevTime();
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.sync_dev_timezone_f) + errorId);
                syncDevTime();
            }
        });
    }

    @Override
    public void syncDevTime() {
        //设置时间
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(calendar.getTime());
        manager.syncDevTime(getDevId(), time, new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.sync_dev_time_s));
                iSetDevToRouterByQrCodeView.onSyncDevTimeResult(true,0);

            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                iSetDevToRouterByQrCodeView.onPrintConfigDev(iSetDevToRouterByQrCodeView.getContext().getString(R.string.sync_dev_time_f) + errorId);
                iSetDevToRouterByQrCodeView.onSyncDevTimeResult(false,errorId);
            }
        });
    }
}
