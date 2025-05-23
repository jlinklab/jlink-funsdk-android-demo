package demo.xm.com.xmfunsdkdemo.ui.device.alarm.view;

import static com.manager.device.media.MediaManager.PLAY_CLOUD_PLAYBACK;
import static com.manager.device.media.attribute.RecordPlayerAttribute.RECORD_LEN_TYPE.RECORD_LEN_SHORT;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.MenuPopupWindow;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.alarm.AlarmInfo;
import com.lib.sdk.bean.cloudmedia.CloudMediaFileInfoBean;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.manager.device.DeviceManager;
import com.manager.device.media.MediaManager;
import com.manager.device.media.attribute.PlayerAttribute;
import com.manager.device.media.playback.CloudRecordManager;
import com.manager.device.media.playback.DevRecordManager;
import com.manager.image.BaseImageManager;
import com.manager.image.CloudImageManager;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import org.apache.commons.lang3.time.CalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.adapter.SliderAdapter;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.listener.DevAlarmContract;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.presenter.DevAlarmPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.record.view.DevRecordActivity;
import demo.xm.com.xmfunsdkdemo.ui.entity.AlarmTranslationIconBean;
import io.reactivex.annotations.Nullable;

/**
 * 设备报警界面,显示相关的列表菜单.
 * Created by jiangping on 2018-10-23.
 */
public class DevAlarmMsgActivity extends DemoBaseActivity<DevAlarmPresenter> implements DevAlarmContract.IDevAlarmView {
    private RecyclerView recyclerView;
    private AlarmMsgAdapter alarmMsgAdapter;
    private CloudRecordManager cloudRecordManager;//云存储回放播放器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dev_alarm);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.guide_module_title_device_alarm));
        titleBar.setRightBtnResource(R.mipmap.ic_more, R.mipmap.ic_more);
        titleBar.setLeftClick(this);
        recyclerView = findViewById(R.id.rv_alarm_info);
        titleBar.setBottomTip(getClass().getName());

        titleBar.setRightIvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                showPopupMenu(titleBar.getRightBtn());
            }
        });
    }

    private void initData() {
        alarmMsgAdapter = new AlarmMsgAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(alarmMsgAdapter);
        showWaitDialog();
        presenter.searchAlarmMsg();
    }

    Dialog calendarDlg;

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.alarm_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // 在这里处理菜单项的点击事件
                switch (item.getItemId()) {
                    case R.id.menu_item1:
                        //显示日期选择
                        CalendarView calendarView = new CalendarView(DevAlarmMsgActivity.this);
                        calendarView.setBackgroundColor(Color.WHITE);
                        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                            @Override
                            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                                showWaitDialog();
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                presenter.setSearchTime(calendar);
                                presenter.searchAlarmMsg();
                                if (calendarDlg != null) {
                                    calendarDlg.dismiss();
                                }
                            }
                        });
                        calendarDlg = XMPromptDlg.onShow(DevAlarmMsgActivity.this, calendarView);
                        return true;
                    case R.id.menu_item2:
                        //删除所有消息
                        XMPromptDlg.onShow(DevAlarmMsgActivity.this, "确定要删除所有消息、图片和视频么?", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                presenter.deleteAllAlarmMsg();
                            }
                        }, null);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    @Override
    public void onUpdateView() {
        hideWaitDialog();
        if (presenter.getAlarmInfoSize() <= 0) {
            showToast("未查询到报警推送消息", Toast.LENGTH_LONG);
        } else {
            alarmMsgAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDeleteAlarmMsgResult(boolean isSuccess) {
        showToast(isSuccess ? "删除成功" : "删除失败", Toast.LENGTH_LONG);
        alarmMsgAdapter.notifyDataSetChanged();
    }

    @Override
    public void onShowPicResult(boolean isSuccess, Bitmap bitmap) {//Download picture display
        hideWaitDialog();
        showToast(isSuccess ? "图片下载成功" : "图片下载失败", Toast.LENGTH_LONG);
        if (isSuccess && bitmap != null) {
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(bitmap);
            XMPromptDlg.onShow(this, imageView);
        }
    }

    @Override
    public void onTurnToVideo(Calendar searchTime) {
        initPlayer();
        cloudRecordManager.stopPlay();

        //按照报警消息时间点查对应的云视频的话，传入的开始时间要基于报警消息时间-5秒 结束时间要基于报警消息时间+10秒
        Calendar startCalendar = (Calendar) searchTime.clone();
        startCalendar.add(Calendar.SECOND, -5);

        Calendar endCalendar = (Calendar) searchTime.clone();
        endCalendar.add(Calendar.SECOND, +10);

        cloudRecordManager.searchFileByTime(startCalendar, endCalendar);
    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        if (cloudRecordManager == null) {
            ViewGroup playView = findViewById(R.id.fl_play_wnd);
            playView.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = playView.getLayoutParams();
            layoutParams.width = screenWidth;
            layoutParams.height = screenWidth * 9 / 16;
            cloudRecordManager = (CloudRecordManager) DeviceManager.getInstance().createRecordPlayer(playView, presenter.getDevId(), PLAY_CLOUD_PLAYBACK);
            cloudRecordManager.setRecordLenType(RECORD_LEN_SHORT);//短视频

            cloudRecordManager.setOnMediaManagerListener(new MediaManager.OnRecordManagerListener() {
                @Override
                public void searchResult(PlayerAttribute playerAttribute, Object data) {
                    CloudMediaFileInfoBean cloudMediaFileInfoBean = cloudRecordManager.getCloudMediaFiles().fileList.get(0);
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.setTime(cloudMediaFileInfoBean.getStartTimeByYear());

                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.setTime(cloudMediaFileInfoBean.getEndTimeByYear());
                    cloudRecordManager.startPlay(startCalendar, endCalendar);
                }

                @Override
                public void deleteVideoResult(String s, boolean b, int i) {

                }

                @Override
                public void onMediaPlayState(PlayerAttribute playerAttribute, int i) {

                }

                @Override
                public void onFailed(PlayerAttribute playerAttribute, int i, int i1) {

                }

                @Override
                public void onShowRateAndTime(PlayerAttribute playerAttribute, boolean b, String s, long l) {

                }

                @Override
                public void onVideoBufferEnd(PlayerAttribute playerAttribute, MsgContent msgContent) {

                }

                @Override
                public void onPlayStateClick(View view) {

                }
            });
        }
    }

    @Override
    public DevAlarmPresenter getPresenter() {
        return new DevAlarmPresenter(this);
    }

    class AlarmMsgAdapter extends RecyclerView.Adapter<AlarmMsgAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_alarm_msg_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AlarmInfo alarmInfo = presenter.getAlarmInfo(position);
            if (alarmInfo != null) {
                /**
                 * Event 对应的内容如下
                 *  public final static String TYPE_LOCAL_ALARM = "LocalAlarm";//本地报警（实际意义根据设备不同而不同）
                 *     public final static String TYPE_MOTION_DETECT = "MotionDetect";//移动侦测
                 *     public final static String TYPE_VIDEO_MOTION = "VideoMotion";//移动侦测
                 *     public final static String TYPE_LOSS_DETECT = "LossDetect";//视频丢失
                 *     public final static String TYPE_VIDEO_LOSS = "VideoLoss";//视频丢失
                 *     public final static String TYPE_BLIND_DETECT = "BlindDetect";//视频遮挡
                 *     public final static String TYPE_VIDEO_BLIND = "VideoBlind";//视频遮挡
                 *     public final static String TYPE_IPC_ALARM = "IPCAlarm";
                 *     public final static String TYPE_LOCAL_IO = "LocalIO";//本地报警（实际意义根据设备不同而不同）
                 *     public final static String TYPE_STORAGE_WRITE_ERROR = "StorageWriteError";//SD卡写错误
                 *     public final static String TYPE_STORAGE_READ_ERROR = "StorageReadError";//SD卡读错误
                 *     public final static String TYPE_STORAGE_FAILURE = "StorageFailure";//SD卡出错
                 *     public final static String TYPE_STORAGE_LOW_SPACE = "StorageLowSpace";//SD卡容量不足
                 *     public final static String TYPE_STORAGE_NOT_EXIST = "StorageNotExist";//SD卡不存在
                 *     public final static String TYPE_SERIAL_ALARM = "SerialAlarm";//串口报警
                 *     public final static String TYPE_CONS_SENSOR_ALARM = "ConsSensorAlarm";//传感器报警
                 *     public final static String TYPE_HUMAN_DETECT = "HumanDetect";//人形检测
                 *     public final static String TYPE_FACE_DETECTION = "FaceDetection";//人脸检测
                 *     public final static String TYPE_FACE_DETECT = "FaceDetect";//人脸检测
                 *     public final static String TYPE_FACE_RECOGNITION = "FaceRecognition";//人脸识别
                 *     public final static String TYPE_REMOTE_SNAP = "RemoteSnap";//远程抓图(天猫精灵抓图)
                 *     public final static String TYPE_NET_IP_CONFLICT ="NetIPConflict";//IP冲突
                 *     public final static String TYPE_SPEED_ALARM = "SpeedAlarm";//速度报警
                 */

                //该翻译仅供参考，可以根据自己的需求来随意改动
                Object afterTranslation = ((SDKDemoApplication) getApplication()).getAlarmTranslationIconBean().getLanguageInfo().get("ZH").get(alarmInfo.getEvent());
                if (afterTranslation instanceof AlarmTranslationIconBean.AlarmLanIconInfo) {
                    holder.lisAlarmMsg.setTitle(((AlarmTranslationIconBean.AlarmLanIconInfo) afterTranslation).getTl());
                } else {
                    holder.lisAlarmMsg.setTitle(alarmInfo.getEvent());
                }
                holder.lisAlarmMsg.setTip(alarmInfo.getStartTime());
                holder.lisAlarmMsg.setTag(position);
                holder.lisAlarmMsg.getImageLeft().setImageBitmap(null);

                //判断当前报警消息是否有报警图片
                if (alarmInfo.isHavePic()) {
                    //加载并显示缩略图
                    if (!StringUtils.isStringNULL(alarmInfo.getPic())) {
                        Glide.with(holder.lisAlarmMsg).load(alarmInfo.getPic()).into(holder.lisAlarmMsg.getImageLeft());
                    } else {
                        presenter.loadThumb(position, new BaseImageManager.OnImageManagerListener() {
                            @Override
                            public void onDownloadResult(boolean isSuccess, String imagePath, Bitmap bitmap, int mediaType, int seq) {
                                ListSelectItem lsiAlarmMsg = recyclerView.findViewWithTag(seq);
                                if (lsiAlarmMsg != null) {
                                    lsiAlarmMsg.getImageLeft().setImageBitmap(bitmap);
                                } else {
                                    holder.lisAlarmMsg.getImageLeft().setImageBitmap(bitmap);
                                }
                            }

                            @Override
                            public void onDeleteResult(boolean b, int i) {

                            }
                        });
                    }

                    holder.btnPicture.setVisibility(View.VISIBLE);
                } else {
                    holder.btnPicture.setVisibility(View.GONE);
                }

                holder.btnVideo.setVisibility(alarmInfo.isVideoInfo() ? View.VISIBLE : View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return presenter.getAlarmInfoSize();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ListSelectItem lisAlarmMsg;
            Button btnPicture;
            Button btnVideo;
            Button btnDelete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                lisAlarmMsg = itemView.findViewById(R.id.lis_alarm_msg);
                btnPicture = itemView.findViewById(R.id.btn_picture);
                btnVideo = itemView.findViewById(R.id.btn_video);
                btnDelete = itemView.findViewById(R.id.btn_delete);
                btnPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SliderView sliderView = new SliderView(DevAlarmMsgActivity.this);
                        sliderView.setInfiniteAdapterEnabled(false);
                        AlarmInfo alarmInfo = presenter.getAlarmInfo(getAdapterPosition());
                        List<AlarmInfo.AlarmPicInfo> alarmPicInfos = alarmInfo.getAlarmPicInfos();
                        List<String> imageUrls = new ArrayList<>();
                        for (AlarmInfo.AlarmPicInfo alarmPicInfo : alarmPicInfos) {
                            imageUrls.add(alarmPicInfo.getUrl());
                        }

                        SliderAdapter adapter = new SliderAdapter(imageUrls);
                        sliderView.setSliderAdapter(adapter);
                        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                        sliderView.setAutoCycle(false);
                        XMPromptDlg.onShow(DevAlarmMsgActivity.this, sliderView, (int) (DevAlarmMsgActivity.this.screenWidth * 0.6), (int) (DevAlarmMsgActivity.this.screenHeight * 0.6), true, null);
                    }
                });

                btnVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.showVideo(getAdapterPosition());
                    }
                });

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XMPromptDlg.onShow(DevAlarmMsgActivity.this, "确定要删除报警消息?", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                presenter.deleteAlarmMsg(getAdapterPosition());
                            }
                        }, null);
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cloudRecordManager != null) {
            cloudRecordManager.release();
        }
    }
}
