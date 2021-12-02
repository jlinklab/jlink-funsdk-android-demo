package demo.xm.com.xmfunsdkdemo.ui.device.config.filetransfer.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.sdk.bean.VoiceReplyBean;
import com.manager.tts.TTSManager;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.dialog.EditDialog;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.filetransfer.contract.FileTransferContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.filetransfer.presenter.FileTransferPresenter;

/**
 * 文件传输
 *
 * @author hws
 * @class
 * @time 2020/8/29 10:59
 */
public class FileTransferActivity extends BaseConfigActivity<FileTransferPresenter> implements FileTransferContract.IDvrCustomAlarmVoiceView {
    private Button btnVoiceRecording;
    private Button btnPlayVoice;
    private Button btnUploadVoice;
    private Button btnLoadLocalTestVoice;//加载本地测试音频文件
    private Button btnTextToSpeech;//文字转换成音频
    private RecyclerView rvVoiceList;//音频列表
    private AudioListAdapter audioListAdapter;

    @Override
    public FileTransferPresenter getPresenter() {
        return new FileTransferPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setup_dvr_custom_alarm_voice);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.file_transfer));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        titleBar.setBottomTip(getClass().getName());

        btnVoiceRecording = findViewById(R.id.btn_voice_recording);
        btnVoiceRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter.isRecording()) {
                    presenter.stopRecording();
                    btnVoiceRecording.setText(getString(R.string.start_recording));
                } else {
                    presenter.startRecording();
                    btnVoiceRecording.setText(getString(R.string.stop_recording));
                }
            }
        });

        btnPlayVoice = findViewById(R.id.btn_play_voice);
        btnPlayVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter.isPlaying()) {
                    presenter.stopVoice();
                    btnPlayVoice.setText(R.string.play_recording_voice);
                } else {
                    presenter.playVoice();
                    btnPlayVoice.setText(R.string.stop_play_recording_voice);
                }
            }
        });

        btnUploadVoice = findViewById(R.id.btn_upload_voice);
        btnUploadVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                presenter.uploadData();
            }
        });

        btnLoadLocalTestVoice = findViewById(R.id.btn_load_local_voice);
        btnLoadLocalTestVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.loadTestPcmData(FileTransferActivity.this);
                btnPlayVoice.setVisibility(View.VISIBLE);
                btnUploadVoice.setVisibility(View.VISIBLE);
            }
        });

        rvVoiceList = findViewById(R.id.rv_voice_list);
        rvVoiceList.setLayoutManager(new LinearLayoutManager(this));

        btnTextToSpeech = findViewById(R.id.btn_text_to_audio);
        btnTextToSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XMPromptDlg.onShowEditDialog(FileTransferActivity.this, getString(R.string.input_context), "", new EditDialog.OnEditContentListener() {
                    @Override
                    public void onResult(String content) {
                        showWaitDialog();
                        presenter.textToAudio(content, TTSManager.VOICE_TYPE.Male);
                    }
                });
            }
        });
    }

    private void initData() {
        showWaitDialog();
        presenter.checkSupport();
    }

    @Override
    public void onSupportResult(boolean isSupport) {
        hideWaitDialog();
        if (isSupport) {
            rvVoiceList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRecordingCompleted() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                btnVoiceRecording.setText(getString(R.string.start_recording));
                btnPlayVoice.setVisibility(View.VISIBLE);
                btnUploadVoice.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onPlayVoiceCompleted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnPlayVoice.setText(R.string.play_recording_voice);
            }
        });
    }

    /**
     * 文字转音频回调
     *
     * @param isSuccess 是否成功
     * @param errorId   错误码
     */
    @Override
    public void onTransformationResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        if (errorId == -2) {
            showToast(getString(R.string.file_size_exceed_max_size), Toast.LENGTH_LONG);
            return;
        } else if (errorId < 0) {
            showToast(getString(R.string.invalid_file), Toast.LENGTH_LONG);
            return;
        } else {
            showToast(getString(R.string.transformat_s), Toast.LENGTH_LONG);
            btnPlayVoice.setVisibility(View.VISIBLE);
            btnUploadVoice.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化结果回调
     *
     * @param isSuccess 是否成功
     * @param errorId   失败错误码
     */
    public void onInitResult(boolean isSuccess, int errorId) {
        if (!isSuccess) {
            showToast(getString(R.string.upload_f) + ":" + errorId, Toast.LENGTH_LONG);
        }
    }

    /**
     * 开始上传文件结果回调
     *
     * @param isSuccess 是否成功
     * @param errorId   失败错误码
     */
    public void onUploadStartResult(boolean isSuccess, int errorId) {
        if (!isSuccess) {
            showToast(getString(R.string.upload_f) + ":" + errorId, Toast.LENGTH_LONG);
        }
    }

    /**
     * 上传数据进度回调
     *
     * @param isSuccess 是否成功
     * @param progress  上传进度
     * @param errorId   失败错误码
     */
    public void onUploadDataProgressResult(boolean isSuccess, int progress, int errorId) {
        if (!isSuccess) {
            showToast(getString(R.string.upload_f) + ":" + errorId, Toast.LENGTH_LONG);
        } else {
            showToast(progress + "", Toast.LENGTH_LONG);
        }
    }

    /**
     * 上传文件结束回调
     */
    public void onUploadEndResult() {
        hideWaitDialog();
        showToast(getString(R.string.upload_s), Toast.LENGTH_LONG);
        if (rvVoiceList.getVisibility() == View.VISIBLE) {
            presenter.getAudioFileList();
        }
    }

    /**
     * 播放设备端音频结果回调
     *
     * @param isSuccess 是否成功
     * @param errorId   失败错误码
     */
    public void onPlayDevVoiceResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        showToast(isSuccess ? getString(R.string.play_success) : getString(R.string.play_failed) + ":" + errorId, Toast.LENGTH_LONG);
    }

    /**
     * 获取音频列表结果回调
     *
     * @param isSuccess 是否成功
     * @param list      音频列表
     * @param errorId   失败错误码
     */
    @Override
    public void onGetAudioListResult(boolean isSuccess, List<VoiceReplyBean> list, int errorId) {
        if (isSuccess) {
            if (audioListAdapter == null) {
                audioListAdapter = new AudioListAdapter(list);
                rvVoiceList.setAdapter(audioListAdapter);
            } else {
                audioListAdapter.setData(list);
            }
        } else {
            showToast(getString(R.string.get_voice_list_failed) + ":" + errorId, Toast.LENGTH_LONG);
        }
    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        return 0;
    }

    class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.ViewHolder> {

        List<VoiceReplyBean> voiceReplyList;

        public AudioListAdapter(List<VoiceReplyBean> voiceReplyList) {
            this.voiceReplyList = voiceReplyList;
        }

        public void setData(List<VoiceReplyBean> voiceReplyList) {
            this.voiceReplyList = voiceReplyList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(FileTransferActivity.this).inflate(R.layout.adapter_voice_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            VoiceReplyBean voiceReply = voiceReplyList.get(position);
            holder.lsiVoiceInfo.setTitle(voiceReply.getFileName());//音频文件名
            holder.lsiVoiceInfo.setTip("" + voiceReply.getFileNum());//音频ID
        }

        @Override
        public int getItemCount() {
            return voiceReplyList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ListSelectItem lsiVoiceInfo;

            public ViewHolder(View itemView) {
                super(itemView);
                lsiVoiceInfo = itemView.findViewById(R.id.lsi_voice_info);
                lsiVoiceInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VoiceReplyBean voiceReplyBean = voiceReplyList.get(getAdapterPosition());
                        presenter.playDevVoice(voiceReplyBean.getFileNum());
                    }
                });
            }
        }
    }
}
