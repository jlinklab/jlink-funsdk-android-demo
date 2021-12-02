package demo.xm.com.xmfunsdkdemo.ui.device.config.alarmvoice.listener;

import com.lib.sdk.bean.VoiceTipTypeBean;

public class AlarmVoiceListContract {
    public interface IAlarmVoiceListPresenter {
        void getConfig();
    }

    public interface IAlarmVoiceListView {
        void onGetVoiceNameListResult(VoiceTipTypeBean voiceTipTypeBean,int errorId);
        /**
         * 是否支持警铃间隔时间设置
         *
         * @param isSupport true支持 false不支持
         */
        void isSupportAlarmVoiceTipIntervalResult(boolean isSupport);
    }
}
