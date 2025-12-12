package demo.xm.com.xmfunsdkdemo.utils;

import com.lib.FunSDK;

public class NetworkCardUtils {

    // 定义卡类型枚举
    public enum CellNetworkCardType {
        China_Mobile,  // 中国移动
        China_Unicom,  // 中国联通
        China_Telecom, // 中国电信
        Unknown        // 未知运营商
    }

    // 判断ICCID所属的运营商
    public static CellNetworkCardType getCarrierFromICCID(String iccid) {
        // 检查ICCID是否为有效的20位数字字符串
        if (iccid == null || iccid.length() != 20) {
            return CellNetworkCardType.Unknown; // 无效ICCID
        }

        // 获取前六位作为运营商标识
        String prefix = iccid.substring(0, 6);

        // 判断前六位是哪个运营商
        if (prefix.equals("898600") || prefix.equals("898602") || prefix.equals("898604") ||
                prefix.equals("898607") || prefix.equals("898608")) {
            return CellNetworkCardType.China_Mobile; // 中国移动
        }

        if (prefix.equals("898601") || prefix.equals("898606") || prefix.equals("898609")) {
            return CellNetworkCardType.China_Unicom; // 中国联通
        }

        if (prefix.equals("898603") || prefix.equals("898611")) {
            return CellNetworkCardType.China_Telecom; // 中国电信
        }

        // 如果不符合以上条件，返回未知
        return CellNetworkCardType.Unknown; // 未知运营商
    }

    public static String getCarrierName(String iccid) {
        CellNetworkCardType carrierType = getCarrierFromICCID(iccid);
        switch (carrierType) {
            case China_Mobile:
                return FunSDK.TS("TR_Setting_China_Mobile");
            case China_Unicom:
                return FunSDK.TS("TR_Setting_China_Unicom");
            case China_Telecom:
                return FunSDK.TS("TR_Setting_China_Telecom");
            default:
                return FunSDK.TS("TR_Unknow");
        }
    }

}
