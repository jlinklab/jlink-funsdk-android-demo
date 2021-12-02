package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.listener;

import com.lib.sdk.bean.ChannelHumanRuleLimitBean;
import com.lib.sdk.bean.HumanDetectionBean;

/**
 * 人形检测、智能警戒等
 *
 * @author hws
 * @class describe
 * @time 2020/12/15 18:45
 */
public interface IntelligentVigilanceContract {
    interface IIntelligentVigilanceView {
        /**
         * 是否支持人形规则（智能警戒）
         *
         * @param isSupport
         */
        void updateHumanRuleSupportResult(boolean isSupport);

        void updateHumanDetectResult(boolean isSuccess, int errorId);

        void saveHumanDetectResult(boolean isSuccess, int errorId);
    }

    interface IIntelligentVigilancePresenter {
        void updateHumanDetectAbility();

        void saveHumanDetect();

        /**
         * 人形检测是否支持
         *
         * @return
         */
        boolean isHumanDetectEnable();

        /**
         * 设置人形检测是否支持
         *
         * @param enable
         */
        void setHumanDetectEnable(boolean enable);

        /**
         * 是否显示轨迹
         *
         * @return
         */
        boolean isShowTrack();

        /**
         * 设置是否显示轨迹
         *
         * @param isShow
         */
        void setShowTrack(boolean isShow);

        int getRuleType();

        void setRuleType(int ruleType);

        /**
         * 规则是否显示
         *
         * @return
         */
        boolean isRuleEnable();

        /**
         * 设置显示规则
         *
         * @param enable
         */
        void setRuleEnable(boolean enable);

        /**
         * 是否支持配置显示踪迹
         *
         * @return
         */
        boolean isTrackSupport();

        /**
         * 伴线是否支持
         *
         * @return
         */
        boolean isLineSupport();

        /**
         * 警戒区域是否支持
         *
         * @return
         */
        boolean isAreaSupport();

        /**
         * 人形跟随是否打开
         *
         * @return
         */
        boolean isTrackDetectEnable();

        HumanDetectionBean getHumanDetection();

        void setHumanDetection(HumanDetectionBean humanDetection);

        ChannelHumanRuleLimitBean getChannelHumanRuleLimitBean();
    }
}
