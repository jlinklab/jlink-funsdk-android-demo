package demo.xm.com.xmfunsdkdemo.utils;

import androidx.annotation.NonNull;

import com.lib.sdk.bean.StringUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音转换工具
 *
 * @描述 TODO
 * @项目名称 App_imooc
 * @包名 com.android.imooc.quickIndex
 * @类名 PinyinUtils
 * @author chenlin
 * @version 1.0
 */
public class PinyinUtils {
    /**
     * 根据传入的字符串(包含汉字),得到拼音
     * Converts to Pinyin based on the passed string (containing Chinese characters)
     * @param str 字符串
     * @return
     */
    public static String getFirstPinYin(@NonNull String str) {
        if (StringUtils.isStringNULL(str)) {
            return "";
        }

        String firstPinYin = "";
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        char[] charArray = str.toCharArray();
        if (charArray.length == 0) {
            return "";
        }
        char c = charArray[0];
        // 如果是空格, 跳过
        if (Character.isWhitespace(c)) {
            return "";
        }

        if (c < 128) {
            // 肯定不是汉字
            firstPinYin = (c + "").toUpperCase();
        } else {
            try {
                // 通过char得到拼音集合. 单 -> dan, shan
                String pinYin = PinyinHelper.toHanyuPinyinStringArray(c, format)[0];
                if (!StringUtils.isStringNULL(pinYin)) {
                    firstPinYin = pinYin.substring(0,1);
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        }
        return firstPinYin;
    }
}
