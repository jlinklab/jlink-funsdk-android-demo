package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.presenter;

import android.os.Message;

import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.ChannelHumanRuleLimitBean;
import com.lib.sdk.bean.DetectTrackBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.HumanDetectionBean;
import com.lib.sdk.bean.JsonConfig;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.listener.IntelligentVigilanceContract;

import static com.lib.sdk.bean.HumanDetectionBean.IA_TRIPWIRE;

/**
 * 人形检测、智能警戒等
 * @author hws
 * @class describe
 * @time 2020/12/15 18:44
 */
public class IntelligentVigilancePresenter extends XMBasePresenter<DeviceManager>
        implements IntelligentVigilanceContract.IIntelligentVigilancePresenter {
    private IntelligentVigilanceContract.IIntelligentVigilanceView iIntelligentVigilanceView;
    private DevConfigManager devConfigManager;
    private HumanDetectionBean humanDetectionBean;
    private ChannelHumanRuleLimitBean channelHumanRuleLimitBean;
    private DetectTrackBean detectTrackBean;
    public IntelligentVigilancePresenter(IntelligentVigilanceContract.IIntelligentVigilanceView iIntelligentVigilanceView) {
        this.iIntelligentVigilanceView = iIntelligentVigilanceView;
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        devConfigManager = manager.getDevConfigManager(getDevId());
    }

    /**
     * 更新人形检测能力集
     */
    @Override
    public void updateHumanDetectAbility() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DevConfigManager.OnDevConfigResultListener() {
            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {

            }

            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                if (result instanceof String) {
                    HandleConfigData handleConfigData = new HandleConfigData();
                    if(handleConfigData.getDataObj((String) result,ChannelHumanRuleLimitBean.class)) {
                        channelHumanRuleLimitBean = (ChannelHumanRuleLimitBean) handleConfigData.getObj();
                        if (iIntelligentVigilanceView != null) {
                            iIntelligentVigilanceView.updateHumanRuleSupportResult(true);
                        }
                    }
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                if (iIntelligentVigilanceView != null) {
                    iIntelligentVigilanceView.updateHumanRuleSupportResult(false);
                }
            }
        });

        devConfigInfo.setChnId(-1);
        devConfigInfo.setJsonName(JsonConfig.HUMAN_RULE_LIMIT);
        devConfigInfo.setCmdId(1360);

        devConfigManager.setDevCmd(devConfigInfo);

        updateHumanDetect();
    }

    /**
     * 更新人形检测配置
     */
    private void updateHumanDetect() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DevConfigManager.OnDevConfigResultListener() {
            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {

            }

            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                if (result instanceof String) {
                    HandleConfigData handleConfigData = new HandleConfigData();
                    if (handleConfigData.getDataObj((String)result, HumanDetectionBean.class)) {
                        humanDetectionBean = (HumanDetectionBean) handleConfigData.getObj();
                        if (iIntelligentVigilanceView != null) {
                            iIntelligentVigilanceView.updateHumanDetectResult(true,0);
                        }
                    }
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                if (iIntelligentVigilanceView != null) {
                    iIntelligentVigilanceView.updateHumanDetectResult(false,errorId);
                }
            }
        });

        devConfigInfo.setChnId(0);
        devConfigInfo.setJsonName(JsonConfig.DETECT_HUMAN_DETECTION);

        devConfigManager.getDevConfig(devConfigInfo);
    }

    /**
     * 更新人形跟随配置
     */
    private void updateDetectTrack() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DevConfigManager.OnDevConfigResultListener() {
            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {

            }

            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                if (result instanceof String) {
                    HandleConfigData handleConfigData = new HandleConfigData();
                    if (handleConfigData.getDataObj((String) result, DetectTrackBean.class)) {
                        detectTrackBean = (DetectTrackBean) handleConfigData.getObj();
                    }
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                if (iIntelligentVigilanceView != null) {
                    iIntelligentVigilanceView.updateHumanDetectResult(false,errorId);
                }
            }
        });

        devConfigInfo.setChnId(-1);
        devConfigInfo.setJsonName(JsonConfig.CFG_DETECT_TRACK);

        devConfigManager.getDevConfig(devConfigInfo);
    }
    @Override
    public void saveHumanDetect() {
        if (humanDetectionBean == null) {
            return;
        }

        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DevConfigManager.OnDevConfigResultListener() {
            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {

            }

            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                if (iIntelligentVigilanceView != null) {
                    iIntelligentVigilanceView.saveHumanDetectResult(true,0);
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                if (iIntelligentVigilanceView != null) {
                    iIntelligentVigilanceView.saveHumanDetectResult(false,errorId);
                }
            }
        });

        devConfigInfo.setChnId(0);
        devConfigInfo.setJsonName(JsonConfig.DETECT_HUMAN_DETECTION);
        devConfigInfo.setJsonData(
                HandleConfigData.getSendData(
                        HandleConfigData.getFullName(JsonConfig.DETECT_HUMAN_DETECTION,0),
                        "0x08",humanDetectionBean));

        devConfigManager.setDevConfig(devConfigInfo);
    }

    /**
     * 人形检测是否支持
     * @return
     */
    @Override
    public boolean isHumanDetectEnable() {
        return humanDetectionBean != null ? humanDetectionBean.isEnable() : false;
    }

    /**
     *设置人形检测是否支持
     * @param enable
     */
    @Override
    public void setHumanDetectEnable(boolean enable) {
        if (humanDetectionBean != null) {
            humanDetectionBean.setEnable(enable);
        }
    }

    /**
     * 是否显示轨迹
     * @return
     */
    @Override
    public boolean isShowTrack() {
        return humanDetectionBean != null ? humanDetectionBean.isShowTrack() : false;
    }

    /**
     * 设置是否显示轨迹
     * @param isShow
     */
    @Override
    public void setShowTrack(boolean isShow) {
        if (humanDetectionBean != null) {
            humanDetectionBean.setShowTrack(isShow);
        }
    }


    @Override
    public int getRuleType() {
        if (humanDetectionBean != null) {
            if (!humanDetectionBean.getPedRules().isEmpty()) {
                return humanDetectionBean.getPedRules().get(0).getRuleType();
            }
        }
        return IA_TRIPWIRE;
    }


    @Override
    public void setRuleType(int ruleType) {
        if (humanDetectionBean != null && !humanDetectionBean.getPedRules().isEmpty()) {
            humanDetectionBean.getPedRules().get(0).setRuleType(ruleType);
        }
    }

    /**
     * 规则是否显示
     * @return
     */
    @Override
    public boolean isRuleEnable() {
        return humanDetectionBean != null ? humanDetectionBean.getPedRules().get(0).isEnable() : false;
    }

    /**
     * 设置显示规则
     * @param enable
     */
    @Override
    public void setRuleEnable(boolean enable) {
        if (humanDetectionBean != null) {
            humanDetectionBean.getPedRules().get(0).setEnable(enable);
        }
    }

    /**
     * 是否支持配置显示踪迹
     * @return
     */
    @Override
    public boolean isTrackSupport() {
        if (channelHumanRuleLimitBean != null) {
            return channelHumanRuleLimitBean.isShowTrack();
        }
        return false;
    }

    /**
     * 伴线是否支持
     * @return
     */
    @Override
    public boolean isLineSupport() {
        if (channelHumanRuleLimitBean != null) {
            return channelHumanRuleLimitBean.isSupportLine();
        }
        return false;
    }

    /**
     * 警戒区域是否支持
     * @return
     */
    @Override
    public boolean isAreaSupport() {
        if (channelHumanRuleLimitBean != null) {
            return channelHumanRuleLimitBean.isSupportArea();
        }
        return false;
    }

    /**
     * 人形跟随是否打开
     * @return
     */
    @Override
    public boolean isTrackDetectEnable() {
        if (detectTrackBean != null) {
            return detectTrackBean.getEnable() == SDKCONST.Switch.Open;
        }
        return false;
    }

    @Override
    public HumanDetectionBean getHumanDetection() {
        return humanDetectionBean;
    }

    @Override
    public void setHumanDetection(HumanDetectionBean humanDetection) {
        this.humanDetectionBean = humanDetection;
    }

    @Override
    public ChannelHumanRuleLimitBean getChannelHumanRuleLimitBean() {
        return channelHumanRuleLimitBean;
    }
}
