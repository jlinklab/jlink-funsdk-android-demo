package demo.xm.com.xmfunsdkdemo.ui.device.config.shadow.listener;

public class DevShadowConfigContract {
    public interface IDevShadowConfigView {
        void onSendDataResult(String result);

        void onReceiveDataResult(String state, String result);
    }

    public interface IDevShadowConfigPresenter {
        /**
         * 获取影子服务配置
         *
         * @param configName {@link com.manager.device.config.shadow.ShadowConfigEnum}
         */
        void getConfig(String configName);

        /**
         * 设置影子服务配置
         *
         * @param jsonData 离线配置
         */
        void setConfig(String configName, Object jsonData);

        void release();
    }
}
