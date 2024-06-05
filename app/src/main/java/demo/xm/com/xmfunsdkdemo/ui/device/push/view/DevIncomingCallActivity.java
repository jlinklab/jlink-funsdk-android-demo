package demo.xm.com.xmfunsdkdemo.ui.device.push.view;

import static demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication.PATH_PHOTO_TEMP;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.ImageUtils;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.alarm.AlarmGroup;
import com.lib.sdk.bean.alarm.AlarmInfo;
import com.manager.device.alarm.DevAlarmInfoManager;
import com.manager.image.BaseImageManager;
import com.manager.image.CloudImageManager;
import com.manager.push.XMPushManager;
import com.utils.BitmapUtils;
import com.utils.LogUtils;
import com.utils.TimeUtils;
import com.xm.activity.base.XMBaseActivity;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.view.DevMonitorActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.view.VideoIntercomActivity;

/**
 * 来电界面
 * Caller page
 */
public class DevIncomingCallActivity extends DemoBaseActivity implements View.OnClickListener {
    private ImageView imageView;
    /**
     * 拒接按钮
     * Decline Button
     */
    private ImageView ivDeclineCall;
    /**
     * 接听按钮
     * Answer Button
     */
    private ImageView ivAnswerCall;
    private String alarmTime;
    /**
     * 是否是家人来电(视频对讲)
     */
    private boolean isFamilyCall;
    private AlarmInfo alarmInfo;//报警消息，包括报警图片下载信息 Alarm message, including alarm picture download information
    private String devId;//设备序列号 serial number
    private int tryCount;//尝试次数累计 Cumulative attempts
    @Override
    public XMBasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_incoming_call);
        imageView = findViewById(R.id.iv_alarm_img);
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.incoming_call));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        titleBar.setBottomTip(getClass().getName());
        ivDeclineCall = findViewById(R.id.iv_decline_call);
        ivDeclineCall.setOnClickListener(this);
        ivAnswerCall = findViewById(R.id.iv_answer_call);
        ivAnswerCall.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        devId = intent.getStringExtra("devId");
        alarmTime = intent.getStringExtra("alarmTime");
        String alarmMsgType = getIntent().getStringExtra("alarm_msg_type");
        isFamilyCall = StringUtils.contrastIgnoreCase(alarmMsgType, XMPushManager.MSG_TYPE_FAMILY_CALL);
        searchAlarmMsg();
    }

    /**
     * 查询当前报警消息记录
     */
    private void searchAlarmMsg() {
        LogUtils.debugInfo("DevIncomingCallActivity","searchAlarmMsg");
        DevAlarmInfoManager devAlarmInfoManager = new DevAlarmInfoManager(new DevAlarmInfoManager.OnAlarmInfoListener() {
            @Override
            public void onSearchResult(List<AlarmGroup> list) {
                if (list == null || list.isEmpty()) {
                    return;
                }

                if (list.get(0) != null) {
                    //根据报警消息信息去获取报警图片
                    alarmInfo = list.get(0).getAlarmInfo(0);
                    if (alarmInfo != null) {
                        if (alarmInfo.isHavePic()) {
                            showAlarmPic();
                        }else {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    searchAlarmMsg();
                                }
                            }, 1000);
                        }
                    }
                }
            }

            @Override
            public void onDeleteResult(boolean b, Message message, MsgContent msgContent, List<AlarmInfo> list) {

            }
        });

        //根据当前报警消息时间查询对应的报警消息记录
        Calendar calendar = TimeUtils.getNormalFormatCalender(alarmTime);
        devAlarmInfoManager.searchAlarmInfoByTime(devId, 0, calendar, calendar, 0);
    }

    /**
     * 显示报警图片，因为设备端把图片推送到服务器比消息晚一些,所以失败后多尝试几次
     * The alarm image is displayed, because the device pushes the image to the server later than the message,
     * so try several more times after the failure
     */
    private void showAlarmPic() {
        LogUtils.debugInfo("DevIncomingCallActivity","showAlarmPic:" + alarmInfo.isHavePic());
        //Cloud image download manager
        //报警图片下载
        CloudImageManager cloudImageManager = new CloudImageManager(PATH_PHOTO_TEMP);//传入保存图片的路径 The input is the path to save the image.
        cloudImageManager.setDevId(devId);
        cloudImageManager.setOnImageManagerListener(new BaseImageManager.OnImageManagerListener() {
            /**
             * 下载回调结果 Download Callback Results
             * @param isSuccess 下载是否成功 Whether the download was successful
             * @param imagePath 图片路径 Picture path
             * @param bitmap
             * @param mediaType 媒体类型 Media Type
             * @param seq
             */
            @Override
            public void onDownloadResult(boolean isSuccess, String imagePath, Bitmap bitmap, int mediaType, int seq) {
                LogUtils.debugInfo("DevIncomingCallActivity","onDownloadResult：" + isSuccess);
                if (isSuccess) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    showToast(getString(R.string.show_alarm_pic_failed), Toast.LENGTH_LONG);
                }
            }

            /**
             * 删除图片回调结果 Delete picture callback result
             * @param isSuccess 删除是否成功 Whether the deletion was successful
             * @param seq
             */
            @Override
            public void onDeleteResult(boolean isSuccess, int seq) {

            }
        });

        cloudImageManager.downloadImage(alarmInfo, SDKCONST.MediaType.PIC, 0, 0, 0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_decline_call) {
            //拒接后关闭来电页面
            //Decline incoming call
            finish();
        }else if (v.getId() == R.id.iv_answer_call) {
            //接听后跳转到预览页面或者视频通话页面
            //Close incoming call page after declining
            if (isFamilyCall) {
                presenter.setDevId(devId);
                turnToActivity(VideoIntercomActivity.class);
            }else {
                presenter.setDevId(devId);
                turnToActivity(DevMonitorActivity.class);
            }
            finish();
        }
    }
}
