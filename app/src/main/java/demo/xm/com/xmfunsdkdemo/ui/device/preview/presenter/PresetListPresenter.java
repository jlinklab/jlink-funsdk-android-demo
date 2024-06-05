package demo.xm.com.xmfunsdkdemo.ui.device.preview.presenter;

import android.os.Message;

import com.basic.G;
import com.lib.MsgContent;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.OPPTZControlBean;
import com.lib.sdk.bean.preset.ConfigGetPreset;
import com.manager.base.BaseManager;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigManager;
import com.manager.device.config.preset.PresetManager;
import com.xm.activity.base.XMBasePresenter;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.ui.device.preview.listener.PresetListContract;

/**
 * 预置点列表
 */
public class PresetListPresenter extends XMBasePresenter<BaseManager> implements PresetListContract.IPresetListPresenter,
        DevConfigManager.OnDevConfigResultListener {
    private PresetManager presetManager;
    private List<ConfigGetPreset> presetList;
    private PresetListContract.IPresetListView iPresetListView;

    @Override
    protected BaseManager getManager() {
        return null;
    }

    public PresetListPresenter(PresetListContract.IPresetListView iPresetListView) {
        this.iPresetListView = iPresetListView;
    }

    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        presetManager = new PresetManager(getDevId(), this);

    }

    @Override
    public void updatePresetList() {
        presetManager.getPresetList(0);
    }

    @Override
    public void deletePreset(int presetId) {
        presetManager.clearPreset(0, presetId);
    }

    @Override
    public void modifyPresetName(int presetId, String presetName) {
        presetManager.modifyPresetName(0, presetId, presetName);
    }

    @Override
    public void onSuccess(String devId, int operationType, Object result) {

    }

    @Override
    public void onFailed(String devId, int msgId, String jsonName, int errorId) {

    }

    @Override
    public void onFunSDKResult(Message message, MsgContent msgContent) {
        if (ConfigGetPreset.JSON_NAME.equals(msgContent.str)) {
            //获取预置点列表
            HandleConfigData handleConfigData = new HandleConfigData();
            if (handleConfigData.getDataObj(G.ToString(msgContent.pData), ConfigGetPreset.class)) {
                presetList = (List<ConfigGetPreset>) handleConfigData.getObj();
                iPresetListView.onGetPresetListResult(presetList);
            }else {
                iPresetListView.onGetPresetListResult(null);
            }
        } else if (OPPTZControlBean.OPPTZCONTROL_JSONNAME.equals(msgContent.str)) {
            if (msgContent.seq == PresetManager.PRESET_MODIFY_NAME) {
                //修改预置点名称
                iPresetListView.onModifyPresetNameResult(message.arg1 >= 0, message.arg1);
            } else if (msgContent.seq == PresetManager.PRESET_REMOVE) {
                //删除预置点
                iPresetListView.onDeletePresetResult(message.arg1 >= 0, message.arg1);
            }
        }
    }
}
