package demo.xm.com.xmfunsdkdemo.ui.device.config.alarmconfig.presenter;

import com.alibaba.fastjson.JSON;
import com.lib.sdk.bean.AlarmInfoBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.HumanDetectionBean;
import com.lib.sdk.bean.JsonConfig;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;

import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.ui.device.config.DevConfigState;
import demo.xm.com.xmfunsdkdemo.ui.device.config.alarmconfig.listener.DevAlarmSetContract;

/**
 * 报警配置界面,包括移动侦测,视频遮挡,报警输出等
 * Created by jiangping on 2018-10-23.
 */
public class DevAlarmSetPresenter extends XMBasePresenter<DeviceManager> implements DevAlarmSetContract.IDevAlarmSetPresenter {
    /**
     * 两个作用：1.获取：处理视频丢失警报部分的json数据，并显示到控件上。2.保存：根据控件信息生成json格式数据以用来保存
     */
    private AlarmInfoBean mLoss;

    /**
     * 两个作用：1.获取：处理视频遮挡部分的json数据，并显示到控件上。2.保存：根据控件信息生成json格式数据以用来保存
     */
    private AlarmInfoBean mBlind;
    /**
     * 两个作用：1.获取：处理视频移动侦测部分的json数据，并显示到控件上。2.保存：根据控件信息生成json格式数据以用来保存
     */
    private AlarmInfoBean mMotion;
    private DevAlarmSetContract.IDevAlarmSetView iDevAlarmSetView;
    private DevConfigManager devConfigManager;
    /**
     * 是否支持人形检测
     */
    private boolean isSupportHumanDetect;
    public DevAlarmSetPresenter(DevAlarmSetContract.IDevAlarmSetView iDevAlarmSetView) {
        this.iDevAlarmSetView = iDevAlarmSetView;
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
    public void getDevAlarm() {
        getLossDetect();
        getMotionDetect();
        getBlindDetect();
        checkSupportHumanDetect();
        checkSupportAlarmVoiceTips();
        checkSupportAlarmVoiceTipsType();
    }

    /**
     * 检测是否支持人形检测
     */
    private void checkSupportHumanDetect() {
        DeviceManager.getInstance().getDevAbility(getDevId(), new DeviceManager.OnDevManagerListener() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, Object isSupport) {
                //isSupport-> ture表示支持，false表示不支持
                if (isSupport instanceof Boolean) {
                    isSupportHumanDetect = true;
                    iDevAlarmSetView.isSupportHumanDetectResult((Boolean) isSupport);
                } else {
                    iDevAlarmSetView.isSupportHumanDetectResult(false);
                }
            }

            /**
             * 失败回调
             *
             * @param devId    设备序列号
             * @param msgId    消息ID
             * @param jsonName
             * @param errorId  错误码
             */
            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                //获取失败，通过errorId分析具体原因
                iDevAlarmSetView.isSupportHumanDetectResult(false);
            }
        }, "AlarmFunction", "PEAInHumanPed");

    }

    /**
     * 检测报警铃声提示音是否支持
     */
    private void checkSupportAlarmVoiceTips() {
        DeviceManager.getInstance().getDevAbility(getDevId(), new DeviceManager.OnDevManagerListener() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, Object isSupport) {
                //isSupport-> ture表示支持，false表示不支持
                if (isSupport instanceof Boolean) {
                    iDevAlarmSetView.isSupportVoiceTipsResult((Boolean) isSupport);
                } else {
                    iDevAlarmSetView.isSupportVoiceTipsResult(false);
                }
            }

            /**
             * 失败回调
             *
             * @param devId    设备序列号
             * @param msgId    消息ID
             * @param jsonName
             * @param errorId  错误码
             */
            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                //获取失败，通过errorId分析具体原因
                iDevAlarmSetView.isSupportVoiceTipsResult(false);
            }
        }, "OtherFunction", "SupportAlarmVoiceTips");

    }

    /**
     * 检测是否支持自定义语音提示音
     */
    private void checkSupportAlarmVoiceTipsType() {
        DeviceManager.getInstance().getDevAbility(getDevId(), new DeviceManager.OnDevManagerListener() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, Object isSupport) {
                //isSupport-> ture表示支持，false表示不支持
                if (isSupport instanceof Boolean) {
                    iDevAlarmSetView.isSupportVoiceTipsTypeResult((Boolean) isSupport);
                } else {
                    iDevAlarmSetView.isSupportVoiceTipsTypeResult(false);
                }
            }

            /**
             * 失败回调
             *
             * @param devId    设备序列号
             * @param msgId    消息ID
             * @param jsonName
             * @param errorId  错误码
             */
            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                //获取失败，通过errorId分析具体原因
                iDevAlarmSetView.isSupportVoiceTipsTypeResult(false);
            }
        }, "OtherFunction", "SupportAlarmVoiceTipsType");

    }

    /**
     * 获取视频丢失警报数据
     */
    private void getLossDetect() {
        DevConfigInfo lossInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                if (result instanceof String) {
                    HandleConfigData handleConfigData = new HandleConfigData();
                    if (handleConfigData.getDataObj((String) result, AlarmInfoBean.class)) {
                        mLoss = (AlarmInfoBean) handleConfigData.getObj();
                    }
                } else {
                    mLoss = (AlarmInfoBean) result;
                }
                iDevAlarmSetView.updateUI(mLoss,
                        JsonConfig.DETECT_LOSSDETECT, DevConfigState.DEV_CONFIG_VIEW_VISIABLE);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevAlarmSetView.updateUI(null,
                        JsonConfig.DETECT_LOSSDETECT, DevConfigState.DEV_CONFIG_VIEW_INVISIABLE);
            }
        });

        lossInfo.setJsonName(JsonConfig.DETECT_LOSSDETECT);
        lossInfo.setChnId(0);  //通道号
        devConfigManager.getDevConfig(lossInfo);
    }

    @Override
    public void saveLossDetect() {
        if (mLoss == null) {
            return;
        }

        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                iDevAlarmSetView.saveLossResult(DevConfigState.DEV_CONFIG_UPDATE_SUCCESS);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevAlarmSetView.saveLossResult(DevConfigState.DEV_CONFIG_UPDATE_FAILED);
            }
        });
        devConfigInfo.setJsonName(JsonConfig.DETECT_LOSSDETECT);
        devConfigInfo.setChnId(0);

        HandleConfigData handleConfigData = new HandleConfigData();
        String jsonData = handleConfigData.getSendData(HandleConfigData.getFullName(JsonConfig.DETECT_LOSSDETECT, 0), "0x08", mLoss);
        devConfigInfo.setJsonData(jsonData);
        devConfigManager.setDevConfig(devConfigInfo);
    }

    /**
     * 获取移动侦测的数据
     */
    private void getMotionDetect() {
        DevConfigInfo motionInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                if (result instanceof String) {
                    HandleConfigData handleConfigData = new HandleConfigData();
                    if (handleConfigData.getDataObj((String) result, AlarmInfoBean.class)) {
                        mMotion = (AlarmInfoBean) handleConfigData.getObj();
                    }
                } else {
                    mMotion = (AlarmInfoBean) result;
                }
                iDevAlarmSetView.updateUI(mMotion,
                        JsonConfig.DETECT_MOTIONDETECT, DevConfigState.DEV_CONFIG_VIEW_VISIABLE);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevAlarmSetView.updateUI(null, JsonConfig.DETECT_MOTIONDETECT, DevConfigState.DEV_CONFIG_VIEW_INVISIABLE);
            }
        });

        motionInfo.setJsonName(JsonConfig.DETECT_MOTIONDETECT);
        motionInfo.setChnId(0);
        devConfigManager.getDevConfig(motionInfo);
    }

    @Override
    public void saveMotionDetect() {
        if (mMotion == null) {
            return;
        }

        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                iDevAlarmSetView.saveMotionResult(DevConfigState.DEV_CONFIG_UPDATE_SUCCESS);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevAlarmSetView.saveMotionResult(DevConfigState.DEV_CONFIG_UPDATE_FAILED);
            }
        });
        devConfigInfo.setJsonName(JsonConfig.DETECT_MOTIONDETECT);
        devConfigInfo.setChnId(0);
        HandleConfigData handleConfigData = new HandleConfigData();
        String jsonData = handleConfigData.getSendData(HandleConfigData.getFullName(JsonConfig.DETECT_MOTIONDETECT, 0), "0x08", mMotion);
        devConfigInfo.setJsonData(jsonData);
        devConfigManager.setDevConfig(devConfigInfo);
    }

    /**
     * 获取视频遮挡数据
     */
    private void getBlindDetect() {
        DevConfigInfo blindInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                if (result instanceof String) {
                    HandleConfigData handleConfigData = new HandleConfigData();
                    if (handleConfigData.getDataObj((String) result, AlarmInfoBean.class)) {
                        mBlind = (AlarmInfoBean) handleConfigData.getObj();
                    }
                } else {
                    mBlind = (AlarmInfoBean) result;
                }
                iDevAlarmSetView.updateUI(mBlind,
                        JsonConfig.DETECT_BLINDDETECT, DevConfigState.DEV_CONFIG_VIEW_VISIABLE);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevAlarmSetView.updateUI(null, JsonConfig.DETECT_BLINDDETECT, DevConfigState.DEV_CONFIG_VIEW_INVISIABLE);
            }
        });

        blindInfo.setJsonName(JsonConfig.DETECT_BLINDDETECT);
        blindInfo.setChnId(0);
        devConfigManager.getDevConfig(blindInfo);
    }

    @Override
    public void saveBlindDetect() {
        if (mBlind == null) {
            return;
        }

        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {
                iDevAlarmSetView.saveBlindResult(DevConfigState.DEV_CONFIG_UPDATE_SUCCESS);
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iDevAlarmSetView.saveBlindResult(DevConfigState.DEV_CONFIG_UPDATE_FAILED);
            }
        });
        devConfigInfo.setJsonName(JsonConfig.DETECT_BLINDDETECT);
        devConfigInfo.setChnId(0);
        HandleConfigData handleConfigData = new HandleConfigData();
        String jsonData = handleConfigData.getSendData(HandleConfigData.getFullName(JsonConfig.DETECT_BLINDDETECT, 0), "0x08", mBlind);
        devConfigInfo.setJsonData(jsonData);
        devConfigManager.setDevConfig(devConfigInfo);
    }


    public AlarmInfoBean getmLoss() {
        return mLoss;
    }

    public AlarmInfoBean getmBlind() {
        return mBlind;
    }

    public AlarmInfoBean getmMotion() {
        return mMotion;
    }

    public boolean isSupportHumanDetect() {
        return isSupportHumanDetect;
    }
}

