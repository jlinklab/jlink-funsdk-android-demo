package demo.xm.com.xmfunsdkdemo.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

public class SPUtil {
    private static final String SP_NAME = "XWorldFile";
    private static SPUtil instance;
    private final SharedPreferences sp;
    SharedPreferences.Editor editor;

    private SPUtil(Context context) {
        this.sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static SPUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SPUtil(context);
        }
        return instance;
    }

    public void setSettingParam(String key, boolean value) {
        if (null == editor) {
            editor = sp.edit();
        }
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void setSettingParam(String key, String value) {
        if (null == editor) {
            editor = sp.edit();
        }
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 判断字符串是否不为空
     * Determines whether the string is not null
     */
    // && !"null".equals(s)
    public static boolean notEmpty(String s) {
        return s != null && !"".equals(s) && !"null".equals(s) && !"undefined".equals(s) && s.trim().length() > 0;
    }

    /**
     * 判断字符串是否为空
     * Determines whether the string is empty
     */
    public static boolean isEmpty(String s) {
        return !notEmpty(s);
    }

    public void setLongParam(String key, long value) {
        if (null == editor) {
            editor = sp.edit();
        }
        editor.putLong(key, value);
        editor.commit();
    }

    public static boolean isTopActivity(Activity act) {
        ActivityManager am = (ActivityManager) act.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = am.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            // 应用程序位于堆栈的顶层
            String _className = tasksInfo.get(0).topActivity.getClassName();
            return act.getClass().getName().equals(_className);
        }
        return false;
    }

    public static int ApplicationRunningState(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo ap : appProcesses) {
            if (ap.processName.equals(context.getPackageName())) {
                if (ap.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {

                    return RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
                } else if (ap.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {

                    return RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                } else if (ap.importance == RunningAppProcessInfo.IMPORTANCE_SERVICE) {

                    return RunningAppProcessInfo.IMPORTANCE_SERVICE;
                } else
                    return 0;
            }

        }
        return 0;

    }

    public void setSettingParam(String key, int value) {
        if (null == editor) {
            editor = sp.edit();
        }
        editor.putInt(key, value);
        editor.commit();
    }

    public void setSettingParam(String key, float value) {
        if (null == editor) {
            editor = sp.edit();
        }
        editor.putFloat(key, value);
        editor.commit();
    }

    public boolean getSettingParam(String key, boolean defValue) {
        try {
            return sp.getBoolean(key, defValue);
        } catch (Exception e) {
            return defValue;
        }

    }

    public String getSettingParam(String key, String defValue) {
        try {
            return sp.getString(key, defValue);
        } catch (Exception e) {
            return defValue;
        }

    }

    public static int strCountByByte(String s) {
        s = s.replaceAll("[^\\x00-\\xff]", "**");
        return s.length();
    }

    public float getSettingParam(String key, float defValue) {
        try {
            return sp.getFloat(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public int getSettingParam(String key, int defValue) {
        try {
            return sp.getInt(key, defValue);
        } catch (Exception e) {
            return defValue;
        }

    }

    public long getSettingParam(String key) {
        try {
            return sp.getLong(key, -1);
        } catch (Exception e) {
            return -1;
        }

    }

    public boolean isFirstTimeUseXWorld() {

        if (sp.getBoolean("isFirstTimeUseXWorld", true)) {
            editor = sp.edit();
            editor.putBoolean("isFirstTimeUseXWorld", false);
            editor.commit();
            return true;
        } else {
            return false;
        }
    }

    public boolean isFirstTimeUsePlayBack() {

        if (sp.getBoolean("isFirstUsePlayBack", true)) {
            editor = sp.edit();
            editor.putBoolean("isFirstUsePlayBack", false);
            editor.commit();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 以简约模式显示设备列表
     * Displays a list of devices in minimalist mode
     */
    public boolean getDeviceListDisplayAsSimple() {
        try {
            return sp.getBoolean("DeviceListDisplayAsSimple", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置是否以简约模式显示设备列表
     * Sets whether the list of devices is displayed in minimalist mode
     */
    public void setDeviceListDisplayAsSimple(boolean asSimple) {
        try {
            editor = sp.edit();
            editor.putBoolean("DeviceListDisplayAsSimple", asSimple);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
