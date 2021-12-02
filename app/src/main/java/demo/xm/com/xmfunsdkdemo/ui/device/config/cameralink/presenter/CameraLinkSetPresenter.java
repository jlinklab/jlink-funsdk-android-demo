package demo.xm.com.xmfunsdkdemo.ui.device.config.cameralink.presenter;

import com.basic.G;
import com.google.gson.Gson;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.cameralink.listener.CameraLinkSetContract;

public class CameraLinkSetPresenter extends XMBasePresenter<DeviceManager> implements CameraLinkSetContract.ICameraLinkSetPresenter {
    private DevConfigManager devConfigManager;
    private JSONObject jsonObject;
    private CameraLinkSetContract.ICameraLinkSetView iCameraLinkSetView;
    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    public CameraLinkSetPresenter(CameraLinkSetContract.ICameraLinkSetView iCameraLinkSetView) {
        this.iCameraLinkSetView = iCameraLinkSetView;
    }

    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        devConfigManager = manager.getDevConfigManager(devId);
        getConfig();
    }

    private void getConfig() {
        iCameraLinkSetView.showWaitDlgShow(true);
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String s, int i, String jsonData) {
                if (jsonData != null) {
                    try {
                        jsonObject =new JSONObject(jsonData);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    if(iCameraLinkSetView != null){
                        iCameraLinkSetView.getCameraLinkSuccess(jsonObject);
                    }
                }
                iCameraLinkSetView.showWaitDlgShow(false);
            }

            @Override
            public void onFailed(String s, int i, String s1, int i1) {
                if(iCameraLinkSetView != null){
                    iCameraLinkSetView.getCameraLinkFailed(s,i,s1,i1);
                }
                iCameraLinkSetView.showWaitDlgShow(false);

            }
        });

        devConfigInfo.setTimeOut(8000);//设置超时时间
        devConfigInfo.setJsonName("Ptz.GunBallLocate");
        devConfigInfo.setChnId(-1);
        devConfigInfo.setCmdId(1042);
        devConfigManager.setDevCmd(devConfigInfo);
    }

    /**
     * 保持相机联动开关
     * @param enable 相机联动开关使能
     */
    public void saveCameraLinkEnable(boolean enable){
        try {
            JSONArray jsonArray = (org.json.JSONArray) jsonObject.optJSONArray("Ptz.GunBallLocate");
            JSONObject jsonObject1 = (org.json.JSONObject) jsonArray.get(0);
            jsonObject1.put("Enable",enable);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Name", "Ptz.GunBallLocate");
            JSONArray jsonArray1 = new JSONArray();
            jsonArray1.put(jsonObject1);
            jsonObject.put("Ptz.GunBallLocate", jsonArray1);
            saveConfig(jsonObject);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void initCameraLink() {
        iCameraLinkSetView.showWaitDlgShow(iCameraLinkSetView.getContext().getString(R.string.camera_link_init_tips));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", "OPGunBallPtzLocateInit");
            JSONArray jsonArray = new JSONArray();
            JSONObject object = new JSONObject();
            jsonArray.put(object);
            jsonObject.put("OPGunBallPtzLocateInit", jsonArray);
            DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String s, int i, Object o) {
                    iCameraLinkSetView.showWaitDlgShow(false);

                }

                @Override
                public void onFailed(String s, int i, String s1, int i1) {
                    iCameraLinkSetView.showWaitDlgShow(false);
                    iCameraLinkSetView.initFailed();

                }
            });
            devConfigInfo.setJsonName("OPGunBallPtzLocateInit");
            devConfigInfo.setChnId(-1);
            devConfigInfo.setCmdId(3032);
            devConfigInfo.setTimeOut(60000);
            devConfigInfo.setJsonData(jsonObject.toString());
            devConfigManager.setDevCmd(devConfigInfo);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }



    }

    private void saveConfig(org.json.JSONObject jsonObject1) {
        iCameraLinkSetView.showWaitDlgShow(true);
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String s, int i, Object o) {
                iCameraLinkSetView.setSaveSuccess();
                iCameraLinkSetView.showWaitDlgShow(false);

            }

            @Override
            public void onFailed(String s, int i, String s1, int i1) {
                iCameraLinkSetView.showWaitDlgShow(false);

            }
        });
        devConfigInfo.setChnId(-1);
        devConfigInfo.setTimeOut(8000);//设置超时时间
        devConfigInfo.setJsonName("Ptz.GunBallLocate");
        devConfigInfo.setJsonData(jsonObject1.toString());
        devConfigInfo.setCmdId(1040);
        devConfigManager.setDevCmd(devConfigInfo);
    }
}
