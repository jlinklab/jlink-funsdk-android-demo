package demo.xm.com.xmfunsdkdemo.ui.device.add.bluetooth.listener;

import androidx.recyclerview.widget.RecyclerView;

import com.lib.sdk.bean.bluetooth.XMBleData;
import com.lib.sdk.bean.bluetooth.XMBleInfo;
import com.manager.db.XMDevInfo;

public interface IDevBlueToothView {
    default void onSearchDevBluetoothResult(XMBleInfo xmBleInfo) {

    }

    default void onConnectDebBleResult(String mac, int resultCode) {

    }

    default void onDevBleItemSelected(XMBleInfo xmBleInfo) {

    }

    default void onConnectWiFiResult(String mac, XMBleData xmBleData) {

    }

    default void onAddDevResult(XMDevInfo xmDevInfo, boolean isSuccess, int errorId) {

    }

    default RecyclerView getRecyclerView() {
        return null;
    }
}
