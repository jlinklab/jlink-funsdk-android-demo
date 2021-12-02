package demo.xm.com.xmfunsdkdemo.ui.device.picture.listener;

import com.lib.sdk.struct.H264_DVR_FILE_DATA;

import java.util.Calendar;
import java.util.List;

public class DevPictureContract {
    public interface IDevPictureView {
        void onUpdateView();

        void onDownloadResult(int state, String filePath);

        void onSearchCalendarResult(boolean isSuccess,Object result);
    }

    public interface IDevPicturePresenter {
        /**
         * 按文件方式查询图片，以文件列表方式显示
         * @param searchTime
         */
        void searchPicByFile(Calendar searchTime);

        /**
         * 获取搜索到的图片文件数据
         *
         * @return
         */
        List<H264_DVR_FILE_DATA> getPicList();

        /**
         * 获取设备图片信息
         *
         * @param position
         * @return
         */
        H264_DVR_FILE_DATA getPicInfo(int position);

        /**
         * 获取图片文件个数
         *
         * @return
         */
        int getPicCount();

        void downloadFile(int position);

        void searchMediaFileCalendar(Calendar searchTime);


    }
}
