package demo.xm.com.xmfunsdkdemo.ui.device.config.decodeconfig.listener;

/**
 * 编码配置界面,包括主码流及辅码流(分辨率,帧数,清晰度,音频,视频)
 * Created by jiangping on 2018-10-23.
 */
public class DevDecodeSetContract {
    public interface IDevDecodeSetView {
        void onGetEncodeConfigResult(boolean isSuccess,int errorId);
        void onGetEncodeCapability(boolean isSuccess,int errorId);
        void onSaveResult(boolean isSuccess);
    }

    public interface IDevDecodeSetPresenter {
        void getDevEncodeInfo();
        void getDevEncodeCapability();
        void setDevEncodeInfo();
    }
}