package demo.xm.com.xmfunsdkdemo.ui.device.config.about.presenter;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.SysDevAbilityInfoBean;
import com.manager.XMFunSDKManager;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
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
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 关于设备界面,包含设备基本信息(序列号,设备型号,硬件版本,软件版本,
 * 发布时间,设备时间,运行时间,网络模式,云连接状态,固件更新及恢复出厂设置)
 * Created by jiangping on 2018-10-23.
 */
public class DevAboutPresenter extends XMBasePresenter<DeviceManager>
        implements DevAboutContract.IDevAboutPresenter, DeviceManager.OnDevUpgradeListener, IFunSDKResult {

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
    private int userId;
    private String sysInfoExJson;
    private String firmwareType = "System";//升级类型 是主控还是锁板（单片机）

    public DevAboutPresenter(DevAboutContract.IDevAboutView iDevAboutView) {
        this.iDevAboutView = iDevAboutView;
        userId = FunSDK.GetId(userId, this);
    }

    @Override
    public void setDevId(String devId) {
        devConfigManager = manager.getDevConfigManager(devId);
        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(devId);
        if (xmDevInfo != null) {
            FunSDK.DevSetPid(xmDevInfo.getDevId(), xmDevInfo.getPid());//将PID信息保存到SDK缓存中
        }
        super.setDevId(devId);
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void getDevInfo(String type) {
        this.firmwareType = type;
        if (StringUtils.contrast(firmwareType, "System")) {
            //获取SystemInfo信息
            DevConfigInfo devConfigInfo = DevConfigInfo.create(new DevConfigManager.OnDevConfigResultListener() {
                @Override
                public void onSuccess(String devId, int msgId, Object result) {
                    if (result instanceof String) {
                        iDevAboutView.onUpdateView((String) result);
                    } else {
                        iDevAboutView.onUpdateView(JSON.toJSONString(result));
                    }

                    XMFunSDKManager.getInstance().clearUpgradeFilesPath();//升级检测之前先将本地缓存的升级文件删除
                    manager.checkDevUpgrade(getDevId(), DevAboutPresenter.this);
                }

                @Override
                public void onFailed(String devId, int msgId, String s1, int errorId) {
                    iDevAboutView.onUpdateView("数据获取失败：" + errorId);
                }

                @Override
                public void onFunSDKResult(Message msg, MsgContent ex) {
                    if (msg.arg1 >= 0) {
                        iDevAboutView.onDevNetConnectMode(msg.arg2);
                    }
                }
            });

            devConfigInfo.setJsonName(JsonConfig.SYSTEM_INFO);
            devConfigInfo.setChnId(-1);
            devConfigManager.getDevConfig(devConfigInfo);
        } else {
            //获取SystemInfoEx信息
            DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String devId, int msgId, Object result) {
                    if (result instanceof String) {
                        sysInfoExJson = (String) result;
                        iDevAboutView.onUpdateView((String) result);
                    } else {
                        sysInfoExJson = JSON.toJSONString(result);
                        iDevAboutView.onUpdateView(JSON.toJSONString(result));
                    }

                    checkDevUpgrade();
                }

                @Override
                public void onFailed(String devId, int msgId, String s1, int errorId) {
                    iDevAboutView.onUpdateView("数据获取失败：" + errorId);
                }
            });

            devConfigInfo.setJsonName(JsonConfig.SYSTEM_EX_INFO);
            devConfigInfo.setChnId(-1);
            devConfigInfo.setCmdId(1020);
            devConfigManager.setDevCmd(devConfigInfo);
        }
    }


    @Override
    public void checkDevUpgrade() {
        HashMap sysInfoExMap = new Gson().fromJson(sysInfoExJson, HashMap.class);
        Object sysInfoExObj = sysInfoExMap.get("SystemInfoEx");
        HashMap hashMap = new HashMap();
        hashMap.put("DevID", getDevId());//设备序列号
        hashMap.put("Type", "Mcu");//升级类型  这个字段用Mcu表示要升级的是单片机，也可以用来表示锁板程序
        hashMap.put("SystemInfoEx", sysInfoExObj);
        FunSDK.DevExModulesCheckUpgrade(userId, new Gson().toJson(hashMap), 0);
    }

    @Override
    public void startDevUpgrade() {
        if ("System".equals(firmwareType)) {
            manager.startDevUpgrade(getDevId(), upgradeType, this);
        } else {
            HashMap hashMap = new HashMap();
            hashMap.put("DevID", getDevId());//设备序列号
            hashMap.put("Type", "Mcu");//升级类型  这个字段用Mcu表示要升级的是单片机，也可以用来表示锁板程序
            FunSDK.DevExModulesStartUpgrade(userId, new Gson().toJson(hashMap), 0);
        }
    }

    @Override
    public void stopDevUpgrade() {
        if ("System".equals(firmwareType)) {
            manager.stopDevUpgrade(getDevId(),this);
        }else {
            FunSDK.DevExModulesStopUpgrade(userId,getDevId(),0);
        }
    }

    @Override
    public boolean isDevUpgradeEnable() {
        return upgradeType != UPGRADE_TYPE_NONE;
    }

    @Override
    public void startDevLocalUpgrade(String type, String filePath) {
        if ("System".equals(type)) {
            manager.startDevUpgradeByLocalFile(getDevId(), filePath, this);
        } else {
            HashMap hashMap = new HashMap();
            hashMap.put("DevID", getDevId());//设备序列号
            hashMap.put("FileName", filePath);//固件绝对路径
            hashMap.put("Type", "Mcu");//升级类型  这个字段用Mcu表示要升级的是单片机，也可以用来表示锁板程序
            FunSDK.DevExModulesStartUpgradeByFile(userId, new Gson().toJson(hashMap), 0);
        }
    }

    @Override
    public void onUpgradeProgress(int state, int progress, int error, String msg) {
        System.out.println("onUpgradeProgress error:" + error);
        if (error >= 0) {
            iDevAboutView.onDevUpgradeProgressResult(state, progress);
        } else {
            upgradeType = UPGRADE_TYPE_NONE;
            iDevAboutView.onDevUpgradeFailed(error);
        }
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
            case UPGRADE_TYPE_LOCAL_FILE:
                iDevAboutView.onCheckDevUpgradeResult(true, true);
                break;
            default:
                break;
        }
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
    public void getDevCapsAbility(Context context) {
        SysAbilityManager.getInstance().isSupports(context, getDevId(), false, new OnSysAbilityResultListener<Map<String, Object>>() {
            @Override
            public void onSupportResult(Map<String, Object> isSupport, boolean isFromServer) {
                if (isSupport != null) {
                    if (isSupport.containsKey("net.cellular.iccid")) {
                        Object iccid = isSupport.get("net.cellular.iccid");
                        if (iccid instanceof String) {
                            iDevAboutView.onGetICCIDResult((String) iccid);
                        }
                    }

                    if (isSupport.containsKey("net.cellular.imei")) {
                        Object iccid = isSupport.get("net.cellular.imei");
                        if (iccid instanceof String) {
                            iDevAboutView.onGetIMEIResult((String) iccid);
                        }
                    }

                    if (isSupport.containsKey("mfrsOemId")) {
                        Object mfrsOemId = isSupport.get("mfrsOemId");
                        if (mfrsOemId instanceof String) {
                            iDevAboutView.onGetDevOemIdResult((String) mfrsOemId);
                        }
                    }
                }

            }
        });
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

    @Override
    public int OnFunSDKResult(Message msg, MsgContent ex) {
        switch (msg.what) {
            case 5119:
            case 5553://升级开始结果回调
                onUpgradeProgress(0, 0, msg.arg1, "");
                break;
            case 5120:
            case 5555://升级进度回调
                onUpgradeProgress(msg.arg1, msg.arg2, msg.arg2, ex.str);
                break;
            case 5552://检测升级回调
                onCheckUpgradeResult(msg.arg1, ex.str, msg.arg1);
                break;
            case 5513://链接断开
                iDevAboutView.onDevUpgradeFailed(-1);
                break;
            default:
                break;
        }
        return 0;
    }
}

