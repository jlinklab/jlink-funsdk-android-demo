package demo.xm.com.xmfunsdkdemo.app;

import static com.lib.EFUN_ATTR.LOGIN_SUP_RSA_ENC;
import static com.utils.FileUtils.makeRootDirectory;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;

import com.lib.EFUN_ATTR;
import com.lib.FunSDK;
import com.lib.SDKCONST;
import com.lib.sdk.bean.StringUtils;
import com.manager.XMFunSDKManager;
import com.manager.path.PathManager;
import com.utils.FileUtils;
import com.utils.PathUtils;
import com.xm.ui.dialog.XMPromptDlg;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoConstant;
import demo.xm.com.xmfunsdkdemo.ui.entity.AlarmTranslationIconBean;
import demo.xm.com.xmfunsdkdemo.utils.SPUtil;

/**
 * created by hws 2018-10-27 14:36
 */
public class SDKDemoApplication extends Application {
    public static String DEFAULT_PATH;
    public static String PATH_DEVICE_TEMP;
    public static String PATH_PHOTO_TEMP;
    public static String PATH_PHOTO;
    public static String PATH_VIDEO;
    /**
     * 保存实时预览/录像回放的原始媒体数据
     */
    public static String PATH_ORIGINAL_MEDIA_DATA;
    public static String PATH_LOG;
    /**
     * 保存对讲的原始数据
     */
    public static String PATH_ORIGINAL_TALK_DATA;
    private LinkedHashMap<String, Activity> actMap;
    private XMFunSDKManager xmFunSDKManager;
    private AlarmTranslationIconBean alarmTranslationIconBean;

    @Override
    public void onCreate() {
        super.onCreate();
        actMap = new LinkedHashMap<>();
        /**
         * 初始化 SDK
         * init SDK
         * 传入申请到的AppKey、movedCard和AppSecret等信息
         * input AppKey, movedCard, AppSecret and other information applied by the platform
         *
         * 如果是P2P定制服务器的话请参考以下方法
         * If it is a P2P customized server, please refer to the following method
         * int customPwdType 加密类型 默认传0
         *                   The default encryption type is 0
         * String customPwd 加密字段 默认传 ""
         *                  The encryption field is passed "" by default.
         * String customServerAddr 定制服务器域名或IP
         *                         Customize the server domain name or IP address
         * int customPort 定制服务器端口
         *                Customizing a server port
         *
         * XMFunSDKManager.getInstance(0,"",customServerAddr,customPort).initXMCloudPlatform(this,appUuid,appKey,appSecret,appMovedCard,true);
         */
        xmFunSDKManager = XMFunSDKManager.getInstance();
        if (xmFunSDKManager.isLoadLibrarySuccess()) {
            //前往（应用开放平台：https://aops.jftech.com），注册申请成为开放平台开发者，然后到【控制台】-【创应用列表】中创建Android应用，等应用审核通过后就可以获取到AppKey、movedCard和AppSecret等信息
            String appUUID = SPUtil.getInstance(this).getSettingParam("APP_UUID", DemoConstant.APP_UUID);
            String appKey = SPUtil.getInstance(this).getSettingParam("APP_KEY", DemoConstant.APP_KEY);
            String appSecret = SPUtil.getInstance(this).getSettingParam("APP_SECRET", DemoConstant.APP_SECRET);
            int appMovedcard = SPUtil.getInstance(this).getSettingParam("APP_MOVEDCARD", DemoConstant.APP_MOVEDCARD);
            xmFunSDKManager.initXMCloudPlatform(
                    this,
                    appUUID,
                    appKey,
                    appSecret,
                    appMovedcard,
                    true);

            /**
             * 有其他定制的服务，在initXMCloudPlatform之后再按照你的需求调用不同的接口
             * There are other customized services, after initXMCloudPlatform call different interfaces according to your needs
             *
             * FunSDK.SysSetServerIPPort("APP_SERVER", "服务器域名或IP/Domain name or IP", 服务器端口/Port);
             * FunSDK.SysSetServerIPPort("STATUS_P2P_SERVER", "服务器域名或IP/Domain name or IP", 服务器端口/Port); // P2P状态查询/P2P Status Query
             * FunSDK.SysSetServerIPPort("STATUS_DSS_SERVER", "服务器域名或IP/Domain name or IP", 服务器端口/Port); // DSS状态查询/DSS Status Query
             * FunSDK.SysSetServerIPPort("STATUS_RPS_SERVER","服务器域名或IP/Domain name or IP", 服务器端口/Port); // RPS状态查询/RPS Status Query
             * FunSDK.SysSetServerIPPort("STATUS_IDR_SERVER", "服务器域名或IP/Domain name or IP", 服务器端口/Port); // WPS状态查询/WPS Status Query
             *
             * FunSDK.SysSetServerIPPort("HLS_DSS_SERVER", "服务器域名或IP/Domain name or IP", 服务器端口/Port); // DSS码流请求/DSS stream request
             * FunSDK.SysSetServerIPPort("CONFIG_SERVER", "服务器域名或IP/Domain name or IP", 服务器端口/Port); // 配置管理中心/Configuration Management Center
             * FunSDK.SysSetServerIPPort("UPGRADE_SERVER", "服务器域名或IP/Domain name or IP", 服务器端口/Port); // 固件升级/Firmware Upgrade
             * FunSDK.SysSetServerIPPort("CAPS_SERVER", "服务器域名或IP/Domain name or IP", 服务器端口/Port); // 能力集控制（和云存储有关）/Capability set control (cloud storage)
             */

            /**
             * 初始化 logcat上的日志，可以通过SDK_LOG过滤
             * Initialize the logs on logcat, which can be filtered by SDK_LOG
             */

            xmFunSDKManager.initLog();

            /**
             * 初始化翻译文件，仅适用于Demo
             * Initialize the translation file, only for Demo
             */
            initLanguage();

            /**
             * 初始化配置路径
             * Initialize the configuration path
             */
            initPath();
            /**
             * 低功耗设备：包括 门铃、门锁等，需要调用此方法否则可能无法登录设备，其他设备无需调用
             * Low-power devices: including doorbells, door locks, etc., you need to call this method,
             * otherwise you may not be able to log in to the device, and other devices do not need to call
             */
            FunSDK.SetFunIntAttr(EFUN_ATTR.SUP_RPS_VIDEO_DEFAULT, SDKCONST.Switch.Open);
            //仅对实时预览生效，通过一定时间内帧的接收平均间隔，动态调整实施预览的缓存策略，用于不同网络环境下确保播放流畅度（牺牲实时性，保证流畅度）
            FunSDK.SetFunIntAttr(EFUN_ATTR.ENABLE_REAL_TIME_BUFFER_ADJUSTMENT, SDKCONST.Switch.Close);

            //SDK内部媒体播放会根据解码速度来动态调整解码器：是否启用帧并行多线程解码、启用帧并行多线程解码的线程数4-->8→16递增。默认是开启的
            FunSDK.SetFunIntAttr(EFUN_ATTR.ENABLE_DYNAMIC_THREAD_DECODE_ADJUSTER, SDKCONST.Switch.Close);


        } else {
            throw new RuntimeException("Failed to load dynamic library");
        }
    }

    /**
     * 初始化翻译文件，仅适用于Demo
     * Initialize the translation file, only for Demo
     */
    private void initLanguage() {
        String lan = Locale.getDefault().getLanguage();
        String setLan = "en.txt"; // 默认为英语 English by default
        if (lan.compareToIgnoreCase("zh") == 0) {
            String country = Locale.getDefault().getCountry();
            if (country.compareToIgnoreCase("TW") == 0 || country.compareToIgnoreCase("HK") == 0) {
                setLan = "zh_TW.txt";
            } else {
                setLan = "zh_CN.txt";
            }
        } else if (lan.compareToIgnoreCase("ko") == 0) {
            String country = Locale.getDefault().getCountry();
            if (country.compareToIgnoreCase("KR") == 0) {
                setLan = "ko_kr.txt";//韩语 Korean
            }
        } else if (lan.compareToIgnoreCase("vi") == 0) {
            setLan = "vi_CN.txt";//越南语 vietnamese
        } else if (lan.compareToIgnoreCase("de") == 0) {
            setLan = "de_US.txt";//德语 German
        } else if (lan.compareToIgnoreCase("es") == 0) {
            setLan = "es_US.txt";//西班牙语 spanish
        } else if (lan.compareToIgnoreCase("fr") == 0) {
            setLan = "fr_US.txt";//法语 French
        } else if (lan.compareToIgnoreCase("it") == 0) {
            setLan = "it_US.txt";//意大利语 italian
        } else if (lan.compareToIgnoreCase("ja") == 0) {
            setLan = "ja_CN.txt";//日本语 Japanese
        } else if (lan.compareToIgnoreCase("pt") == 0) {
            setLan = "pt_BR.txt";//葡萄牙语 Portuguese
        } else if (lan.compareToIgnoreCase("th") == 0) {
            setLan = "th_CN.txt";//泰语 Thai
        } else if (lan.compareToIgnoreCase("tr") == 0) {
            setLan = "tr.txt";   //土耳其语 turkish
        } else if (lan.compareToIgnoreCase("ru") == 0) {
            setLan = "ru_CN.txt";//俄语 Russian
        }

        XMFunSDKManager.initLanguage(this, getAssets(), "language/" + setLan);
    }

    /**
     * 初始化配置路径
     * Initialize the configuration path
     */
    private void initPath() {
        /**
         * 请使用私有目录保存图片、视频和其他重要文件，Android10及以上版本 Google已经开始分区存储了，外部存储是不能直接保存的。
         * 如果保存到私有目录下的话，还得考虑APP被删除或者缓存数据被删除，所有文件都会被删除的
         * Please use a private directory to save pictures, videos and other important files. Android 10 and above versions Google has started partitioning storage, and external storage cannot be directly saved.
         * If you save it in a private directory, you have to consider that the APP is deleted or the cache data is deleted, and all files will be deleted.
         */
        DEFAULT_PATH = PathUtils.getAndroidPath(this) + File.separator + "xmfunsdkdemo" + File.separator;
        /**
         * 缩略图保存路径
         * Thumbnail save path
         */
        PATH_PHOTO_TEMP = DEFAULT_PATH + ".temp_images";

        /**
         * 下载设备缩略图此路径可用
         * Download device thumbnails This path is available
         */
        PATH_DEVICE_TEMP = PathUtils.getAndroidPath(this) + File.separator + ".temp_capture";

        /**
         * 图片保存路径
         * Image save path
         */
        PATH_PHOTO = getPathForPhoto();

        /**
         * 视频保存路径
         * Video save path
         */
        PATH_VIDEO = getPathForVideo();

        File pFile = new File(DEFAULT_PATH);
        if (!pFile.exists()) {
            makeRootDirectory(pFile.getPath());
        }

        pFile = new File(PATH_PHOTO_TEMP);
        if (!pFile.exists()) {
            makeRootDirectory(pFile.getPath());
        }

        pFile = new File(PATH_DEVICE_TEMP);
        if (!pFile.exists()) {
            makeRootDirectory(pFile.getPath());
        }

        pFile = new File(PATH_PHOTO);
        if (!pFile.exists()) {
            makeRootDirectory(pFile.getPath());
        }

        pFile = new File(PATH_VIDEO);
        if (!pFile.exists()) {
            makeRootDirectory(pFile.getPath());
        }


        PATH_ORIGINAL_MEDIA_DATA = PathManager.getInstance(this).getAppPath() + File.separator + "original_media_data";
        pFile = new File(PATH_ORIGINAL_MEDIA_DATA);
        if (!pFile.exists()) {
            makeRootDirectory(pFile.getPath());
        }

        PATH_ORIGINAL_TALK_DATA = PathManager.getInstance(this).getAppPath() + File.separator + "talk_data";
        pFile = new File(PATH_ORIGINAL_TALK_DATA);
        if (!pFile.exists()) {
            makeRootDirectory(pFile.getPath());
        }


        PATH_LOG = DEFAULT_PATH + "log" + File.separator;
        pFile = new File(PATH_LOG);
        if (!pFile.exists()) {
            makeRootDirectory(pFile.getPath());
        }
    }

    public static boolean makeRootDirectory(String filePath) {
        File file = null;
        String newPath = null;
        String[] path = filePath.split("/");
        for (int i = 0; i < path.length; i++) {
            if (newPath == null) {
                newPath = path[i];
            } else {
                newPath = newPath + "/" + path[i];
            }
            if (StringUtils.isStringNULL(newPath)) {
                continue;
            }
            file = new File(newPath);
            if (!file.exists()) {
                return file.mkdir();
            }
        }
        return true;
    }

    private String getPathForPhoto() {
        SharedPreferences bell = getSharedPreferences("my_pref", 0);
        String path = bell.getString("img_save_path", null);
        if (path == null) {
            String galleryPath = Environment.getExternalStorageDirectory()
                    + File.separator + Environment.DIRECTORY_DCIM
                    + File.separator + "Camera" + File.separator;
            if (!FileUtils.isFileAvailable(galleryPath)) {
                galleryPath = Environment.getExternalStorageDirectory()
                        + File.separator + Environment.DIRECTORY_DCIM
                        + File.separator;
            }
            if (!FileUtils.isFileAvailable(galleryPath)) {
                path = DEFAULT_PATH + "snapshot";
            } else {
                path = galleryPath;
            }
        }
        return path;
    }

    private String getPathForVideo() {
        SharedPreferences bell = getSharedPreferences("my_pref", 0);
        String path = bell.getString("video_save_path", null);
        if (path == null) {
            String galleryPath = Environment.getExternalStorageDirectory()
                    + File.separator + Environment.DIRECTORY_DCIM
                    + File.separator + "Camera" + File.separator;
            if (!FileUtils.isFileAvailable(galleryPath)) {
                galleryPath = Environment.getExternalStorageDirectory()
                        + File.separator + Environment.DIRECTORY_DCIM
                        + File.separator;
            }
            if (!FileUtils.isFileAvailable(galleryPath)) {
                path = DEFAULT_PATH + "videorecord";
            } else {
                path = galleryPath;
            }
        }
        return path;
    }

    /**
     * 清除缓存
     * clear cache
     */
    public static void clearCache() {
        FileUtils.deleteFiles(new File(PATH_PHOTO_TEMP));
        FileUtils.deleteFiles(new File(PATH_DEVICE_TEMP));
    }

    public void addActivity(Activity activity) {
        if (null == actMap) {
            actMap = new LinkedHashMap<>();
        }
        actMap.put(activity.getClass().getSimpleName(), activity);
    }

    public void removeActivity(String _class) {
        if (actMap != null) {
            actMap.remove(_class);
        }
    }

    public void exit() {
        for (Map.Entry<String, Activity> it : actMap.entrySet()) {
            Activity act = ((Activity) it.getValue());
            if (null != act) {
                act.finish();
            }
        }
    }

    public AlarmTranslationIconBean getAlarmTranslationIconBean() {
        return alarmTranslationIconBean;
    }

    public void setAlarmTranslationIconBean(AlarmTranslationIconBean alarmTranslationIconBean) {
        this.alarmTranslationIconBean = alarmTranslationIconBean;
    }
}
