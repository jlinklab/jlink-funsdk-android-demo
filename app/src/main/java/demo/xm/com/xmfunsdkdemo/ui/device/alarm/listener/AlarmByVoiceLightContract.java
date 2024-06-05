package demo.xm.com.xmfunsdkdemo.ui.device.alarm.listener;

import com.lib.sdk.bean.WhiteLightBean;


/**
 * 声光报警界面,可改变控制模式
 */
public class AlarmByVoiceLightContract {
    public interface IAlarmByVoiceLightView {
        /**
         * 根据result获取的数据显示到控件上
         */
        void onUpdateView(boolean isSuccess, WhiteLightBean whiteLightBean) ;

        /**
         * 控件数据保存回调
         */
        void onSaveResult(boolean isSuccess);
    }

    public interface IAlarmByVoiceLightPresenter {
        /**
         * 获取声光报警配置
         */
        void getAlarmByVoiceLightConfig();
        WhiteLightBean getWhiteLightBean();

        /**
         * 保存声光报警配置
         */
        void saveAlarmByVoiceLightConfig();
    }
}
