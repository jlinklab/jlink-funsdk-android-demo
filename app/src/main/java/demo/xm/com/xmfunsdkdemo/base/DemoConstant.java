package demo.xm.com.xmfunsdkdemo.base;

/**
 * 常量类
 * Constant class
 */
public class DemoConstant {
    /**
     * 应用证书,以下4个字段必须要在开放平台注册应用之后替换掉，测试Key会定期更换，如果未替换后果自负
     * 前往（https://aops.jftech.com/） 新人指南，注册申请成为开放平台开发者，
     * 然后到【控制台】-【创建应用页面】中创建Android应用，
     * 等应用审核通过后就可以获取到AppKey、movedCard和AppSecret等信息
     * 
     * The following four fields of the application certificate must be replaced after the application is registered on the open platform.
     * The test key will be replaced periodically. If it is not replaced, the consequences will be borne by yourself、
     * Go to（https://aops.jftech.com/）A guide for newcomers, register and apply to become an open platform developer,
     * Then go to [Console] - [Create Application Page] to create an Android application,
     * AppKey, movedCard, AppSecret and other information can be obtained after the application is approved
     */
    public static final String APP_UUID = "e0534f3240274897821a126be19b6d46";
    public static final String APP_KEY = "0621ef206a1d4cafbe0c5545c3882ea8";
    public static final String APP_SECRET = "90f8bc17be2a425db6068c749dee4f5d";
    public static final int APP_MOVEDCARD = 2;
    public static final String MULTI_LENS_TWO_SENSOR = "MultiLensTwoSensor";//双目设备 APP放大
    public static final String MULTI_LENS_THREE_SENSOR = "MultiLensThreeSensor";//三目设备 APP放大
    public static final String SUPPORT_SCALE_TWO_LENS = "SupportScaleTwoLens";//双目设备 设备端放大
    public static final String SUPPORT_SCALE_THREE_LENS = "SupportScaleThreeLens";//三目设备 设备端放大
    public static final String LAST_CHANGE_SCALE_TIMES = "last_change_scale_times";//记录设备最近一次的倍数
    public static final String SENSOR_MAX_TIMES = "sensor_max_time";//最大倍数
}
