package demo.xm.com.xmfunsdkdemo.ui.device.alarm.presenter;

import android.graphics.Bitmap;
import android.os.Message;

import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.alarm.AlarmGroup;
import com.lib.sdk.bean.alarm.AlarmInfo;
import com.manager.device.DeviceManager;
import com.manager.device.alarm.DevAlarmInfoManager;
import com.manager.image.BaseImageManager;
import com.manager.image.CloudImageManager;
import com.xm.activity.base.XMBasePresenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication;
import demo.xm.com.xmfunsdkdemo.ui.device.alarm.listener.DevAlarmContract;

/**
 * 设备报警界面,显示相关的列表菜单.
 * Created by jiangping on 2018-10-23.
 */
public class DevAlarmPresenter extends XMBasePresenter<DeviceManager> implements DevAlarmContract.IDevAlarmPresenter {
    private DevAlarmContract.IDevAlarmView iDevAlarmView;
    private DevAlarmInfoManager devAlarmInfoManager;
    private CloudImageManager cloudImageManager;
    private List<AlarmInfo> alarmMsgList;
    private Calendar searchTime = Calendar.getInstance();

    public DevAlarmPresenter(final DevAlarmContract.IDevAlarmView iDevAlarmView) {
        this.iDevAlarmView = iDevAlarmView;
        devAlarmInfoManager = new DevAlarmInfoManager(new DevAlarmInfoManager.OnAlarmInfoListener() {//Callback of the query list
            @Override
            public void onSearchResult(List<AlarmGroup> list) {
                dealWithAlarmInfo(list);
                iDevAlarmView.onUpdateView();
            }

            @Override
            public void onDeleteResult(boolean isSuccess, Message message, MsgContent msgContent, List<AlarmInfo> list) {//Remove callbacks for a single item
                if (isSuccess) {
                    if (list != null) {
                        for (AlarmInfo alarmInfo : list) {
                            if (alarmMsgList.contains(alarmInfo)) {
                                alarmMsgList.remove(alarmInfo);
                            }
                        }
                    } else {
                        alarmMsgList.clear();
                    }
                }
                iDevAlarmView.onDeleteAlarmMsgResult(isSuccess);
            }
        });

    }

    private void dealWithAlarmInfo(List<AlarmGroup> alarmGroupList) {
        if (alarmGroupList != null) {
            alarmMsgList = new ArrayList<>();
            for (AlarmGroup alarmGroup : alarmGroupList) {
                for (AlarmInfo alarmInfo : alarmGroup.getInfoList()) {
                    alarmMsgList.add(alarmInfo);
                }
            }
        }
    }

    @Override
    public void setDevId(String devId) {
        cloudImageManager = new CloudImageManager(SDKDemoApplication.PATH_PHOTO);//Cloud image download manager
        cloudImageManager.setDevId(devId);
        cloudImageManager.setOnImageManagerListener(new BaseImageManager.OnImageManagerListener() {
            @Override
            public void onDownloadResult(boolean isSuccess, String imagePath, Bitmap bitmap, int mediaType, int seq) {
                iDevAlarmView.onShowPicResult(isSuccess, bitmap);
            }

            @Override
            public void onDeleteResult(boolean b, int i) {

            }
        });
        super.setDevId(devId);
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    /**
     * 设置查询时间
     *
     * @param searchTime
     */
    public void setSearchTime(Calendar searchTime) {
        this.searchTime = searchTime;
    }

    /**
     * 查询报警消息
     */
    @Override
    public void searchAlarmMsg() {
        Date searchDate = searchTime.getTime();
        devAlarmInfoManager.searchAlarmInfo(getDevId()/*设备序列号*/, 0 /*通道ID*/, 0/*报警类型*/, searchDate/*查询开始时间*/, 1/*查询的天数*/, 270/*缩略图-宽 原图默认传0*/, 640/*缩略图-高 原图默认传0*/);
    }

    @Override
    public int getAlarmInfoSize() {
        return alarmMsgList != null ? alarmMsgList.size() : 0;
    }

    /**
     * 删除单条报警消息
     *
     * @param position
     */
    @Override
    public void deleteAlarmMsg(int position) {
        AlarmInfo alarmInfo = getAlarmInfo(position);
        devAlarmInfoManager.deleteAlarmInfo(getDevId(), "MSG", alarmInfo);
    }

    /**
     * 删除所有报警消息
     */
    @Override
    public void deleteAllAlarmMsg() {
        //删除消息和图片
        devAlarmInfoManager.deleteAllAlarmInfos(getDevId(), "MSG");
        //删除视频
        devAlarmInfoManager.deleteAllAlarmInfos(getDevId(), "VIDEO");
    }

    /**
     * 显示报警图片
     *
     * @param position
     */
    @Override
    public void showPicture(int position) {
        AlarmInfo alarmInfo = getAlarmInfo(position);
        cloudImageManager.downloadImage(alarmInfo, SDKCONST.MediaType.PIC, position, 0, 0);
    }

    @Override
    public void loadThumb(int position, BaseImageManager.OnImageManagerListener onImageManagerListener) {
        AlarmInfo alarmInfo = getAlarmInfo(position);
        cloudImageManager.downloadImage(alarmInfo, SDKCONST.MediaType.PIC, position, 96, 54, onImageManagerListener, true);
    }

    @Override
    public void showVideo(int position) {

    }

    @Override
    public AlarmInfo getAlarmInfo(int position) {
        if (alarmMsgList == null || position >= alarmMsgList.size()) {
            return null;
        }

        return alarmMsgList.get(position);
    }
}

