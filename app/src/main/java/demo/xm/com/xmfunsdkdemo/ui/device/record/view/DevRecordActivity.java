package demo.xm.com.xmfunsdkdemo.ui.device.record.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.manager.ScreenOrientationManager;
import com.manager.device.media.attribute.PlayerAttribute;
import com.utils.TimeUtils;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.BtnColorBK;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XMRecyclerView;
import com.xm.ui.widget.XTitleBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.record.adapter.RecordTimeAxisAdapter;
import demo.xm.com.xmfunsdkdemo.ui.device.record.listener.DevRecordContract;
import demo.xm.com.xmfunsdkdemo.ui.device.record.presenter.DevRecordPresenter;
import io.reactivex.annotations.Nullable;

import static com.manager.device.media.MediaManager.PLAY_CLOUD_PLAYBACK;
import static com.manager.device.media.MediaManager.PLAY_DEV_PLAYBACK;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_CANNOT_PLAY;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_MEDIA_PLAY_SPEED;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_PAUSE;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_PLAY_COMPLETED;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_SAVE_PIC_FILE_S;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_SAVE_RECORD_FILE_S;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_STOP;
import static com.manager.device.media.download.DownloadManager.DOWNLOAD_STATE_COMPLETE_ALL;
import static com.manager.device.media.download.DownloadManager.DOWNLOAD_STATE_FAILED;
import static com.manager.device.media.download.DownloadManager.DOWNLOAD_STATE_START;

public class DevRecordActivity extends DemoBaseActivity<DevRecordPresenter> implements DevRecordContract.IDevRecordView, XTitleBar.OnRightClickListener {
    private Calendar calendarShow;
    private RecyclerView rvRecordList;
    private RecyclerView rvRecordFun;
    private XMRecyclerView rvRecordTimeAxis;
    private RecordListAdapter recordListAdapter;
    private RecordFunAdapter recordFunAdapter;
    private RecordTimeAxisAdapter recordTimeAxisAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ViewGroup wndLayout;
    private TextView tvPlaySpeed;
    private boolean isCanScroll = true;
    private byte[] lock = new byte[1];
    private int recordType;//录像类型，是本地卡回放还是云回放
    private long searchTime;//初始查询时间
    private Calendar searchMonthCalendar = Calendar.getInstance();
    private int portraitWidth;
    private int portraitHeight;
    private ScreenOrientationManager screenOrientationManager;//Screen rotation manager

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_record_list);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.app_name));
        titleBar.setRightBtnResource(R.mipmap.icon_date, R.mipmap.icon_date);
        titleBar.setLeftClick(this);
        titleBar.setRightIvClick(this);
        titleBar.setBottomTip(DevRecordActivity.class.getName());
        rvRecordList = findViewById(R.id.rv_records);
        rvRecordFun = findViewById(R.id.rv_record_fun);
        tvPlaySpeed = findViewById(R.id.tv_play_speed);
        ((RadioButton) findViewById(R.id.rb_by_file)).setChecked(true);
        RelativeLayout rlBanner = findViewById(R.id.banner_rl);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        rvRecordTimeAxis = new XMRecyclerView(this, null);
        rlBanner.addView(rvRecordTimeAxis);

        ImageView arrowView = new ImageView(this);
        arrowView.setImageResource(R.mipmap.arrows);
        arrowView.setScaleType(ImageView.ScaleType.FIT_XY);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        rlBanner.addView(arrowView, params);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvRecordTimeAxis.setLayoutManager(linearLayoutManager);

        rvRecordTimeAxis.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        setCanScroll(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        dealWithTimeScrollEnd();
                        dealWithSlideStop();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        wndLayout = findViewById(R.id.wnd_layout);
    }

    private void initData() {
        Intent intent = getIntent();
        recordType = intent.getIntExtra("recordType", PLAY_DEV_PLAYBACK);
        int chnId = intent.getIntExtra("chnId", 0);
        searchTime = intent.getLongExtra("searchTime", 0L);
        presenter.setChnId(chnId);
        calendarShow = Calendar.getInstance();
        if (searchTime != 0) {
            calendarShow.setTimeInMillis(searchTime);
        }
        presenter.setRecordType(recordType);
        recordFunAdapter = new RecordFunAdapter();
        rvRecordFun.setLayoutManager(new GridLayoutManager(this, 4));
        rvRecordFun.setAdapter(recordFunAdapter);

        rvRecordList.setLayoutManager(new LinearLayoutManager(this));
        recordListAdapter = new RecordListAdapter();
        rvRecordList.setAdapter(recordListAdapter);

        recordTimeAxisAdapter = new RecordTimeAxisAdapter(this,
                presenter.getRecordTimeList(),
                screenWidth,
                presenter.getShowCount(),
                presenter.getTimeUnit());
        rvRecordTimeAxis.setAdapter(recordTimeAxisAdapter);
        showWaitDialog();
        presenter.initRecordPlayer((ViewGroup) findViewById(R.id.layoutPlayWnd), recordType);
        presenter.searchRecordByFile(calendarShow);
        presenter.searchRecordByTime(calendarShow);

        showTitleDate();
        screenOrientationManager = ScreenOrientationManager.getInstance();
    }

    private void showTitleDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        if (recordType == PLAY_CLOUD_PLAYBACK) {
            titleBar.setTitleText(dateFormat.format(calendarShow.getTime()) + "(" + getString(R.string.cloud_playback) + ")");
        } else {
            titleBar.setTitleText(dateFormat.format(calendarShow.getTime()) + "(" + getString(R.string.sd_playback) + ")");
        }
    }

    @Override
    public DevRecordPresenter getPresenter() {
        return new DevRecordPresenter(this);
    }

    @Override
    public void onSearchRecordByFileResult(boolean isSuccess) {
        hideWaitDialog();
        if (isSuccess) {
            recordListAdapter.notifyDataSetChanged();
        } else {
            showToast(getString(R.string.search_record_failed), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onSearchRecordByTimeResult(boolean isSuccess) {
        hideWaitDialog();
        if (isSuccess) {
            recordListAdapter.notifyDataSetChanged();
            recordTimeAxisAdapter.notifyDataSetChanged();
            if (searchTime != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(searchTime);
                int times = calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
                presenter.seekToTime(times);
            }
        } else {
            showToast(getString(R.string.search_record_failed), Toast.LENGTH_LONG);
        }
    }

    /**
     * 播放状态回调
     *
     * @param playState
     */
    @Override
    public void onPlayStateResult(int playState, int playSpeed) {
        if (playState == PlayerAttribute.E_STATE_PlAY) {
            hideWaitDialog();
            recordFunAdapter.changeBtnState(0, getString(R.string.playback_pause), true);
        } else if (playState == E_STATE_STOP
                || playState == E_STATE_PAUSE
                || playState == E_STATE_PLAY_COMPLETED
                || playState == E_STATE_CANNOT_PLAY) {
            recordFunAdapter.changeBtnState(0, getString(R.string.playback_play), false);
        } else if (playState == E_STATE_MEDIA_PLAY_SPEED) {
            tvPlaySpeed.setText(getString(R.string.play_speed) + ":" + playSpeed);
        }

        if (playState == E_STATE_SAVE_RECORD_FILE_S) {
            showToast(getString(R.string.record_s), Toast.LENGTH_LONG);
        } else if (playState == E_STATE_SAVE_PIC_FILE_S) {
            showToast(getString(R.string.capture_s), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onPlayInfoResult(String time, String rate) {
        if (!StringUtils.isStringNULL(time) && isCanScroll()) {
            Calendar playCalendar = TimeUtils.getNormalFormatCalender(time);
            if (playCalendar != null) {
                //获取当前选择播放的日期，并将时间设置为00:00:00
                //Get the currently selected playing date and set the time to 00:00:00
                Calendar curDateTime = (Calendar) calendarShow.clone();
                curDateTime.set(Calendar.HOUR_OF_DAY, 0);
                curDateTime.set(Calendar.MINUTE, 0);
                curDateTime.set(Calendar.SECOND, 0);

                //如果当前播放的时间比当前选择的播放日期0点的时间还小，那就是跨天了，那时间轴默认显示最开始的位置
                //If the current playback time is shorter than the 0 o'clock time of the currently selected
                // playback date, it is a cross-day, and the time axis displays the initial position by default
                if (playCalendar.getTimeInMillis() < curDateTime.getTimeInMillis()) {
                    linearLayoutManager.scrollToPositionWithOffset(0, 0);
                    return;
                }

                int hour = playCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = playCalendar.get(Calendar.MINUTE);
                int second = playCalendar.get(Calendar.SECOND);
                presenter.setPlayTimeBySecond(second);
                int minutes = (hour) * 60 + minute;
                int position = minutes / presenter.getTimeUnit();
                int offset = (int) (((minutes % presenter.getTimeUnit()) + presenter.getPlayTimeBySecond() / 60.0f) * (screenWidth / presenter.getShowCount())
                        / presenter.getTimeUnit());
                linearLayoutManager.scrollToPositionWithOffset(position, offset * (-1));
            }
        }
    }

    @Override
    public void onSearchCalendarResult(boolean isSuccess, Object result) {
        if (result instanceof String) {
            XMPromptDlg.onShow(this, (String) result, null);
        } else {
            HashMap<Object, Boolean> recordDateMap = (HashMap<Object, Boolean>) result;
            List recordDateList = new ArrayList();
            HashMap<String, Object> itemMap;
            for (Map.Entry<Object, Boolean> info : recordDateMap.entrySet()) {
                itemMap = new HashMap<>();
                itemMap.put("date", info.getKey());
                recordDateList.add(itemMap);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, recordDateList,
                    R.layout.adapter_date_list,
                    new String[]{"date"}, new int[]{R.id.tv_date});

            View layout = LayoutInflater.from(this).inflate(R.layout.dialog_date, null);
            Dialog dialog = XMPromptDlg.onShow(this, layout);

            TextView tvTitle = layout.findViewById(R.id.tv_title);
            tvTitle.setText(getString(R.string.month) + ":" + (searchMonthCalendar.get(Calendar.MONTH) + 1));

            ListView listView = layout.findViewById(R.id.lv_date);
            listView.setAdapter(simpleAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    HashMap<String, Object> itemData = (HashMap<String, Object>) simpleAdapter.getItem(i);
                    String date = (String) itemData.get("date");
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                        calendarShow = Calendar.getInstance();
                        calendarShow.setTime(dateFormat.parse(date));
                        presenter.searchRecordByTime(calendarShow);
                        presenter.searchRecordByFile(calendarShow);
                        showTitleDate();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    dialog.dismiss();
                }
            });

            //取消 Cancel
            Button btnCancel = layout.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            //上个月 last month
            Button btnLastMonth = layout.findViewById(R.id.btn_pre_month);
            btnLastMonth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchMonthCalendar.add(Calendar.MONTH, -1);
                    presenter.searchMediaFileCalendar(searchMonthCalendar);
                    dialog.dismiss();
                }
            });

            //下个月 next month
            Button btnNextMonth = layout.findViewById(R.id.btn_next_month);
            btnNextMonth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchMonthCalendar.add(Calendar.MONTH, 1);
                    presenter.searchMediaFileCalendar(searchMonthCalendar);
                    dialog.dismiss();
                }
            });

        }
    }

    @Override
    public void onDownloadState(int state, String filePath) {
        if (state == DOWNLOAD_STATE_FAILED) {
            hideWaitDialog();
            Toast.makeText(this, getString(R.string.download_f), Toast.LENGTH_LONG).show();
        } else if (state == DOWNLOAD_STATE_START) {
            Toast.makeText(this, getString(R.string.download_start), Toast.LENGTH_LONG).show();
            hideWaitDialog();
        } else if (state == DOWNLOAD_STATE_COMPLETE_ALL) {
            Toast.makeText(this, getString(R.string.download_s), Toast.LENGTH_LONG).show();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
            hideWaitDialog();
        }
    }

    @Override
    public void onDownloadProgress(int progress) {
        String content = String.format(getString(R.string.download_progress), progress);
        showWaitDialog(content);
    }

    private void dealWithTimeScrollEnd() {
        if (!isCanScroll()) {
            int firstPos = linearLayoutManager.findFirstVisibleItemPosition();
            int firstFset = rvRecordTimeAxis.getChildAt(0).getLeft() * (-1);
            int seconds = firstFset * presenter.getShowCount() * presenter.getTimeUnit() * 60 / screenWidth;
            presenter.setPlayTimeByMinute(firstPos * presenter.getTimeUnit() + seconds / 60);
            presenter.setPlayTimeBySecond(seconds % 60);
        }
    }

    private void dealWithSlideStop() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                showWaitDialog();
                int times = presenter.getPlayTimeByMinute() * 60 + presenter.getPlayTimeBySecond();

                presenter.setPlayTimeBySecond(times % 60);
                int minutes = (times / 60);
                int position = minutes / presenter.getTimeUnit();
                int offset = (int) (((minutes % presenter.getTimeUnit()) + presenter.getPlayTimeBySecond() / 60.0f)
                        * (screenWidth / presenter.getShowCount()) / presenter.getTimeUnit());
                linearLayoutManager.scrollToPositionWithOffset(position, offset * (-1));

                setCanScroll(true);
                presenter.seekToTime(times);
            }
        });
    }

    private void setCanScroll(boolean isCanScroll) {
        synchronized (lock) {
            this.isCanScroll = isCanScroll;
        }
    }


    private boolean isCanScroll() {
        synchronized (lock) {
            return this.isCanScroll;
        }
    }

    @Override
    public void onUpdateView() {

    }

    @Override
    public void onUpdateVideoThumb(Bitmap bitmap, int position) {
        recordListAdapter.updateVideoThumb(bitmap, position);
    }

    @Override
    public void onDeleteVideoResult(boolean isSuccess, int errorId) {
        if (isSuccess) {
            presenter.searchRecordByFile(calendarShow);
            presenter.searchRecordByTime(calendarShow);
            ToastUtils.showLong(getString(R.string.delete_s));
        } else {
            hideWaitDialog();
            ToastUtils.showLong(getString(R.string.delete_f) + ":" + errorId);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onRightClick() {
        presenter.searchMediaFileCalendar(searchMonthCalendar);
    }

    class RecordFunAdapter extends RecyclerView.Adapter<RecordFunAdapter.ViewHolder> {
        private String[] monitorFun = new String[]{
                getString(R.string.device_opt_play),
                getString(R.string.device_setup_encode_audio),
                getString(R.string.capture),
                getString(R.string.cut_video),
                getString(R.string.playback_fast_play),
                getString(R.string.playback_slow_play),
                getString(R.string.device_opt_fullscreen),
                getString(R.string.sel_record_file_type),
                getString(R.string.is_enable_epitome_record)};

        @NonNull
        @Override
        public RecordFunAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecordFunAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_monitor_fun, null));
        }

        @Override
        public void onBindViewHolder(@NonNull RecordFunAdapter.ViewHolder holder, int position) {
            holder.btnRecordFun.setText(monitorFun[position]);
            holder.btnRecordFun.setTag(position);
        }

        @Override
        public int getItemCount() {
            return monitorFun.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            BtnColorBK btnRecordFun;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                btnRecordFun = itemView.findViewById(R.id.btn_monitor_fun);
                btnRecordFun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        boolean isBtnChange = dealWithRecordFunction(position, btnRecordFun.isSelected());
                        if (isBtnChange) {
                            btnRecordFun.setSelected(!btnRecordFun.isSelected());
                        }
                    }
                });
            }
        }

        public void changeBtnState(int position, String title, boolean isSelected) {
            BtnColorBK btnRecordFun = rvRecordFun.findViewWithTag(position);
            if (btnRecordFun != null) {
                btnRecordFun.setText(title);
                btnRecordFun.setSelected(isSelected);
            }
        }
    }

    class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_record_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            H264_DVR_FILE_DATA recordInfo = presenter.getRecordInfo(position);
            if (recordInfo != null) {
                holder.lsiRecordInfo.setTitle(String.format("%s-%s", recordInfo.getStartTimeOfDay(), recordInfo.getEndTimeOfDay()));
                holder.lsiRecordInfo.setTip(recordInfo.getFileName());
                holder.lsiRecordInfo.setTag("lsiRecordInfo:" + position);
                Bitmap bitmap = presenter.getLocalVideoThumb(position);
                if (bitmap == null) {
                    presenter.downloadVideoThumb(position);
                    holder.lsiRecordInfo.setLeftImageResource(R.mipmap.ic_thumb);
                } else {
                    holder.lsiRecordInfo.getImageLeft().setImageBitmap(bitmap);
                }
            }
        }

        @Override
        public int getItemCount() {
            return presenter.getRecordCount();
        }

        /**
         * 更新缩略图
         *
         * @param bitmap
         * @param position
         */
        public void updateVideoThumb(Bitmap bitmap, int position) {
            if (bitmap != null) {
                ListSelectItem lsiRecordInfo = rvRecordList.findViewWithTag("lsiRecordInfo:" + position);
                if (lsiRecordInfo != null) {
                    lsiRecordInfo.getImageLeft().setImageBitmap(bitmap);
                }
            }

        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ListSelectItem lsiRecordInfo;
            Button btnDownload;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                lsiRecordInfo = itemView.findViewById(R.id.lsi_record_info);
                View layout = lsiRecordInfo.getRightExtraView();
                btnDownload = layout.findViewById(R.id.btn_record_download);

                lsiRecordInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showWaitDialog();
                        presenter.stopPlay();
                        presenter.startPlayRecord(getAdapterPosition());
                    }
                });

                lsiRecordInfo.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        XMPromptDlg.onShow(DevRecordActivity.this, getString(R.string.is_sure_delete_cloud_video), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showWaitDialog();
                                presenter.stopPlay();
                                presenter.deleteVideo(getAdapterPosition());
                            }
                        },null);
                        return true;
                    }
                });

                btnDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showWaitDialog();
                        presenter.downloadVideoByFile(getAdapterPosition());
                    }
                });
            }
        }
    }

    private boolean dealWithRecordFunction(int position, boolean isSelected) {
        switch (position) {
            case 0://播放和暂停
                if (presenter.isRecordPlay()) {
                    presenter.pausePlay();
                } else {
                    presenter.rePlay();
                }
                break;
            case 1://开启和关闭音频
                if (isSelected) {
                    presenter.closeVoice();
                } else {
                    presenter.openVoice();
                }
                return true;
            case 2://视频抓图
                presenter.capture();
                break;
            case 3://视频剪切
                if (isSelected) {
                    presenter.stopRecord();
                } else {
                    presenter.startRecord();
                }
                return true;
            case 4://快速播放
                presenter.playFast();
                break;
            case 5://慢速播放
                presenter.playSlow();
                break;
            case 6://全屏
                screenOrientationManager.landscapeScreen(this, true);
                break;
            case 7://切换录像类型
                TextView textView = new TextView(this);
                textView.setText(getString(R.string.record_file_type) + ":");
                Spinner spinner = new Spinner(this);
                spinner.setBackgroundColor(Color.WHITE);
                String[] data = {getString(R.string.record_file_all), getString(R.string.record_file_normal), getString(R.string.record_file_alarm)};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(presenter.getRecordFileType());


                LinearLayout layout = new LinearLayout(this);
                layout.setBackgroundColor(Color.WHITE);
                layout.addView(textView);
                layout.addView(spinner);
                Dialog dialog = XMPromptDlg.onShow(this,layout);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != presenter.getRecordFileType()) {
                            showWaitDialog();
                            presenter.setSearchRecordFileType(position);//position枚举对应的值 0：全部 1：普通 2：报警
                            presenter.searchRecordByFile(calendarShow);
                            presenter.searchRecordByTime(calendarShow);
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
            case 8:
                if (!isSelected) {
                    XMPromptDlg.onShow(DevRecordActivity.this, getString(R.string.support_epitome_record_tips), null);
                }
                showWaitDialog();
                presenter.setEpitomeRecordEnable(!isSelected);
                presenter.searchRecordByFile(calendarShow);
                presenter.searchRecordByTime(calendarShow);
                return true;
            default:
                break;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenOrientationManager.portraitScreen(this, true);
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.pausePlay();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.rePlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stopPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroyPlay();
        if (screenOrientationManager != null) {
            screenOrientationManager.release(this);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            titleBar.setVisibility(View.GONE);
            portraitWidth = wndLayout.getWidth();
            portraitHeight = wndLayout.getHeight();
            ViewGroup.LayoutParams layoutParams = wndLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            wndLayout.requestLayout();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            titleBar.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = wndLayout.getLayoutParams();
            layoutParams.width = portraitWidth;
            layoutParams.height = portraitHeight;
            wndLayout.requestLayout();
        }
        super.onConfigurationChanged(newConfig);
    }
}
