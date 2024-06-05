package demo.xm.com.xmfunsdkdemo.ui.device.preview.listener;

import android.content.Context;
import android.view.ViewGroup;

import com.lib.sdk.bean.ElectCapacityBean;
import com.lib.sdk.bean.WifiRouteInfo;
import com.lib.sdk.bean.preset.ConfigGetPreset;
import com.manager.device.DeviceManager;
import com.xm.linke.face.FaceFeature;

import java.util.List;

/**
 * Created by jiangping on 2018-10-23.
 */
public class PresetListContract {
    public interface IPresetListView {
        /**
         * 获取预置点列表结果返回
         *
         * @param presetList
         */
        void onGetPresetListResult(List<ConfigGetPreset> presetList);

        /**
         * 删除预置点结果回调
         *
         * @param isSuccess 是否成功
         * @param errorId   错误码
         */
        void onDeletePresetResult(boolean isSuccess, int errorId);

        /**
         * 修改预置点名称结果回调
         *
         * @param isSuccess 是否成功
         * @param errorId   错误码
         */
        void onModifyPresetNameResult(boolean isSuccess, int errorId);
    }

    public interface IPresetListPresenter {
        /**
         * 更新预置点列表
         */
        void updatePresetList();

        /**
         * 删除预置点
         *
         * @param presetId 预置点Id
         */
        void deletePreset(int presetId);

        /**
         * 修改预置点名称
         *
         * @param presetId   预置点Id
         * @param presetName 预置点名
         */
        void modifyPresetName(int presetId, String presetName);
    }
}

