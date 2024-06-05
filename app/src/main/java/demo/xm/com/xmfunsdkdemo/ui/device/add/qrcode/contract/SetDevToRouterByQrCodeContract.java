package demo.xm.com.xmfunsdkdemo.ui.device.add.qrcode.contract;

import android.content.Context;
import android.graphics.Bitmap;

import com.manager.db.XMDevInfo;

/**
 * @author hws
 * @class
 * @time 2020/8/11 17:21
 */
public interface SetDevToRouterByQrCodeContract {
    interface ISetDevToRouterByQrCodeView {
        Context getContext();

        void onSetDevToRouterResult(boolean isSuccess, XMDevInfo xmDevInfo);

        void onAddDevToAccountResult(boolean isSuccess, int errorId);

        void onPrintConfigDev(String printLog);

        void onSyncDevTimeResult(boolean isSuccess,int errorId);
    }

    interface ISetDevToRouterByQrCodePresenter {
        void initWiFi();

        String getConnectWiFiSsid();

        /**
         * 开始通过二维码方式将设备配置到路由器下
         * Start configuring the device to the router by means of QR code
         */
        Bitmap startSetDevToRouterByQrCode(String ssid, String wifiPwd);

        /**
         * @param ssid
         * @param wifiPwd
         * @param pwdType 密码加密类型 有密码如果获取不到加密类型就传1 没有密码传0
         *                Pass 1 means if you have a password and you can't get the encryption type. Pass 0 means if you don't have a password
         * @return
         */
        Bitmap startSetDevToRouterByQrCodeSimple(String ssid, String wifiPwd, int pwdType);

        /**
         * 停止二维码配网
         * Stop configuring networks with QR codes
         */
        void stopSetDevToRouterByQrCode();

        /**
         * 同步设备时区
         */
        void syncDevTimeZone();

        /**
         * 同步设备时间
         */
        void syncDevTime();
    }
}
