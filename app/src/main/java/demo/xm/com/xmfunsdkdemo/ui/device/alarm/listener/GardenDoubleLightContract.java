package demo.xm.com.xmfunsdkdemo.ui.device.alarm.listener;

import android.app.Activity;

import com.lib.sdk.bean.WhiteLightBean;

public class GardenDoubleLightContract {

    public interface IGardenDoubleLightView {

        void onShowWaitDialog();
        void onHideWaitDialog();

        Activity getActivity();
        /**
         * 灯光颜色
         */
        void showColorWhiteLight(int selectValue);

        /**
         * 灯光开关工作模式
         */
        void showWorkMode(WhiteLightBean mWhiteLight);

    }


    public interface IGardenDoubleLightPresenter {
        /**
         * 获取白光灯工作模式设置
         */
        void getWhiteLight();
        /**
         * 获取摄像机参数
         */
        void getCameraParam();

        /**
         * 保存图像设置配置
         */
        void saveDayNightColorConfig(String dayNightColor);
        /**
         * 保存白光灯工作模式设置
         */
        void saveWhiteLight();
        /**
         * 获取白光灯工作模式设置数据
         */
        WhiteLightBean getWhiteLightBean();

    }
}
