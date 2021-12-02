package demo.xm.com.xmfunsdkdemo.ui.device.aov.listener;

import android.app.Activity;

import java.util.List;

public class SetFPSContract {
    public interface ISetFPSView {

        void onShowWaitDialog();

        Activity getActivity();

        void onHideWaitDialog();

        /**
         * 显示AOV能力数据
         */
        void showAovAbility();
    }

    public interface ISetFPSPresenter {
        /**
         * 获取AOV能力
         */
        void getAovAbility();

        /**
         * 获取AOV报警时间间隔列表
         * @return 返回一个整数列表，包含所有可用的AOV报警时间间隔
         */
        List<Integer> getAovAlarmTimeIntervalList();

        /**
         * 获取帧率列表
         * @return 返回一个字符串列表，包含所有可用的帧率选项
         */
        List<String> getFpsList();

        /**
         * 获取录像延迟列表
         * @return 返回一个整数列表，包含所有录像延迟的值
         */
        List<Integer> getRecordLatchList();

        /**
         * 设置当前帧率
         * 该方法允许用户设置设备当前使用的帧率
         * @param fps 帧率的字符串表示
         */
        void setCurFps(String fps);

        /**
         * 获取当前帧率
         * @return 返回当前设置的帧率字符串
         */
        String getCurFps();

        /**
         * 设置设备电池工作模式
         * 该方法允许用户根据需求设置设备的电池工作模式
         * @param mode 设备的工作模式字符串
         */
        void setMode(String mode);

        /**
         * 获取当前设备电池工作模式
         * @return 返回当前设置的设备电池工作模式字符串
         */
        String getMode();

        /**
         * 设置当前录像延迟值
         * @param recordLatch 录像延迟的整数值
         */
        void setCurRecordLatch(int recordLatch);

        /**
         * 获取当前录像延迟值
         * @return 返回当前设置的记录像延迟整数值
         */
        int getCurRecordLatch();

        /**
         * 设置当前时间间隔
         * 该方法允许用户设置当前操作或记录的时间间隔
         * @param interval 时间间隔的整数值
         */
        void setCurInterval(int interval);

        /**
         * 获取当前时间间隔
         * @return 返回当前设置的时间间隔整数值
         */
        int getCurInterval();
    }
}
