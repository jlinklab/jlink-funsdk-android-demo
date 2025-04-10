package demo.xm.com.xmfunsdkdemo.ui.user.login.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.basic.G;
import com.lib.EFUN_ERROR;
import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.manager.account.AccountManager;
import com.manager.account.LocalAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.manager.device.config.PwdErrorManager;
import com.utils.XMWifiManager;
import com.xm.activity.base.XMBaseActivity;
import com.xm.base.code.ErrorCodeManager;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.base.DemoConstant;
import demo.xm.com.xmfunsdkdemo.ui.activity.MainActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.list.view.ChannelListActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.list.view.DevListActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view.AlertSetActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.view.IntelligentVigilanceActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.view.DevMonitorActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.push.view.DevPushService;
import demo.xm.com.xmfunsdkdemo.ui.user.forget.view.UserForgetPwdActivity;
import demo.xm.com.xmfunsdkdemo.ui.user.login.listener.UserLoginContract;
import demo.xm.com.xmfunsdkdemo.ui.user.login.presenter.UserLoginPresenter;
import demo.xm.com.xmfunsdkdemo.ui.user.register.view.UserRegisterActivity;
import demo.xm.com.xmfunsdkdemo.utils.SPUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * 用户登录界面,通过账号或手机号及相应密码进行登录
 * User login interface, login through the account or mobile phone number and the corresponding password
 * Created by jiangping on 2018-10-23.
 */
@RuntimePermissions
public class UserLoginActivity extends DemoBaseActivity<UserLoginPresenter> implements UserLoginContract.IUserLoginView, View.OnClickListener {
    private EditText etUserName;
    private EditText etPwd;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String users;
    private String pwds;
    private Disposable disposable;
    private ScanResult scanResult;
    private XMWifiManager xmWifiManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_login);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setRightTitleText(getString(R.string.user_login_forgot_passwd));
        titleBar.getLeftBtn().setVisibility(View.GONE);
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(UserLoginActivity.class.getName());
        titleBar.setRightTvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                enterForgotPassword();
            }
        });

        Button btnlogin = findViewById(R.id.btn_user_login);
        btnlogin.setOnClickListener(this);
        etUserName = findViewById(R.id.et_user_login_username);
        etPwd = findViewById(R.id.et_user_login_psw);

        Button btnRegister = findViewById(R.id.btn_user_register);
        btnRegister.setOnClickListener(this);

        etUserName.setText(presenter.getUserName());
        etPwd.setText(presenter.getPassword());

        Button btnLoginByLocal = findViewById(R.id.btn_login_by_local);
        btnLoginByLocal.setOnClickListener(view -> presenter.loginByLocal());

        Button btnLoginByAp = findViewById(R.id.btn_login_by_ap);
        btnLoginByAp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLoginActivityPermissionsDispatcher.initWiFiPermissionWithPermissionCheck(UserLoginActivity.this);
            }
        });

        Button btnAccessToDev = findViewById(R.id.btn_direct_access_to_device);
        btnAccessToDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果您的设备都存在您的云端服务器并且不想通过我们的账户系统，那么您可以将您的设备直接添加到App缓存即可，具体实现请查看Demo的示例代码！
                //If your devices are all hosted on your cloud server and you don't want to go through our account system,
                // you can simply add your devices to the app cache. Please refer to the example code in the demo for the specific implementation!
                XMPromptDlg.onShow(UserLoginActivity.this, getString(R.string.not_account_login_tips), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //不用JF账号添加设备并获取设备的话，需要调用一下初始化接口
                        LocalAccountManager.getInstance().init();
                        //并且从第三方服务器获取到的设备列表同步数据给本地缓存,如果有多台设备的话，需要循环添加
//                        SDBDeviceInfo sdbDeviceInfo = new SDBDeviceInfo();
//                        G.SetValue(sdbDeviceInfo.st_0_Devmac, "");//设备序列号
//                        G.SetValue(sdbDeviceInfo.st_5_loginPsw, "");//设置设备登录密码
//                        G.SetValue(sdbDeviceInfo.st_4_loginName, "admin");//设置设备登录名，默认一般是admin
//                        sdbDeviceInfo.st_7_nType = 21;//设备类型，如果设备是低功耗需要唤醒的，必须要设置，比如门铃设备是21，具体值参考https://docs.jftech.com/docs?menusId=ab9a6dddd50c46a6af8a913b472ed015&siderid=1e394db91bc34d908839eeee09cdf5ec
//                        sdbDeviceInfo.setDevToken("");//设备登录Token，如果设备支持Token的话需要设置，不支持的话不需要设置
//                        sdbDeviceInfo.setPid("");//如果设备有PID的话，需要设置
//                        DevDataCenter.getInstance().addDev(sdbDeviceInfo);
//                        FunSDK.AddDevInfoToDataCenter(G.ObjToBytes(sdbDeviceInfo), 0, 0, "");
//                        turnToActivity(DevListActivity.class);
                        turnToActivity(MainActivity.class);
                        finish();
                    }
                });

            }
        });

        (findViewById(R.id.btn_login_history)).setOnClickListener(this);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
    }


    private void initData() {
        DevDataCenter.getInstance().clear();
        users = pref.getString("Users", "");
        pwds = pref.getString("Pwds", "");
        //前往（应用开放平台：https://aops.jftech.com），注册申请成为开放平台开发者，然后到【控制台】-【创应用列表】中创建Android应用，等应用审核通过后就可以获取到AppKey、movedCard和AppSecret等信息
        if (TextUtils.isEmpty(DemoConstant.APP_UUID) || TextUtils.isEmpty(DemoConstant.APP_KEY) || TextUtils.isEmpty(DemoConstant.APP_SECRET) || DemoConstant.APP_MOVEDCARD <= 0) {
            XMPromptDlg.onShow(this, getString(R.string.funsdk_integration_tips), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            return;
        }
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    protected void initWiFiPermission() {
        xmWifiManager = XMWifiManager.getInstance(this);
        if (checkLocationService()) {// Whether to enable location permission
            checkGetWiFiInfoOk();
        } else {
            XMPromptDlg.onShow(UserLoginActivity.this, FunSDK.TS("System_SDK_INT_Tip"), FunSDK.TS("cancel"), FunSDK.TS("confirm"), null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onUpdateView() {
        //After successful login, go to the main screen and open the push service
        if (presenter.getErrorId() == 0) {
            showToast(getString(R.string.user_register_login_success), Toast.LENGTH_LONG);
            if (SPUtil.isEmpty(users)) {
                editor.putString("Users", etUserName.getText().toString());
                editor.putString("Pwds", etPwd.getText().toString());
                editor.apply();
            } else {
                if (!users.contains(etUserName.getText().toString())) {
                    editor.putString("Users", users + "," + etUserName.getText().toString());
                    editor.putString("Pwds", pwds + "," + etPwd.getText().toString());
                    editor.apply();
                }
            }
            turnToActivity(MainActivity.class);
            startService(new Intent(this, DevPushService.class));
            finish();
        } else {
            hideWaitDialog();
            showToast(getString(R.string.user_register_login_fail) + ":" + ErrorCodeManager.getSDKStrErrorByNO(presenter.getErrorId()), Toast.LENGTH_LONG);

        }
    }

    @Override
    public void onTurnToIntelligentVigilance() {
        turnToActivity(IntelligentVigilanceActivity.class);
    }

    @Override
    public void onGetChannelListResult(boolean isSuccess, int resultId) {
        hideWaitDialog();
        if (isSuccess) {
            //如果返回的数据是通道数并且大于1就跳转到通道列表
            /*If the number of channels returned is greater than 1, jump to the list of channels*/
            if (resultId > 1) {
                turnToActivity(ChannelListActivity.class);
            } else {
                turnToActivity(DevMonitorActivity.class);
            }
        } else {
            if (resultId == EFUN_ERROR.EE_DVR_PASSWORD_NOT_VALID) {
                XMDevInfo devInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
                XMPromptDlg.onShowPasswordErrorDialog(this, devInfo.getSdbDevInfo(), 0, new PwdErrorManager.OnRepeatSendMsgListener() {
                    @Override
                    public void onSendMsg(int msgId) {
                        showWaitDialog();
                        presenter.loginByAP();
                    }
                }, false);
            } else if (resultId < 0) {
                showToast(getString(R.string.login_dev_failed) + resultId, Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public UserLoginPresenter getPresenter() {
        return new UserLoginPresenter(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_user_login:
                if (etPwd.getText().toString().trim().length() != 0 && etUserName.getText().toString().trim().length() != 0) {
                    try {
                        showWaitDialog();
                        presenter.loginByAccount(etUserName.getText().toString(), etPwd.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    showToast(getResources().getString(R.string.user_login_please_input_username_and_pwd), Toast.LENGTH_SHORT);
                }
                break;
            case R.id.btn_user_register:
                enterUserRegister();
                break;
            case R.id.btn_login_history:
                showLoginHistory();
            default:
                break;
        }
    }

    private void showLoginHistory() {
        String[] users1 = users.split(",");
        String[] pwd1 = pwds.split(",");
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.please_to_choose)).setSingleChoiceItems(users1, 0, (dialog, which) -> {
            etUserName.setText(users1[which]);
            etPwd.setText(pwd1[which]);
            dialog.dismiss();
        }).setNegativeButton(getResources().getString(R.string.common_cancel), (dialog, which) -> dialog.dismiss()).show();
    }

    //Enter user forgot password
    private void enterForgotPassword() {
        Intent intent = new Intent();
        intent.setClass(this, UserForgetPwdActivity.class);
        startActivity(intent);
    }

    //Go to the user registration
    private void enterUserRegister() {
        Intent intent = new Intent();
        intent.setClass(this, UserRegisterActivity.class);
        startActivity(intent);
    }

    protected boolean checkLocationService() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean isOpenGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            return isOpenGPS;
        } else {
            return true;
        }
    }

    /**
     * Confirm that the current WiFi information can be obtained normally
     */
    private void checkGetWiFiInfoOk() {
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
                    Toast.makeText(UserLoginActivity.this, FunSDK.TS("Network_Error"), Toast.LENGTH_LONG).show();
                } else {
                    String curSSID = xmWifiManager.getSSID();
                    if (!StringUtils.isStringNULL(curSSID)) {
                        XMPromptDlg.onShow(UserLoginActivity.this, "当前手机连接的WiFi热点:【" + curSSID + "】， 请确认该热点是否为您想要访问的设备？", "切换WiFi热点", "继续访问", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showWaitDialog();
                                presenter.loginByAP();
                            }
                        });
                    }
                }
            }
        });
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UserLoginActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
