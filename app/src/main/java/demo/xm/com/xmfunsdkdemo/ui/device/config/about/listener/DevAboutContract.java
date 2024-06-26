package demo.xm.com.xmfunsdkdemo.ui.device.config.about.listener;

import android.content.Context;

/**
 * 关于设备界面,包含设备基本信息(序列号,设备型号,硬件版本,软件版本,
 * 发布时间,设备时间,运行时间,网络模式,云连接状态,固件更新及恢复出厂设置)
 * Created by jiangping on 2018-10-23.
 */
public class DevAboutContract {
    public interface IDevAboutView {
        void onUpdateView(String result);

        void onCheckDevUpgradeResult(boolean isSuccess, boolean isNeedUpgrade);

        void onDevUpgradeProgressResult(int upgradeState, int progress);

        void syncDevTimeZoneResult(boolean isSuccess, int errorId);

        void syncDevTimeResult(boolean isSuccess, int errorId);

        void onGetDevOemIdResult(String oemId);

        void onDevUpgradeFailed(int errorId);
    }

    public interface IDevAboutPresenter {
        void getDevInfo(String type);

        /**
         * 检测升级
         */
        void checkDevUpgrade();

        /**
         * 开始升级
         */
        void startDevUpgrade();

        /**
         * 结束升级
         */
        void stopDevUpgrade();

        /**
         * 是否可以升级
         *
         * @return
         */
        boolean isDevUpgradeEnable();

        /**
         * 同步设备时区
         */
        void syncDevTimeZone();

        /**
         * 本地文件升级
         *
         * @param filePath 文件路径
         */
        void startDevLocalUpgrade(String type, String filePath);

        /**
         * 同步设备时间
         */
        void syncDevTime();

        /**
         * 获取设备OemId
         */
        void getDevOemId(Context context);
    }
}
