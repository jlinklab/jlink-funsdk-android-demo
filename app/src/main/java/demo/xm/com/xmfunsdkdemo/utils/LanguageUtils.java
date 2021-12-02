package demo.xm.com.xmfunsdkdemo.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lib.FunSDK;

import java.util.Locale;

/**
 * Created by hanzhenbo on 2017-07-17.
 */

public class LanguageUtils {

    public static final String Simplified_Chinese = "zh_CN";
    public static final String Traditional_Chinese = "zh_TW";
    public static final String English = "en";

    public static void initLanguage(View view){
        if (view instanceof ViewGroup)
            initLanguage((ViewGroup) view);
    }

    /**
     * 翻译页面
     * Translate the page
     * @param layout
     */
    public static void initLanguage(ViewGroup layout) {
        if (layout == null) {
            return;
        }
        int count = layout.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = layout.getChildAt(i);
            if (v instanceof ViewGroup) {
                initLanguage((ViewGroup) v);
            } else {
                if (v instanceof TextView) {
                    ((TextView) v).setText(FunSDK.TS(((TextView) v).getText().toString()));
                    if (((TextView) v).getHint() != null) {
                        ((TextView) v).setHint(FunSDK.TS(((TextView) v).getHint().toString()));
                    }
                }
            }
        }
    }

    /**
     * 获取手机系统语言
     * Get the phone system language
     * @return
     */
    public static String getLanguage(){
        String lan = Locale.getDefault().getLanguage();
        String setLan = English; // 默认为英语
        if (lan.compareToIgnoreCase("zh") == 0) {
            String country = Locale.getDefault().getCountry();
            if (country.compareToIgnoreCase("TW") == 0
                    || country.compareToIgnoreCase("HK") == 0) {
                setLan = Traditional_Chinese;
            } else {
                setLan = Simplified_Chinese;
            }
        } else if(lan.compareToIgnoreCase("ko") == 0) {
            String country = Locale.getDefault().getCountry();
            if (country.compareToIgnoreCase("KR") == 0) {
                setLan = "ko_KR";
            } else {
                setLan = English;
            }
        }
        return setLan;
    }

}
