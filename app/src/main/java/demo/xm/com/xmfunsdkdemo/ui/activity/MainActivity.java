package demo.xm.com.xmfunsdkdemo.ui.activity;

import static android.Manifest.permission.CHANGE_WIFI_MULTICAST_STATE;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.manager.account.XMAccountManager;
import com.manager.db.DevDataCenter;
import com.utils.AESECBUtils;
import com.utils.MacroUtils;
import com.utils.XUtils;
import com.xm.base.OkHttpManager;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ItemSetLayout;
import com.xm.ui.widget.XTitleBar;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.base.DemoConstant;
import demo.xm.com.xmfunsdkdemo.ui.activity.scanqrcode.CaptureActivity;
import demo.xm.com.xmfunsdkdemo.ui.adapter.ItemSetAdapter;
import demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth.DevBluetoothConnectActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth.DevBluetoothListActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.lan.view.DevLanConnectActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.list.view.DevListActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.qrcode.view.SetDevToRouterByQrCodeActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.quick.view.DevQuickConnectActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.add.sn.view.DevSnConnectActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.push.view.DevPushService;
import demo.xm.com.xmfunsdkdemo.ui.user.forget.view.UserForgetPwdActivity;
import demo.xm.com.xmfunsdkdemo.ui.user.info.view.UserInfoActivity;
import demo.xm.com.xmfunsdkdemo.ui.user.local.view.UserSaveLocalPwdActivity;
import demo.xm.com.xmfunsdkdemo.ui.user.login.view.UserLoginActivity;
import demo.xm.com.xmfunsdkdemo.ui.user.modify.view.UserModifyPwdActivity;
import demo.xm.com.xmfunsdkdemo.ui.user.register.view.UserRegisterActivity;
import io.reactivex.annotations.Nullable;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.manager.db.Define.LOGIN_BY_LOCAL;
import static com.manager.db.Define.LOGIN_NONE;

/**
 * FunSDkDemo主界面,包括用户相关,添加设备,设备列表,
 * FunSDkDemo:main interface, including user related, add device, device list, about Demo and other major modules
 * Created by jiangping on 2018-10-23.
 */
@RuntimePermissions
public class MainActivity extends DemoBaseActivity<MainPresenter> implements MainContract.IMainView {
    private static String APP_KEY = "0621ef206a1d4cafbe0c5545c3882ea8";
    private RecyclerView userlv;
    private RecyclerView devLv;
    private WifiManager.MulticastLock multicastLock;
    private NetworkConnectChangeReceiver networkConnectChangeReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        if (TextUtils.isEmpty(DemoConstant.APP_UUID) || TextUtils.isEmpty(DemoConstant.APP_KEY) || TextUtils.isEmpty(DemoConstant.APP_SECRET) || DemoConstant.APP_MOVEDCARD <= 0) {
            XMPromptDlg.onShow(this, getString(R.string.funsdk_integration_tips), null);
            return;
        }
        MainActivityPermissionsDispatcher.initDataWithPermissionCheck(this);
    }

    /**
     * 获取SD卡读写权限、振动权限、录音权限等
     * Access to SD card read and write permissions, vibration permissions, recording permissions, etc
     */
    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE
            , Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO, CHANGE_WIFI_MULTICAST_STATE})
    protected void initData() {
        allowMulticast();
    }

    /**
     * 获取组播
     */
    private void allowMulticast() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            networkConnectChangeReceiver = new NetworkConnectChangeReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkConnectChangeReceiver, intentFilter);
            multicastLock = wifiManager.createMulticastLock(XUtils.getAppName(getApplicationContext()));
            multicastLock.acquire();
        }
    }

    @Override
    public MainPresenter getPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            String code = data.getStringExtra("result");
            System.out.println("XCTest code: " + code);
            if (!TextUtils.isEmpty(code) && code.contains("qrLogin")) {
                XMAccountManager.getInstance().checkQrCodeForPcLogin(code, new OkHttpManager.OnOkHttpListener<Boolean>() {
                    @Override
                    public void onSuccess(String s, Boolean aBoolean) {
                        showToast(FunSDK.TS("s: " + s + ", aBoolean:" + aBoolean), Toast.LENGTH_LONG);

                    }

                    @Override
                    public void onFailed(int i, String s) {
                        showToast(FunSDK.TS(i + "  s: " + s), Toast.LENGTH_LONG);
                    }
                });
            }

        }
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.getLeftBtn().setVisibility(View.GONE);
        titleBar.setRightTvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, UserInfoActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        titleBar.setRightTitleText(getString(R.string.user_not_login));
        titleBar.setTitleText(getString(R.string.app_name));
        titleBar.setBottomTip(MainActivity.class.getName());

        ItemSetLayout userLayout = findViewById(R.id.item_set_user);
        //如果未登录（包括账号、本地登录），直接隐藏账户相关信息
        if (!DevDataCenter.getInstance().isLoginByAccount()) {
            userLayout.setVisibility(View.GONE);
        } else {
            userLayout.setVisibility(View.VISIBLE);
        }
        userlv = userLayout.findViewById(R.id.item_set_lv);
        ArrayList<String> data = new ArrayList<>();
        data.add(getString(R.string.set_user_register));
        data.add(getString(R.string.set_user_login));
        data.add(getString(R.string.set_user_modify_pwd));
        data.add(getString(R.string.set_user_forget_pwd));
        data.add(getString(R.string.set_user_info));
        data.add(getString(R.string.set_user_save_local_pwd));
        ItemSetAdapter adapter = new ItemSetAdapter(data, new ItemSetAdapter.OnItemSetClickListener() {
            @Override
            public void onItem(int position) {
                switch (position) {
                    case 0:
                        turnToActivity(UserRegisterActivity.class);
                        break;
                    case 1:
                        turnToActivity(UserLoginActivity.class);
                        break;
                    case 2:
                        turnToActivity(UserModifyPwdActivity.class);
                        break;
                    case 3:
                        turnToActivity(UserForgetPwdActivity.class);
                        break;
                    case 4:
                        turnToActivity(UserInfoActivity.class);
                        break;
                    case 5:
                        turnToActivity(UserSaveLocalPwdActivity.class);
                        break;
                    default:
                        break;
                }
            }
        });
        userlv.setLayoutManager(new LinearLayoutManager(this));
        userlv.setAdapter(adapter);

        ItemSetLayout devLayout = findViewById(R.id.item_set_dev);
        devLv = devLayout.findViewById(R.id.item_set_lv);
        data = new ArrayList<>();
        data.add(getString(R.string.add_dev_by_quick_config));
        data.add(getString(R.string.add_dev_by_input_sn));
        data.add(getString(R.string.add_dev_by_router));
        data.add(getString(R.string.set_dev_to_router_by_qr_code));
        data.add(getString(R.string.add_dev_by_bluetooth));
        adapter = new ItemSetAdapter(data, new ItemSetAdapter.OnItemSetClickListener() {
            @Override
            public void onItem(int position) {
                switch (position) {
                    case 0:
                        turnToActivity(DevQuickConnectActivity.class);
                        break;
                    case 1:
                        turnToActivity(DevSnConnectActivity.class);
                        break;
                    case 2:
                        turnToActivity(DevLanConnectActivity.class);
                        break;
                    case 3:
                        turnToActivity(SetDevToRouterByQrCodeActivity.class);
                        break;
                    case 4:
                        turnToActivity(DevBluetoothListActivity.class);
                        break;
                    default:
                        break;
                }
            }
        });
        devLv.setLayoutManager(new LinearLayoutManager(this));
        devLv.setAdapter(adapter);

        ItemSetLayout listLayout = findViewById(R.id.item_set_list);
        listLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DevDataCenter.getInstance().isLoginByAccount()) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, DevListActivity.class);
                    MainActivity.this.startActivity(intent);
                } else {
                    XMPromptDlg.onShow(MainActivity.this, getString(R.string.not_login_dev_list_tips), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, DevListActivity.class);
                            MainActivity.this.startActivity(intent);
                        }
                    });
                }

            }
        });

        ItemSetLayout errorLayout = findViewById(R.id.item_set_error_list);
        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, CheckErrorCodeActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        ItemSetLayout viewDocLayout = findViewById(R.id.item_view_doc);
        viewDocLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBrowser(androidDoc);
            }
        });

        ItemSetLayout AboutLayout = findViewById(R.id.item_set_about);
        AboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AboutActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    //Refresh user login to the top right corner of the page
    private void refreshLoginStat() {
        if (null != titleBar.getRightTitleText()) {
            if (DevDataCenter.getInstance().isLoginByAccount()) {//Both can be considered true for now(DevDataCenter.getInstance().isLoginByAccount())
                titleBar.setRightTitleText(DevDataCenter.getInstance().getAccountUserName());
            } else {
                int loginType = DevDataCenter.getInstance().getLoginType();
                if (loginType == LOGIN_BY_LOCAL) {
                    titleBar.setRightTitleText(getString(R.string.login_by_local));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLoginStat();
    }

    @Override
    public void onUpdateView() {
        refreshLoginStat();
        startService(new Intent(this, DevPushService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (multicastLock != null) {
            multicastLock.release();
        }
    }

    class NetworkConnectChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (StringUtils.contrast(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (multicastLock != null) {
                    multicastLock.release();
                    multicastLock.acquire();
                }
            }
        }
    }
}
