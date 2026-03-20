package demo.xm.com.xmfunsdkdemo.utils;

import android.content.Context;

import com.lib.sdk.bean.StringUtils;

;

/**
 * 密码加密管理
 */
public class WifiNameAndPwdManager {


    public static final String NEW_WIFI_PWD_PREFIX = "new_wifi_pwd_";
    private static WifiNameAndPwdManager instance;
    private WifiNameAndPwdManager(Context context) {
    }

    public synchronized static WifiNameAndPwdManager getInstance(Context context) {
        if (instance == null) {
            instance = new WifiNameAndPwdManager(context);
        }

        return instance;
    }



    /**
     * 获取WiFi密码
     * @param context
     * @return
     */
    public String getWiFiPwd(Context context,String ssid) {
        if (context == null) {
            return "";
        }

        String pwd = SPUtil.getInstance(context).getSettingParam(NEW_WIFI_PWD_PREFIX + ssid,"");
        if (StringUtils.isStringNULL(pwd)) {
            return "";
        }

        return pwd;
    }

    /**
     * 保存WiFi密码
     * @param context
     * @param pwd
     * @param ssid
     */
    public void saveWiFiPwd(Context context,String pwd,String ssid) {
        if (context == null) {
            return;
        }

        SPUtil.getInstance(context).setSettingParam(NEW_WIFI_PWD_PREFIX + ssid,pwd);
    }


}
