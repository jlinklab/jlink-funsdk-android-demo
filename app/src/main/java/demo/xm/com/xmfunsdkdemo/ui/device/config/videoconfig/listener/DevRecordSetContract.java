package demo.xm.com.xmfunsdkdemo.ui.device.config.videoconfig.listener;

/**
 * 录像配置界面,可改变录像方式,关掉音频,改变文件长度
 * Created by jiangping on 2018-10-23.
 */
public class DevRecordSetContract {
    public interface IDevRecordSetView {
        /**
         * 根据result获取的数据显示到控件上
         * @param result
         * @param key
         * @param state
         */
        void onUpdateView(String result, String key, int state);

        /**
         * 控件数据保存回调
         * @param key
         * @param state
         */
        void onSaveResult(String key,int state);
    }

    public interface IDevRecordSetPresenter {
        /**
         * 去获取配置数据
         */
        void getRecordInfo();

        /**
         * 去保存配置数据
         * @param key
         * @param jsonData
         */
        void setRecordInfo(String key, String jsonData);
    }
}
