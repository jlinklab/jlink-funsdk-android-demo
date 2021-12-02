package demo.xm.com.xmfunsdkdemo.ui.device.aov.listener;

import android.app.Activity;

public class AOVDoubleLightSettingContract {

    public interface IAOVDoubleLightSettingView {

        void onShowWaitDialog();

        Activity getActivity();

        void onHideWaitDialog();


        /**
         * 显示工作模式
         *
         * @param workMode 工作模式的字符串表示-
         */
        void showWorkMode(String workMode);

        /**
         * 显示指示灯的状态
         *
         * @param state 指示灯的状态
         */
        void showIndicatorLight(int state);

        /**
         * 显示微光灯的状态
         *
         * @param state 微光灯的状态
         */
        void showMicroLight(int state);

        /**
         * 显示摄像头在白天的灯光模式
         *
         * @param supportNightVision    是否支持夜视模式
         * @param supportFullColor      是否支持全彩模式
         * @param supportDoubleLight    是否支持双灯模式
         */
        void showCameraDayLightModes(boolean supportNightVision, boolean supportFullColor, boolean supportDoubleLight);

        /**
         * 显示指示灯的支持情况
         *
         * @param isSupport 是否支持指示灯
         */
        void showStatusLed(boolean isSupport);

        /**
         * 显示微光补光灯的支持情况
         *
         * @param isSupport 是否支持微光补光灯
         */
        void showMicroFillLight(boolean isSupport);

    }


    public interface IAOVDoubleLightSettingPresenter {
        /**
         * 获取摄像头的日光模式设置
         */
        void getCameraDayLightModes();

        /**
         * 获取Facebook额外状态控制信息
         */
        void getFbExtraStateCtrl();

        /**
         * 获取摄像头高级参数
         */
        void getCameraParamEx();

        /**
         * 保存工作模式
         * @param mode 工作模式的名称
         */
        void saveWorkMode(String mode);

        /**
         * 保存指示灯状态
         * @param switchState 指示灯的开关状态
         */
        void saveIndicatorLight(int switchState);

        /**
         * 保存微光灯光状态
         * @param switchState 微光灯光的开关状态
         */
        void saveMicroLight(int switchState);

        /**
         * 检查是否支持状态指示灯
         */
        void checkIsSupportStatusLed();

        /**
         * 检查是否支持微光灯
         */
        void checkIsSupportMicroFillLight();
    }
}
