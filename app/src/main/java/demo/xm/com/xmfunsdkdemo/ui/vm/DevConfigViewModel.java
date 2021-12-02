package demo.xm.com.xmfunsdkdemo.ui.vm;

/**
 * 设备配置界面中的单项属性设置及获取
 * Setting and obtaining individual properties in the device configuration interface
 * Created by jiangping on 2018-10-23.
 */
public class DevConfigViewModel {
    private int name;
    private int des;
    private int imageId = 0;

    public DevConfigViewModel(int name, int des) {
        this.name = name;
        this.des = des;
    }

    public DevConfigViewModel(int name, int des, int imageId) {
        this.name = name;
        this.des = des;
        this.imageId = imageId;
    }

    public int getName() {
        return name;
    }

    public int getDes() {
        return des;
    }

    public int getImageId() {
        return imageId;
    }
}
