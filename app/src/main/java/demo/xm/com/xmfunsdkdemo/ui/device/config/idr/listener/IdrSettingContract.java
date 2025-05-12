package demo.xm.com.xmfunsdkdemo.ui.device.config.idr.listener;

import android.app.Activity;

import com.lib.sdk.bean.AlarmInfoBean;

public class IdrSettingContract {

    public interface IIdrSettingView {
        void onShowWaitDialog();

        Activity getActivity();


        void onHideWaitDialog();

        /**
         * 显示pir报警配置
         */

        void updatePirView(AlarmInfoBean mAlarmInfoBean);

        /**
         * 显示灵敏度配置
         */
        void showPirSensitiveView(AlarmInfoBean mAlarmInfoBean);

        /**
         * 显示报警时间段配置
         */
        void showPirTimeSection(AlarmInfoBean mAlarmInfoBean);

        /**
         * 显示人形检测
         */
        void setLsiHumanDetectionState(boolean isEnabled);

        /**
         * 录像开关
         */
        void setLsiRecordEnableState(boolean isEnable);


        /**
         * 隐藏pir报警配置
         */
        void hidePirView();

        /**
         * 报警录像时长配置
         */
        void initPirRecordDuration(boolean isSupportLowPowerLongAlarmRecord);
    }

    public interface IIdrSettingPresenter {
        /**
         * 保存报警时间段配置
         */
        void savePirTimeSection(int[] startTime,int[] endTime,boolean enable,int weekMaskOne,String times);

        /**
         * 保存灵敏度配置
         */
        void saveSensitive(int value);

        /**
         * 保存人形检测
         */
         void saveHumanDetectionConfig(boolean isEnable);


        /**
         * 初始化数据
         */
         void initData();
    }
}
