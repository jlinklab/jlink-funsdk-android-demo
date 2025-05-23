package demo.xm.com.xmfunsdkdemo.ui.device.record.presenter;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.SystemFunctionBean;
import com.lib.sdk.bean.cloudmedia.CloudMediaFileInfoBean;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.manager.db.Define;
import com.manager.db.DownloadInfo;
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
import com.manager.path.PathManager;
import com.utils.FileUtils;
import com.utils.LogUtils;
import com.utils.TimeUtils;
import com.xm.activity.base.XMBasePresenter;
import com.xmgl.vrsoft.VRSoftDefine;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication;
import demo.xm.com.xmfunsdkdemo.ui.device.record.listener.DevRecordContract;

import static com.manager.db.Define.DOWNLOAD_VIDEO_BY_FILE;
import static com.manager.db.Define.DOWNLOAD_VIDEO_BY_MORE_CLOUD;
import static com.manager.db.Define.DOWNLOAD_VIDEO_BY_SHORT_CLOUD;
import static com.manager.db.Define.DOWNLOAD_VIDEO_BY_TIME;
import static com.manager.device.media.MediaManager.PLAY_CLOUD_PLAYBACK;
import static com.manager.device.media.attribute.PlayerAttribute.EE_STATE_SD_SEARCH_RECORD_BY_FILE;
import static com.manager.device.media.attribute.PlayerAttribute.EE_STATE_SD_SEARCH_RECORD_BY_TIME;
import static com.manager.device.media.attribute.RecordPlayerAttribute.PLAY_SPEED_FAST;
import static com.manager.device.media.attribute.RecordPlayerAttribute.PLAY_SPEED_SLOW;
import static com.manager.device.media.download.DownloadManager.DOWNLOAD_STATE_PROGRESS;
import static com.manager.device.media.playback.DevRecordManager.RECORD_TYPE_ALL;
import static com.manager.device.media.playback.DevRecordManager.RECORD_TYPE_E;

public class DevRecordPresenter extends XMBasePresenter<DeviceManager> implements
        DevRecordContract.IDevRecordPresenter, RecordManager.OnRecordManagerListener,
        MediaFileCalendarManager.OnMediaFileCalendarListener, DownloadManager.OnDownloadListener, BaseImageManager.OnImageManagerListener {
    public static final int MN_COUNT = 8;
    public static final int TIME_UNIT = 60;
    public static final int RECORD_TYPE_SEL_ALL = 0;//所有录像
    public static final int RECORD_TYPE_SEL_ONLY_NORMAL = 1;//普通录像
    public static final int RECORD_TYPE_SEL_ONLY_ALARM = 2;//报警录像
    public static final int RECORD_TYPE_SEL_ONLY_EPITOME = 3;//缩影录像
    private DevRecordContract.IDevRecordView iDevRecordView;
    private RecordManager[] recordManagers;
    private MediaFileCalendarManager mediaFileCalendarManager;
    private DownloadManager downloadManager;
    private List<H264_DVR_FILE_DATA> recordList;
    private List<Map<String, Object>> recordTimeList;
    private Map<String, Object> recordTimeMap;
    private Calendar searchTime;
    private int timeUnit = TIME_UNIT;
    private int timeCount = MN_COUNT;
    private int playTimeBySecond;
    private int playTimeByMinute;
    private int recordPlayType;
    private int playSpeed;
    private int recordFileType;
    private H264_DVR_FILE_DATA curPlayFileInfo;//当前播放的录像文件信息
    private Calendar playStartCalendar;//录像播放开始时间
    private Calendar playEndCalendar;//录像播放结束时间
    private boolean isSupportMulti;//是否支持真多目
    private boolean isPlayerInit;//播放器是否已经初始化
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


    /**
     * 是否为真多目设备
     */
    public void isSupportMultiChnSplitWindows() {
        DeviceManager.getInstance().getDevAllAbility(getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            @Override
            public void onSuccess(String s, int i, SystemFunctionBean systemFunctionBean) {
                isSupportMulti = systemFunctionBean.OtherFunction.MultiChnSplitWindows;
                iDevRecordView.onSupportMultiChnSplitWindowsResult(isSupportMulti);
            }

            @Override
            public void onFailed(String s, int i, String s1, int i1) {
                isSupportMulti = false;
                iDevRecordView.onSupportMultiChnSplitWindowsResult(isSupportMulti);
            }
        });
    }
    @Override
    public void initRecordPlayer(ViewGroup playView, int recordType) {
        recordManagers = new RecordManager[1];
        /**
         * mediaType:
         * MediaManager.PLAY_DEV_PLAYBACK:本地录像回放
         * MediaManager.PLAY_CLOUD_PLAYBACK:云存储远程录像回放
         */

        RecordPlayerAttribute recordPlayerAttribute = new RecordPlayerAttribute(getDevId());
        if (recordType == MediaManager.PLAY_CLOUD_PLAYBACK) {
            recordManagers[0] = new CloudRecordManager(playView, recordPlayerAttribute);
        } else {
            //SD卡回放
            recordManagers[0] = new DevRecordManager(playView, recordPlayerAttribute);
            ((DevRecordManager) recordManagers[0]).setRecordFileType(SDKCONST.EMSType.h264, RECORD_TYPE_ALL);
        }

        recordManagers[0].setChnId(getChnId());
        recordManagers[0].setVideoFullScreen(true);//默认按比例显示
        recordManagers[0].setOnMediaManagerListener(this);
        recordManagers[0].initVideoThumb(PathManager.getInstance(iDevRecordView.getContext()).getTempImages(), getDevId(), this);

        isPlayerInit = true;
    }

    public void initMoreCloudRecordPlayers(ViewGroup... playViews) {
        recordManagers = new RecordManager[playViews.length];
        /**
         * mediaType:
         * MediaManager.PLAY_DEV_PLAYBACK:本地录像回放
         * MediaManager.PLAY_CLOUD_PLAYBACK:云存储远程录像回放
         */

        RecordPlayerAttribute recordPlayerAttribute = new RecordPlayerAttribute(getDevId());
        recordManagers[0] = new CloudRecordManager(playViews[0], recordPlayerAttribute);
        recordManagers[0].setChnId(0);
        recordManagers[0].setVideoFullScreen(true);//默认按比例显示
        recordManagers[0].setOnMediaManagerListener(this);
        recordManagers[0].initVideoThumb(PathManager.getInstance(iDevRecordView.getContext()).getTempImages(), getDevId(), this);

        recordPlayerAttribute = new RecordPlayerAttribute(getDevId());
        recordManagers[1] = new CloudRecordManager(playViews[1], recordPlayerAttribute);
        recordManagers[1].setChnId(1);
        recordManagers[1].setVideoFullScreen(true);//默认按比例显示
        recordManagers[1].setOnMediaManagerListener(this);
        recordManagers[1].initVideoThumb(PathManager.getInstance(iDevRecordView.getContext()).getTempImages(), getDevId(), this);

        isPlayerInit = true;
    }

    @Override
    public void setSearchRecordFileType(int chnId,int recordFileType) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }
        this.recordFileType = recordFileType;
        if (recordManagers[chnId] instanceof DevRecordManager) {
            if (recordFileType == RECORD_TYPE_SEL_ALL) {//所有录像
                ((DevRecordManager) recordManagers[chnId]).setRecordFileType(SDKCONST.EMSType.h264, RECORD_TYPE_ALL);
            } else if (recordFileType == RECORD_TYPE_SEL_ONLY_ALARM) {//报警录像
                ((DevRecordManager) recordManagers[chnId]).setRecordFileType(SDKCONST.EMSType.h264, DevRecordManager.RECORD_TYPE_A, DevRecordManager.RECORD_TYPE_M);
            } else if (recordFileType == RECORD_TYPE_SEL_ONLY_NORMAL) {//普通录像
                ((DevRecordManager) recordManagers[chnId]).setRecordFileType(SDKCONST.EMSType.h264, DevRecordManager.RECORD_TYPE_R, DevRecordManager.RECORD_TYPE_H);
            } else if (recordFileType == RECORD_TYPE_SEL_ONLY_EPITOME) {//缩影录像
                ((DevRecordManager) recordManagers[chnId]).setRecordFileType(SDKCONST.EMSType.h264, RECORD_TYPE_E);
            }
        }
    }

    public int getRecordFileType() {
        return recordFileType;
    }

    /**
     * 查询录像文件列表（本地回放才支持）
     */
    @Override
    public void searchRecordByFile(int chnId,Calendar searchTime) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }
        this.searchTime = searchTime;
        if (recordManagers[chnId] instanceof DevRecordManager) {
            searchTime.set(Calendar.HOUR_OF_DAY, 0);
            searchTime.set(Calendar.MINUTE, 0);
            searchTime.set(Calendar.SECOND, 0);

            Calendar endTime = (Calendar) searchTime.clone();
            endTime.set(Calendar.HOUR_OF_DAY, 23);
            endTime.set(Calendar.MINUTE, 59);
            endTime.set(Calendar.SECOND, 59);

            ((DevRecordManager) recordManagers[chnId]).searchFileByTime(searchTime, endTime);
        }
    }

    /**
     * 查询SD卡或者存储的录像（可以显示时间轴）
     * @param chnId
     * @param searchTime
     */
    @Override
    public void searchRecordByTime(int chnId,Calendar searchTime) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }

        this.searchTime = searchTime;
        recordManagers[chnId].searchFileByTime(searchTime);
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
        if (!isPlayerInit) {
            return;
        }

        curPlayFileInfo = recordList.get(position);
        playStartCalendar = TimeUtils.getNormalFormatCalender(curPlayFileInfo.getStartTimeOfYear());
        playEndCalendar = TimeUtils.getNormalFormatCalender(curPlayFileInfo.getEndTimeOfYear());
//        endCalendar = Calendar.getInstance();
//        endCalendar.setTime(playCalendar.getTime());
//        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
//        endCalendar.set(Calendar.MINUTE, 59);
//        endCalendar.set(Calendar.SECOND, 59);
        recordManagers[0].startPlay(playStartCalendar, playEndCalendar);
        if (isSupportMulti) {
            //如果是真多目设备，云存储支持多个通道同时播放
            int playHandle = FunSDK.MultiMediaCloudStorageRecordPlay(getPlayMultiCloudRequestJson(playStartCalendar, playEndCalendar),
                    new View[]{recordManagers[0].getSurfaceView(), recordManagers[1].getSurfaceView()});
            int playHandleChn0 = FunSDK.GetMediaPlayerHandle(playHandle, 0);
            int playHandleChn1 = FunSDK.GetMediaPlayerHandle(playHandle, 1);
            recordManagers[0].getPlayerAttribute().setPlayHandle(playHandleChn0);
            recordManagers[1].getPlayerAttribute().setPlayHandle(playHandleChn1);
        }
    }

    @Override
    public void capture(int chnId) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }
        recordManagers[chnId].capture(SDKDemoApplication.PATH_PHOTO);
    }

    @Override
    public void startRecord(int chnId) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }
        if (!recordManagers[chnId].isRecord()) {
            recordManagers[chnId].startRecord(SDKDemoApplication.PATH_VIDEO);
        }
    }

    @Override
    public void stopRecord(int chnId) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }

        if (recordManagers[chnId].isRecord()) {
            recordManagers[chnId].stopRecord();
        }
    }

    @Override
    public boolean isRecording(int chnId) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return false;
        }
        return recordManagers[chnId].isRecord();
    }

    @Override
    public boolean isVoiceOpen(int chnId) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return false;
        }
        return recordManagers[chnId].getPlayerAttribute().isSound();
    }

    @Override
    public void openVoice(int chnId) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }
        recordManagers[chnId].openVoiceBySound();
    }

    @Override
    public void closeVoice(int chnId) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }
        recordManagers[chnId].closeVoiceBySound();
    }

    @Override
    public void pausePlay(int chnId) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }
        //暂停之前把视频剪切动作结束
        recordManagers[chnId].stopRecord();
        recordManagers[chnId].pausePlay();
    }

    @Override
    public void rePlay(int chnId) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }
        recordManagers[chnId].rePlay();
    }

    @Override
    public void stopPlay(int chnId) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }
        recordManagers[chnId].stopPlay();
    }

    @Override
    public void destroyPlay() {
        if (!isPlayerInit) {
            return;
        }

        for (RecordManager recordManager : recordManagers) {
            recordManager.destroyPlay();
        }
        downloadManager.stopDownload();
    }

    @Override
    public boolean isRecordPlay(int chnId) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return false;
        }
        return recordManagers[chnId].getPlayState() == PlayerAttribute.E_STATE_PlAY;
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
    public void seekToTime(int chnId,Calendar calendar, int times) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }
        //获取当前播放的日期
        int[] dateTime = {calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0};
        int absTime = FunSDK.ToTimeType(dateTime) + times;
        recordManagers[chnId].seekToTime(times, absTime);
    }

    @Override
    public void seekToTime(int chnId,int times) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }
        //获取当前播放的日期
        int[] dateTime = {searchTime.get(Calendar.YEAR), searchTime.get(Calendar.MONTH) + 1,
                searchTime.get(Calendar.DAY_OF_MONTH), 0, 0, 0};
        int absTime = FunSDK.ToTimeType(dateTime) + times;
        recordManagers[chnId].seekToTime(times, absTime);
    }

    @Override
    public void setPlayTimeByMinute(int chnId,int minute) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }
        //result >= 0 返回有效的时间,< 0 保持原来的时间
        int result = recordManagers[chnId].dealWithRecordEffectiveByMinute(minute);
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
    public void downloadVideoByFile(int chnId,int position) {
        if (position >= recordList.size()) {
            return;
        }

        H264_DVR_FILE_DATA data = recordList.get(position);
        if (data != null) {
            String fileName = data.getLongStartTime() + "_" + data.getLongEndTime() + "_" + chnId + ".mp4";
            DownloadInfo downloadInfo = new DownloadInfo();
            downloadInfo.setStartTime(TimeUtils.getNormalFormatCalender(data.getStartTimeOfYear()));
            downloadInfo.setEndTime(TimeUtils.getNormalFormatCalender(data.getEndTimeOfYear()));
            downloadInfo.setDevId(getDevId());
            downloadInfo.setChnId(chnId);
            downloadInfo.setObj(data);
            downloadInfo.setFileType(RECORD_TYPE_E);
            //下载类型：0->卡存按文件下载 1->云存储下载 2->卡存按时间下载
            //Download type: 0->Download by file from SD card 1->Download from cloud storage 2->Download by time from SD card
            if (recordPlayType == PLAY_CLOUD_PLAYBACK) {
                if (isSupportMulti) {
                    downloadInfo.setDownloadType(DOWNLOAD_VIDEO_BY_MORE_CLOUD);
                }else {
                    downloadInfo.setDownloadType(DOWNLOAD_VIDEO_BY_SHORT_CLOUD);
                }
            }else {
                downloadInfo.setDownloadType(DOWNLOAD_VIDEO_BY_FILE);
            }

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
    public void playFast(int chnId) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }
        playSpeed = ++playSpeed % (PLAY_SPEED_FAST + 1);
        recordManagers[chnId].setPlaySpeed(playSpeed);
    }

    @Override
    public void playSlow(int chnId) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }
        playSpeed = --playSpeed % (PLAY_SPEED_SLOW - 1);
        recordManagers[chnId].setPlaySpeed(playSpeed);
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
        iDevRecordView.onPlayStateResult(attribute.getChnnel(), state, playSpeed);
        recordManagers[attribute.getChnnel()].setTwoLensesScreen(VRSoftDefine.XMTwoLensesScreen.ScreenDouble);
    }

    @Override
    public void onFailed(PlayerAttribute attribute, int msgId, int errorId) {
        //TODO 处理查询和播放失败的返回结果
        LogUtils.debugInfo("PlayBack", "msgId:" + msgId + " errorId:" + errorId);
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
                //SD卡录像文件返回的结果
                recordList.clear();
                recordList.addAll(((DevRecordManager) recordManagers[attribute.getChnnel()]).getFileDataList());
                iDevRecordView.onSearchRecordByFileResult(true);
            } else {
                if (recordManagers[attribute.getChnnel()] instanceof CloudRecordManager) {
                    //云存储录像返回的结果
                    recordList.clear();
                    recordList.addAll(((CloudRecordManager) recordManagers[attribute.getChnnel()]).getCloudMediaFiles().cloudMediaInfoToH264FileData());
                }

                //SD卡或云存储录像时间轴
                //data是二维数组，总共是720个字节，其中1个字节代表2分钟，右4位和左4位分别表示一分钟的录像类型，所以总共是1440分钟（24小时）
                dealWithRecordTimeList((char[][]) data);
                iDevRecordView.onSearchRecordByTimeResult(true);
            }
        } else {
            //按文件查询失败回调
            if (attribute.getPlayState() == EE_STATE_SD_SEARCH_RECORD_BY_FILE) {
                recordList.clear();
                iDevRecordView.onSearchRecordByFileResult(false);
            } else if (attribute.getPlayState() == EE_STATE_SD_SEARCH_RECORD_BY_TIME) {
                //按时间查询失败回调
                dealWithRecordTimeList(new char[144][]);
                iDevRecordView.onSearchRecordByTimeResult(false);
            }
        }
    }

    @Override
    public void deleteVideoResult(String devId, boolean isSuccess, int errorId) {
        if (iDevRecordView != null) {
            iDevRecordView.onDeleteVideoResult(isSuccess, errorId);
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
    public Bitmap getLocalVideoThumb(int chnId,int position) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return null;
        }
        H264_DVR_FILE_DATA h264DvrFileData = recordList.get(position);
        if (recordManagers[chnId] instanceof DevRecordManager) {
            return ((DevRecordManager) recordManagers[chnId]).getLocalVideoThumb(h264DvrFileData);
        } else if (recordManagers[chnId] instanceof CloudRecordManager) {
            CloudMediaFileInfoBean cloudMediaFileInfoBean = JSON.parseObject(h264DvrFileData.getAlarmExFileInfo(), CloudMediaFileInfoBean.class);
            return ((CloudRecordManager) recordManagers[chnId]).getLocalVideoThumb(cloudMediaFileInfoBean);
        }

        return null;
    }

    /**
     * 下载视频缩略图
     *
     * @param position
     */
    public void downloadVideoThumb(int chnId,int position) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }

        H264_DVR_FILE_DATA h264DvrFileData = recordList.get(position);
        if (recordManagers[chnId] instanceof DevRecordManager) {
            if (recordFileType == RECORD_TYPE_SEL_ONLY_EPITOME) {//缩影录像
                ((DevRecordManager) recordManagers[chnId]).downloadVideoThumb(h264DvrFileData, position, RECORD_TYPE_E);
            } else {
                ((DevRecordManager) recordManagers[chnId]).downloadVideoThumb(h264DvrFileData,position,RECORD_TYPE_ALL);
            }
        } else if (recordManagers[chnId] instanceof CloudRecordManager) {
            CloudMediaFileInfoBean cloudMediaFileInfoBean = JSON.parseObject(h264DvrFileData.getAlarmExFileInfo(), CloudMediaFileInfoBean.class);
            ((CloudRecordManager) recordManagers[chnId]).downloadVideoThumb(cloudMediaFileInfoBean, position);
        }
    }

    /**
     * 删除云视频
     *
     * @param position
     */
    public void deleteVideo(int chnId,int position) {
        if (!isPlayerInit || chnId >= recordManagers.length) {
            return;
        }

        if (recordManagers[chnId] instanceof CloudRecordManager) {
            H264_DVR_FILE_DATA h264DvrFileData = recordList.get(position);
            ((CloudRecordManager) recordManagers[chnId]).deleteVideo(h264DvrFileData.fileName);
        }
    }

    public H264_DVR_FILE_DATA getCurPlayFileInfo() {
        return curPlayFileInfo;
    }

    /**
     * 真多目云回放请求json
     * @param sCalendar
     * @param eCalendar
     * @return
     */
    private String getPlayMultiCloudRequestJson(Calendar sCalendar, Calendar eCalendar) {
        JSONObject jsonObject = new JSONObject();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            jsonObject.put("msg", "short_video_play");
            jsonObject.put("sn", recordManagers[0].getDevId());//设备序列号
            jsonObject.put("st", format.format(sCalendar.getTime()));//开始时间
            jsonObject.put("et", format.format(eCalendar.getTime()));//结束时间
            JSONArray jsonArray = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("channel", recordManagers[0].getChnId());//通道号
            object.put("seq",recordManagers[0].getChnId());
            object.put("user",recordManagers[0].getListenerId());//监听回调ID
            jsonArray.put(object);
            if (recordManagers[1] != null) {
                object = new JSONObject();
                object.put("channel", recordManagers[1].getChnId());
                object.put("seq",recordManagers[1].getChnId());
                object.put("user",recordManagers[1].getListenerId());
                jsonArray.put(object);
            }
            jsonObject.put("channellist", jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
