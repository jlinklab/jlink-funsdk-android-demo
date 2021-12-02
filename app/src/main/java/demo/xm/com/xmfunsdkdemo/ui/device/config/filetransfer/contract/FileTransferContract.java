package demo.xm.com.xmfunsdkdemo.ui.device.config.filetransfer.contract;

import com.lib.IFunSDKResult;
import com.manager.device.media.file.DevAudioFileManager;
import com.manager.tts.TTSManager;

/**
 * @author hws
 * @class
 * @time 2020/8/29 11:00
 */
public interface FileTransferContract {
    interface IDvrCustomAlarmVoiceView extends DevAudioFileManager.OnDevAudioFileManagerListener {
        void onSupportResult(boolean isSupport);

        void onRecordingCompleted();

        void onPlayVoiceCompleted();

        /**
         * 文字转音频回调
         *
         * @param isSuccess 是否成功
         * @param errorId   错误码
         */
        void onTransformationResult(boolean isSuccess, int errorId);
    }

    interface IDvrCustomAlarmVoicePresenter extends IFunSDKResult {
        void checkSupport();

        void startRecording();

        void stopRecording();

        boolean isRecording();

        void playVoice();

        void stopVoice();

        boolean isPlaying();

        void uploadData();

        /**
         * 播放设备端声音
         */
        void playDevVoice(int fileNumber);

        /**
         * 获取音频文件列表
         */
        void getAudioFileList();

        /**
         * 文字转音频
         *
         * @param content   文字内容
         * @param voiceType 声音类型 {@link TTSManager.VOICE_TYPE#Male 男声
         * @link TTSManager.VOICE_TYPE#Female 女声}
         */
        void textToAudio(String content, TTSManager.VOICE_TYPE voiceType);
    }
}
