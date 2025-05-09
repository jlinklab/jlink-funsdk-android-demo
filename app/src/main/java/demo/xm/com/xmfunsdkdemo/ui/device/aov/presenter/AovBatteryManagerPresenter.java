package demo.xm.com.xmfunsdkdemo.ui.device.aov.presenter;

import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.lib.AS.AlarmService;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.sdk.bean.StringUtils;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.manager.device.idr.IDRDevBatteryManager;
import com.manager.device.idr.IDRManager;
import com.xm.activity.base.XMBasePresenter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import demo.xm.com.xmfunsdkdemo.ui.device.aov.listener.AovBatteryManagerContract;


public class AovBatteryManagerPresenter extends XMBasePresenter<DeviceManager> implements AovBatteryManagerContract.IAovBatteryManagerPresenter, IFunSDKResult {

    private IDRManager model;
    private int minElectr;
    private int maxElectr;
    private int curElectr;
    int userId = 0;
    private List<Entry> batteryEntry = new ArrayList<>();
    private List<Entry> signalEntry = new ArrayList<>();
    private List<JSONObject> todayBatteryList = new ArrayList<>();
    private List<JSONObject> oneWeekBatteryList = new ArrayList<>();


    private boolean isSearchToday = true;
    private int curBattery = -1;
    private int todayAlarmTime = 0;
    private int oneWeekAlarmTime = 0;
    private boolean isCharging = false;


    private JSONArray realViewTimeArray = new JSONArray(); // 预览时间数组
    private JSONArray wakeUpTimeArray = new JSONArray(); //
    // 创建一个 AtomicInteger 计数器，用于跟踪请求的数量
    private Calendar toDayStCalendar = Calendar.getInstance();

    private Calendar onWeekStCalendar = Calendar.getInstance();
    private boolean isFirstGetLog = false;
    private boolean haveLastListData = false;

    private JSONObject electrModeJson;

    private Calendar startCalendar;
    private Calendar endCalendar;

    public Calendar getToDayStCalendar() {
        return toDayStCalendar;
    }

    public Calendar getOnWeekStCalendar() {
        return onWeekStCalendar;
    }


    public Calendar getEndCalendar() {
        return endCalendar;
    }

    private DevConfigManager mDevConfigManager;


    private AovBatteryManagerContract.IAovBatteryManagerView iAovBatteryManagerView;

    public AovBatteryManagerPresenter(AovBatteryManagerContract.IAovBatteryManagerView iAovBatteryManagerView){
        this.iAovBatteryManagerView = iAovBatteryManagerView;
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



    private IDRDevBatteryManager.OnBatteryLevelListener mOnBatteryLevelListener =
            new IDRDevBatteryManager.OnBatteryLevelListener() {
                @Override
                public void onBatteryLevel(int devStorageStatus, int electable, int level, int percent) {
                    boolean isCharging = electable == 1;
                    curBattery = percent;
                    iAovBatteryManagerView.showPowerState(isCharging,percent);
                }
            };




    public void saveLowElectrMode(int process){
        if (electrModeJson != null) {
            iAovBatteryManagerView.onShowWaitDialog();
            electrModeJson.put("PowerThreshold", process + minElectr);
            JSONObject sendObj = new JSONObject();
            sendObj.put("Name", "Dev.LowElectrMode");
            sendObj.put("Dev.LowElectrMode", electrModeJson);
            sendObj.put("SessionID", "0x01");
            Log.e("dzc", sendObj.toJSONString());



            DevConfigInfo devConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
                @Override
                public void onSuccess(String devId, int msgId, Object result) {
                    iAovBatteryManagerView.onHideWaitDialog();
                    Toast.makeText(iAovBatteryManagerView.getActivity(), FunSDK.TS("Save_Success"), Toast.LENGTH_SHORT).show();
                    iAovBatteryManagerView.showPowerMode(electrModeJson.getIntValue("PowerThreshold"));
                }

                @Override
                public void onFailed(String devId, int msgId, String s1, int errorId) {
                    iAovBatteryManagerView.onHideWaitDialog();
                    Toast.makeText(iAovBatteryManagerView.getActivity().getApplicationContext(),
                            FunSDK.TS("Save_Failed"), Toast.LENGTH_SHORT).show();

                }
            });
            devConfigInfo.setJsonName("Dev.LowElectrMode");
            devConfigInfo.setChnId(-1);
            devConfigInfo.setJsonData(sendObj.toJSONString());
            mDevConfigManager.setDevConfig(devConfigInfo);
        }
    }

    public void setBatteryData(int curBattery,boolean isCharging) {
        this.curBattery = curBattery;
        this.isCharging = isCharging;

    }


    private void getAovAbility() {
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                try {
                    iAovBatteryManagerView.onHideWaitDialog();
                    JSONObject jsonObject = JSON.parseObject(result);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("Ability.AovAbility");
                    minElectr = jsonObject1.getIntValue("LowElectrMin");
                    maxElectr = jsonObject1.getIntValue("LowElectrMax");
                    iAovBatteryManagerView.showBatteryLevel(maxElectr, minElectr,curElectr);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iAovBatteryManagerView.onHideWaitDialog();
                Toast.makeText(iAovBatteryManagerView.getActivity().getApplicationContext(), FunSDK.TS("no_data"), Toast.LENGTH_SHORT).show();
            }
        });
        mainConfigInfo.setJsonName("Ability.AovAbility");
        mainConfigInfo.setChnId(-1);
        mDevConfigManager.getDevConfig(mainConfigInfo);



    }
    public void initData() {
        
        iAovBatteryManagerView.onShowWaitDialog();
        userId = FunSDK.RegUser(this);
        model = new IDRManager(iAovBatteryManagerView.getActivity(), getDevId());
        model.reReceiveBatteryInfo(mOnBatteryLevelListener);

        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                if (!StringUtils.isStringNULL(result)) {
                    JSONObject jsonObject = new Gson().fromJson(result, JSONObject.class);
                    electrModeJson = jsonObject.getJSONObject("Dev.LowElectrMode");
                    curElectr = electrModeJson.getIntValue("PowerThreshold");
                    iAovBatteryManagerView.showPowerMode(electrModeJson.getIntValue("PowerThreshold"));


                    getAovAbility();

                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                Toast.makeText(iAovBatteryManagerView.getActivity().getApplicationContext(), FunSDK.TS("no_data"), Toast.LENGTH_SHORT).show();
            }
        });
        mainConfigInfo.setJsonName("Dev.LowElectrMode");
        mainConfigInfo.setChnId(-1);
        mDevConfigManager.getDevConfig(mainConfigInfo);



        endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        endCalendar.set(Calendar.MILLISECOND, 59);
        startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        toDayStCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toDayStCalendar.set(Calendar.MINUTE, 0);
        toDayStCalendar.set(Calendar.SECOND, 0);
        toDayStCalendar.set(Calendar.MILLISECOND, 0);

        onWeekStCalendar.set(Calendar.HOUR_OF_DAY, 0);
        onWeekStCalendar.set(Calendar.MINUTE, 0);
        onWeekStCalendar.set(Calendar.SECOND, 0);
        onWeekStCalendar.set(Calendar.MILLISECOND, 0);
        onWeekStCalendar.add(Calendar.DAY_OF_YEAR, -6);
        initChartData(todayBatteryList,toDayStCalendar);
        initDevLog(startCalendar);


        iAovBatteryManagerView.showPowerState(isCharging,curBattery);
        getStorageInfoCountString();
        getPreViewTimeAndWakeTime();



    }

    /**
     * 获取唤醒次数获取预览时间次数
     */

    public void getPreViewTimeAndWakeTime() {


        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener<String>() {
            @Override
            public void onSuccess(String devId, int msgId, String result) {
                try {
                    JSONObject jsonObject = JSON.parseObject(result);
                    JSONObject data = jsonObject.getJSONObject("System.LowPowerWorkTime");
                    realViewTimeArray = data.getJSONArray("RealViewTime");
                    wakeUpTimeArray = data.getJSONArray("WakeupTime");
                    getTodayWakePreViewTime();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                Toast.makeText(iAovBatteryManagerView.getActivity().getApplicationContext(), FunSDK.TS("no_data"), Toast.LENGTH_SHORT).show();
            }
        });
        mainConfigInfo.setJsonName("System.LowPowerWorkTime");
        mainConfigInfo.setChnId(-1);
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }

    /**
     * 显示云事件消息条数
     */
    public void getStorageInfoCountString() {
        startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg","get_storage_count");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObject.put("st",sdf.format(startCalendar.getTime()));
        jsonObject.put("et",sdf.format(endCalendar.getTime()));
        jsonObject.put("type","MSG");
        JSONArray jsonArray = new JSONArray();
        JSONObject snInfo = new JSONObject();
        snInfo.put("sn",getDevId());
        snInfo.put("ch",-1);
        jsonArray.add(snInfo);
        jsonObject.put("snlist",jsonArray);
        AlarmService.GetStorageInfoCount(userId,jsonObject.toJSONString(),0); // 请求当天的数据
        startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        startCalendar.add(Calendar.DAY_OF_YEAR, -6);
        jsonObject.put("st",sdf.format(startCalendar.getTime()));
        AlarmService.GetStorageInfoCount(userId,jsonObject.toJSONString(),1);  // 请求一个礼拜的数据
    }

    public void initDevLog(Calendar startCalendar) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Url", "/api/pub/v1/data");
        JSONObject jsonObject1 = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        jsonObject1.put("endTime", sdf.format(endCalendar.getTime()));
        jsonObject1.put("id", getDevId());
        jsonObject1.put("page", 1);
        jsonObject1.put("size", 10000);
        jsonObject1.put("startTime", sdf.format(startCalendar.getTime()));
        jsonObject1.put("subtype", null);
        jsonObject1.put("timezoneMin", 480);
        jsonObject1.put("type", "devicelog");
        jsonObject1.put("isAddLastList",1);
        jsonObject.put("ReqJson", jsonObject1);
        FunSDK.SysGetLogs(userId, jsonObject.toJSONString(), 40000, 0); // 请求今天的

    }
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        switch (message.what) {
            case EUIMSG.SYS_SERVICE_GET_LOGS: {

                try {
                    if (!StringUtils.isStringNULL(msgContent.str) ) {
                        if(!isFirstGetLog){
                            initBatteryList(todayBatteryList,msgContent.str);
                            initChartData(todayBatteryList,toDayStCalendar);
                            isFirstGetLog = true;

                            initDevLog(onWeekStCalendar);
                        }else {
                            initBatteryList(oneWeekBatteryList,msgContent.str);
                            if(!isSearchToday){
                                initChartData(oneWeekBatteryList,onWeekStCalendar);
                            }
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(iAovBatteryManagerView.getActivity(),FunSDK.TS("TR_Data_Parsing_Failed"),Toast.LENGTH_SHORT).show();
                    iAovBatteryManagerView.getActivity().finish();
                }

                break;
            }
            case EUIMSG.AS_GET_STORAGE_INFO_COUNT:
                try {
                    if(!StringUtils.isStringNULL(msgContent.str) ){
                        if(msgContent.seq == 0){
                            todayAlarmTime = initAlarmTime(msgContent.str);
                            iAovBatteryManagerView.showAlarmNum(todayAlarmTime + "");

                        }else {
                            oneWeekAlarmTime = initAlarmTime(msgContent.str);
                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(iAovBatteryManagerView.getActivity(),FunSDK.TS("TR_Data_Parsing_Failed"), Toast.LENGTH_SHORT).show();
                    iAovBatteryManagerView.getActivity().finish();

                }
                break;
        }
        return 0;
    }


    private int initAlarmTime(String str){
        int alarmTime = 0;
        try {
            JSONObject jsonObject = JSONObject.parseObject(str);
            JSONArray dtArray = jsonObject.getJSONArray("dt");
            for (int i = 0; i < dtArray.size(); i++) {
                JSONObject data = (JSONObject) dtArray.get(i);
                String sn = data.getString("sn");

                if(StringUtils.contrast(sn,getDevId())){
                    int total = 0;
                    JSONArray num = data.getJSONArray("numlist");
                    for (int i1 = 0; i1 < num.size(); i1++) {
                        JSONObject countObject = (JSONObject) num.get(i1);
                        String count = countObject.getString("count");
                        total = total + Integer.parseInt(count);
                    }
                    alarmTime = total;

                }


            }
        }catch (Exception e){
            e.printStackTrace();
            return  0;
        }
        return alarmTime;

    }

    private void initBatteryList(List<JSONObject> batteryList,String str){
        batteryList.clear();
        JSONObject jsonObject = (JSONObject) JSONObject.parse(str);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray devLogInfo = data.getJSONArray("list");
        if(data.containsKey("lastList")){
            JSONArray devLogLastInfo = data.getJSONArray("lastList");
            for (int i = 0; i < devLogLastInfo.size(); i++) {
                JSONObject item = (JSONObject) devLogLastInfo.get(i);
                String logInfo = item.getString("logInfo");
                if (logInfo.contains("bl")) {
                    batteryList.add(item);
                    haveLastListData = true;
                }
            }
        }
        for (int i = 0; i < devLogInfo.size(); i++) {
            JSONObject item = (JSONObject) devLogInfo.get(i);
            String logInfo = item.getString("logInfo");
            if (logInfo.contains("bl")) {
                batteryList.add(item);
            }
        }

    }

    /**
     * 转换时间格式分钟转换小时
     * @param
     * @return
     */
    private String secToHour(int min){
        if(min < 60){
            return min + FunSDK.TS("s");
        }else if(min >= 60 && min <3600 ){
            double sec = (float) (min / 60.0);
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(sec) + FunSDK.TS("sMin");
        }else if(min >= 3600 && min < 3600*24){
            double hour = (float) ((float) min/3600.0);
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(hour)+ FunSDK.TS("sHour");
        }else {
            double day = (float) ((float) min/86400.0);
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(day)+ FunSDK.TS("day");
        }

    }

    public void initTodayChartData(){
        initChartData(todayBatteryList,toDayStCalendar);
    }


    public void initOneWeekData(){
        initChartData(oneWeekBatteryList,onWeekStCalendar);
    }


    public void initChartData(List<JSONObject> batteryList,Calendar startCalendar) {
        batteryEntry.clear();
        signalEntry.clear();
        try {
            for (int i = 0; i < batteryList.size(); i++) {
                JSONObject jsonObject = batteryList.get(i);
                String utcTime = jsonObject.getString("utcTime");
                String logInfo = jsonObject.getString("logInfo");
                JSONObject logJson = JSONObject.parseObject(logInfo);
                if (logJson.containsKey("bl")) {
                    if(i == 0 && haveLastListData){
                        int time = 0;
                        batteryEntry.add(new Entry(time, logJson.getIntValue("bl")));
                    }else {
                        int time = (int) (parseTimestamp(utcTime) - startCalendar.getTimeInMillis());
                        batteryEntry.add(new Entry(time, logJson.getIntValue("bl")));
                    }

                }
                if (logJson.containsKey("ss4g")) {
                    if(i == 0 && haveLastListData){
                        int time = 0;
                        float scale = (float) (100 / 3.0);
                        signalEntry.add(new Entry(time, logJson.getIntValue("ss4g") * scale));
                    }
                    int time = (int) (parseTimestamp(utcTime) - startCalendar.getTimeInMillis());
                    float scale = (float) (100 / 3.0);
                    signalEntry.add(new Entry(time, logJson.getIntValue("ss4g") * scale));

                }


            }
            Collections.sort(batteryEntry, new EntryComparator());
            Collections.sort(signalEntry, new EntryComparator());
            iAovBatteryManagerView.showChartData(isSearchToday);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public int getTodayAlarmTime(){
        return todayAlarmTime;
    }


    public int getOneWeekAlarmTime(){
        return oneWeekAlarmTime;
    }

    private long parseTimestamp(String timestamp) {
        Log.e("dzc","timestamp:"+timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = null;
        try {
            utcDate = sdf.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.setTimeZone(TimeZone.getDefault());
        Date locatlDate = null;
        String localTime = sdf.format(utcDate.getTime());
        Log.e("dzc","localTime:"+localTime);
        try {
            locatlDate = sdf.parse(localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return locatlDate.getTime() ;
    }



    private static class EntryComparator implements Comparator<Entry> {


        @Override
        public int compare(Entry entry1, Entry entry2) {

            return Float.compare(entry1.getX(), entry2.getX());
        }
    }

    public void setIsSearchToday(boolean isSearchToday) {
        this.isSearchToday = isSearchToday;

    }


    public void getTodayWakePreViewTime(){
        if(realViewTimeArray != null){
            iAovBatteryManagerView.showPrePreViewTime(secToHour(realViewTimeArray.getInteger(realViewTimeArray.size() -1)));// 当天的最后一个获取
        }
        if(wakeUpTimeArray != null){
            iAovBatteryManagerView.showWakeTime(secToHour(wakeUpTimeArray.getInteger(realViewTimeArray.size() -1)));
        }
    }
    public void getOneWeekWakePreViewTime(){
        if(realViewTimeArray != null){
            int total = 0;
            for (int i = 0; i < realViewTimeArray.size(); i++) {
                total = total+realViewTimeArray.getInteger(i);

            }
            iAovBatteryManagerView.showPrePreViewTime(secToHour(total));
        }
        if(wakeUpTimeArray != null){
            int total = 0;
            for (int i = 0; i < wakeUpTimeArray.size(); i++) {
                total = total+wakeUpTimeArray.getInteger(i);

            }
            iAovBatteryManagerView.showWakeTime(secToHour(total));
        }


    }

    @Override
    public void releaseIDRModel() {
        model.clear();
    }

    public List<Entry> getBatteryEntry(){
        return batteryEntry;
    }

    public List<Entry> getSignalEntry(){
        return signalEntry;
    }

    public boolean isSearchToday(){
        return isSearchToday;
    }

}
