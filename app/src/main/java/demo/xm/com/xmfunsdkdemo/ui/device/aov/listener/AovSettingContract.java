package demo.xm.com.xmfunsdkdemo.ui.device.aov.listener;

public class AovSettingContract {
    public interface IAovSettingView {
        /**
         * 该方法用于在收到关于是否支持黑光灯功能的结果后进行处理
         */
        void onSupportBlackLightResult();

        /**
         * 该方法用于在收到关于是否支持双灯功能的结果后进行处理
         */
        void onSupportDoubleLightResult();

        /**
         * 显示AOV功能的支持状态
         * 包括对灯光的支持、工作模式的支持以及电池管理的支持
         *
         * @param isSupportLight          表示是否支持灯光功能
         * @param isSupportWorkMode       表示是否支持工作模式的切换
         * @param isSupportBatteryManager 表示是否支持电池管理功能
         */
        public void showSupportAovAbility(boolean isSupportLight, boolean isSupportWorkMode,
                                          boolean isSupportBatteryManager);
    }


    public interface IAovSettingPresenter {
        /**
         * 检查是否支持灯光类型
         */
        void checkSupportLightType();

        /**
         * 检查是否支持Aov能力
         */
        public void checkSupportAovAbility();

    }
}
