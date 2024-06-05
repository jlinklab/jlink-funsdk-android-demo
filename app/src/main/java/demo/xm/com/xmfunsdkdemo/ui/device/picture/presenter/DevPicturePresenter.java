package demo.xm.com.xmfunsdkdemo.ui.device.picture.presenter;

import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.manager.db.Define;
import com.manager.db.DownloadInfo;
import com.manager.db.SearchFileInfo;

import com.manager.device.media.calendar.MediaFileCalendarManager;
import com.manager.device.media.download.DownloadManager;
import com.manager.device.media.file.FileManager;
import com.utils.TimeUtils;
import com.xm.activity.base.XMBasePresenter;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication;
import demo.xm.com.xmfunsdkdemo.ui.device.picture.listener.DevPictureContract;

import static com.manager.db.Define.DOWNLOAD_VIDEO_BY_FILE;

public class DevPicturePresenter extends XMBasePresenter<FileManager> implements
        DevPictureContract.IDevPicturePresenter, FileManager.OnFileSearchListener<H264_DVR_FILE_DATA>
        , DownloadManager.OnDownloadListener, MediaFileCalendarManager.OnMediaFileCalendarListener {
    private DownloadManager downloadManager;
    private DevPictureContract.IDevPictureView iDevPictureView;
    private List<H264_DVR_FILE_DATA> picList;
    private MediaFileCalendarManager mediaFileCalendarManager;

    public DevPicturePresenter(DevPictureContract.IDevPictureView iDevPictureView) {
        this.iDevPictureView = iDevPictureView;
        downloadManager = DownloadManager.getInstance(this);
    }

    @Override
    protected FileManager getManager() {
        return new FileManager(this);
    }

    @Override
    public void setDevId(String s) {
        super.setDevId(s);
        mediaFileCalendarManager = new MediaFileCalendarManager(this);
        mediaFileCalendarManager.init(getDevId(), null, "jpg");
    }

    @Override
    public List<H264_DVR_FILE_DATA> getPicList() {
        return picList;
    }

    @Override
    public H264_DVR_FILE_DATA getPicInfo(int position) {
        if (picList != null && position >= 0 && position < picList.size()) {
            return picList.get(position);
        }

        return null;
    }

    @Override
    public int getPicCount() {
        return picList != null ? picList.size() : 0;
    }

    @Override
    public void downloadFile(int position) {
        if (picList == null || position >= picList.size()) {
            return;
        }

        H264_DVR_FILE_DATA data = picList.get(position);
        if (data != null) {
            String fileName = data.getFileName() + ".jpg";
            DownloadInfo downloadInfo = new DownloadInfo();
            downloadInfo.setStartTime(TimeUtils.getNormalFormatCalender(data.getStartTimeOfYear()));
            downloadInfo.setEndTime(TimeUtils.getNormalFormatCalender(data.getEndTimeOfYear()));
            downloadInfo.setDevId(getDevId());
            downloadInfo.setObj(data);
            //下载类型：0->卡存按文件下载 1->云存储下载 2->卡存按时间下载
            //Download type: 0->Download by file from SD card 1->Download from cloud storage 2->Download by time from SD card
            downloadInfo.setDownloadType(0);
            downloadInfo.setSaveFileName(SDKDemoApplication.PATH_PHOTO + File.separator + fileName);
            downloadManager.addDownload(downloadInfo);
            downloadManager.startDownload();
        }
    }

    @Override
    public void searchMediaFileCalendar(Calendar searchTime) {
        Calendar searchCalendar = Calendar.getInstance();
        mediaFileCalendarManager.searchFile(searchCalendar, Define.MEDIA_TYPE_DEVICE, 0);
    }

    @Override
    public void searchPicByFile(Calendar searchTime) {
        Calendar startTime = searchTime;
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);

        Calendar endTime = (Calendar) searchTime.clone();
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        endTime.set(Calendar.MINUTE, 59);
        endTime.set(Calendar.SECOND, 59);

        SearchFileInfo searchFileInfo = new SearchFileInfo();
        searchFileInfo.setChnId(getChnId());
        searchFileInfo.setStartTime(startTime);
        searchFileInfo.setEndTime(endTime);

        manager.searchPictureByFile(getDevId(), searchFileInfo);
    }

    @Override
    public void onSearchResult(List<H264_DVR_FILE_DATA> list) {
        this.picList = list;
        if (iDevPictureView != null) {
            iDevPictureView.onUpdateView();
        }
    }

    @Override
    public void onDownload(DownloadInfo downloadInfo) {
        if (downloadInfo != null) {
            iDevPictureView.onDownloadResult(downloadInfo.getDownloadState(), downloadInfo.getSaveFileName());
        }
    }

    @Override
    public void onHaveFileData(HashMap<Object, Boolean> fileMaps, int i) {
        iDevPictureView.onSearchCalendarResult(true, fileMaps);
    }

    @Override
    public void onFailed(int msgId, int errorId) {
        iDevPictureView.onSearchCalendarResult(false, "错误码：" + errorId);
    }
}
