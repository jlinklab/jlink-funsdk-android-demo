package demo.xm.com.xmfunsdkdemo.ui.device.config.advance.listener;

import android.widget.TextView;

import java.util.ArrayList;

import demo.xm.com.xmfunsdkdemo.ui.device.config.advance.data.GPIOInfo;

/**
 * 高级配置界面,该功能主要是一些更加专业化的设置项,包括曝光时间,情景模式,电子慢快门,色彩模式等
 * Created by jiangping on 2018-10-23.
 */
public class DevAdvanceContract {
    public interface IDevAdvanceView {
        void onGetGPIOResult(ArrayList<GPIOInfo> gpioInfoArrayList);

        void onSaveGPIOResult(boolean isSuccess);

        /**
         * 修改设备名水印回调/Callback for modifying device name watermark
         *
         * @param isSuccess
         */
        void onModifyDevNameOsd(boolean isSuccess, int errorId);

    }

    public interface IDevAdvancePresenter {
        /**
         * 获取GPIO配置
         * Get GPIO Configuration
         */
        void getGPIO();

        /**
         * 保存GPIO配置
         * Save GPIO Configuration
         */
        void saveGPIO();

        void setGPIO(int position, int status);

        /**
         * 修改设备预览的OSD信息/Modify OSD (On-Screen Display) information for device preview
         *
         * @param devName
         * @param tvOSD   存放水印信息的TextView，该TextView需要设置成invisible/The TextView for storing watermark information, which needs to be set to invisible
         */
        void modifyDevNameForOSD(String devName, TextView tvOSD);
    }
}
