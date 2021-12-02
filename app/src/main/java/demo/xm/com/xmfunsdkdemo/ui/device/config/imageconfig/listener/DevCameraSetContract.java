package demo.xm.com.xmfunsdkdemo.ui.device.config.imageconfig.listener;

/**
 * 图像配置界面,改变图像的画质,画像字符叠加
 * Created by jiangping on 2018-10-23.
 */
public class DevCameraSetContract {
    public interface IDevCameraSetView {
        void onUpdateView(String result, boolean isSupportSoftPhotosensitive);
        void onWDRConfig(boolean isSupport,boolean isOpen);
        void onSetWDRConfigResult(boolean isSuccess,int errorId);
        void onSaveResult(int state);
        void error(String errorMsg);
    }

    public interface IDevCameraSetPresenter {
        void getCameraInfo();
        void setCameraInfo(String jsonData);
        void setWDRConfig(boolean isOpen);
    }
}
