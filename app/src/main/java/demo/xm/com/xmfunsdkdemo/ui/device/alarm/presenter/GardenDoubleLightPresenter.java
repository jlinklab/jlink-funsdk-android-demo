package demo.xm.com.xmfunsdkdemo.ui.device.alarm.presenter;

import android.widget.Toast;

import com.lib.FunSDK;
import com.lib.sdk.bean.CameraParamBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.WhiteLightBean;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import java.util.ArrayList;

import demo.xm.com.xmfunsdkdemo.ui.device.alarm.listener.GardenDoubleLightContract;

public class GardenDoubleLightPresenter  extends XMBasePresenter<DeviceManager> implements GardenDoubleLightContract.IGardenDoubleLightPresenter{


    private GardenDoubleLightContract.IGardenDoubleLightView iGardenDoubleLightView;

    private DevConfigManager mDevConfigManager;

    protected WhiteLightBean mWhiteLight;

    public CameraParamBean mCameraParamBean;

    public GardenDoubleLightPresenter(GardenDoubleLightContract.IGardenDoubleLightView iGardenDoubleLightView) {
        this.iGardenDoubleLightView = iGardenDoubleLightView;
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void setDevId(String devId) {
        mDevConfigManager = manager.getDevConfigManager(devId);
        super.setDevId(devId);
    }

    /**
     * 保存白光灯工作模式设置
     */
    @Override
    public void saveWhiteLight() {

        iGardenDoubleLightView.onShowWaitDialog();

        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                saveDataSuccess();
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                saveDataFail();
            }
        });
        devConfigInfo.setJsonName(JsonConfig.WHITE_LIGHT);
        devConfigInfo.setChnId(getChnId());
        devConfigInfo.setJsonData(HandleConfigData.getSendData(JsonConfig.WHITE_LIGHT, "0x01", mWhiteLight));
        mDevConfigManager.setDevConfig(devConfigInfo);
    }


    /**
     * 获取白光灯工作模式设置
     */
    public void getWhiteLight(){
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<WhiteLightBean>() {
            @Override
            public void onSuccess(String devId, int msgId, WhiteLightBean whiteLightBean) {
                mWhiteLight = whiteLightBean;
                if (mWhiteLight != null) {
                    iGardenDoubleLightView.showWorkMode(mWhiteLight);
                    getCameraParam();
                } else {
                    getDataFail();
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName(JsonConfig.WHITE_LIGHT);
        mainConfigInfo.setChnId(getChnId());
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }


    /**
     * 获取摄像机参数
     */
    public void getCameraParam(){
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                if(result instanceof ArrayList){
                    ArrayList<CameraParamBean> list = (ArrayList<CameraParamBean>)result;
                    if(list!=null && list.size()>0){
                        mCameraParamBean = list.get(0);
                    }
                    if (mCameraParamBean != null) {
                        int selectValue = Integer.parseInt(mCameraParamBean.DayNightColor.substring(2), 16);
                        iGardenDoubleLightView.showColorWhiteLight(selectValue);
                        iGardenDoubleLightView.onHideWaitDialog();
                    } else {
                        getDataFail();
                    }
                } else {
                    getDataFail();
                }

            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName(JsonConfig.CAMERA_PARAM);
        mainConfigInfo.setChnId(getChnId());
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }


    /**
     * 保存图像设置配置
     */
    @Override
    public void saveDayNightColorConfig(String dayNightColor){
        if (mCameraParamBean != null) {
            iGardenDoubleLightView.onShowWaitDialog();
            mCameraParamBean.DayNightColor = dayNightColor;
            DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String devId, int msgId, Object result) {
                    saveDataSuccess();
                }

                @Override
                public void onFailed(String devId, int msgId, String s1, int errorId) {
                    saveDataFail();
                }
            });
            devConfigInfo.setJsonName(JsonConfig.CAMERA_PARAM);
            devConfigInfo.setChnId(getChnId());
            devConfigInfo.setJsonData(HandleConfigData.getSendData(JsonConfig.CAMERA_PARAM, "0x01", mCameraParamBean));
            mDevConfigManager.setDevConfig(devConfigInfo);
        } else {
            Toast.makeText(iGardenDoubleLightView.getActivity().getApplicationContext(), FunSDK.TS("Data_exception"), Toast.LENGTH_SHORT).show();
        }
    }




    private void getDataFail(){
        iGardenDoubleLightView.onHideWaitDialog();
        Toast.makeText(iGardenDoubleLightView.getActivity().getApplicationContext(), FunSDK.TS("no_data"), Toast.LENGTH_SHORT).show();
    }


    private void saveDataFail(){
        iGardenDoubleLightView.onHideWaitDialog();
        Toast.makeText(iGardenDoubleLightView.getActivity().getApplicationContext(), FunSDK.TS("Save_Failed"), Toast.LENGTH_SHORT).show();
    }

    private void saveDataSuccess(){
        iGardenDoubleLightView.onHideWaitDialog();
    }

    @Override
    public WhiteLightBean getWhiteLightBean() {
        return mWhiteLight;
    }



}
