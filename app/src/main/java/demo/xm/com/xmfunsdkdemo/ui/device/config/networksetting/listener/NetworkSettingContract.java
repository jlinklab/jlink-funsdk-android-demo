package demo.xm.com.xmfunsdkdemo.ui.device.config.networksetting.listener;

import android.app.Activity;

import  demo.xm.com.xmfunsdkdemo.bean.wifi.WifiAP;


import java.util.List;



public class NetworkSettingContract {

    public interface INetworkSettingView {

        void onShowWaitDialog();

        void onHideWaitDialog();

        Activity getActivity();

        void onShowWifiNameAndPassword(String ssid, String password);

        void dealWithChangeWifiFailed(String ssid);

        void goMainActivity();

        void onSwitchToDeviceSuccess();

        void onGetWifiListResult(boolean isSuccess, List<WifiAP> wifiList);

    }


    public interface INetworkSettingPresenter {

    }
}
