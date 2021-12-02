package demo.xm.com.xmfunsdkdemo.ui.device.config;

public class DevConfigState {
    public static final int DEV_CONFIG_UPDATE_FAILED = 0;  //保存失败
    public static int DEV_CONFIG_UPDATE_SUCCESS = 1; //保存成功
    public static final int DEV_CONFIG_VIEW_INVISIABLE = -1;//不可见状态(若设备不支持外部设备报警和人形检测功能，则为不可见状态)，故没有保存功能
    public static final int DEV_CONFIG_VIEW_VISIABLE = 2;//可见状态
    public static final int DEV_CONFIG_UNLOAD = 3;     //还未加载
}
