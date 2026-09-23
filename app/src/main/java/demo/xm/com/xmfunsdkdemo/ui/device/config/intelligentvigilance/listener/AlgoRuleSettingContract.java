package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.listener;

import com.lib.sdk.bean.ChannelHumanRuleLimitBean;
import com.lib.sdk.bean.HumanDetectionBean;

import demo.xm.com.xmfunsdkdemo.ui.device.config.alarm.model.AlgoType;

/**
 * 多算法智能报警 - 规则设置 MVP Contract
 */
public interface AlgoRuleSettingContract {

    interface IAlgoRuleSettingView {
        /** 显示/隐藏加载等待框 */
        void showWaitDialog();
        void hideWaitDialog();

        /** 显示错误提示 */
        void showError(String errMsg);

        /** 显示保存成功提示 */
        void showSaveSuccess();

        /** 配置加载完成回调（Presenter requestConfig 成功后调用） */
        void onConfigLoaded();

        /** 保存完成回调（Presenter requestSaveConfig 回调后调用） */
        void onConfigSaveComplete(boolean success);

        /** 返回结果给上级页面 */
        void returnResult(String pedRuleJson);

        /** 结束页面 */
        void finishActivity();
    }

    interface IAlgoRuleSettingPresenter {
        /** 初始化配置管理器并请求设备配置 */
        void initConfig();

        /** 保存 PedRule 到设备 */
        void saveToDevice(boolean showToast);

        /** 保存 PedRule 到设备（默认显示 Toast） */
        void saveToDevice();

        /** 设置 PedRule 规则数据 */
        void setPedRuleJson(String json);

        /** 获取 HumanDetectionBean */
        HumanDetectionBean getHumanDetectionBean();

        /** 获取 ChannelHumanRuleLimitBean */
        ChannelHumanRuleLimitBean getRuleLimitBean();

        /** 获取算法类型 */
        AlgoType getAlgoType();

        /** 获取设备序列号 */
        String getDevId();

        /** 获取通道号 */
        int getChannel();

        /** 是否已修改过数据 */
        boolean isDirty();

        /** 释放资源 */
        void release();
    }
}
