package demo.xm.com.xmfunsdkdemo.ui.device.alarm.listener;

import android.app.Activity;

import com.lib.sdk.bean.AlarmInfoBean;
import com.lib.sdk.bean.DevVolumeBean;
import com.lib.sdk.bean.IntelliAlertAlarmBean;
import com.lib.sdk.bean.LP4GLedParameterBean;
import com.lib.sdk.bean.VoiceTipBean;
import com.lib.sdk.bean.WhiteLightBean;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.ui.entity.WorkModeBean;


/**
 * 声光报警：双光枪机界面,可改变控制模式
 */
public class DoubleLightBoxContract {
    public interface IDoubleLightBoxView {
        void onShowWaitDialog();

        void onHideWaitDialog();

        Activity getActivity();

        /**
         * 根据result获取的数据显示到控件上
         */
        void onUpdateView(boolean isSuccess, WhiteLightBean whiteLightBean);

        /**
         * 控件数据保存回调
         */
        void onSaveResult(boolean isSuccess);

        /**
         * 报警铃声设置
         */
        void initRingtone(List<VoiceTipBean> mVoiceTipBeanList, IntelliAlertAlarmBean mIntelliAlertAlarmBean, String voiceType);

        /**
         * 报警音量设置
         */
        void initVolume(DevVolumeBean mDevHornVolumeBean);

        /**
         * 智能警戒配置
         */
        void initSmartAlarmData(IntelliAlertAlarmBean mIntelliAlertAlarmBean);

        /**
         */
        void initWhiteLightSwitch(String[] modeName,Integer[] modeValue);
    }

    public interface IDoubleLightBoxPresenter {
        /**
         * 获取声光报警配置
         */
        void getAlarmByVoiceLightConfig();

        WhiteLightBean getWhiteLightBean();

        /**
         * 保存报警铃声配置
         */
        void saveIntelliAlertAlarmVoiceType(int value);

        /**
         * 保存声光报警配置
         */
        void saveAlarmByVoiceLightConfig();

        /**
         * 保存智能报警间隔配置
         */
        void saveSmartAlarmDurationConfig(int duration);

        /**
         * 保存智能报警开关配置
         */
        void saveSmartAlarmSwitchConfig(boolean isOpen);


        /**
         * 保存报警灯光开关配置
         */
        void saveSmartAlarmLightSwitchConfig(boolean isOpen);

        /**
         * 保存报警音量配置
         */
        void saveDevHornVolume(int volume);

        /**
         * 设置报警音量
         */
        void saveVolume();
    }
}
