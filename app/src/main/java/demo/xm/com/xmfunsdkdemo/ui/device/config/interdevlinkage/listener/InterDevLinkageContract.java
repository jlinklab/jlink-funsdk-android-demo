package demo.xm.com.xmfunsdkdemo.ui.device.config.interdevlinkage.listener;

import java.util.List;

public class InterDevLinkageContract {
    public interface IInterDevLinkageView {
        /**
         * 支持联动的设备列表返回结果
         *
         * @param supportLinkDevList
         * @param isBind
         */
        void onCheckDevSupportLinkResult(List<String> supportLinkDevList,boolean isBind);

        /**
         * 设备之间的关联结果回调
         *
         * @param isSuccess
         * @param errorId
         */
        void onInterDevLinkResult(boolean isSuccess, int errorId);

        /**
         * 解除设备之间的关联结果回调
         *
         * @param isSuccess
         * @param errorId
         */
        void onInterDevUnlinkResult(boolean isSuccess, int errorId);
    }

    public interface IInterDevLinkagePresenter {
        /**
         * 关联设备
         *
         * @param linkDevId
         */
        void linkDev(String linkDevId);

        void unlinkDev();

        void release();
    }
}
