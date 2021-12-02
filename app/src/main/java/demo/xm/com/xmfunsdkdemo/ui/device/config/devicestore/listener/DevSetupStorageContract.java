package demo.xm.com.xmfunsdkdemo.ui.device.config.devicestore.listener;

import com.lib.sdk.bean.StorageInfoBean;

import java.util.List;
import com.lib.sdk.bean.GeneralInfoBean;

/**
 * 设备存储管理界面,包括录像分区及图片分区的存储容量,剩余容量,录像满时停止或是循环录像.
 * Created by jiangping on 2018-10-23.
 */
public class DevSetupStorageContract {
    public interface IDevSetupStorageView {
        /**
         * 获取存储配置结果
         */
        void getStorageConfigResult(GeneralInfoBean generalInfoBean);
        /**
         * 修改存储满时配置结果
         */
        void changeVideoFullStateResult(boolean isSuccess);
        /**
         * 获取存储容量信息失败
         */
        void getStorageDataError(String errorString);
        /**
         * 获取存储容量信息成功
         */
        void getStorageDataSuccess(String totalSizeString,String remainSizeString,String videoPartSizeString,String picPartSizeString,boolean isShowPicPart);
        /**
         * 格式化结果
         */
        void onFormatResult(boolean isSuccess);
    }

    public interface IDevSetupStoragePresenter {
        void setDevId(String devId);
        /**
         * 获取存储信息
         */
        void getStorageInfo();

        /**
         * 格式化
         */
        void formatStorage();
        /**
         * 修改存储满时配置
         */
        void changeVideoFullState(boolean isStopWrite);
        /**
         * 获取存储配置
         */
        void getStorageConfig();
    }
}
