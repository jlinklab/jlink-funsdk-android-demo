package demo.xm.com.xmfunsdkdemo.ui.device.aov.listener;

import android.app.Activity;

import com.lib.sdk.bean.CameraParamExBean;
import com.lib.sdk.bean.FbExtraStateCtrlBean;
import com.lib.sdk.bean.WhiteLightBean;

public class AOVBlackLightSettingContract {

    public interface IAOVBlackLightSettingView {

        void onShowWaitDialog();

        Activity getActivity();

        void updateView(WhiteLightBean mWhiteLight,boolean mSupportSetBrightness,boolean mSoftLedThr);

        void onHideWaitDialog();


        /**
         * 显示指示灯状态
         *
         * @param state 指示灯的状态
         */
        void showIndicatorLightState(int state);

        /**
         * 显示微光灯状态
         *
         * @param state 微光灯的状态
         */
        void showMicroLight(int state);

        /**
         * 显示敏感度
         *
         * @param softLedThr 敏感度阈值
         */
        void showSensitive(int softLedThr);

        /**
         * 显示指示灯
         *
         * @param isSupport 表示是否支持该功能
         */
        void showIndicator(boolean isSupport);

        /**
         * 显示微光灯
         *
         * @param isSupport 表示是否支持该功能
         */
        void showMicroFillLight(boolean isSupport);

    }


    public interface IAOVBlackLightSettingPresenter {
        /**
         * 保存亮度设置
         * @param brightness 亮度值
         */
        void saveBrightness(int brightness);

        /**
         * 获取额外状态控制信息
         */
        void getFbExtraStateCtrl();

        /**
         * 获取额外状态信息
         * @return FbExtraStateCtrlBean对象
         */
        FbExtraStateCtrlBean getFbExtraStateInfo();

        /**
         * 设置时间数据
         * 此方法用于保存用户选择的时间设置，包括小时和分钟
         * @param timeSelect 1开始 2 结束
         * @param hours 小时数
         * @param minute 分钟数
         */
        void setTimeData(int timeSelect, int hours, int minute);

        /**
         * 保存敏感度设置
         * @param value 敏感度值
         */
        void saveSensitive(int value);

        /**
         * 获取当前分钟数
         * @return 当前设置或获取到的分钟数
         */
        int getMinute();

        /**
         * 获取当前小时数
         * @return 当前设置或获取到的小时数
         */
        int getHours();

        /**
         * 设置分钟数
         * @param minute 要设置的分钟数值
         */
        void setMinute(int minute);

        /**
         * 设置小时数
         * @param hours 要设置的小时数值
         */
        void setHours(int hours);

        /**
         * 获取相机参数扩展信息
         */
        void getCameraParamEx();

        /**
         * 保存工作模式设置
         * @param mode 工作模式字符串，表示当前选择的工作模式
         */
        void saveWorkMode(String mode);

        /**
         * 保存指示灯状态
         * @param switchState 指示灯的开关状态，可能表示开或关
         */
        void saveIndicatorLight(int switchState);

        /**
         * 获取相机参数扩展Bean
         * @return CameraParamExBean对象
         */
        CameraParamExBean getCameraParamExBean();

        /**
         * 保存微光灯状态
         * 此方法用于保存微光灯的开关状态
         * @param switchState 微光灯的开关状态
         */
        void saveMicroLight(int switchState);

        /**
         * 获取白光灯信息
         * @return WhiteLightBean对象
         */
        WhiteLightBean getWhiteLightBean();

        /**
         * 获取当前时间选择标识，开始or结束
         * @return 当前的时间选择标识
         */
        int getTimeSelect();

        /**
         * 保存工作时段设置
         * 此方法用于保存开始或结束的工作时段
         * @param hour 小时数
         * @param min 分钟数
         * @param isStart 表示是否是开始时段
         */
        void saveWorkPeriod(int hour, int min, boolean isStart);

        /**
         * 获取白光灯信息
         */
        void getWhiteLight();

        /**
         * 检查是否支持白光灯自动开关灯判断阈值
         */
        void checkIsSupportSoftLedThr();

        /**
         * 检查是否支持指示灯
         */
        void checkIsSupportStatusLed();

        /**
         * 检查是否支持微光灯
         */
        void checkIsSupportMicroFillLight();

        /**
         * 检查是否支持亮度设置
         * @return 如果设备支持亮度设置
         */
        boolean isSupportSetBrightness();

        /**
         * 检查敏感度值是否可以设置
         * @return 敏感度值是否可以设置
         */
        boolean isSoftLedThr();



    }
}
