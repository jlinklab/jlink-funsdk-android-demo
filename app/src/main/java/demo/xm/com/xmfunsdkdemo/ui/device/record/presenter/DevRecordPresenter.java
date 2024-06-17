package demo.xm.com.xmfunsdkdemo.ui.device.record.presenter;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.cloudmedia.CloudMediaFileInfoBean;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.manager.db.Define;
import com.manager.db.DevDataCenter;
import com.manager.db.DownloadInfo;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.manager.device.media.MediaManager;
import com.manager.device.media.attribute.PlayerAttribute;
import com.manager.device.media.attribute.RecordPlayerAttribute;
import com.manager.device.media.calendar.MediaFileCalendarManager;
import com.manager.device.media.download.DownloadManager;
import com.manager.device.media.playback.CloudRecordManager;
import com.manager.device.media.playback.DevRecordManager;
import com.manager.device.media.playback.RecordManager;
import com.manager.image.BaseImageManager;
import com.manager.image.CloudImageManager;
import com.manager.path.PathManager;
import com.utils.FileUtils;
import com.utils.TimeUtils;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.dialog.XMPromptDlg;
import com.xmgl.vrsoft.VRSoftDefine;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication;
import demo.xm.com.xmfunsdkdemo.ui.device.record.listener.DevRecordContract;

import static com.manager.db.Define.DOWNLOAD_VIDEO_BY_CLOUD;
import static com.manager.db.Define.DOWNLOAD_VIDEO_BY_FILE;
import static com.manager.db.Define.DOWNLOAD_VIDEO_BY_TIME;
import static com.manager.device.media.MediaManager.PLAY_CLOUD_PLAYBACK;
import static com.manager.device.media.attribute.RecordPlayerAttribute.PLAY_SPEED_FAST;
import static com.manager.device.media.attribute.RecordPlayerAttribute.PLAY_SPEED_SLOW;
import static com.manager.device.media.download.DownloadManager.DOWNLOAD_STATE_PROGRESS;

public class DevRecordPresenter extends XMBasePresenter<DeviceManager> implements
        DevRecordContract.IDevRecordPresenter, RecordManager.OnRecordManagerListener,
        MediaFileCalendarManager.OnMediaFileCalendarListener, DownloadManager.OnDownloadListener, BaseImageManager.OnImageManagerListener {
    public static final int MN_COUNT = 8;
    public static final int TIME_UNIT = 60;
    public static final int RECORD_TYPE_ALL = 0;//所有录像
    public static final int RECORD_TYPE_ONLY_NORMAL = 1;//普通录像
    public static final int RECORD_TYPE_ONLY_ALARM = 2;//报警录像
    private DevRecordContract.IDevRecordView iDevRecordView;
    private RecordManager recordManager;
    private MediaFileCalendarManager mediaFileCalendarManager;
    private DownloadManager downloadManager;
    private List<H264_DVR_FILE_DATA> recordList;
    private List<Map<String, Object>> recordTimeList;
    private Map<String, Object> recordTimeMap;
    private TreeMap<Object, Boolean> haveRecordMap;
    private Calendar searchTime;
    private int timeUnit = TIME_UNIT;
    private int timeCount = MN_COUNT;
    private int playTimeBySecond;
    private int playTimeByMinute;
    private int recordPlayType;
    private int playSpeed;
    private int recordFileType;
    private boolean isEpitomeRecordEnable;//缩影录像开关

    public DevRecordPresenter(DevRecordContract.IDevRecordView iDevRecordView) {
        this.iDevRecordView = iDevRecordView;
        recordList = new ArrayList<>();
        recordTimeList = new ArrayList<>();
        downloadManager = DownloadManager.getInstance(this);
    }

    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        mediaFileCalendarManager = new MediaFileCalendarManager(this);
        mediaFileCalendarManager.init(devId, null, "h264");
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }


    @Override
    public void initRecordPlayer(ViewGroup playView, int recordType) {
        /**
         * mediaType:
         * MediaManager.PLAY_DEV_PLAYBACK:本地录像回放
         * MediaManager.PLAY_CLOUD_PLAYBACK:云存储远程录像回放
         */

        RecordPlayerAttribute recordPlayerAttribute = new RecordPlayerAttribute(getDevId());
        if (recordType == MediaManager.PLAY_CLOUD_PLAYBACK) {
            recordManager = new CloudRecordManager(playView, recordPlayerAttribute);
        } else {
            //SD卡回放
            recordManager = new DevRecordManager(playView, recordPlayerAttribute);
            //设备录像文件类型
            // subType对应的枚举参考:https://docs.jftech.com/docs?menusId=ab0ed73834f54368be3e375075e27fb2&siderid=9f6293ec863e46e6961cc85403b15ac4
            int subTypeMask = 1 << SDKCONST.EMSSubType.ALL;//多个类型参考:(1 << EMSSubType.DYNAMIC) | (1 << EMSSubType.STRANDED)
            int recordFileType = (SDKCONST.EMSType.h264 << 26) | (subTypeMask & 0x3FFFFFF);
            ((DevRecordManager) recordManager).setRecordFileType(recordFileType);
        }

        recordManager.setChnId(getChnId());
        recordManager.setVideoFullScreen(true);//默认按比例显示
        recordManager.setOnMediaManagerListener(this);
        recordManager.initVideoThumb(PathManager.getInstance(iDevRecordView.getContext()).getTempImages(), getDevId(), this);
    }

    @Override
    public void setSearchRecordFileType(int recordFileType) {
        this.recordFileType = recordFileType;
        if (recordManager instanceof DevRecordManager) {
            //设备录像文件类型
            // subType对应的枚举参考:https://docs.jftech.com/docs?menusId=ab0ed73834f54368be3e375075e27fb2&siderid=9f6293ec863e46e6961cc85403b15ac4
            // 多个类型参考:(1 << EMSSubType.DYNAMIC) | (1 << EMSSubType.STRANDED)
            int subTypeMask = 0;
            if (recordFileType == RECORD_TYPE_ALL) {//所有录像
                subTypeMask = 1 << SDKCONST.EMSSubType.ALL;
            } else if (recordFileType == RECORD_TYPE_ONLY_ALARM) {//报警录像
                subTypeMask = (1 << SDKCONST.EMSSubType.ALERT) | (1 << SDKCONST.EMSSubType.DYNAMIC) | (1 << SDKCONST.EMSSubType.INVASION) | (1 << SDKCONST.EMSSubType.STRANDED);
            } else if (recordFileType == RECORD_TYPE_ONLY_NORMAL) {//普通录像
                subTypeMask = (1 << SDKCONST.EMSSubType.ORIGINAL) | (1 << SDKCONST.EMSSubType.HAND);
            }
            ((DevRecordManager) recordManager).setRecordFileType((SDKCONST.EMSType.h264 << 26) | (subTypeMask & 0x3FFFFFF));
        }
    }

    @Override
    public void setEpitomeRecordEnable(boolean isEnable) {
        this.isEpitomeRecordEnable = isEnable;
        if (recordManager instanceof DevRecordManager) {
            ((DevRecordManager) recordManager).setRecordStreamType(isEnable ? 5 : 2);
        }
    }

    public int getRecordFileType() {
        return recordFileType;
    }

    /**
     * 本地回放才支持
     */
    @Override
    public void searchRecordByFile(Calendar searchTime) {
        this.searchTime = searchTime;
        if (recordManager instanceof DevRecordManager) {
            searchTime.set(Calendar.HOUR_OF_DAY, 0);
            searchTime.set(Calendar.MINUTE, 0);
            searchTime.set(Calendar.SECOND, 0);

            Calendar endTime = (Calendar) searchTime.clone();
            endTime.set(Calendar.HOUR_OF_DAY, 23);
            endTime.set(Calendar.MINUTE, 59);
            endTime.set(Calendar.SECOND, 59);

            ((DevRecordManager) recordManager).searchFileByTime(searchTime, endTime);
        }
    }

    @Override
    public void searchRecordByTime(Calendar searchTime) {
        this.searchTime = searchTime;
        int[] times = new int[]{
                searchTime.get(Calendar.YEAR),
                searchTime.get(Calendar.MONTH) + 1,
                searchTime.get(Calendar.DATE)
        };
        recordManager.searchFileByTime(times);
    }

    @Override
    public List<H264_DVR_FILE_DATA> getRecordList() {
        return recordList;
    }

    @Override
    public H264_DVR_FILE_DATA getRecordInfo(int position) {
        if (position >= 0 && position < recordList.size()) {
            return recordList.get(position);
        }

        return null;
    }

    @Override
    public int getRecordCount() {
        return recordList != null ? recordList.size() : 0;
    }

    /**
     * @param position
     */
    @Override
    public void startPlayRecord(int position) {
        H264_DVR_FILE_DATA recordInfo = recordList.get(position);
        Calendar playCalendar = TimeUtils.getNormalFormatCalender(recordInfo.getStartTimeOfYear());
        Calendar endCalendar = TimeUtils.getNormalFormatCalender(recordInfo.getEndTimeOfYear());
//        endCalendar = Calendar.getInstance();
//        endCalendar.setTime(playCalendar.getTime());
//        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
//        endCalendar.set(Calendar.MINUTE, 59);
//        endCalendar.set(Calendar.SECOND, 59);
        recordManager.startPlay(playCalendar, endCalendar);

    }

    @Override
    public void capture() {
        recordManager.capture(SDKDemoApplication.PATH_PHOTO);
    }

    @Override
    public void startRecord() {
        if (!recordManager.isRecord()) {
            recordManager.startRecord(SDKDemoApplication.PATH_VIDEO);
        }
    }

    @Override
    public void stopRecord() {
        if (recordManager.isRecord()) {
            recordManager.stopRecord();
        }
    }

    @Override
    public boolean isRecording() {
        return recordManager.isRecord();
    }

    @Override
    public void openVoice() {
        recordManager.openVoiceBySound();
    }

    @Override
    public void closeVoice() {
        recordManager.closeVoiceBySound();
    }

    @Override
    public void pausePlay() {
        recordManager.pausePlay();
    }

    @Override
    public void rePlay() {
        recordManager.rePlay();
    }

    @Override
    public void stopPlay() {
        recordManager.stopPlay();
    }

    @Override
    public void destroyPlay() {
        recordManager.destroyPlay();
    }

    @Override
    public boolean isRecordPlay() {
        return recordManager.getPlayState() == PlayerAttribute.E_STATE_PlAY;
    }

    @Override
    public List<Map<String, Object>> getRecordTimeList() {
        return recordTimeList;
    }

    @Override
    public int getShowCount() {
        return timeCount;
    }

    @Override
    public int getTimeUnit() {
        return timeUnit;
    }

    @Override
    public void setPlayTimeBySecond(int secondTime) {
        this.playTimeBySecond = secondTime;
    }

    @Override
    public int getPlayTimeBySecond() {
        return playTimeBySecond;
    }

    /**
     * @param times 当前要播放的时间点（24小时内换算的时间，单位秒，比如：0点10分就是换成600秒）
     */
    @Override
    public void seekToTime(int times) {
        //获取当前播放的日期
        int[] dateTime = {searchTime.get(Calendar.YEAR), searchTime.get(Calendar.MONTH) + 1,
                searchTime.get(Calendar.DAY_OF_MONTH), 0, 0, 0};
        int absTime = FunSDK.ToTimeType(dateTime) + times;
        recordManager.seekToTime(times, absTime);
    }

    @Override
    public void setPlayTimeByMinute(int minute) {
        //result >= 0 返回有效的时间,< 0 保持原来的时间
        int result = recordManager.dealWithRecordEffectiveByMinute(minute);
        if (result >= 0) {
            playTimeByMinute = result;
            playTimeBySecond = 0;
        }
    }

    @Override
    public int getPlayTimeByMinute() {
        return playTimeByMinute;
    }

    @Override
    public void setRecordType(int recordType) {
        this.recordPlayType = recordType;
    }

    @Override
    public void downloadVideoByFile(int position) {
        if (position >= recordList.size()) {
            return;
        }

        H264_DVR_FILE_DATA data = recordList.get(position);
        if (data != null) {
            String fileName = data.getLongStartTime() + "_" + data.getLongEndTime() + ".mp4";
            DownloadInfo downloadInfo = new DownloadInfo();
            downloadInfo.setStartTime(TimeUtils.getNormalFormatCalender(data.getStartTimeOfYear()));
            downloadInfo.setEndTime(TimeUtils.getNormalFormatCalender(data.getEndTimeOfYear()));
            downloadInfo.setDevId(getDevId());
            downloadInfo.setObj(data);
            //下载类型：0->卡存按文件下载 1->云存储下载 2->卡存按时间下载
            //Download type: 0->Download by file from SD card 1->Download from cloud storage 2->Download by time from SD card
            downloadInfo.setDownloadType(recordPlayType == PLAY_CLOUD_PLAYBACK
                    ? DOWNLOAD_VIDEO_BY_CLOUD : DOWNLOAD_VIDEO_BY_FILE);
            downloadInfo.setSaveFileName(SDKDemoApplication.PATH_VIDEO + File.separator + fileName);
            downloadManager.addDownload(downloadInfo);
            downloadManager.startDownload();
        }
    }

    /**
     * 按时间下载录像
     *
     * @param startTime
     * @param endTime
     */
    @Override
    public void downloadVideoByTime(Calendar startTime, Calendar endTime) {
        String fileName = startTime.getTimeInMillis() + "_" + endTime.getTimeInMillis() + ".mp4";
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setStartTime(startTime);
        downloadInfo.setEndTime(endTime);
        downloadInfo.setDevId(getDevId());
        downloadInfo.setFileName(fileName);
        //下载类型：0->卡存按文件下载 1->云存储下载 2->卡存按时间下载
        //Download type: 0->Download by file from SD card 1->Download from cloud storage 2->Download by time from SD card
        downloadInfo.setDownloadType(DOWNLOAD_VIDEO_BY_TIME);
        downloadInfo.setSaveFileName(SDKDemoApplication.PATH_VIDEO + File.separator + fileName);
        downloadManager.addDownload(downloadInfo);
        downloadManager.startDownload();
    }

    @Override
    public void playFast() {
        playSpeed = ++playSpeed % (PLAY_SPEED_FAST + 1);
        recordManager.setPlaySpeed(playSpeed);
    }

    @Override
    public void playSlow() {
        playSpeed = --playSpeed % (PLAY_SPEED_SLOW - 1);
        recordManager.setPlaySpeed(playSpeed);
    }

    @Override
    public void updateVideoView(int width, int height) {

    }

    @Override
    public void searchMediaFileCalendar(Calendar searchCalendar) {
        if (recordPlayType == PLAY_CLOUD_PLAYBACK) {
            mediaFileCalendarManager.searchFile(searchCalendar, Define.MEDIA_TYPE_CLOUD, 0);
        } else {
            mediaFileCalendarManager.searchFile(searchCalendar, Define.MEDIA_TYPE_DEVICE, 0);
        }
    }

    /**
     * 播放状态的回调
     * public static final int E_STATE_UNINIT = -1;//未初始化
     * public static final int E_STATE_PlAY = 0;// 播放
     * public static final int E_STATE_PAUSE = 1;// 暂停
     * public static final int E_STATE_BUFFER = 2;// 获取数据
     * public static final int E_STATE_REFRESH = 3;// 刷新
     * public static final int E_STATE_STOP = 4;// 停止
     * public static final int E_STATE_RESUME = 5;// 重置
     * public static final int E_STATE_CANNOT_PLAY = 6;// 不能播放
     * public static final int E_STATE_READY_PLAY = 7;// 准备播放
     * public static final int E_STATE_MEDIA_DISCONNECT = 8;// 媒体链接断开
     * public static final int E_STATE_MEDIA_SOUND_ON = 9;// 音频开启
     * public static final int E_STATE_MEDIA_SOUND_OFF = 10;// 音频关闭
     * public static final int E_STATE_RECONNECT = 11;// 重连
     * public static final int E_STATE_CHANGE_VR_MODE = 12;//VR模式
     * public static final int E_HARDDECODER_FAILURE = -5;// 硬解码失败
     * public static final int E_OPEN_FAILED = 13; //连接失败，请刷新（目前用于xmeye）
     * public static final int E_NO_VIDEO = 14; //无视频（目前用于xmeye）
     * public static final int E_STATE_SET_PLAY_VIEW = 15;//重建播放窗口设置回调
     * public static final int E_STATE_PLAY_COMPLETED = 16;//播放结束 一般用于录像回放
     * public static final int E_STATE_PLAY_SEEK = 17;//录像回放定位
     * public static final int E_STATE_SAVE_RECORD_FILE_S = 18;//录像保存成功
     * public static final int E_STATE_SAVE_PIC_FILE_S = 19;//图片保存成功
     *
     * @param attribute
     * @param state
     */
    @Override
    public void onMediaPlayState(PlayerAttribute attribute, int state) {
        playSpeed = ((RecordPlayerAttribute) attribute).getPlaySpeed();
        iDevRecordView.onPlayStateResult(state, playSpeed);
        recordManager.setTwoLensesScreen(VRSoftDefine.XMTwoLensesScreen.ScreenDouble);
    }

    @Override
    public void onFailed(PlayerAttribute attribute, int msgId, int errorId) {
        //TODO 处理查询和播放失败的返回结果
    }

    /**
     * 显示码率和时间戳
     *
     * @param attribute
     * @param isShowTime
     * @param time
     * @param rate
     */
    @Override
    public void onShowRateAndTime(PlayerAttribute attribute, boolean isShowTime, String time, long rate) {
        iDevRecordView.onPlayInfoResult(time, FileUtils.FormetFileSize(rate) + "/S");
    }

    /**
     * 视频缓冲结束的回调
     *
     * @param attribute
     * @param ex
     */
    @Override
    public void onVideoBufferEnd(PlayerAttribute attribute, MsgContent ex) {

    }

    @Override
    public void onPlayStateClick(View view) {

    }

    /**
     * 录像文件查询成功结果
     *
     * @param attribute
     * @param data
     */
    @Override
    public void searchResult(PlayerAttribute attribute, Object data) {
        if (data != null) {
            if (data instanceof H264_DVR_FILE_DATA[]) {
                recordList.clear();
                recordList.addAll(((DevRecordManager) recordManager).getFileDataList());
                iDevRecordView.onSearchRecordByFileResult(true);
            } else {
                if (recordManager instanceof CloudRecordManager) {
                    recordList.clear();
                    recordList.addAll(((CloudRecordManager) recordManager).getCloudMediaFiles().cloudMediaInfoToH264FileData());
                }

                dealWithRecordTimeList((char[][]) data);
                iDevRecordView.onSearchRecordByTimeResult(true);
            }

            haveRecordMap = DevDataCenter.getInstance().getHasRecordFile();
        } else {
            iDevRecordView.onSearchRecordByFileResult(false);
        }
    }

    @Override
    public void deleteVideoResult(String devId, boolean isSuccess, int errorId) {
        if (iDevRecordView != null) {
            iDevRecordView.onDeleteVideoResult(isSuccess,errorId);
        }
    }

    private void dealWithRecordTimeList(char[][] minutes) {
        recordTimeList.clear();
        int count = 24 * 60 / timeUnit;
        int n = timeUnit / 10;
        int i = 0, j = 0;

        for (i = 0; i < timeCount / 2; i++) {
            recordTimeMap = new HashMap<String, Object>();
            recordTimeList.add(recordTimeMap);
        }

        for (i = 0; i < count; i++) {

            String time = TimeUtils.formatTimes(i * timeUnit);
            System.out.println("time:" + time);
            char[][] data = new char[n][];

            recordTimeMap = new HashMap<String, Object>();
            for (j = 0; j < n; ++j) {
                data[j] = minutes[n * i + j];
            }

            recordTimeMap.put("data", data);
            recordTimeMap.put("time", time);
            recordTimeList.add(recordTimeMap);
        }

        for (i = 0; i < timeCount / 2; i++) {
            recordTimeMap = new HashMap<String, Object>();
            recordTimeList.add(recordTimeMap);
        }
    }

    @Override
    public void onHaveFileData(HashMap<Object, Boolean> fileMaps, int position) {
        iDevRecordView.onSearchCalendarResult(true, fileMaps);
    }

    @Override
    public void onFailed(int msgId, int errorId) {
        iDevRecordView.onSearchCalendarResult(false, "错误码：" + errorId);
    }

    @Override
    public void onDownload(DownloadInfo downloadInfo) {
        if (downloadInfo != null) {
            if (downloadInfo.getDownloadState() == DOWNLOAD_STATE_PROGRESS) {
                int progress = (downloadInfo.getDownloadProgress() + 100 * downloadInfo.getSeq());
                iDevRecordView.onDownloadProgress(progress);
            } else {
                iDevRecordView.onDownloadState(downloadInfo.getDownloadState(), downloadInfo.getSaveFileName());
            }

            System.out.println("download-->" + downloadInfo.getDownloadState() + " progress:" + downloadInfo.getDownloadProgress());
        }
    }

    @Override
    public void onDownloadResult(boolean isSuccess, String imagePath, Bitmap bitmap, int mediaType, int seq) {
        System.out.println("onDownloadResult:" + seq);
        if (iDevRecordView != null && bitmap != null) {
            iDevRecordView.onUpdateVideoThumb(bitmap, seq);
        }
    }

    @Override
    public void onDeleteResult(boolean isSuccess, int seq) {

    }

    /**
     * 获取本地缓存的视频缩略图
     *
     * @param position
     * @return
     */
    public Bitmap getLocalVideoThumb(int position) {
        H264_DVR_FILE_DATA h264DvrFileData = recordList.get(position);
        if (recordManager instanceof DevRecordManager) {
            return ((DevRecordManager) recordManager).getLocalVideoThumb(h264DvrFileData);
        } else if (recordManager instanceof CloudRecordManager) {
            CloudMediaFileInfoBean cloudMediaFileInfoBean = JSON.parseObject(h264DvrFileData.getAlarmExFileInfo(), CloudMediaFileInfoBean.class);
            return ((CloudRecordManager) recordManager).getLocalVideoThumb(cloudMediaFileInfoBean);
        }

        return null;
    }

    /**
     * 下载视频缩略图
     *
     * @param position
     */
    public void downloadVideoThumb(int position) {
        H264_DVR_FILE_DATA h264DvrFileData = recordList.get(position);
        if (recordManager instanceof DevRecordManager) {
            ((DevRecordManager) recordManager).downloadVideoThumb(h264DvrFileData, position);
        } else if (recordManager instanceof CloudRecordManager) {
            CloudMediaFileInfoBean cloudMediaFileInfoBean = JSON.parseObject(h264DvrFileData.getAlarmExFileInfo(), CloudMediaFileInfoBean.class);
            ((CloudRecordManager) recordManager).downloadVideoThumb(cloudMediaFileInfoBean, position);
        }
    }

    /**
     * 删除云视频
     *
     * @param position
     */
    public void deleteVideo(int position) {
        if (recordManager instanceof CloudRecordManager) {
            H264_DVR_FILE_DATA h264DvrFileData = recordList.get(position);
            ((CloudRecordManager) recordManager).deleteVideo(h264DvrFileData.fileName);
        }
    }
}
