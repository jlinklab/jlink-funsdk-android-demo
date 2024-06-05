package demo.xm.com.xmfunsdkdemo.ui.device.config.filetransfer.presenter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Message;
import android.provider.MediaStore;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.sdk.bean.OPFileBean;
import com.manager.device.DeviceManager;
import com.manager.device.media.audio.XMAudioPlayManager;
import com.manager.device.media.audio.XMRecordingManager;
import com.manager.device.media.file.DevAudioFileManager;
import com.manager.tts.TTSManager;
import com.xm.activity.base.XMBasePresenter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.filetransfer.OpSpecifyAudioBean;
import demo.xm.com.xmfunsdkdemo.ui.device.config.filetransfer.contract.FileTransferContract;

/**
 * 文件传输
 *
 * @author hws
 * @class
 * @time 2020/8/29 11:00
 */
public class FileTransferPresenter extends XMBasePresenter<DeviceManager> implements
        FileTransferContract.IDvrCustomAlarmVoicePresenter, XMRecordingManager.OnRecordingListener {
    /**
     * 发送的音频文件最大字节数
     */
    public static final int SEND_AUDIO_DATA_MAX_SIZE = 84 * 1024;
    public static final int LOCAL_ALARMAUDIO_FILE_IMPORT = 7;//本地端报警音频导入
    public static final int CUSTOM_VOICE_ID = 1000;//自定义的音频ID，这个ID到时候用来播放的时候传入的

    private int userId;
    private ByteBuffer audioBuffer;
    private int audioSize;
    private XMRecordingManager xmRecordingManager;
    private XMAudioPlayManager xmAudioPlayManager;
    /**
     * 音频传输管理类，用于上传音频数据到设备端以及播放设备端音频，该功能部分设备才支持，需要判读能力集：SupportAlarmVoiceTips 语音提示 SupportAlarmVoiceTipsType 自定义语音提示音
     */
    private DevAudioFileManager devAudioFileManager;

    private FileTransferContract.IDvrCustomAlarmVoiceView iDvrCustomAlarmVoiceView;
    private TTSManager ttsManager;//文字转音频管理类

    public FileTransferPresenter(FileTransferContract.IDvrCustomAlarmVoiceView iDvrCustomAlarmVoiceView) {
        this.iDvrCustomAlarmVoiceView = iDvrCustomAlarmVoiceView;
        userId = FunSDK.GetId(userId, this);
        xmRecordingManager = new XMRecordingManager(this, 5);
        devAudioFileManager = DevAudioFileManager.getInstance();
        devAudioFileManager.setOnDevAudioFileManagerListener(iDvrCustomAlarmVoiceView);
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void checkSupport() {
        //当前是判断是否支持本地端自定义报警音
        DeviceManager.getInstance().getDevAbility(getDevId(), new DeviceManager.OnDevManagerListener() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, Object isSupport) {
                iDvrCustomAlarmVoiceView.onSupportResult((Boolean) isSupport);
                getAudioFileList();
            }

            /**
             * 失败回调
             *
             * @param devId    设备序列号
             * @param msgId    消息ID
             * @param jsonName
             * @param errorId  错误码
             */
            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                iDvrCustomAlarmVoiceView.onSupportResult(false);
            }
        }, "OtherFunction", "SupportAlarmVoiceTipsType");
        //暂时默认都支持本地端自定义报警声
//        iDvrCustomAlarmVoiceView.onSupportResult(true);
        /**
         * 其他能力集有：
         * SupportMultiTransData 支持多次传输数据(大于64k)
         * SupportExportIPCLog 支持导出IPC日志(导入导出IPC配置 以及 导出IPC日志 使用同一能力)
         */
    }

    @Override
    public void startRecording() {
        xmRecordingManager.startRecording();
    }

    @Override
    public void stopRecording() {
        xmRecordingManager.stopRecording();
    }

    @Override
    public boolean isRecording() {
        if (xmRecordingManager != null) {
            return xmRecordingManager.isRecording();
        }
        return false;
    }

    @Override
    public void playVoice() {
        if (xmAudioPlayManager != null) {
            xmAudioPlayManager.startPlay();
        }
    }

    @Override
    public void stopVoice() {
        if (xmAudioPlayManager != null) {
            xmAudioPlayManager.stopPlay();
        }
    }

    @Override
    public boolean isPlaying() {
        if (xmAudioPlayManager != null) {
            return xmAudioPlayManager.isPlaying();
        }

        return false;
    }

    /**
     * 上传音频数据
     */
    @Override
    public void uploadData() {
        if (audioBuffer == null) {
            return;
        }

        //音频格式：PCM，采样率：8000，声道数：1，比特率：16
        byte[] audioData = new byte[audioSize];
        audioBuffer.get(audioData, 0, audioSize);
        audioBuffer.flip();
        devAudioFileManager.uploadAudioFileToDev(getDevId(), CUSTOM_VOICE_ID, audioData, audioData.length);
    }

    /**
     * 播放自定义的音频
     *
     * @param fileNumber 文件ID
     */
    @Override
    public void playDevVoice(int fileNumber) {
        devAudioFileManager.playDevVoice(getDevId(), fileNumber);
    }

    @Override
    public void getAudioFileList() {
        devAudioFileManager.getAudioFileList(getDevId());
    }

    /**
     * 文字转音频
     *
     * @param content   文字内容
     * @param voiceType 声音类型 {@link TTSManager.VOICE_TYPE#Male 男声
     * @link TTSManager.VOICE_TYPE#Female 女声}
     */
    @Override
    public void textToAudio(String content, TTSManager.VOICE_TYPE voiceType) {
        if (ttsManager == null) {
            ttsManager = new TTSManager(new TTSManager.OnTTSManagerListener() {
                @Override
                public void onTTSResult(int errorId, byte[] data, int realDataSize) {
                    if (errorId < 0) {
                        iDvrCustomAlarmVoiceView.onTransformationResult(false, errorId);
                        return;
                    }

                    if (realDataSize <= 0) {
                        errorId = -1;
                        iDvrCustomAlarmVoiceView.onTransformationResult(false, errorId);
                        return;
                    } else if (realDataSize > SEND_AUDIO_DATA_MAX_SIZE) {
                        errorId = -2;
                        iDvrCustomAlarmVoiceView.onTransformationResult(false, errorId);
                        return;
                    }

                    //音频文件发给设备端必须要16位对齐，因为发给设备的文件大小要除以2 所以这里要32位对齐
                    int dataSize = realDataSize;
                    if (realDataSize % 32 != 0) {
                        dataSize = (realDataSize / 32 + 1) * 32;
                    }

                    audioBuffer = ByteBuffer.allocate(dataSize);
                    audioBuffer.put(data);

                    if (dataSize != realDataSize) {
                        byte[] inputDatas = new byte[dataSize - realDataSize];
                        audioBuffer.put(inputDatas);
                    }

                    audioBuffer.flip();
                    audioSize = dataSize;//音频大小
                    xmAudioPlayManager = new XMAudioPlayManager(audioBuffer, audioSize, new XMAudioPlayManager.OnAudioPlayListener() {
                        @Override
                        public void onPlayTime(int i) {

                        }

                        @Override
                        public void onPlayCompleted() {
                            iDvrCustomAlarmVoiceView.onPlayVoiceCompleted();
                        }
                    });

                    iDvrCustomAlarmVoiceView.onTransformationResult(true, 0);
                }
            });
        }

        ttsManager.textToAudio(content, voiceType);
    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        return 0;
    }

    @Override
    public void onRecording(int i) {

    }

    @Override
    public void onComplete(ByteBuffer byteBuffer, int bufferSize) {
        this.audioBuffer = byteBuffer;
        this.audioSize = bufferSize;
        iDvrCustomAlarmVoiceView.onRecordingCompleted();
        xmAudioPlayManager = new XMAudioPlayManager(byteBuffer, bufferSize, new XMAudioPlayManager.OnAudioPlayListener() {
            @Override
            public void onPlayTime(int i) {

            }

            @Override
            public void onPlayCompleted() {
                iDvrCustomAlarmVoiceView.onPlayVoiceCompleted();
            }
        });
    }

    /**
     * 加载本地的测试音频数据，该音频数据 采样率:8k 声道数:单通道 比特率:16比特
     *
     * @param context
     * @return
     */
    public void loadTestPcmData(Context context) {
        Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.test);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            int bytesRead;
            byte[] buffer = new byte[4096];

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }


            byte[] audioData = byteArrayOutputStream.toByteArray();
            int realDataSize = audioData.length;
            //音频文件发给设备端必须要16位对齐，因为发给设备的文件大小要除以2 所以这里要32位对齐
            int dataSize = realDataSize;
            if (realDataSize % 32 != 0) {
                dataSize = (realDataSize / 32 + 1) * 32;
            }


            audioBuffer = ByteBuffer.allocate(dataSize);
            audioBuffer.put(audioData);

            if (dataSize != realDataSize) {
                byte[] inputDatas = new byte[dataSize - realDataSize];
                audioBuffer.put(inputDatas);
            }

            audioBuffer.flip();
            audioSize = dataSize;
            xmAudioPlayManager = new XMAudioPlayManager(audioBuffer, audioSize, new XMAudioPlayManager.OnAudioPlayListener() {
                @Override
                public void onPlayTime(int i) {

                }

                @Override
                public void onPlayCompleted() {
                    iDvrCustomAlarmVoiceView.onPlayVoiceCompleted();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
