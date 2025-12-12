package demo.xm.com.xmfunsdkdemo.ui.device.config.idrnetwork.listener

import android.app.Activity

class IDRNetworkSwitchContract {

    public interface IIDRNetworkSwitchView {

        fun getActivity(): Activity


        fun dealWith4GInfo()

        fun resetLastCardLocation()

        fun hideSwitchTips()

    }


    public interface IIDRNetworkSwitchPresenter {

    }
}