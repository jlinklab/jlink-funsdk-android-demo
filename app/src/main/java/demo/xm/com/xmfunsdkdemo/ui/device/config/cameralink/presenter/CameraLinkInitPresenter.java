package demo.xm.com.xmfunsdkdemo.ui.device.config.cameralink.presenter;

import com.manager.base.BaseManager;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import demo.xm.com.xmfunsdkdemo.ui.device.config.cameralink.listener.CameraLinkInitContract;

public class CameraLinkInitPresenter extends XMBasePresenter<DeviceManager>  implements CameraLinkInitContract.ICameraLinkInitSetPresenter {
    private CameraLinkInitContract.ICameraLinkInitView iCameraLinkInitView;
    private DevConfigManager devConfigManager;

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }
    public CameraLinkInitPresenter(CameraLinkInitContract.ICameraLinkInitView iCameraLinkInitView){
        this.iCameraLinkInitView = iCameraLinkInitView;

    }

    @Override
    public void setDevId(String devId) {
        devConfigManager = manager.getDevConfigManager(devId);
        super.setDevId(devId);
        initCameraLink();
    }

    private void initCameraLink() {
        iCameraLinkInitView.showWaitDlgShow(true);
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
                    iCameraLinkInitView.showWaitDlgShow(false);

                }

                @Override
                public void onFailed(String s, int i, String s1, int i1) {
                    iCameraLinkInitView.showWaitDlgShow(false);
                    iCameraLinkInitView.initFailed();

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
}
