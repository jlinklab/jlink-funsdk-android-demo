package demo.xm.com.xmfunsdkdemo.ui.device.record.listener;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;

import com.lib.sdk.struct.H264_DVR_FILE_DATA;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DevRecordContract {
    public interface IDevRecordView {
        void onSearchRecordByFileResult(boolean isSuccess);

        void onSearchRecordByTimeResult(boolean isSuccess);

        void onPlayStateResult(int playState, int playSpeed);

        void onPlayInfoResult(String time, String rate);

        void onSearchCalendarResult(boolean isSuccess, Object result);

        void onDownloadState(int state, String filePath);

        void onDownloadProgress(int progress);

        void onUpdateView();

        /**
         * 更新视频缩略图布局
         *
         * @param bitmap
         */
        void onUpdateVideoThumb(Bitmap bitmap, int pos);

        void onDeleteVideoResult(boolean isSuccess,int errorId);

        Context getContext();
    }

    public interface IDevRecordPresenter {
        /**
         * 初始化录像播放器
         *
         * @param playView
         */
        void initRecordPlayer(ViewGroup playView, int recordType);

        /**
         * 设置查询的录像类型
         *
         * @param recordFileType 是否查询报警录像
         */
        void setSearchRecordFileType(int recordFileType);

        /**
         * 是否开启只查询播放缩影录像
         *
         * @param isEnable
         */
        void setEpitomeRecordEnable(boolean isEnable);

        /**
         * 按文件方式查询录像，以文件列表方式显示
         */
        void searchRecordByFile(Calendar searchTime);

        /**
         * 按时间方式查询录像，以时间轴方式显示
         */
        void searchRecordByTime(Calendar searchTime);

        /**
         * 获取搜索到的录像文件数据
         *
         * @return
         */
        List<H264_DVR_FILE_DATA> getRecordList();

        /**
         * 获取录像信息
         *
         * @param position
         * @return
         */
        H264_DVR_FILE_DATA getRecordInfo(int position);

        /**
         * 获取录像文件个数
         *
         * @return
         */
        int getRecordCount();

        void startPlayRecord(int position);

        /**
         * 抓图
         */
        void capture();

        /**
         * 开始视频剪切
         */
        void startRecord();

        /**
         * 停止视频剪切
         */
        void stopRecord();

        /**
         * 当前是否处于视频剪切中
         *
         * @return
         */
        boolean isRecording();

        /**
         * 打开音频
         */
        void openVoice();

        /**
         * 关闭音频
         */
        void closeVoice();

        void pausePlay();

        void rePlay();

        void stopPlay();

        void destroyPlay();

        boolean isRecordPlay();

        List<Map<String, Object>> getRecordTimeList();

        /**
         * 时间轴一行显示的个数
         *
         * @return
         */
        int getShowCount();

        /**
         * 时间轴显示的单位
         *
         * @return
         */
        int getTimeUnit();

        /**
         * 设置播放的时间，单位秒
         *
         * @param secondTime
         */
        void setPlayTimeBySecond(int secondTime);

        /**
         * 获取播放时间，单位秒
         *
         * @return
         */
        int getPlayTimeBySecond();

        /**
         * 定位时间播放 24小时内换算的时间，单位秒
         *
         * @param times
         */
        void seekToTime(Calendar calendar,int times);

        /**
         * 定位时间播放 24小时内换算的时间，单位秒
         *
         * @param times
         */
        void seekToTime(int times);

        void setPlayTimeByMinute(int minute);

        int getPlayTimeByMinute();

        /**
         * 获取媒体文件日历
         */
        void searchMediaFileCalendar(Calendar searchCalendar);

        /**
         * 设置录像类型：0-》设备本地回放 1-》云回放
         *
         * @param recordType
         */
        void setRecordType(int recordType);

        /**
         * 下载录像文件
         *
         * @param position
         */
        void downloadVideoByFile(int position);

        void downloadVideoByTime(Calendar startTime, Calendar endTime);

        /**
         * 快速播放
         */

        void playFast();

        /**
         * 慢速播放
         */
        void playSlow();

        /**
         * 更新视频播放布局
         * @param width
         * @param height
         */
        void updateVideoView(int width,int height);
    }
}
