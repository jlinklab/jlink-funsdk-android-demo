package demo.xm.com.xmfunsdkdemo.ui.device.add.lan.listener;

import com.manager.db.XMDevInfo;

import java.util.List;

/**
 * 局域网连接设备界面,显示设备列表菜单,包括名称,类型,mac,sn,ip,状态,以及是否在线
 * LAN connection device interface, display device list menu, including name,
 * type,mac,sn,ip, status, and whether online
 * Created by jiangping on 2018-10-23.
 */
public class DevLanConnectContract {
    public interface IDevLanConnectView {
        void onUpdateView();
        void onAddDevResult(boolean isSuccess,int errorId);
    }

    public interface IDevLanConnectPresenter {
        void searchLanDevice();
        List<XMDevInfo> getLanDevList();
        XMDevInfo getLanDevInfo(int position);
        void addDeviceToAccount(XMDevInfo xmDevInfo);
    }
}
