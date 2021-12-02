package demo.xm.com.xmfunsdkdemo.ui.device.config.simpleconfig.presenter;

import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.basic.G;
import com.lib.MsgContent;
import com.lib.sdk.bean.FbExtraStateCtrlBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.NetworkPmsBean;
import com.lib.sdk.bean.StringUtils;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import java.util.HashMap;

import demo.xm.com.xmfunsdkdemo.ui.device.config.simpleconfig.listener.DevSimpleConfigContract;

/**
 * hws
 */
public class DevSimpleConfigPresenter extends XMBasePresenter<DeviceManager> implements DevSimpleConfigContract.IDevSimpleConfigPresenter {
    private DevSimpleConfigContract.IDevSimpleConfigView iDevSimpleConfig;
    private DevConfigManager devConfigManager;
    private HashMap<String, Object> configs;
    private String sendJson = "";

    public DevSimpleConfigPresenter(DevSimpleConfigContract.IDevSimpleConfigView iDevSimpleConfig) {
        this.iDevSimpleConfig = iDevSimpleConfig;
        configs = new HashMap<>();
    }

    @Override
    public void setDevId(String devId) {
        devConfigManager = manager.getDevConfigManager(devId);
        super.setDevId(devId);
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    @Override
    public void getConfig(String jsonName, int chnId) {
        checkFunSupport(jsonName, chnId);
    }

    /**
     * 获取能力集，有些配置需要通过能力集判断显示还是隐藏
     *
     * @param jsonName
     * @param chnId
     */
    private void checkFunSupport(final String jsonName, final int chnId) {
        switch (jsonName) {
            case JsonConfig.CFG_FbExtraStateCtrl:
                /**
                 * 状态灯的能力集是SupportStatusLed
                 * 提示音的能力集是SupportCloseVoiceTip
                 *下面是根据状态灯能力集判断是否要去获取FbExtraStateCtrl配置，FbExtraStateCtrl配置包括了状态灯开关和提示音开关
                 */
                manager.getDevAbility(getDevId(), new DeviceManager.OnDevManagerListener<Boolean>() {
                    @Override
                    public void onSuccess(String s, int i, Boolean result) {
                        if (!result) {
                            iDevSimpleConfig.onReceiveDataResult("该配置不支持", "该配置不支持");
                        } else {
                            dealWithCheckFunSupport(jsonName, chnId);
                        }
                    }

                    @Override
                    public void onFailed(String s, int i, String s1, int i1) {

                    }
                }, "OtherFunction", "SupportStatusLed");
                break;
            default:
                dealWithCheckFunSupport(jsonName, chnId);
                break;
        }
    }

    private void dealWithCheckFunSupport(String jsonName, int chnId) {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DevConfigManager.OnDevConfigResultListener() {

            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                if (result instanceof String) {
                    iDevSimpleConfig.onReceiveDataResult("获取成功", (String) result);
                } else {
                    iDevSimpleConfig.onReceiveDataResult("获取成功", JSON.toJSONString(result));
                }


            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                if (errorId == -11406) {
                    iDevSimpleConfig.onReceiveDataResult("该配置设备不支持或者不是配置类型", "该配置设备不支持或者不是配置类型");
                } else {
                    iDevSimpleConfig.onReceiveDataResult("数据获取失败：" + errorId, "数据获取失败：" + errorId);
                }

            }

            @Override
            public void onFunSDKResult(Message message, MsgContent msgContent) {
                if (message.arg1 < 0) {
                    return;
                }

                HandleConfigData handleConfigData = new HandleConfigData();
                String jsonData = G.ToStringJson(msgContent.pData);
                switch (msgContent.str) {
                    case JsonConfig.CFG_PMS://网络服务-推送
                        if (handleConfigData.getDataObj(jsonData, NetworkPmsBean.class)) {
                            configs.put(msgContent.str, handleConfigData.getObj());
                        }
                        break;
                    case JsonConfig.CFG_FbExtraStateCtrl://设备状态灯和提示音
                        if (handleConfigData.getDataObj(jsonData, FbExtraStateCtrlBean.class)) {
                            configs.put(msgContent.str, handleConfigData.getObj());
                        }
                        break;
                    default:
                        configs.put(msgContent.str, jsonData);
                        break;
                }
            }
        });

        devConfigInfo.setJsonName(jsonName);
        devConfigInfo.setChnId(chnId);
        devConfigManager.getDevConfig(devConfigInfo);
    }

    @Override
    public void saveConfig(String jsonName, int chnId, String jsonData) {
        if (!configs.containsKey(jsonName)) {
            iDevSimpleConfig.onReceiveDataResult("请先获取配置，再进行保存", "请先获取配置，再进行保存");
            return;
        }

        /**
         * 将之前获取到的alarmInfoBean对象进行设置，然后进行序列化，人形检测是需要通道号的，所以要对JsonName特殊处理
         * 用到了HandleConfigData.getFullName
         */
        switch (jsonName) {
            case JsonConfig.CFG_PMS://网络服务-推送
                NetworkPmsBean networkPmsBean = (NetworkPmsBean) configs.get(jsonName);
                sendJson = HandleConfigData.getSendData(HandleConfigData.getFullName(jsonName, chnId),
                        "0x08", networkPmsBean);
                break;
            case JsonConfig.CFG_FbExtraStateCtrl://设备状态灯和提示音
                FbExtraStateCtrlBean fbExtraStateCtrlBean = (FbExtraStateCtrlBean) configs.get(jsonName);
                sendJson = HandleConfigData.getSendData(HandleConfigData.getFullName(jsonName, chnId),
                        "0x08", fbExtraStateCtrlBean);
                break;
            default:
                sendJson = jsonData;
                break;
        }

        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DevConfigManager.OnDevConfigResultListener() {
            @Override
            public void onFunSDKResult(Message message, MsgContent msgContent) {
            }

            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                iDevSimpleConfig.onReceiveDataResult("保存成功", sendJson);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevSimpleConfig.onReceiveDataResult("保存数据失败：" + errorId, "保存数据失败：" + errorId);
            }
        });

        devConfigInfo.setJsonName(jsonName);
        /**
         * 人形检测配置是需要传入通道号的
         */
        devConfigInfo.setChnId(chnId);

        devConfigInfo.setJsonData(sendJson);
        devConfigManager.setDevConfig(devConfigInfo);
    }

    @Override
    public void cmdConfig(String jsonName, int cmdId, int chnId, String jsonData) {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DevConfigManager.OnDevConfigResultListener() {
            @Override
            public void onFunSDKResult(Message message, MsgContent msgContent) {
            }

            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                if (result instanceof String) {
                    iDevSimpleConfig.onReceiveDataResult("", result + "");
                } else {
                    iDevSimpleConfig.onReceiveDataResult("", JSON.toJSONString(result));
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevSimpleConfig.onReceiveDataResult("", "发送成功失败：" + errorId);
            }
        });


        devConfigInfo.setJsonName(jsonName);
        /**
         * 人形检测配置是需要传入通道号的
         */
        devConfigInfo.setChnId(chnId);
        devConfigInfo.setCmdId(cmdId);

        if (StringUtils.isStringNULL(jsonData)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Name", jsonName);
            jsonObject.put("SessionID", "0x08");
            jsonData = jsonObject.toJSONString();
        }

        devConfigInfo.setJsonData(jsonData);
        devConfigManager.setDevCmd(devConfigInfo);
    }
}

