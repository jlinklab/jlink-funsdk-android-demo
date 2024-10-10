package demo.xm.com.xmfunsdkdemo.ui.device.config.alarmconfig.listener;

import com.lib.sdk.bean.AlarmInfoBean;
import com.lib.sdk.bean.HumanDetectionBean;

/**
 * 报警配置界面,包括移动侦测,视频遮挡,报警输出等
 * Created by jiangping on 2018-10-23.
 */
public class DevAlarmSetContract {
    public interface IDevAlarmSetView {

        /**
         * 处理生成的json数据并显示到空间上
         *
         * @param alarmInfoBean 报警配置
         * @param key           视频丢失警报、移动侦测、视频遮挡、外部设备报警
         * @param state         状态：可见、不可见
         */
        void updateUI(AlarmInfoBean alarmInfoBean, final String key, final int state);

        void saveLossResult(final int state);   //保存视频丢失警报回调
        void savePirAlarmResult(final int state);   //保存pir警报回调

        void saveMotionResult(final int state);

        void saveBlindResult(final int state);

        void saveHumanResult(final int state);

        /**
         * 是否支持人形检测回调
         *
         * @param isSupport true支持 false不支持
         */
        void isSupportHumanDetectResult(boolean isSupport);

        /**
         * 是否支持报警提示音回调
         *
         * @param isSupport true支持 false不支持
         */
        void isSupportVoiceTipsResult(boolean isSupport);

        /**
         * 是否支持自定义语音提示音
         *
         * @param isSupport true支持 false不支持
         */
        void isSupportVoiceTipsTypeResult(boolean isSupport);
    }

    public interface IDevAlarmSetPresenter {
        void getDevAlarm();

        void saveLossDetect();
        void savePIRAlarm();

        void saveMotionDetect();

        void saveBlindDetect();
    }
}
