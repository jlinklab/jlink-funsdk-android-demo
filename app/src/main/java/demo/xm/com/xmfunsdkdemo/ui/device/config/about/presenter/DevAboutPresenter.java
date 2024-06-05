package demo.xm.com.xmfunsdkdemo.ui.device.config.about.presenter;

import com.alibaba.fastjson.JSON;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.SysDevAbilityInfoBean;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.manager.sysability.OnSysAbilityResultListener;
import com.manager.sysability.SysAbilityManager;
import com.utils.FileUtils;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication;
import demo.xm.com.xmfunsdkdemo.ui.device.config.about.listener.DevAboutContract;

import static com.manager.device.DeviceManager.UPGRADE_TYPE_CLOUD;
import static com.manager.device.DeviceManager.UPGRADE_TYPE_FILE_DOWNLOAD;
import static com.manager.device.DeviceManager.UPGRADE_TYPE_LOCAL_FILE;
import static com.manager.device.DeviceManager.UPGRADE_TYPE_NONE;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 关于设备界面,包含设备基本信息(序列号,设备型号,硬件版本,软件版本,
 * 发布时间,设备时间,运行时间,网络模式,云连接状态,固件更新及恢复出厂设置)
 * Created by jiangping on 2018-10-23.
 */
public class DevAboutPresenter extends XMBasePresenter<DeviceManager>
        implements DevAboutContract.IDevAboutPresenter, DeviceManager.OnDevUpgradeListener {

    private DevAboutContract.IDevAboutView iDevAboutView;
    private DevConfigManager devConfigManager;
    /**
     * 升级类型
     * 没有可升级的
     * {@link com.manager.device.DeviceManager#UPGRADE_TYPE_NONE}
     * 云升级
     * {@link com.manager.device.DeviceManager#UPGRADE_TYPE_CLOUD}
     * 固件下载后升级
     * {@link com.manager.device.DeviceManager#UPGRADE_TYPE_FILE_DOWNLOAD}
     * 选择本地固件升级
     * {@link com.manager.device.DeviceManager#UPGRADE_TYPE_LOCAL_FILE}
     */
    private int upgradeType;

    public DevAboutPresenter(DevAboutContract.IDevAboutView iDevAboutView) {
        this.iDevAboutView = iDevAboutView;
    }

    @Override
    public void setDevId(String devId) {
        devConfigManager = manager.getDevConfigManager(devId);
        super.setDevId(devId);
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void getDevInfo() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                if (result instanceof String) {
                    iDevAboutView.onUpdateView((String) result);
                } else {
                    iDevAboutView.onUpdateView(JSON.toJSONString(result));
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevAboutView.onUpdateView("数据获取失败：" + errorId);
            }
        });

        devConfigInfo.setJsonName(JsonConfig.SYSTEM_INFO);
        devConfigInfo.setChnId(-1);
        devConfigManager.getDevConfig(devConfigInfo);
    }

    @Override
    public void checkDevUpgrade() {
        manager.checkDevUpgrade(getDevId(), this);
    }

    @Override
    public void startDevUpgrade() {
        manager.startDevUpgrade(getDevId(), upgradeType, this);
    }

    @Override
    public void stopDevUpgrade() {
    }

    @Override
    public boolean isDevUpgradeEnable() {
        return upgradeType != UPGRADE_TYPE_NONE;
    }

    @Override
    public void startDevLocalUpgrade(String filePath) {
        manager.startDevUpgradeByLocalFile(getDevId(), filePath, this);
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
                iDevAboutView.syncDevTimeZoneResult(true, 0);
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                iDevAboutView.syncDevTimeZoneResult(false, errorId);
            }
        });
    }

    @Override
    public void syncDevTime() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(calendar.getTime());
        manager.syncDevTime(getDevId(), time, new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                iDevAboutView.syncDevTimeResult(true, 0);
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                iDevAboutView.syncDevTimeResult(false, errorId);
            }
        });
    }

    @Override
    public void getDevOemId(Context context) {
        SysAbilityManager.getInstance().getSysDevAbilityInfos(context, getDevId(), false, new OnSysAbilityResultListener<SysDevAbilityInfoBean>() {
            @Override
            public void onSupportResult(SysDevAbilityInfoBean sysDevAbilityInfoBean, boolean b) {
                if (sysDevAbilityInfoBean != null) {
                    iDevAboutView.onGetDevOemIdResult(sysDevAbilityInfoBean.getMfrsOemId());
                }
            }
        });
    }

    @Override
    public void onUpgradeProgress(int state, int progress, int error, String msg) {
        iDevAboutView.onDevUpgradeProgressResult(state, progress);
    }

    @Override
    public void onCheckUpgradeResult(int upgradeType, String verInfo, int errorId) {
        this.upgradeType = upgradeType;
        switch (upgradeType) {
            case UPGRADE_TYPE_NONE:
                iDevAboutView.onCheckDevUpgradeResult(true, false);
                break;
            case UPGRADE_TYPE_CLOUD:
            case UPGRADE_TYPE_FILE_DOWNLOAD:
                iDevAboutView.onCheckDevUpgradeResult(true, true);
                break;
            case UPGRADE_TYPE_LOCAL_FILE:
                break;
            default:
                break;
        }
    }

    public String saveFileFromUri(Context context, Uri uri) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                String filePath = SDKDemoApplication.DEFAULT_PATH + File.separator + "local_update.bin";
                if (FileUtils.isFileExist(filePath)) {
                    FileUtils.deleteFile(filePath);
                }

                File outputFile = new File(filePath);  // 保存到私有目录的文件名和扩展名
                outputStream = new FileOutputStream(outputFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                inputStream.close();
                return filePath;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}

