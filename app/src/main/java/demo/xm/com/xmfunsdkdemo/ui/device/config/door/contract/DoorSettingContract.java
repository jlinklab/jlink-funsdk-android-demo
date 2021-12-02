package demo.xm.com.xmfunsdkdemo.ui.device.config.door.contract;

/**
 * @author hws
 * @class
 * @time 2020/8/14 13:34
 */
public interface DoorSettingContract {
    interface IDoorSettingView {
        void onUpdateManageShutDownResult(boolean isSuccess,int sleepTime);
        void onUpdateCorridorModeResult(boolean isSuccess,int mode);
    }

    interface IDoorSettingPresenter {
        /**
         * 获取休眠时间
         */
        void updateManageShutDown();
        void saveManageShutDown(int sleepTime);

        void updateCorridorMode();
        /**
         * 镜头方向
         * @param mode // 0 正常 1 90度 2 180度 3 270度
         */
        void saveCorridorMode(int mode);
    }
}
