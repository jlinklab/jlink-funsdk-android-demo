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


    public static String getLanguageNoTxt() {
        String lan = Locale.getDefault().getLanguage();
        String setLan = "en"; // 默认为英语
        if (lan.compareToIgnoreCase("zh") == 0) {
            String country = Locale.getDefault().getCountry();
            if (country.compareToIgnoreCase("TW") == 0 || country.compareToIgnoreCase("HK") == 0) {
                setLan = "zh_TW";
            } else {
                setLan = "zh_CN";
            }
        } else if (lan.compareToIgnoreCase("ko") == 0) {
            String country = Locale.getDefault().getCountry();
            if (country.compareToIgnoreCase("KR") == 0) {
                setLan = "ko_KR";//韩语
            }
        } else if (lan.compareToIgnoreCase("vi") == 0) {
            setLan = "vi_CN";//越南语
        } else if (lan.compareToIgnoreCase("de") == 0) {
            setLan = "de_US";//德语
        } else if (lan.compareToIgnoreCase("es") == 0) {
            setLan = "es_US";//西班牙语
        } else if (lan.compareToIgnoreCase("fr") == 0) {
            setLan = "fr_US";//法语
        } else if (lan.compareToIgnoreCase("it") == 0) {
            setLan = "it_US";//意大利语
        } else if (lan.compareToIgnoreCase("ja") == 0) {
            setLan = "ja_CN";//日本语
        } else if (lan.compareToIgnoreCase("pt") == 0) {
            setLan = "pt_BR";//葡萄牙语
        } else if (lan.compareToIgnoreCase("th") == 0) {
            setLan = "th_CN";//泰语
        } else if (lan.compareToIgnoreCase("tr") == 0) {
            setLan = "tr";   //土耳其语
        } else if (lan.compareToIgnoreCase("ru") == 0) {
            setLan = "ru_CN";//俄语
        } else if (lan.compareToIgnoreCase("pl") == 0) {
            setLan = "pl";//波兰语
        }

        return setLan;
    }

}
