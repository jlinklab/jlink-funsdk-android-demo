package demo.xm.com.xmfunsdkdemo.ui.device.aov.listener;

import android.app.Activity;
import android.content.Context;

import demo.xm.com.xmfunsdkdemo.ui.entity.AovWorkModeBean;

public class AOVWorkingModeContract {
    public interface IAOVWorkingModeView {

        void onShowWaitDialog();

        Activity getActivity();

        void onHideWaitDialog();

        /**
         * 显示工作模式界面
         *
         * @param workBean 工作模式的详细信息
         */
        public void showWorkModel(AovWorkModeBean workBean);

        /**
         * 保存成功状态
         *
         * @param saveSuccess 表示保存操作是否成功
         */
        public void saveSuccess(boolean saveSuccess);

        /**
         * 回传配置获取状态
         *
         * @param getConfigBack 表示配置获取是否成功
         */
        public void getConfigBack(boolean getConfigBack);

        /**
         * 显示提示信息
         *
         * @param showTips    是否显示提示，true表示显示，false表示不显示
         * @param powerThreshold 电源的阈值，用于提示信息的判断依据
         */
        public void showTips(boolean showTips, int powerThreshold);

        /**
         * 处理双光源相机支持情况的结果
         *
         * @param isSupport 表示当前设备是否支持双光源相机功能
         */
        void onSupportDoubleLightBoxCameraResult(boolean isSupport);

        /**
         * 显示AOV报警间隔
         *
         * @param isSupport 表示AOV报警间隔功能是否支持
         */
        void showAovInterval(boolean isSupport);
    }


    public interface IAOVWorkingModePresenter {

        /**
         * 设置设备的充电百分比
         *
         * @param percent 充电百分比
         */
        public void setPercent(int percent);

        /**
         * 获取当前的工作模式信息
         *
         * @return 返回一个包含工作模式信息的AovWorkModeBean对象
         */
        public AovWorkModeBean getWorkBean();

        /**
         * 设置设备的工作模式
         */
        public void setAovWorkModel();

        /**
         * 获取设备的低电量模式状态
         */
        public void getLowElectrMode();

        /**
         * 获取当前设备的工作模式信息
         */
        public void getAovWorkModel();

        /**
         * 获取设备的电池信息
         *
         * @param context 上下文
         */
        public void getDevBattery(Context context);

        /**
         * 释放IDR模型资源
         */
        public void releaseIDRModel();

        /**
         * 检查设备是否支持设置AOV报警间隔功能
         */
        void checkIsSupportAovAlarmHold();

        /**
         * 检查设备是否支持双光灯录像机功能
         */
        void checkIsSupportDoubleLightBoxCamera();
    }
}
