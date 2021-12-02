package demo.xm.com.xmfunsdkdemo.ui.device.alarm.listener;

import android.app.Activity;

import com.lib.sdk.bean.AlarmInfoBean;
import com.lib.sdk.bean.DevVolumeBean;
import com.lib.sdk.bean.IntelliAlertAlarmBean;
import com.lib.sdk.bean.LP4GLedParameterBean;
import com.lib.sdk.bean.VoiceTipBean;
import com.lib.sdk.bean.WhiteLightBean;
import demo.xm.com.xmfunsdkdemo.ui.entity.WorkModeBean;

import java.util.List;


/**
 * 声光报警界面,可改变控制模式
 */
public class WhiteLightContract {
    public interface IWhiteLightView {

        void onShowWaitDialog();
        void onHideWaitDialog();
        /**
         * 灯光颜色
         */
        void showColorWhiteLight(long selectValue);

        /**
         *根据pir报警配置显示设置项
         */
        void showPIRAlarmInfo(AlarmInfoBean mPIRAlarmInfoBean, WorkModeBean workModeBean,boolean mSupportGardenLight);
        /**
         * 报警铃声设置
         */
       void initRingtone(List<VoiceTipBean> mVoiceTipBeanList, IntelliAlertAlarmBean mIntelliAlertAlarmBean,String voiceType);
        /**
         * 报警音量设置
         */
        void initVolume(DevVolumeBean mDevHornVolumeBean);
        /**
         * 智能警戒配置
         */
        void initSmartAlarmData(boolean isNvr,IntelliAlertAlarmBean mIntelliAlertAlarmBean,
                                boolean mSupportGardenLight,boolean mSupportSetAlarmLed,boolean isNoWBS);

        /**
         * 灯光设置
         */
        void initDoubleLightSwitch(WorkModeBean workModeBean, LP4GLedParameterBean mLp4GLedParameterBean);
        /**
         * 庭院灯照明开关
         */
        void initGardenLightSwitch(LP4GLedParameterBean mLp4GLedParameterBean,boolean mSupportSetBrightness);
        /**
         * 灯光开关工作模式
         */
        void showWorkMode(WhiteLightBean mWhiteLight);
        /**
         * pir报警标题
         */
        void showPirAlarmTitle(WorkModeBean workModeBean,boolean mSupportGardenLight);

        Activity getActivity();
    }

    public interface IWhiteLightPresenter {
        /**
         * 获取声光报警配置
         */
        void getVoiceLightAlarmConfig();
        /**
         * 获取白光灯工作模式设置数据
         */
        WhiteLightBean getWhiteLightBean();
        /**
         * 保存白光灯工作模式设置
         */
        void saveWhiteLight();
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
         * 保存图像设置配置
         */
        void saveDayNightColorConfig(String dayNightColor);

        /**
         * 保存庭院灯配置
         */
        void saveGardenConfig();
        /**
         * 设置报警音量
         */
        void saveVolume();
        /**
         * 保存报警铃声配置
         */
        void saveIntelliAlertAlarmVoiceType(int value);

        /**
         * 保存灯光设置
         */
        void saveDoubleLightSwitch(int value);
        /**
         * 保存庭院灯照明开关配置
         */
        void saveGardenLightSwitch(boolean isOpen);

        /**
         * 是否支持设置灯光亮度
         */

        boolean isSupportSetBrightness();

        /**
         *保存报警音量配置
         */
        void saveDevHornVolume(int volume);


        /**
         *保存灯光亮度配置
         */
        void saveBrightness(int brightness);


    }
}
