package demo.xm.com.xmfunsdkdemo.ui.device.add.quick.presenter;

import static com.lib.sdk.bean.JsonConfig.GET_CLOUD_CRY_NUM;
import static com.lib.sdk.bean.JsonConfig.GET_RANDOM_USER;
import static com.manager.db.Define.LOGIN_NONE;

import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.basic.BaseJson;
import com.basic.G;
import com.blankj.utilcode.util.ToastUtils;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.sdk.bean.StringUtils;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.utils.XMWifiManager;
import com.utils.XUtils;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.dialog.XMPromptDlg;

import org.json.JSONObject;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.add.quick.listener.DevQuickConnectContract;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Quickly configure WIFI by sending the SSID and password of WIFI in the broadcast packet
 * Created by jiangping on 2018-10-23.
 */
public class DevQuickConnectPresenter extends XMBasePresenter<DeviceManager> implements DevQuickConnectContract.IDevQuickConnectPresenter, DeviceManager.OnQuickSetWiFiListener {
    private ScanResult scanResult;
    private DevQuickConnectContract.IDevQuickConnectView iDevQuickConnectView;
    private XMWifiManager xmWifiManager;
    private Disposable disposable;

    public DevQuickConnectPresenter(@Nullable DevQuickConnectContract.IDevQuickConnectView iDevQuickConnectView) {
        if (iDevQuickConnectView == null) {
            throw new NullPointerException();
        }
        this.iDevQuickConnectView = iDevQuickConnectView;
        xmWifiManager = XMWifiManager.getInstance(iDevQuickConnectView.getContext());
    }

    /**
     * Get the name of the currently connected WiFi hotspot
     *
     * @return
     */
    @Override
    public String getCurSSID() {
        return xmWifiManager.getSSID();
    }

    /**
     * Start the quick configuration to configure the device to the router
     *
     * @param pwd WiFi Password
     */


    /*快速配置wifi,若手机已经连接wifi或热点，则自动获wifi或热点名称*/
    @Override
    public void startQuickSetWiFi(String pwd) {
        WifiInfo wifiInfo = xmWifiManager.getWifiInfo();   //若手机端已连接wifi，则自动获取wifi信息
        DhcpInfo dhcpInfo = xmWifiManager.getDhcpInfo();
        if (scanResult != null && wifiInfo != null && dhcpInfo != null) { //XUtils.initSSID(wifiInfo.getSSID() fix bug
            //获取手机已经连接的wifi信息，并自动填充wifi的SSID
            manager.startQuickSetWiFi(XUtils.initSSID(wifiInfo.getSSID()), pwd, scanResult.capabilities, dhcpInfo, 180 * 1000, this);
        }
    }


    /*快速配置wifi,手机并未连接wifi或热点，需手动输入wifi或热点名称及密码*/
    @Override
    public void startQuickSetWiFiSimple(String ssid, String pwd, int pwdType) {//pwdType = (pwd == "" ? 0 : 1)
        manager.startQuickSetWiFiSimple(ssid, pwd, pwdType, 180 * 1000, this);
    }

    /**
     * Stop the quick configuration to configure the device to the router
     */
    @Override
    public void stopQuickSetWiFi() {
        manager.stopQuickSetWiFi();
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    /**
     * Confirm that the current WiFi information can be obtained normally
     */
    public void checkGetWiFiInfoOk() {
        disposable = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Exception {
                String curSSID = xmWifiManager.getSSID();
                scanResult = xmWifiManager.getCurScanResult(curSSID);
                if (scanResult == null) {
                    emitter.onNext(0);
                } else {
                    emitter.onNext(scanResult);
                }
            }
        }).subscribeOn(Schedulers.newThread()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) {

            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object result) throws Exception {
                if (disposable != null) {
                    disposable.dispose();
                    disposable = null;
                }
                if (result instanceof Integer && (Integer) result == 0) {
                    Toast.makeText(iDevQuickConnectView.getContext(), FunSDK.TS("Network_Error"), Toast.LENGTH_LONG).show();
                } else {
                    if ((((ScanResult) result).frequency > 4900 && ((ScanResult) result).frequency < 5900)) {
                        Toast.makeText(iDevQuickConnectView.getContext(), FunSDK.TS("Frequency_support"), Toast.LENGTH_LONG).show();
                    } else {
                        iDevQuickConnectView.onUpdateView();
                    }
                }
            }
        });
    }

    /**
     * Distribution network result callback
     *
     * @param xmDevInfo Device Information
     * @param errorId   Error Id
     */
    @Override
    public void onQuickSetResult(XMDevInfo xmDevInfo, int errorId) {//Callback for startQuickSetWiFi and startQuickSetWiFiSimple
        if (xmDevInfo != null) {
            getDevRandomUserPwd(xmDevInfo);
            Toast.makeText(iDevQuickConnectView.getContext(), "devId:" + xmDevInfo.getDevId(), Toast.LENGTH_LONG).show();
        } else {
            ToastUtils.showLong("配网失败：" + errorId);
        }
    }

    boolean isInit = false;
    boolean isNeedGetDevRandomUserPwdAgain = true;//是否需要再次获取随机用户名密码（用于在配网成功后，因设备的34567端口还没有建立起来，App通过IP方式去访问设备会失败，所以需要重试）
    /**
     * 获取设备的随机用户名和密码
     */
    private void getDevRandomUserPwd(XMDevInfo xmDevInfo) {
        iDevQuickConnectView.onPrintConfigDev(iDevQuickConnectView.getContext().getString(R.string.start_get_random_user_pwd));
        DeviceManager.getInstance().getDevRandomUserPwd(xmDevInfo, new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                iDevQuickConnectView.onPrintConfigDev(iDevQuickConnectView.getContext().getString(R.string.get_random_user_pwd_s));
                iDevQuickConnectView.onPrintConfigDev(iDevQuickConnectView.getContext().getString(R.string.start_get_dev_token));
                //获取设备登录Token信息：先要登录设备，然后通过DevGetLocalEncToken来获取
                DeviceManager.getInstance().loginDev(devId, new DeviceManager.OnDevManagerListener() {
                    @Override
                    public void onSuccess(String devId, int operationType, Object result) {
                        iDevQuickConnectView.onPrintConfigDev(iDevQuickConnectView.getContext().getString(R.string.get_dev_token_s));
                        //获取设备登录Token信息
                        String devToken = FunSDK.DevGetLocalEncToken(devId);
                        if (!StringUtils.isStringNULL(devToken)) {
                            xmDevInfo.setDevToken(devToken);
                            System.out.println("devToken:" + devToken);
                            getCloudCryNum(xmDevInfo);
                        }else {
                            //如果不支持Token的话直接添加设备
                            //是否要将该设备从其他账号下移除
                            XMPromptDlg.onShow(iDevQuickConnectView.getContext(), iDevQuickConnectView.getContext().getString(R.string.is_need_delete_dev_from_other_account), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addDevice(xmDevInfo, true);
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addDevice(xmDevInfo, false);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                        System.out.println("login:" + errorId);
                        addDevice(xmDevInfo, true);
                        iDevQuickConnectView.onPrintConfigDev(iDevQuickConnectView.getContext().getString(R.string.get_dev_token_f) + errorId);
                    }
                });
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                System.out.println("errorId:" + errorId);
                iDevQuickConnectView.onPrintConfigDev(iDevQuickConnectView.getContext().getString(R.string.get_dev_random_user_pwd_f) + errorId);
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

    boolean isNeedGetCloudTryNum = true;//是否需要再次获取校验码
    /**
     * 获取设备配网成功后的校验码，这个校验码用来给服务器校验能否能进行弱绑定，将原来已经添加到其他账号的设备重新添加当前账号
     */
    private void getCloudCryNum(XMDevInfo xmDevInfo) {
        iDevQuickConnectView.onPrintConfigDev(iDevQuickConnectView.getContext().getString(R.string.start_get_dev_verification_code));
        DevConfigManager devConfigManager = DeviceManager.getInstance().getDevConfigManager(xmDevInfo.getDevId());
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                iDevQuickConnectView.onPrintConfigDev(iDevQuickConnectView.getContext().getString(R.string.get_dev_verification_s));
                if (result instanceof String) {
                    try {
                        org.json.JSONObject jsonObject = new JSONObject((String) result);
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
                iDevQuickConnectView.onPrintConfigDev(iDevQuickConnectView.getContext().getString(R.string.get_dev_verification_f) + errorId);
                if (isNeedGetCloudTryNum && (errorId == -10005 || errorId == -100000)) {
                    //如果获取随机用户名密码超时的话，可以延时1s重试一次
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getCloudCryNum(xmDevInfo);
                        }
                    }, 1000);

                    isNeedGetCloudTryNum = false;
                    return;
                }
            }
        });

        devConfigInfo.setJsonName(GET_CLOUD_CRY_NUM);
        devConfigInfo.setCmdId(1020);
        devConfigManager.setDevCmd(devConfigInfo);
    }

    /**
     * Add device to account
     *
     * @param xmDevInfo             Device Information
     * @param isUnbindDevUnderOther Do you want to unbind all accounts that have been added to this device before?
     */
    private void addDevice(XMDevInfo xmDevInfo, boolean isUnbindDevUnderOther) {
        //未使用AccountManager(包括XMAccountManager或LocalAccountManager)登录（包括账号登录和本地临时登录），只能将设备信息临时缓存，重启应用后无法查到设备信息。
        iDevQuickConnectView.onPrintConfigDev(iDevQuickConnectView.getContext().getString(R.string.start_add_dev_to_account));
        if (DevDataCenter.getInstance().getLoginType() == LOGIN_NONE) {
            DevDataCenter.getInstance().addDev(xmDevInfo);
            DeviceManager.getInstance().setLocalDevLoginInfo(xmDevInfo.getDevId(),xmDevInfo.getDevUserName(),xmDevInfo.getDevPassword(),xmDevInfo.getDevToken());
            iDevQuickConnectView.onPrintConfigDev(iDevQuickConnectView.getContext().getString(R.string.add_dev_to_account_s));
            setDevId(xmDevInfo.getDevId());
            Toast.makeText(iDevQuickConnectView.getContext(), R.string.add_s, Toast.LENGTH_LONG).show();
            iDevQuickConnectView.onAddDevResult(true);
        }else {
            AccountManager.getInstance().addDev(xmDevInfo, isUnbindDevUnderOther, new BaseAccountManager.OnAccountManagerListener() {
                @Override
                public void onSuccess(int msgId) {
                    iDevQuickConnectView.onPrintConfigDev(iDevQuickConnectView.getContext().getString(R.string.add_dev_to_account_s));
                    setDevId(xmDevInfo.getDevId());
                    Toast.makeText(iDevQuickConnectView.getContext(), R.string.add_s, Toast.LENGTH_LONG).show();
                    iDevQuickConnectView.onAddDevResult(true);
                }

                @Override
                public void onFailed(int msgId, int errorId) {
                    Toast.makeText(iDevQuickConnectView.getContext(), iDevQuickConnectView.getContext().getString(R.string.add_f) + ":" + errorId, Toast.LENGTH_LONG).show();
                    iDevQuickConnectView.onAddDevResult(false);
                    iDevQuickConnectView.onPrintConfigDev(iDevQuickConnectView.getContext().getString(R.string.add_dev_to_account_f) + errorId);
                }

                @Override
                public void onFunSDKResult(Message message, MsgContent msgContent) {

                }
            });
        }
    }
}

