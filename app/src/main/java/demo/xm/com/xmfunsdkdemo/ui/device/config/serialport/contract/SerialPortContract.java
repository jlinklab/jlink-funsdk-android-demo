package demo.xm.com.xmfunsdkdemo.ui.device.config.serialport.contract;

/**
 * 串口透传
 * @author hws
 * @class describe
 * @time 2021/1/4 19:25
 */
public interface SerialPortContract {
    interface ISerialPortView {
        void onOpenSerialPortResult(boolean isOpen);
        void onSerialPortResult(byte[] data);
    }

    interface ISerialPortPresenter {
        /**
         * 打开串口
         */
        void openSerialPort();

        /**
         * 关闭串口
         */
        void closeSerialPort();

        /**
         * 读取串口数据
         */
        void readSerialPortData();

        /**
         * 写串口数据
         */
        void writeSerialPortData(byte[] data);
    }
}
