package demo.xm.com.xmfunsdkdemo.ui.device.cloud.listener;

import android.content.Context;

/**
 * @author cjm
 * @class describe
 * @time 2020/7/28 11:16
 */
public interface CloudStateContract {
    interface ICloudStateView {
        void onUpdateCloudStateResult(int state,String result);
    }

    interface ICloudStatePresenter {
        void updateCloudState(Context context);
        boolean isCanTurnToCloudWeb();
    }
}
