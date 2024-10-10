package demo.xm.com.xmfunsdkdemo.ui.device.aov.presenter;

import android.os.Message;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.basic.G;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.sdk.bean.StringUtils;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.xm.activity.base.XMBasePresenter;

import java.util.ArrayList;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.ui.device.aov.listener.SetFPSContract;

public class SetFPSPresenter extends XMBasePresenter<DeviceManager> implements SetFPSContract.ISetFPSPresenter{

    private DevConfigManager mDevConfigManager;


    private List<String> fpsList = new ArrayList<>();
    private List<Integer> recordLatchList = new ArrayList<>();
    private List<Integer> aovAlarmTimeIntervalList = new ArrayList<>(); // 抑制时间列表



    private SetFPSContract.ISetFPSView iSetFPSView;


    private String curFps;
    private String mode;

    private int curRecordLatch;
    private int curInterval;


    public SetFPSPresenter(SetFPSContract.ISetFPSView iSetFPSView){
        this.iSetFPSView = iSetFPSView;
    }

    @Override
    public void setDevId(String devId) {
        mDevConfigManager = manager.getDevConfigManager(devId);
        super.setDevId(devId);
    }

    private void getDataFail(){
        iSetFPSView.onHideWaitDialog();
        Toast.makeText(iSetFPSView.getActivity().getApplicationContext(), FunSDK.TS("no_data"), Toast.LENGTH_SHORT).show();

    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }


    public int OnFunSDKResult(Message message, MsgContent msgContent) {
//        switch (message.what) {
//            case EUIMSG.DEV_GET_JSON:
//                if (message.arg1 >= 0) {
//                    if (StringUtils.contrast(msgContent.str, "Ability.AovAbility")) {
//                        iSetFPSView.onHideWaitDialog();
//                        JSONObject jsonObject = JSON.parseObject(G.ToString(msgContent.pData));
//                        JSONObject jsonObject1 = jsonObject.getJSONObject("Ability.AovAbility");
//                        JSONArray fpsArray = jsonObject1.getJSONArray("VideoFps");
//                        for (int i = 0; i < fpsArray.size(); i++) {
//                            fpsList.add(fpsArray.get(i).toString());
//                        }
//                        JSONArray recordLatchArray = jsonObject1.getJSONArray("RecordLatch");
//                        for (int i = 0; i < recordLatchArray.size(); i++) {
//                            recordLatchList.add(Integer.parseInt(recordLatchArray.get(i).toString()));
//                        }
//                        if(jsonObject1.containsKey("AlarmHoldTime")){
//                            JSONObject alarmHoldTimeObj = jsonObject1.getJSONObject("AlarmHoldTime");
//                            JSONArray holdTimeList = alarmHoldTimeObj.getJSONArray("HoldTimeList");
//                            for (int i = 0; i < holdTimeList.size(); i++) {
//                                aovAlarmTimeIntervalList.add(Integer.parseInt(holdTimeList.get(i).toString()));
//                            }
//                        }
//
//                        iSetFPSView.showAovAbility();
//
//                    }
//                } else {
//                    ErrorManager.Instance().ShowError(message.what, message.arg1, msgContent.str, true);
//                }
//
//        }
        return 0;
    }

    @Override
    public void getAovAbility() {
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                iSetFPSView.onHideWaitDialog();
                JSONObject jsonObject = JSON.parseObject(result);
                JSONObject jsonObject1 = jsonObject.getJSONObject("Ability.AovAbility");
                JSONArray fpsArray = jsonObject1.getJSONArray("VideoFps");
                for (int i = 0; i < fpsArray.size(); i++) {
                    fpsList.add(fpsArray.get(i).toString());
                }
                JSONArray recordLatchArray = jsonObject1.getJSONArray("RecordLatch");
                for (int i = 0; i < recordLatchArray.size(); i++) {
                    recordLatchList.add(Integer.parseInt(recordLatchArray.get(i).toString()));
                }
                if(jsonObject1.containsKey("AlarmHoldTime")){
                    JSONObject alarmHoldTimeObj = jsonObject1.getJSONObject("AlarmHoldTime");
                    JSONArray holdTimeList = alarmHoldTimeObj.getJSONArray("HoldTimeList");
                    for (int i = 0; i < holdTimeList.size(); i++) {
                        aovAlarmTimeIntervalList.add(Integer.parseInt(holdTimeList.get(i).toString()));
                    }
                }

                iSetFPSView.showAovAbility();
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName("Ability.AovAbility");
        mainConfigInfo.setChnId(-1);
        mDevConfigManager.getDevConfig(mainConfigInfo);



    }

    @Override
    public List<Integer> getAovAlarmTimeIntervalList() {
        return aovAlarmTimeIntervalList;
    }

    @Override
    public List<String> getFpsList() {
        return fpsList;
    }

    @Override
    public List<Integer> getRecordLatchList() {
        return recordLatchList;
    }

    @Override
    public void setCurFps(String fps) {
        curFps = fps;
    }

    @Override
    public String getCurFps() {
        return curFps;
    }

    @Override
    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String getMode() {
        return mode;
    }

    @Override
    public void setCurRecordLatch(int recordLatch) {
        curRecordLatch = recordLatch;
    }

    @Override
    public int getCurRecordLatch() {
        return curRecordLatch;
    }

    @Override
    public void setCurInterval(int interval) {
        curInterval = interval;
    }

    @Override
    public int getCurInterval() {
        return curInterval;
    }

}
