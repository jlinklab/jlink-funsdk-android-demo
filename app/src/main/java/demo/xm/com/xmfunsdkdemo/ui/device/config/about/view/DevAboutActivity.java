package demo.xm.com.xmfunsdkdemo.ui.device.config.about.view;

import static com.lib.sdk.bean.SystemInfoBean.CONNECT_TYPE_P2P;
import static com.lib.sdk.bean.SystemInfoBean.CONNECT_TYPE_RPS;
import static com.lib.sdk.bean.SystemInfoBean.CONNECT_TYPE_RTS;
import static com.lib.sdk.bean.SystemInfoBean.CONNECT_TYPE_RTS_P2P;
import static com.lib.sdk.bean.SystemInfoBean.CONNECT_TYPE_TRANSMIT;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.ECONFIG;
import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.SysDevAbilityInfoBean;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.sysability.SysAbilityManager;
import com.utils.FileUtils;
import com.utils.XUtils;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ItemSetLayout;
import com.xm.ui.widget.ListSelectItem;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.about.listener.DevAboutContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.about.presenter.DevAboutPresenter;
import io.reactivex.annotations.Nullable;

/**
 * 关于设备界面,包含设备基本信息(序列号,设备型号,硬件版本,软件版本,
 * 发布时间,设备时间,运行时间,网络模式,云连接状态,固件更新及恢复出厂设置)
 * Created by jiangping on 2018-10-23.
 */
public class DevAboutActivity extends BaseConfigActivity<DevAboutPresenter> implements DevAboutContract.IDevAboutView, View.OnClickListener {
    private TextView devSnText = null;
    private TextView devModelText = null;
    private TextView devHWVerText = null;
    private TextView devSWVerText = null;
    private TextView devPubDateText = null;
    private TextView devPubTimeText = null;
    private TextView devRunTimeText = null;
    private TextView devNatCodeText = null;
    private TextView devNatStatusText = null;
    private ImageView devSNCodeImg = null;
    private TextView devUpdateText = null;
    private ListSelectItem lsiDevUpgrade;
    private ListSelectItem lsiDevPid;//设备PID信息
    private ListSelectItem lsiDevLocalUpgrade;//本地升级
    private ListSelectItem lsiSyncDevTime;//同步时间
    private ListSelectItem lsiSyncDevTimeZone;//同步时区
    private ListSelectItem lsiOemId;//OEMID信息
    private ListSelectItem lsiICCID;//ICCID信息
    private ListSelectItem lsiIMEI;//IMEI信息
    private ListSelectItem lsiNetworkMode;//网络模式
    private Button defaltConfigBtn = null;
    private TextView tvDevInfo;
    private boolean isLocalUpgrade;//是否为本地升级
    private static final int SYS_LOCAL_FILE_REQUEST_CODE = 0x08;
    private String firmwareType;//固件類型 默认是System（主控），Mcu（单片机）

    @Override
    public DevAboutPresenter getPresenter() {
        return new DevAboutPresenter(this);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_system_info);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_system_info));
        titleBar.setLeftClick(this);

        devSnText = findViewById(R.id.textDeviceSN);
        devModelText = findViewById(R.id.textDeviceModel);
        devHWVerText = findViewById(R.id.textDeviceHWVer);
        devSWVerText = findViewById(R.id.textDeviceSWVer);
        devPubDateText = findViewById(R.id.textDevicePubDate);

        devRunTimeText = findViewById(R.id.textDeviceRunTime);
        devNatCodeText = findViewById(R.id.textDeviceNatCode);
        devNatStatusText = findViewById(R.id.textDeviceNatStatus);
        devSNCodeImg = findViewById(R.id.imgDeviceQRCode);
        devUpdateText = findViewById(R.id.textDeviceUpgrade);
        defaltConfigBtn = findViewById(R.id.defealtconfig);
        defaltConfigBtn.setOnClickListener(this);
        tvDevInfo = ((ItemSetLayout) findViewById(R.id.isl_device_info)).getMainLayout().findViewById(R.id.tv_content);

        lsiDevUpgrade = findViewById(R.id.lsi_check_dev_upgrade);
        lsiDevUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (presenter.isDevUpgradeEnable()) {
                    presenter.startDevUpgrade();
                } else {
                    showToast(getString(R.string.already_latest), Toast.LENGTH_LONG);
                }
            }
        });

        lsiDevLocalUpgrade = findViewById(R.id.lsi_local_dev_upgrade);
        lsiDevLocalUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, SYS_LOCAL_FILE_REQUEST_CODE);
            }
        });

        lsiDevPid = findViewById(R.id.lsi_dev_pid);

        lsiSyncDevTime = findViewById(R.id.lsi_sync_dev_time);
        lsiSyncDevTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                presenter.syncDevTime();
            }
        });

        lsiSyncDevTimeZone = findViewById(R.id.lsi_sync_dev_time_zone);
        lsiSyncDevTimeZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                presenter.syncDevTimeZone();
            }
        });

        lsiOemId = findViewById(R.id.lsi_dev_oemid);
        lsiICCID = findViewById(R.id.lsi_iccid);
        lsiIMEI = findViewById(R.id.lsi_imei);
        lsiNetworkMode = findViewById(R.id.lsi_network_mode);
    }

    private void initData() {
        Intent intent = getIntent();
        firmwareType = intent.getStringExtra("firmwareType");
        if (StringUtils.isStringNULL(firmwareType)) {
            firmwareType = "System";
        }

        presenter.getDevInfo(firmwareType);
        presenter.getDevCapsAbility(this);

        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
        if (xmDevInfo != null) {
            lsiDevPid.setRightText(StringUtils.isStringNULL(xmDevInfo.getPid()) ? "" : xmDevInfo.getPid());
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onUpdateView(String result) {
        tvDevInfo.setText(result);
    }

    @Override
    public void onCheckDevUpgradeResult(boolean isSuccess, boolean isNeedUpgrade) {
        if (isNeedUpgrade) {
            lsiDevUpgrade.setRightText(getString(R.string.have_new_version_click_to_upgrade));
        } else {
            lsiDevUpgrade.setRightText(getString(R.string.already_latest));
        }
    }

    @Override
    public void onDevUpgradeProgressResult(int upgradeState, int progress) {
        switch (upgradeState) {
            //正在下载升级包
            case ECONFIG.EUPGRADE_STEP_DOWN:
                if (isLocalUpgrade) {
                    lsiDevLocalUpgrade.setRightText(getString(R.string.download_dev_firmware) + ":" + progress);
                } else {
                    lsiDevUpgrade.setRightText(getString(R.string.download_dev_firmware) + ":" + progress);
                }
                break;
            //正在上传
            case ECONFIG.EUPGRADE_STEP_UP:
                if (isLocalUpgrade) {
                    lsiDevLocalUpgrade.setRightText(getString(R.string.upload_dev_firmware) + ":" + progress);
                } else {
                    lsiDevUpgrade.setRightText(getString(R.string.upload_dev_firmware) + ":" + progress);
                }
                break;
            //正在升级
            case ECONFIG.EUPGRADE_STEP_UPGRADE:
                if (isLocalUpgrade) {
                    lsiDevLocalUpgrade.setRightText(getString(R.string.dev_upgrading) + ":" + progress);
                } else {
                    lsiDevUpgrade.setRightText(getString(R.string.dev_upgrading) + ":" + progress);
                }
                break;
            //升级完成
            case ECONFIG.EUPGRADE_STEP_COMPELETE:
                if (isLocalUpgrade) {
                    lsiDevLocalUpgrade.setRightText(getString(R.string.completed_dev_upgrade));
                } else {
                    lsiDevUpgrade.setRightText(getString(R.string.completed_dev_upgrade));
                }

                isLocalUpgrade = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void syncDevTimeZoneResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        showToast(isSuccess ? getString(R.string.set_dev_config_success) : getString(R.string.set_dev_config_failed) + ":" + errorId, Toast.LENGTH_LONG);
    }

    @Override
    public void syncDevTimeResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        showToast(isSuccess ? getString(R.string.set_dev_config_success) : getString(R.string.set_dev_config_failed) + ":" + errorId, Toast.LENGTH_LONG);
    }

    @Override
    public void onGetDevOemIdResult(String oemId) {
        lsiOemId.setRightText(oemId);
    }

    @Override
    public void onGetICCIDResult(String iccid) {
        if (iccid != null) {
            lsiICCID.setVisibility(View.VISIBLE);
            lsiICCID.setRightText(iccid);
        }
    }

    @Override
    public void onGetIMEIResult(String imei) {
        if (imei != null) {
            lsiIMEI.setVisibility(View.VISIBLE);
            lsiIMEI.setRightText(imei);
        }
    }

    @Override
    public void onDevUpgradeFailed(int errorId) {
        //如果升级失败了，提示失败原因，然后重新检测升级
        XMPromptDlg.onShow(this, FunSDK.TS("TR_download_failure_click") + ":" + errorId, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onDevNetConnectMode(int netConnectType) {
        if (netConnectType == CONNECT_TYPE_P2P) {
            lsiNetworkMode.setRightText("P2P");
        } else if (netConnectType == CONNECT_TYPE_TRANSMIT) {
            lsiNetworkMode.setRightText(getString(R.string.settings_about_transmit_mode));
        } else if (netConnectType == CONNECT_TYPE_RPS) {
            lsiNetworkMode.setRightText(FunSDK.TS("RPS"));
        } else if (netConnectType == CONNECT_TYPE_RTS_P2P) {
            lsiNetworkMode.setRightText(FunSDK.TS("RTS P2P"));
        } else if (netConnectType == CONNECT_TYPE_RTS) {
            lsiNetworkMode.setRightText(FunSDK.TS("RTS Proxy"));
        } else {
            lsiNetworkMode.setRightText("IP");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SYS_LOCAL_FILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String filePath = presenter.saveFileFromUri(this, uri);
                XMPromptDlg.onShow(DevAboutActivity.this, getString(R.string.please_sel_firmware_type), getString(R.string.main_control), getString(R.string.mcu), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.startDevLocalUpgrade(firmwareType, filePath);
                        isLocalUpgrade = true;
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.startDevLocalUpgrade(firmwareType, filePath);
                        isLocalUpgrade = true;
                    }
                });

            }
        }
    }
}
