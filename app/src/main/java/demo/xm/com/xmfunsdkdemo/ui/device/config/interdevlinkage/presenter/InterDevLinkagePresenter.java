package demo.xm.com.xmfunsdkdemo.ui.device.config.interdevlinkage.presenter;

import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.manager.base.BaseManager;
import com.manager.db.DevDataCenter;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.shadow.DevShadowManager;
import com.manager.device.config.shadow.OnDevShadowManagerListener;
import com.manager.device.config.shadow.ShadowConfigEnum;
import com.xm.activity.base.XMBasePresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import demo.xm.com.xmfunsdkdemo.ui.device.config.interdevlinkage.listener.InterDevLinkageContract;

/**
 * 设备之间联动操作（比如带屏摇头机和门锁之间的联动）
 */
public class InterDevLinkagePresenter extends XMBasePresenter implements InterDevLinkageContract.IInterDevLinkagePresenter {
    private DevShadowManager devShadowManager;
    private List<String> needLinkDevList = new ArrayList<>();//需要绑定的设备列表
    private HashMap<String, Bundle> needLinkDevInfo = new HashMap<>();
    private AtomicInteger checkDevSupportLinkCount = new AtomicInteger();//查询设备是否支持联动基数
    private InterDevLinkageContract.IInterDevLinkageView iInterDevLinkageView;
    private String pin;//关联的PIN码
    private int bindAttr;//当前设备的绑定属性，0：无  1：主设备（摇头机）2：从设备（门锁）
    private String bindSn;//当前设备绑定的其他设备序列号前12位
    private boolean isBind;//当前设备是否已经绑定了其他设备
    private boolean isNeedBindOtherDev;//需要其他设备绑定当前设备

    @Override
    protected BaseManager getManager() {
        return null;
    }

    public InterDevLinkagePresenter(InterDevLinkageContract.IInterDevLinkageView iInterDevLinkageView) {
        this.iInterDevLinkageView = iInterDevLinkageView;
        devShadowManager = DevShadowManager.getInstance();
        devShadowManager.addDevShadowListener(onDevShadowManagerListener);
    }

    private OnDevShadowManagerListener onDevShadowManagerListener = new OnDevShadowManagerListener() {
        @Override
        public void onDevShadowConfigResult(String devId, String configData, int errorId) {
            checkDevSupportLinkCount.decrementAndGet();
            if (errorId >= 0 && configData != null) {
                //解析影子服务器返回的数据，如果有NetWork.LANLinkageBindInfo返回，表示支持联动
                HashMap hashMap = JSON.parseObject(configData, HashMap.class);
                JSONObject jsonObject = (JSONObject) hashMap.get("data");
                if (jsonObject != null && jsonObject.containsKey(ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName())) {
                    jsonObject = jsonObject.getJSONObject(ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
                    if (jsonObject == null) {
                        return;
                    }

                    Integer otherDevBindAttr = 0;
                    if (jsonObject.containsKey("BindAttr")) {
                        otherDevBindAttr = jsonObject.getInteger("BindAttr");
                    }

                    if (jsonObject.containsKey("BindList")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("BindList");
                        if (jsonArray != null && jsonArray.size() > 0) {
                            jsonObject = jsonArray.getJSONObject(0);
                            if (jsonObject != null && jsonObject.containsKey("SN")) {
                                String linkSn = jsonObject.getString("SN");
                                String linkPin = jsonObject.getString("PIN");

                                //如果影子服务器请求返回的序列号是当前操作的设备，就将绑定信息同步一下即可
                                if (Objects.equals(devId, getDevId())) {
                                    setPin(linkPin);
                                    setBindAttr(otherDevBindAttr);
                                    setBindSn(linkSn);
                                    setBind(!Objects.equals(linkSn, "NoBound"));
                                    return;
                                }

                                //如果绑定属性相同 就过滤掉
                                if (Objects.equals(otherDevBindAttr, bindAttr)) {
                                    return;
                                }

                                //如果未绑定的话，就显示出来
                                if (Objects.equals(linkSn, "NoBound")) {
                                    //主设备已经绑定了一个设备序列号，并且这个序列号和该设备序列号同一个需要显示出来，否则不显示
                                    if (!isBind || (isBind && Objects.equals(bindSn, devId.substring(0, 12)))) {
                                        isNeedBindOtherDev = true;
                                        dealWithDev(devId, otherDevBindAttr, linkSn, linkPin, isBind);
                                    }
                                } else if (!isBind && Objects.equals(linkSn, getDevId().substring(0, 12))) {
                                    //如果已经绑定过并且绑定的就是主设备，而且主设备没有绑定该设备，就需要显示出来继续绑定,并同步PIN码
                                    InterDevLinkagePresenter.this.pin = linkPin;
                                    dealWithDev(devId, otherDevBindAttr, linkSn, linkPin, isBind);
                                }
                            }
                        }
                    }
                }
            }

            if (checkDevSupportLinkCount.get() <= 0) {
                iInterDevLinkageView.onCheckDevSupportLinkResult(needLinkDevList, isBind);
            }
        }

        @Override
        public void onSetDevShadowConfigResult(String devId, boolean isSuccess, int errorId) {
        }

        @Override
        public void onLinkShadow(String s, int i) {

        }

        @Override
        public void onUnLinkShadow(String s, int i) {

        }

        @Override
        public void onLinkShadowDisconnect() {

        }
    };

    private void dealWithDev(String devId, int bindAttr, String linkSn, String linkPin, boolean isBind) {
        needLinkDevList.add(devId);
        Bundle bundle = new Bundle();
        bundle.putInt("bindAttr", bindAttr);
        bundle.putString("bindSn", linkSn);
        bundle.putString("pin", linkPin);
        bundle.putBoolean("isBind", isBind);
        needLinkDevInfo.put(devId, bundle);
    }

    @Override
    public void setDevId(String devId) {
        super.setDevId(devId);
        initData();
    }

    private void initData() {
        needLinkDevInfo.clear();
        needLinkDevList.clear();
        List<String> devList = DevDataCenter.getInstance().getDevList();
        for (String devId : devList) {
            checkDevSupportLinkCount.incrementAndGet();
            devShadowManager.getDevCfgsFromShadowService(devId, ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
        }
    }

    /**
     * 当前设备绑定其他设备
     *
     * @param linkDevId 其他设备序列号
     */
    @Override
    public void linkDev(String linkDevId) {
        if (!isBind && !StringUtils.isStringNULL(linkDevId)) {
            DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String devId, int operationType, Object result) {
                    if (isNeedBindOtherDev) {
                        associatedDev(linkDevId);
                    } else {
                        if (iInterDevLinkageView != null) {
                            iInterDevLinkageView.onInterDevLinkResult(true, 0);
                        }

                        needLinkDevInfo.remove(getDevId());
                        needLinkDevInfo.remove(linkDevId);
                        needLinkDevList.remove(getDevId());
                        needLinkDevList.remove(linkDevId);
                        devShadowManager.getDevCfgsFromShadowService(getDevId(), ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
                        devShadowManager.getDevCfgsFromShadowService(linkDevId, ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
                    }
                }

                @Override
                public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                    if (iInterDevLinkageView != null) {
                        iInterDevLinkageView.onInterDevLinkResult(false, errorId);
                    }
                }
            });


            HashMap bindInfoMap = new HashMap();
            List<HashMap> bodyList = new ArrayList<>();
            HashMap bodyMap = new HashMap();
            bodyMap.put("SN", linkDevId.substring(0, 12));//取序列号前12位
            bodyMap.put("PIN", pin);
            bodyList.add(bodyMap);
            bindInfoMap.put("BindList", bodyList);
            bindInfoMap.put("BindAttr", bindAttr);

            HashMap sendMap = new HashMap();
            sendMap.put("Name", ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
            sendMap.put(ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName(), bindInfoMap);
            sendMap.put("SessionID", "0x0000001b");
            devConfigInfo.setJsonName(ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
            devConfigInfo.setJsonData(new Gson().toJson(sendMap));
            DeviceManager.getInstance().getDevConfigManager(getDevId()).setDevConfig(devConfigInfo);
        } else {
            associatedDev(linkDevId);
        }
    }

    @Override
    public void unlinkDev() {
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                if (iInterDevLinkageView != null) {
                    iInterDevLinkageView.onInterDevUnlinkResult(true, 0);
                }

                needLinkDevInfo.remove(getDevId());
                needLinkDevInfo.remove(bindSn);
                needLinkDevList.remove(getDevId());
                needLinkDevList.remove(bindSn);
                devShadowManager.getDevCfgsFromShadowService(getDevId(), ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
                devShadowManager.getDevCfgsFromShadowService(bindSn, ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
                isBind = false;
                pin = null;
                bindAttr = 0;
                bindSn = null;
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                if (iInterDevLinkageView != null) {
                    iInterDevLinkageView.onInterDevUnlinkResult(false, errorId);
                }
            }
        });

        HashMap bindInfoMap = new HashMap();
        List<HashMap> bodyList = new ArrayList<>();
        HashMap bodyMap = new HashMap();
        bodyMap.put("SN", "NoBound");
        bodyMap.put("PIN", "");
        bodyList.add(bodyMap);
        bindInfoMap.put("BindList", bodyList);
        bindInfoMap.put("BindAttr", bindAttr);

        HashMap sendMap = new HashMap();
        sendMap.put("Name", ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
        sendMap.put(ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName(), bindInfoMap);
        sendMap.put("SessionID", "0x0000001b");
        devConfigInfo.setJsonName(ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
        devConfigInfo.setJsonData(new Gson().toJson(sendMap));
        DeviceManager.getInstance().getDevConfigManager(getDevId()).setDevConfig(devConfigInfo);
    }

    @Override
    public void release() {
        DevShadowManager.getInstance().removeDevShadowListener(onDevShadowManagerListener);
    }

    /**
     * 当前设备被其他设备绑定
     *
     * @param associatedDevId 其他设备序列号
     */
    private void associatedDev(String associatedDevId) {
        if (!StringUtils.isStringNULL(associatedDevId)) {
            DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String devId, int operationType, Object result) {
                    if (iInterDevLinkageView != null) {
                        iInterDevLinkageView.onInterDevLinkResult(true, 0);
                    }

                    needLinkDevInfo.remove(getDevId());
                    needLinkDevInfo.remove(associatedDevId);
                    needLinkDevList.remove(getDevId());
                    needLinkDevList.remove(associatedDevId);
                    devShadowManager.getDevCfgsFromShadowService(getDevId(), ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
                    devShadowManager.getDevCfgsFromShadowService(associatedDevId, ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
                }

                @Override
                public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                    if (iInterDevLinkageView != null) {
                        iInterDevLinkageView.onInterDevLinkResult(false, errorId);
                    }
                }
            });

            Bundle bundle = needLinkDevInfo.get(associatedDevId);
            int bindAttr = 0;
            if (bundle != null) {
                bindAttr = bundle.getInt("bindAttr");
            }

            HashMap bindInfoMap = new HashMap();
            List<HashMap> bodyList = new ArrayList<>();
            HashMap bodyMap = new HashMap();
            bodyMap.put("SN", getDevId().substring(0, 12));//序列号前12位
            bodyMap.put("PIN", pin);
            bodyList.add(bodyMap);
            bindInfoMap.put("BindList", bodyList);
            bindInfoMap.put("BindAttr", bindAttr);

            HashMap sendMap = new HashMap();
            sendMap.put("Name", ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
            sendMap.put(ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName(), bindInfoMap);
            sendMap.put("SessionID", "0x0000001b");
            devConfigInfo.setJsonName(ShadowConfigEnum.FunEnum.LAN_LINK_BIND_INFO.getFieldName());
            devConfigInfo.setJsonData(new Gson().toJson(sendMap));
            DeviceManager.getInstance().getDevConfigManager(associatedDevId).setDevConfig(devConfigInfo);
        }
    }

    public void setBindAttr(int bindAttr) {
        this.bindAttr = bindAttr;
    }

    public void setBind(boolean bind) {
        this.isBind = bind;
    }

    public void setPin(String pin) {
        this.pin = pin;
        //如果当前设备未绑定过其他设备，对应PIN码未生成，就需要创建一个新的
        if (StringUtils.isStringNULL(pin)) {
            //生成随机数
            Random random = new Random();
            // 生成范围在 520 到 1520 之间的随机数
            int randomNumber = random.nextInt(1001) + 520;
            this.pin = FunSDK.DevMD5Encrypt(String.valueOf(randomNumber)).substring(0, 6);
        }
    }

    public void setBindSn(String bindSn) {
        this.bindSn = bindSn;
    }
}
