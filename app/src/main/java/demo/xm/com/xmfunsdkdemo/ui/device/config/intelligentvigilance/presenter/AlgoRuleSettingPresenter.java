package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.presenter;

import com.lib.FunSDK;
import com.lib.sdk.bean.ChannelHumanRuleLimitBean;
import com.lib.sdk.bean.HumanDetectionBean;
import com.manager.device.DeviceManager;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.device.config.alarm.manager.AlgoDetectConfigManager;
import demo.xm.com.xmfunsdkdemo.ui.device.config.alarm.model.AlgoType;
import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.listener.AlgoRuleSettingContract;

/**
 * 多算法智能报警 - 规则设置 Presenter
 * <p>
 * 管理 AlgoDetectConfigManager，处理设备配置的加载与保存，
 * 包括并发保护机制（mPendingSave / mIsSaving）。
 */
public class AlgoRuleSettingPresenter extends XMBasePresenter<DeviceManager>
        implements AlgoRuleSettingContract.IAlgoRuleSettingPresenter,
        AlgoDetectConfigManager.OnConfigResultListener {

    private AlgoRuleSettingContract.IAlgoRuleSettingView mView;

    /** 页面自有配置管理器：进入页面后请求配置，页面内每次修改立即保存到设备 */
    private AlgoDetectConfigManager mConfigManager;
    /** 配置字典是否已加载完成（requestSaveConfig 依赖先 requestConfig 成功） */
    private boolean mConfigReady = false;
    /** 是否正在保存中（并发保护：保存回调到达前的新修改挂起，回调后重试） */
    private boolean mIsSaving = false;
    /** 是否有挂起的保存（保存中/配置未就绪时又发生修改）：null=无挂起，非 null 记录该次保存成功后是否显示 Toast */
    private Boolean mPendingSave = null;
    /** 本次保存成功后是否显示"保存成功"Toast */
    private boolean mShowSaveToast = true;
    /** 本次会话是否发生过修改（决定返回时是否 setResult 回传数据） */
    private boolean mDirty = false;

    private HumanDetectionBean mHumanDetectionBean;
    private ChannelHumanRuleLimitBean mRuleLimitBean;
    private AlgoType mAlgoType;
    private String mDevId;
    private int mChannel;

    public AlgoRuleSettingPresenter(AlgoRuleSettingContract.IAlgoRuleSettingView view) {
        this.mView = view;
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    // ==================== 数据设置 ====================

    public void setHumanDetectionBean(HumanDetectionBean bean) {
        mHumanDetectionBean = bean;
    }

    public void setRuleLimitBean(ChannelHumanRuleLimitBean bean) {
        mRuleLimitBean = bean;
    }

    public void setAlgoType(AlgoType algoType) {
        mAlgoType = algoType;
    }

    public void setDevId(String devId) {
        mDevId = devId;
    }

    public void setChannel(int channel) {
        mChannel = channel;
    }

    // ==================== IAlgoRuleSettingPresenter 实现 ====================

    @Override
    public void initConfig() {
        if (mDevId == null || mDevId.isEmpty()) {
            mView.finishActivity();
            return;
        }
        mConfigManager = new AlgoDetectConfigManager();
        mConfigManager.setAlgoType(mAlgoType != null ? mAlgoType : AlgoType.HumanDetect);
        mConfigManager.setDevId(mDevId);
        mConfigManager.setChannel(mChannel);
        // requestSaveConfig 依赖已加载的配置字典，必须先 requestConfig 成功
        mConfigManager.requestConfig(mDevId, mChannel, this);
    }

    @Override
    public void saveToDevice() {
        saveToDevice(true);
    }

    @Override
    public void saveToDevice(boolean showToast) {
        mDirty = true;
        if (mConfigManager == null || !mConfigReady) {
            // 配置字典未就绪，挂起待 requestConfig 成功后重试
            mPendingSave = showToast;
            return;
        }
        if (mIsSaving) {
            // 上一次保存尚未回调，本次修改挂起，回调后重试
            mPendingSave = showToast;
            return;
        }

        String pedRuleJson = com.alibaba.fastjson.JSON.toJSONString(mHumanDetectionBean.getPedRules());
        mConfigManager.setPedRuleJson(pedRuleJson);
        mView.showWaitDialog();

        mIsSaving = true;
        mShowSaveToast = showToast;
        mConfigManager.requestSaveConfig(this);
    }

    @Override
    public void setPedRuleJson(String json) {
        if (mConfigManager != null) {
            mConfigManager.setPedRuleJson(json);
        }
    }

    @Override
    public HumanDetectionBean getHumanDetectionBean() {
        return mHumanDetectionBean;
    }

    @Override
    public ChannelHumanRuleLimitBean getRuleLimitBean() {
        return mRuleLimitBean;
    }

    @Override
    public AlgoType getAlgoType() {
        return mAlgoType;
    }

    @Override
    public String getDevId() {
        return mDevId;
    }

    @Override
    public int getChannel() {
        return mChannel;
    }

    @Override
    public boolean isDirty() {
        return mDirty;
    }

    @Override
    public void release() {
        if (mConfigManager != null) {
            mConfigManager.release();
            mConfigManager = null;
        }
    }

    // ==================== AlgoDetectConfigManager.OnConfigResultListener ====================

    @Override
    public void onConfigResult(AlgoType algoType, boolean success, String configData) {
        // 静默回调：不覆盖页面已展示的数据，仅标记配置就绪
        mConfigReady = success;
        if (success && mPendingSave != null) {
            boolean pendingShowToast = mPendingSave;
            mPendingSave = null;
            saveToDevice(pendingShowToast);
        }
        mView.onConfigLoaded();
    }

    @Override
    public void onConfigSaved(AlgoType algoType, boolean success) {
        mIsSaving = false;
        mView.hideWaitDialog();
        if (success && mShowSaveToast) {
            mView.showSaveSuccess();
        }
        mShowSaveToast = true;
        if (mPendingSave != null) {
            boolean pendingShowToast = mPendingSave;
            mPendingSave = null;
            saveToDevice(pendingShowToast);
        }
        mView.onConfigSaveComplete(success);
    }

    @Override
    public void onConfigFailed(AlgoType algoType, int what, int errCode, String errStr, boolean isGet) {
        String errMsg = FunSDK.TS("set_dev_config_failed") + ":" + errCode;
        mView.showError(errMsg);
    }
}
