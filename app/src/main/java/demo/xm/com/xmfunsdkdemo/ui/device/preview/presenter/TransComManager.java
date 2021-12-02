package demo.xm.com.xmfunsdkdemo.ui.device.preview.presenter;

import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.manager.device.config.SerialPortsInfo;
import com.utils.LogUtils;

/**
 * 串口管理类
 * (串口相关的SDK请求所使用的useId通过此单例里的devConfigManager回调）
 */
public class TransComManager {
    private DevConfigManager devConfigManager;
    private static TransComManager instance;
    private String devId;


    public synchronized static TransComManager getInstance() {
        if (instance == null) {
            instance = new TransComManager();
        }

        return instance;
    }

    /**
     * 初始化串口操作用的decConfigManager
     * @param devId
     */
    public void initDevConfigManager(String devId) {
        this.devId = devId;
        if(devConfigManager == null){
            devConfigManager = DevConfigManager.create(devId);
        }
    }

    /**
     * 打开串口
     * @param serialPortsType 串口类型
     */
    public void openSerialPorts(int serialPortsType){
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String s, int i, Object abilityKey) {
                LogUtils.debugInfo("TransCom","打开串口成功");
            }

            @Override
            public void onFailed(String s, int errorId, String s1, int i1) {
            }
        });

        SerialPortsInfo serialPortsInfo = new SerialPortsInfo();
        serialPortsInfo.setSerialPortsType(serialPortsType);
        devConfigInfo.setSerialPortsInfo(serialPortsInfo);
        //打开串口
        if (devConfigManager != null) {
            devConfigManager.openSerialPorts(devConfigInfo);
        }
    }

    /**
     * 关闭串口
     * @param serialPortsType 串口类型
     */
    public void closeSerialPorts(int serialPortsType) {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String s, int i, Object abilityKey) {
                LogUtils.debugInfo("TransCom","关闭串口成功");
            }

            @Override
            public void onFailed(String s, int errorId, String s1, int i1) {
            }
        });

        SerialPortsInfo serialPortsInfo = new SerialPortsInfo();
        serialPortsInfo.setSerialPortsType(serialPortsType);
        devConfigInfo.setSerialPortsInfo(serialPortsInfo);
        //关闭串口
        if (devConfigManager != null) {
            devConfigManager.closeSerialPorts(devConfigInfo);
        }
    }

    /**
     * 发送串口数据
     * @param devConfigInfo
     */
    public void sendSerialPortsData(DevConfigInfo devConfigInfo){
        devConfigManager.setSerialPortsData(devConfigInfo);
    }



    public void release() {
        if (devConfigManager != null) {
            devConfigManager = null;
        }
    }
}
