package demo.xm.com.xmfunsdkdemo.ui.device.preview.listener;

import android.view.ViewGroup;

public class VideoIntercomContract {
    public interface IVideoIntercomView {
        /**
         * 开启音视频结果回调
         *
         * @param isSuccess   是否成功
         * @param videoHeight 视频高度
         * @param videoWidth  视频宽度
         * @param errorStr    错误内容
         */
        void startCameraVideoResult(boolean isSuccess, int videoHeight, int videoWidth, String errorStr);

        /**
         * 关闭音视频结果回调
         *
         * @param isSuccess    是否成功
         * @param isNeedFinish 是否需要主动挂断关闭
         */
        void stopCameraVideoResult(boolean isSuccess, boolean isNeedFinish);
        /**
         * 音视频对讲功能未启用返回
         */
        void onVideoTalkFunctionNoEnable();

        /**
         * 关闭摄像头回调
         */
        void closeCameraResult();

        /**
         * 打开摄像头回调
         */
        void openCameraResult();
    }

    public interface IVideoIntercomPresenter {
        void initMonitor(ViewGroup playView);
        void startMonitor();
        void stopMonitor();
        void destroyMonitor();
        void release();
    }
}
