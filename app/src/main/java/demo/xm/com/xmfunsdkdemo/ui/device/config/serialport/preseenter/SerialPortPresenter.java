package demo.xm.com.xmfunsdkdemo.ui.device.config.serialport.preseenter;

import com.lib.SDKCONST;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.manager.device.config.SerialPortsInfo;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.device.config.serialport.contract.SerialPortContract;
import demo.xm.com.xmfunsdkdemo.utils.TypeConversion;

import static com.lib.EUIMSG.DEV_ON_TRANSPORT_COM_DATA;

/**
 * 串口透传
 * @author hws
 * @class describe
 * @time 2021/1/4 19:23
 */
public class SerialPortPresenter extends XMBasePresenter<DeviceManager> implements
        SerialPortContract.ISerialPortPresenter {
    private DevConfigManager devConfigManager;
    private SerialPortContract.ISerialPortView iSerialPortView;
    public SerialPortPresenter(SerialPortContract.ISerialPortView iSerialPortView) {
        this.iSerialPortView = iSerialPortView;
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        devConfigManager = manager.getDevConfigManager(devId);
    }

    /**
     * 打开串口
     */
    @Override
    public void openSerialPort() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String s, int i, Object result) {
                System.out.println("打开成功");
                iSerialPortView.onOpenSerialPortResult(true);
                devConfigManager.setSerialPortsDataReceiveListener(DevConfigInfo.create(new DeviceManager.OnDevManagerListener<byte[]>() {
                    @Override
                    public void onSuccess(String s, int operationType, byte[] result) {
                        //接收串口数据
                        if (operationType == DEV_ON_TRANSPORT_COM_DATA) {
                            if (result != null) {
                                String info = TypeConversion.bytes2HexString(result,result.length);
                                iSerialPortView.onSerialPortResult(info);
                            }
                        }
                    }

                    @Override
                    public void onFailed(String s, int i, String s1, int i1) {

                    }
                }));
            }

            @Override
            public void onFailed(String s, int i, String s1, int errorId) {
                System.out.println("onFailed");
            }
        });

        SerialPortsInfo serialPortsInfo = new SerialPortsInfo();
        //串口类型有 232和485
        serialPortsInfo.setSerialPortsType(SDKCONST.SDK_CommTypes.SDK_COMM_TYPES_RS232);
        devConfigInfo.setSerialPortsInfo(serialPortsInfo);
        devConfigManager.openSerialPorts(devConfigInfo);
    }

    /**
     * 关闭串口
     */
    @Override
    public void closeSerialPort() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String s, int i, Object result) {
                System.out.println("关闭成功");
                iSerialPortView.onOpenSerialPortResult(false);
            }

            @Override
            public void onFailed(String s, int i, String s1, int errorId) {
                System.out.println("onFailed");
            }
        });

        SerialPortsInfo serialPortsInfo = new SerialPortsInfo();
        serialPortsInfo.setSerialPortsType(SDKCONST.SDK_CommTypes.SDK_COMM_TYPES_RS232);
        devConfigInfo.setSerialPortsInfo(serialPortsInfo);
        devConfigManager.closeSerialPorts(devConfigInfo);
    }

    /**
     * 读数据
     */
    @Override
    public void readSerialPortData() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String s, int operationType, Object result) {

            }

            @Override
            public void onFailed(String s, int i, String s1, int errorId) {
            }
        });

        SerialPortsInfo serialPortsInfo = new SerialPortsInfo();
        serialPortsInfo.setSerialPortsType(SDKCONST.SDK_CommTypes.SDK_COMM_TYPES_RS232);
        devConfigInfo.setSerialPortsInfo(serialPortsInfo);
        devConfigManager.getSerialPortsData(devConfigInfo);
    }

    /**
     * 写数据
     * @param data
     */
    @Override
    public void writeSerialPortData(byte[] data) {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<byte[]>() {
            @Override
            public void onSuccess(String s, int operationType, byte[] result) {

            }

            @Override
            public void onFailed(String s, int i, String s1, int errorId) {
            }
        });

        SerialPortsInfo serialPortsInfo = new SerialPortsInfo();
        serialPortsInfo.setSerialPortsType(SDKCONST.SDK_CommTypes.SDK_COMM_TYPES_RS232);
        //传入数据并发送
        serialPortsInfo.setSerialPortsData("06000865000004010970010E".getBytes());
        devConfigInfo.setSerialPortsInfo(serialPortsInfo);
        devConfigManager.setSerialPortsData(devConfigInfo);
    }
}
