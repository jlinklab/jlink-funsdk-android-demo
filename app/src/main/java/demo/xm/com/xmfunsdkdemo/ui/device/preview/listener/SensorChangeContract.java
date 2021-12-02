package demo.xm.com.xmfunsdkdemo.ui.device.preview.listener;

import android.content.Context;

import com.manager.device.config.SensorManager;
import com.manager.device.media.monitor.MonitorManager;

public interface SensorChangeContract {
    interface ISensorChangeView extends SensorManager.OnSensorChangeListener {
        Context getContext();
    }

    interface ISensorChangePresenter {
        void initSensor(String devId, int streamType, int chnId);

        /**
         * 当手指离开屏幕后进行视频比例缩放
         *
         * @param monitorManager 播放器
         * @param scale          缩放比例
         * @param chnId          通道号
         * @param streamSync     是否同步倍数：0代表不存在，1代表同步，2代表不同步(不想兼容同步倍数功能时，传0)
         */
        int ctrlVideoScaleWhenUp(MonitorManager monitorManager, int scale, int chnId, int streamSync);

        /**
         * 控制视频缩放比例
         *
         * @param monitorManager       播放器
         * @param isDevSupportMoreLens 设备是否支持多目
         * @param scale                缩放比例
         * @param chnId                通道号
         * @param streamSync           是否同步倍数：0代表不存在，1代表同步，2代表不同步(不想兼容同步倍数功能时，传0)
         */
        int ctrlVideoScale(MonitorManager monitorManager, boolean isDevSupportMoreLens, float scale, int chnId, int streamSync);

        /**
         * 控制视频缩放比例
         *
         * @param monitorManager 播放器
         * @param isDevSupportMoreLens 设备是否支持多目
         * @param progress       滑动条的进度值
         * @param chnId          通道号
         * @param streamSync     是否同步倍数：0代表不存在，1代表同步，2代表不同步(不想兼容同步倍数功能时，传0)
         */
        float ctrlVideoScaleByProgress(MonitorManager monitorManager, boolean isDevSupportMoreLens, int progress, int chnId, int streamSync);

        /**
         * 切换镜头
         *
         * @param devId      设备序列号
         * @param sensorId   设备镜头Id （0、1、2）
         * @param streamType 码流类型（主码流：Main，副码流：Extra）
         */
        void switchSensor(String devId, int sensorId, int streamType, int chnId);

        /**
         * 设置当前缩放比例
         *
         * @param scale
         */
        void setCurScale(float scale);

        /**
         * 获取当前缩放比例
         *
         * @return
         */
        float getCurScale();

        /**
         * 设置要改变的SensorId
         *
         * @param sensorId
         */
        void setChangeSensorId(int sensorId);

        /**
         * 获取当前改变的SensorId
         *
         * @return
         */
        float getChangeSensorId();

        /**
         * 获取当前拖动条缩放比例进度
         *
         * @return
         */
        int getProgress();

        /**
         * 设置当前SensorId
         *
         * @param sensorId
         */
        void setCurSensorId(int sensorId);

        /**
         * 获取当前SensorId
         *
         * @return
         */
        int getCurSensorId();

        /**
         * 设置Sensor个数
         *
         * @param count
         */
        void setSensorCount(int count);

        /**
         * 获取Sensor个数
         *
         * @return
         */
        int getSensorCount();

        /**
         * 获取需要切换镜头的变倍
         *
         * @return
         */
        float getNeedChangeSensorScale();

        /**
         * 每个镜头间的变倍个数
         *
         * @param count
         */
        void setSensorItemCount(int count);
    }
}
