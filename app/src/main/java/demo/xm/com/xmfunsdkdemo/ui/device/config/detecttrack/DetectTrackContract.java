package demo.xm.com.xmfunsdkdemo.ui.device.config.detecttrack;

import com.google.gson.internal.LinkedTreeMap;
import com.lib.sdk.bean.DetectTrackBean;

import java.util.HashMap;

public class DetectTrackContract {
    public interface IDetectTrackView {
        void onGetDetectTrackResult(boolean isSuccess, LinkedTreeMap<String, Object> resultMap, int errorId);

        void onSetDetectRackResult(boolean isSuccess, int errorId);

        void onSetWatchPresetResult(boolean isSuccess,int errorId);
    }

    public interface IDetectTrackPresenter {
        void getDetectTrack();

        void setDetectTrack();

        /**
         * 设置守望位置
         */
        void setWatchPreset();

        /**
         * 设备云台控制
         * Equipment head control
         *
         * @param chnId       通道号
         * @param nPTZCommand 云台命令
         * @param speed       运动步长 *默认值4，范围1 ~ 8, 设备程序会把该步长转换为相应的速度值，1的速度最慢，8的速度最快。推荐水平三档：2、4、8 垂直三档：1、2、4
         * @param bStop       是否停止
         */
        void devicePTZControl(int chnId, int nPTZCommand, int speed, boolean bStop);
    }
}
