package demo.xm.com.xmfunsdkdemo.ui.vm;

/**
 * 设备列表界面中的单项属性设置及获取
 * Setting and obtaining single property in the device list interface
 * Created by jiangping on 2018-10-23.
 */
public class DevLanViewModel {
    private int name;
    private int type;
    private int mac;
    private int sn;
    private int ip;
    private int more;

    private int status;
    private int imageId;

    public DevLanViewModel(int name, int type, int mac, int sn, int ip, int more, int status, int imageId) {
        this.name = name;
        this.type = type;
        this.mac = mac;
        this.sn = sn;
        this.ip = ip;
        this.more = more;

        this.status = status;
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public int getStatus() {
        return status;
    }

    public int getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public int getMac() {
        return mac;
    }

    public int getSn() {
        return sn;
    }

    public int getIp() {
        return ip;
    }

    public int getMore() {
        return more;
    }


}
