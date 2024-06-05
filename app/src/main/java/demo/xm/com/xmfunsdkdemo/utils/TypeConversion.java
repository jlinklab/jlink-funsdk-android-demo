package demo.xm.com.xmfunsdkdemo.utils;

/**
 * 类型转换
 * 1、bytes2HexString(byte[] b, int length)  将字节数组-->16进制字符串  有空格
 * 2、hexString2Bytes(String src)           16进制字符串-->字节数组
 * 3、string2HexString(String strPart)      普通字符串-->16进制字符串
 * 4、hexString2String(String src)          16进制字符串-->普通字符串
 * 5、char2Byte                             字符-->字节数据
 * 6、intToHexString(int a,int len)        10进制数字-->转成16进制字符串
 * 7、stringToByteArray(String s)         普通字符串-->字节数组
 * 8、bytes2HexString(byte[] b）          将字节数组-->16进制字符串  没有空格
 * 9、bytes20xHexString(byte[] b）        将字节数组-->16进制字符串 有0x标记
 *
 * asciiStr2HexStr(String ascStr)       将ASCII编码的字符串转换成对应ASCII码值16进制数组成的字符串
 */

public class TypeConversion {

    /**
     * 1、bytes2HexString
     * 字节数组-->16进制字符串
     * @param b   字节数组
     * @param length  字节数组长度
     * @return 16进制字符串 有空格类似“0A D5 CD 8F BD E5 F8”
     */
    public static String bytes2HexString(byte[] b, int length) {
        StringBuffer result = new StringBuffer();
        String hex;
        for (int i = 0; i < length; i++) {
            hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            result.append(hex.toUpperCase()).append(" ");
        }
        return result.toString();
    }

    /**
     * 2、hexString2Bytes
     * 16进制字符串-->字节数组
     * @param src  16进制字符串
     * @return 字节数组
     */
    public static byte[] hexString2Bytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer
                    .valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }


    /**
     * 3、string2HexString
     * 普通字符串-->16进制字符串
     * @param strPart 普通字符串
     * @return 16进制字符串
     *  如：0102ABCD  转换结果  3031303241424344
     */
    public static String string2HexString(String strPart) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString.append(strHex);
        }
        return hexString.toString();
    }

    /**
     * 4、hexString2String
     * 16进制字符串-->普通字符串
     * @param src  16进制字符串
     * @return 普通字符串
     * 如：3031303241424344  转换结果  0102ABCD
     */
    public static String hexString2String(String src) {
        String temp = "";
        for (int i = 0; i < src.length() / 2; i++) {
            temp = temp
                    + (char) Integer.valueOf(src.substring(i * 2, i * 2 + 2),
                    16).byteValue();
        }
        return temp;
    }

    /**
     * 5、char2Byte
     * 字符-->字节数据  char-->integer-->byte
     * @param src  字符
     * @return   字节
     * 如：'A'  转换成 65（ASCII值）
     */
    public static Byte char2Byte(Character src) {
        return Integer.valueOf((int)src).byteValue();
    }

    /**
     * 6、intToHexString
     * 10进制数字-->转成16进制字符串 无空格
     * @param a   10进制数据
     * @param len 占用字节数
     * @return  16进制字符串(大写)
     * 如：443  转换成(占1字节)  1BB
     * 如：443  转换成(占2字节)  01BB
     * 如：27  转换成(占1字节)  1B
     * 如：27  转换成(占2字节)  001B
     */
    public static String intToHexString(int a, int len){
        len<<=1;
        String hexString = Integer.toHexString(a);
        int b = len -hexString.length();
        if(b>0){
            for(int i=0;i<b;i++)  {
                hexString = "0" + hexString;
            }
        }
        return hexString.toUpperCase();
    }


    ////////////////////////////////////////  新增  ////////////////////////////////////////////

    /**
     * 7、stringToByteArray
     * 普通字符串-->字节数组
     * @param s  普通字符串
     * @return    字节数组
     */
    public static byte[] stringToByteArray(String s) {
        int sl = s.length();
        byte[] charArray = new byte[sl];
        for (int i = 0; i < sl; i++) {
            char charElement = s.charAt(i);
            charArray[i] = (byte) charElement;
        }
        return charArray;
    }

    /**
     * 8、bytes2HexString
     * 将字节数组-->16进制字符串
     * @param b  字节数组
     * @return   16进制字符串  没有空格 类似于“0AD5CD8FBDE5F8”
     */
    public static String bytes2HexString(byte[] b) {
        if (b == null) {
            return "";
        }
        String rs = "";
        int bl = b.length;
        byte bt;
        String bts = "";
        int btsl;
        for (int i = 0; i < bl; i++) {
            bt = b[i];
            bts = Integer.toHexString(bt);
            btsl = bts.length();
            if (btsl > 2) {
                bts = bts.substring(btsl - 2).toUpperCase();
            } else if (btsl == 1) {
                bts = "0" + bts.toUpperCase();
            } else {
                bts = bts.toUpperCase();
            }
            rs += bts;
        }
        return rs;
    }

    /**
     * 9、bytes20xHexString
     * 将字节数组-->16进制字符串 有0x标记
     * @param b  字节数组
     * @return  16进制字符串  有空格 类似于“0x0A 0xD5 0xCD 0x8F 0xBD 0xE5 0xF8”
     */
    public static String bytes20xHexString(byte[] b) {
        String rs = "";
        int bl = b.length;
        byte bt;
        String bts = "";
        int btsl;
        for (int i = 0; i < bl; i++) {
            bt = b[i];
            bts = Integer.toHexString(bt);
            btsl = bts.length();
            if (btsl > 2) {
                bts = "0x" + bts.substring(btsl - 2).toUpperCase();
            } else if (btsl == 1) {
                bts = "0x0" + bts.toUpperCase();
            } else {
                bts = "0x" + bts.toUpperCase();
            }
            rs += (bts + " ");
        }
        return rs;
    }

    /**
     * asciiStr2HexStr
     * ASCII编码的字符串-->对应ASCII码值16进制数组成的字符串
     * @param ascStr ASCII编码的字符串（字符串中字符的ASCII码值在[0,127]）
     * @return  16进制字符串  如“Abcde123” --> int [65 98 99 100 101 49 50 51] --> hexStr [41 62 63 64 65 31 32 33]
     */
    public static String asciiStr2HexStr(String ascStr){
        StringBuilder hexStrBuilder = new StringBuilder();
        char[] asc = ascStr.toCharArray(); //利用toCharArray方法转换
        for (char c : asc) {
            String hexStr = intToHexString((int)c,1);
            hexStrBuilder.append(hexStr);
        }
        return hexStrBuilder.toString();
    }

}
