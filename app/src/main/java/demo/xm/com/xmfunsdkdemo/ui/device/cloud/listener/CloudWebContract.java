package demo.xm.com.xmfunsdkdemo.ui.device.cloud.listener;

/**
 * @author hws
 * @class describe
 * @time 2019-10-23 10:02
 */
public interface CloudWebContract {
    interface ICloudWebView {
        void onUpdateSysInfoResult(boolean isSuccess, int errorId);
    }

    interface ICloudWebPresenter {
        void setGoodsType(String goodsType);
        String getGoodsType();
        void initSystemInfo();
        String getDevHardWare();
        String getDevSoftWare();
    }
}
