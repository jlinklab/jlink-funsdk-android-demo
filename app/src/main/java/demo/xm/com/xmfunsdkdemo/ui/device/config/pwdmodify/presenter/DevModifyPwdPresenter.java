package demo.xm.com.xmfunsdkdemo.ui.device.config.pwdmodify.presenter;

import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.basic.G;
import com.lib.MsgContent;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.device.config.pwdmodify.listener.DevModifyPwdContract;

/**
 * 密码修改界面,为了保护隐私,可以更改设备的访问密码
 * Created by jiangping on 2018-10-23.
 */
public class DevModifyPwdPresenter extends XMBasePresenter<DeviceManager> implements DevModifyPwdContract.IDevModifyPwdPresenter {

    private DevModifyPwdContract.IDevModifyPwdView iDevModifyPwdView;
    private DevConfigManager devConfigManager;

    public DevModifyPwdPresenter(DevModifyPwdContract.IDevModifyPwdView iDevModifyPwdView) {
        this.iDevModifyPwdView = iDevModifyPwdView;
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        devConfigManager = DevConfigManager.create(devId);
        getDevUserName();
    }

    @Override
    public void modifyDevPwd(String oldPwd, String newPwd) {
        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(getDevId());
        if (xmDevInfo != null) {

            manager.modifyDevPwd(getDevId(), xmDevInfo.getDevUserName(), oldPwd, newPwd, new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String s, int msgId, Object o) {
                    iDevModifyPwdView.onChangePwdResult(0);
                    XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(getDevId());
                    DeviceManager.getInstance().setLocalDevUserPwd(getDevId(),xmDevInfo.getDevUserName(),newPwd);
                    DeviceManager.getInstance().logoutDev(getDevId());
                }

                @Override
                public void onFailed(String s, int i, String s1, int errorId) {
                    iDevModifyPwdView.onChangePwdResult(errorId);
                }
            });
        }
    }


    JSONObject devUserInfo;
    String oldName;

    /**
     * 从设备端获取设备用户名
     */
    public void getDevUserName() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DevConfigManager.OnDevConfigResultListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                JSONObject jsonObject = JSON.parseObject((String) result);
                JSONArray users = (JSONArray) jsonObject.get("Users");
                devUserInfo = (JSONObject) users.get(0);
                oldName = devUserInfo.getString("Name");
                iDevModifyPwdView.onGetDevUserNameResult(oldName);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {

            }

            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {

            }
        });

        devConfigInfo.setJsonName("GetAllUser");
        devConfigInfo.setCmdId(1472);
        devConfigInfo.setChnId(-1);
        devConfigManager.setDevCmd(devConfigInfo);
    }

    public void modifyDevUserName(String oldUserName, String newUserName) {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DevConfigManager.OnDevConfigResultListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                iDevModifyPwdView.onChangeUserNameResult(0);
                XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(getDevId());
                DeviceManager.getInstance().setLocalDevUserPwd(getDevId(),newUserName,xmDevInfo.getDevPassword());
                DeviceManager.getInstance().logoutDev(getDevId());

            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevModifyPwdView.onChangeUserNameResult(errorId);
            }

            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {

            }
        });

        JSONObject json = new JSONObject();
        if (devUserInfo != null) {
            devUserInfo.put("Name", newUserName);
            try {
                json.put("Name", "ModifyUser");
                json.put("UserName", oldUserName);
                json.put("User", devUserInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        devConfigInfo.setJsonName("ModifyUser");
        devConfigInfo.setCmdId(1484);
        devConfigInfo.setChnId(-1);
        devConfigInfo.setJsonData(json.toJSONString());
        devConfigManager.setDevCmd(devConfigInfo);
    }
}

