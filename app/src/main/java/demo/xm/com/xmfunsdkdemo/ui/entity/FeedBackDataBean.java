package demo.xm.com.xmfunsdkdemo.ui.entity;

import java.util.List;

/**
 *  意见反馈页面 要传递给H5的数据
 */
public class FeedBackDataBean {

    public String userId;
    public List<DevBean> devList;
    public String appversion; // App版本号
    public String appName; // App名称
    public String packageName;// App包名
    public String contact;// （客户注册时的用户名）


    public static class DevBean{
        public String devId;
        public String devName;
        public int devType;
        public String qrcodeInfo;// 二维码信息
        public String oemId;//设备oemId
        public String pid;//设备pid
        public String qrCodeOem;//扫码信息中的oemId

    }




}
