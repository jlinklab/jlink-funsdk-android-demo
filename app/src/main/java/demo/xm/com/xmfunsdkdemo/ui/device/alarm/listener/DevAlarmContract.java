package demo.xm.com.xmfunsdkdemo.ui.device.alarm.listener;

import android.graphics.Bitmap;

import com.lib.sdk.bean.alarm.AlarmInfo;
import com.manager.image.BaseImageManager;

import java.util.Calendar;

/**
 * 设备报警界面,显示相关的列表菜单.
 * Created by jiangping on 2018-10-23.
 */
public class DevAlarmContract {
    public interface IDevAlarmView {
        void onUpdateView();

        void onDeleteAlarmMsgResult(boolean isSuccess);

        void onShowPicResult(boolean isSuccess, Bitmap bitmap);

        void onTurnToVideo(Calendar searchTime);
    }

    public interface IDevAlarmPresenter {
        /**
         * 查询报警消息
         */
        void searchAlarmMsg();

        /**
         * 获取消息数量
         *
         * @return
         */
        int getAlarmInfoSize();

        /**
         * 删除报警消息
         */
        void deleteAlarmMsg(int position);

        /**
         * 删除所有报警消息（包括图片和视频）
         */
        void deleteAllAlarmMsg();

        /**
         * 加载缩略图
         *
         * @param position
         */
        void loadThumb(int position, BaseImageManager.OnImageManagerListener onImageManagerListener);

        /**
         * 显示图片
         */
        void showPicture(int position);

        /**
         * 显示视频
         */
        void showVideo(int position);

        /**
         * 获取报警消息信息
         *
         * @param position
         * @return
         */
        AlarmInfo getAlarmInfo(int position);
    }
}
