package demo.xm.com.xmfunsdkdemo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;


import com.utils.RomUtils;
import com.utils.XUtils;
import com.xm.ui.widget.XTitleBar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import demo.xm.com.xmfunsdkdemo.R;

public class StatusBarUtils {
    @TargetApi(19)
    public static void transparentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @TargetApi(19)
    public static void transparentStatusBar(Dialog dialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = dialog.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = dialog.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    /**
     * 修改状态栏颜色，支持4.4以上版本
     * @param activity
     * @param colorId
     */
    public static void setStatusBarColor(Activity activity, int colorId) {
        if (activity == null) {
            return;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorId));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setStatusBarColor(Dialog dialog, int colorId) {
        if (dialog == null) {
            return;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = dialog.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(dialog.getContext().getResources().getColor(colorId));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  修改状态栏文字颜色，这里小米，魅族区别对待。
     */
    public static void setLightStatusBar(final Activity activity, final boolean dark) {
        switch (RomUtils.getLightStatusBarAvailableRomType()) {
            case RomUtils.AvailableRomType.MIUI:
                MIUISetStatusBarLightMode(activity, dark);
                break;
            case RomUtils.AvailableRomType.FLYME:
            case RomUtils.AvailableRomType.ANDROID_NATIVE:
                setAndroidNativeLightStatusBar(activity, dark);
                break;
            default:
                break;
        }
    }

    public static void setLightStatusBar(final Dialog dialog, final boolean dark) {
        switch (RomUtils.getLightStatusBarAvailableRomType()) {
            case RomUtils.AvailableRomType.MIUI:
                MIUISetStatusBarLightMode(dialog, dark);
                break;
            case RomUtils.AvailableRomType.FLYME:
            case RomUtils.AvailableRomType.ANDROID_NATIVE:
                setAndroidNativeLightStatusBar(dialog, dark);
                break;
            default:
                break;
        }
    }


    public static boolean MIUISetStatusBarLightMode(Activity activity, boolean dark) {
        if (activity == null) {
            return false;
        }

        boolean result = false;
        try {
            Window window = activity.getWindow();
            if (window != null) {
                Class clazz = window.getClass();
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && RomUtils.isMiUIV7OrAbove()) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (dark) {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean MIUISetStatusBarLightMode(Dialog dialog, boolean dark) {
        if (dialog == null) {
            return false;
        }

        boolean result = false;
        try {
            Window window = dialog.getWindow();
            if (window != null) {
                Class clazz = window.getClass();
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && RomUtils.isMiUIV7OrAbove()) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (dark) {
                        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static boolean setFlymeLightStatusBar(Activity activity, boolean dark) {
        if (activity == null) {
            return false;
        }

        boolean result = false;
        try {
            if (activity != null) {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }

    private static boolean setFlymeLightStatusBar(Dialog dialog, boolean dark) {
        if (dialog == null) {
            return false;
        }

        boolean result = false;
        try {
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class
                    .getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            dialog.getWindow().setAttributes(lp);
            result = true;
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        if (activity == null) {
            return;
        }

        try {
            View decor = activity.getWindow().getDecorView();
            if (dark) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setAndroidNativeLightStatusBar(Dialog dialog, boolean dark) {
        if (dialog == null) {
            return;
        }

        try {
            View decor = dialog.getWindow().getDecorView();
            if (dark) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置根布局参数
     */
    public static void setRootView(Activity activity) {
        try {
            ViewGroup parent =  activity.findViewById(android.R.id.content);
            parent.setFitsSystemWindows(false);
            parent.setClipToPadding(false);
            adapterTitleBar(activity,parent);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                // parent  设置了  setFitsSystemWindows(false), 需要设置相应的padding
                if (!parent.getFitsSystemWindows()) {
                    ViewCompatUtils.addRootViewBottomPadding(parent);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setRootView(ViewGroup rootView) {
        try {
            if (rootView != null) {
                rootView.setFitsSystemWindows(false);
                rootView.setClipToPadding(false);
                adapterTitleBar(rootView.getContext(),rootView);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean adapterTitleBar(Context context, ViewGroup viewGroup) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }

        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof FrameLayout) {
                return false;
            } else if (child instanceof XTitleBar) {
                if (i == 0) {
                    child.setPadding(0, XUtils.getStatusBarHeight(context), 0, 0);
                } else {
                    viewGroup.setPadding(0, XUtils.getStatusBarHeight(context), 0, 0);
                }
                return false;
            } else if (child instanceof ViewGroup) {
                boolean isOk = adapterTitleBar(context, (ViewGroup) child);
                if (isOk) {
                    child.setPadding(0, XUtils.getStatusBarHeight(context), 0, 0);
                }
            }
        }
        return false;
    }

    public static void setViewStatusBarPadding(Context context, View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (view != null) {
            view.setPadding(0, XUtils.getStatusBarHeight(context), 0, 0);
        }
    }
}
