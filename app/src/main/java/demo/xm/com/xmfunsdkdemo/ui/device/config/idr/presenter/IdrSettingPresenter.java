package demo.xm.com.xmfunsdkdemo.ui.device.config.idr.presenter;

import static demo.xm.com.xmfunsdkdemo.ui.device.config.idr.view.IDRSettingActivity.DETECTION_DURATION_SET_REQUEST;

import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.basic.G;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.AlarmInfoBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.HumanDetectionBean;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.NetWorkSetEnableVideo;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.SystemFunctionBean;
import com.manager.device.DeviceManager;
import com.manager.device.config.DevConfigInfo;
import com.manager.device.config.DevConfigManager;
import com.utils.TimeUtils;
import com.xm.activity.base.XMBasePresenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.DevConfigState;
import demo.xm.com.xmfunsdkdemo.ui.device.config.idr.listener.IdrSettingContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.idr.view.DetectionDurationSetActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.idr.view.IDRSettingActivity;

public class IdrSettingPresenter extends XMBasePresenter<DeviceManager> implements IdrSettingContract.IIdrSettingPresenter{



    public static final String SET_ENABLE_VIDEO = "NetWork.SetEnableVideo";


    private AlarmInfoBean mAlarmInfoBean;


    private HumanDetectionBean mHumanDetectionBean;
    private NetWorkSetEnableVideo mNetWorkSetEnableVideo;

    private DevConfigManager mDevConfigManager;

    private IdrSettingContract.IIdrSettingView iIdrSettingView;
    
    
    


    public IdrSettingPresenter(IdrSettingContract.IIdrSettingView iIdrSettingView){
        this.iIdrSettingView = iIdrSettingView;
    }


    @Override
    public void setDevId(String devId) {
        mDevConfigManager = manager.getDevConfigManager(devId);
        super.setDevId(devId);
    }

    @Override
    protected DeviceManager getManager() {
        return DeviceManager.getInstance();
    }

    /**
     * 初始化数据
     */
    public void initData() {

        //更新录像开关
        getNetWorkSetEnableVideoConfig();

        //PIR报警
        checkSupportPirAlarmDevAbility();


        //检查是否支持低功耗长时间报警录像
        checkSupportLowPowerLongAlarmRecord();

        iIdrSettingView.onShowWaitDialog();
    }





    /**
     * 检查是否支持低功耗长时间报警录像
     */
    public void checkSupportLowPowerLongAlarmRecord(){
        DeviceManager.getInstance().getDevAllAbility(getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, SystemFunctionBean result) {
                if (result != null && result.OtherFunction!=null) {
                    if(result.OtherFunction.SupportLowPowerLongAlarmRecord){
                        iIdrSettingView.initPirRecordDuration(true);
                        return;
                    }
                }
                iIdrSettingView.initPirRecordDuration(false);
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
                iIdrSettingView.initPirRecordDuration(false);
            }
        });

    }




    /**
     * 检查是否支持PIR报警
     */
    public void checkSupportPirAlarmDevAbility(){
        DeviceManager.getInstance().getDevAllAbility(getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, SystemFunctionBean result) {
                if (result != null && result.OtherFunction!=null) {
                    if(result.OtherFunction.SupportPirAlarm){
                        getPIRAlarm();
                        return;
                    }
                }
                iIdrSettingView.hidePirView();
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
                iIdrSettingView.hidePirView();
            }
        });

    }



    /**
     * 更新人形检测配置
     */
    private void updateHumanDetect() {
        iIdrSettingView.onShowWaitDialog();
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DevConfigManager.OnDevConfigResultListener() {
            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {

            }

            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                iIdrSettingView.onHideWaitDialog();
                if (result instanceof String) {
                    HandleConfigData handleConfigData = new HandleConfigData();
                    if (handleConfigData.getDataObj((String)result, HumanDetectionBean.class)) {


                        //获取人形检测报警结果
                        mHumanDetectionBean = (HumanDetectionBean) handleConfigData.getObj();
                        iIdrSettingView.setLsiHumanDetectionState(mHumanDetectionBean.isEnable());

                    }
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                iIdrSettingView.onHideWaitDialog();
                getDataFail();
            }
        });

        devConfigInfo.setChnId(0);
        devConfigInfo.setJsonName(JsonConfig.DETECT_HUMAN_DETECTION);

        mDevConfigManager.getDevConfig(devConfigInfo);
    }


    /**
     * 更新录像开关
     */
    private void getNetWorkSetEnableVideoConfig() {
        iIdrSettingView.onShowWaitDialog();
        DevConfigInfo devConfigInfo = DevConfigInfo.create(new DevConfigManager.OnDevConfigResultListener() {
            @Override
            public void onFunSDKResult(Message msg, MsgContent ex) {

            }

            @Override
            public void onSuccess(String devId, int operationType, Object result) {
                iIdrSettingView.onHideWaitDialog();
                if (result instanceof String) {
                    HandleConfigData handleConfigData = new HandleConfigData();
                    if (handleConfigData.getDataObj((String)result, NetWorkSetEnableVideo.class)) {


                        //获取人形检测报警结果
                        mNetWorkSetEnableVideo = (NetWorkSetEnableVideo) handleConfigData.getObj();
                        if (mNetWorkSetEnableVideo != null) {
                            iIdrSettingView.setLsiRecordEnableState(mNetWorkSetEnableVideo.Enable);

                        }

                    }
                }
            }

            @Override
            public void onFailed(String devId, int msgId, String jsonName, int errorId) {
                iIdrSettingView.onHideWaitDialog();
                getDataFail();
            }
        });

        devConfigInfo.setChnId(-1);
        devConfigInfo.setJsonName(SET_ENABLE_VIDEO);

        mDevConfigManager.getDevConfig(devConfigInfo);
    }



    private void getDataFail(){
        iIdrSettingView.onHideWaitDialog();
        Toast.makeText(iIdrSettingView.getActivity().getApplicationContext(), FunSDK.TS("Data_exception"), Toast.LENGTH_SHORT).show();
    }

    private void saveDataFail(){
        iIdrSettingView.onHideWaitDialog();
        Toast.makeText(iIdrSettingView.getActivity().getApplicationContext(), FunSDK.TS("Save_Failed"), Toast.LENGTH_SHORT).show();
    }


    private void saveDataSuccess(){
        iIdrSettingView.onHideWaitDialog();
        Toast.makeText(iIdrSettingView.getActivity().getApplicationContext(), FunSDK.TS("Save_Success"), Toast.LENGTH_SHORT).show();
    }



    public AlarmInfoBean getAlarmInfoBean() {
        return mAlarmInfoBean;
    }







    //保存PIR配置
    public void savePirAlarmConfig() {
        iIdrSettingView.onShowWaitDialog();

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
        devConfigInfo.setJsonName(JsonConfig.ALARM_PIR);
        devConfigInfo.setChnId(0);

        HandleConfigData handleConfigData = new HandleConfigData();
        String jsonData = handleConfigData.getSendData(HandleConfigData.getFullName(JsonConfig.ALARM_PIR, 0), "0x01", mAlarmInfoBean);
        devConfigInfo.setJsonData(jsonData);
        mDevConfigManager.setDevConfig(devConfigInfo);
    }

    //保存人形检测
    public void saveHumanDetectionConfig(boolean isEnable) {
        if (mHumanDetectionBean != null) {
            mHumanDetectionBean.setEnable(isEnable);
            iIdrSettingView.onShowWaitDialog();


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
            devConfigInfo.setJsonName(JsonConfig.DETECT_HUMAN_DETECTION);
            devConfigInfo.setChnId(0);
            devConfigInfo.setJsonData(HandleConfigData.getSendData(HandleConfigData.getFullName(JsonConfig.DETECT_HUMAN_DETECTION,0), "0x08", mHumanDetectionBean));
            mDevConfigManager.setDevConfig(devConfigInfo);
        }
    }



    /*
    * 保存录像开关
     */

    public void saveNetWorkSetEnableVideoConfig(boolean isEnable) {
        if (mNetWorkSetEnableVideo != null) {
            mNetWorkSetEnableVideo.Enable = isEnable;
            iIdrSettingView.onShowWaitDialog();




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
            devConfigInfo.setJsonName(SET_ENABLE_VIDEO);
            devConfigInfo.setChnId(-1);
            devConfigInfo.setJsonData(HandleConfigData.getSendData(HandleConfigData.getFullName(SET_ENABLE_VIDEO,-1), "0x1", mNetWorkSetEnableVideo));
            mDevConfigManager.setDevConfig(devConfigInfo);
        }
    }



    /**
     * 跳转报警时段设置
     * 设备报警配置实际有两个时间段可以配置PirTimeSectionOne，PirTimeSectionTwo，通过这两个时间段组装成一个可以跨天的报警时段
     * 如app上只显示一个报警时段，可以跨天设置，比如设置22:00-08:00，则实际设置到PirTimeSectionOne设置为22:00-23:59，PirTimeSectionTwo设置为0:00-8:00
     * 如果未跨天只用PirTimeSectionOne，跨天通过PirTimeSectionOne，PirTimeSectionTwo组装
     * WeekMask，Enable以PirTimeSectionOne设置的为准
     */
    public void turnToDetectionSchedule() {
        if (mAlarmInfoBean != null && mAlarmInfoBean.PirTimeSection != null) {
            //报警时段1
            AlarmInfoBean.PirTimeSections.PirTimeSection pirTimeSection1 = mAlarmInfoBean.PirTimeSection.PirTimeSectionOne;
            if (pirTimeSection1 != null) {

                int[][] startEndTimeOne = new int[2][2];
                SimpleDateFormat format = new SimpleDateFormat("hh:mm");
                try {
                    Date date = format.parse(pirTimeSection1.StartTime);
                    if (date != null) {
                        startEndTimeOne[0][0] = date.getHours();
                        startEndTimeOne[0][1] = date.getMinutes();
                    }
                    date = format.parse(pirTimeSection1.EndTime);
                    if (date != null) {
                        startEndTimeOne[1][0] = date.getHours();
                        startEndTimeOne[1][1] = date.getMinutes();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }



                //是否跨天
                boolean isDifferentDay = false;
                int[][] startEndTimeTwo = new int[2][2];
                //报警时段PirTimeSectionOne
                AlarmInfoBean.PirTimeSections.PirTimeSection pirTimeSection2 = mAlarmInfoBean.PirTimeSection.PirTimeSectionTwo;
                if (pirTimeSection2 != null) {
                    try {
                        Date date = format.parse(pirTimeSection2.StartTime);
                        if (date != null) {
                            startEndTimeTwo[0][0] = date.getHours();
                            startEndTimeTwo[0][1] = date.getMinutes();
                        }
                        date = format.parse(pirTimeSection2.EndTime);
                        if (date != null) {
                            startEndTimeTwo[1][0] = date.getHours();
                            startEndTimeTwo[1][1] = date.getMinutes();
                        }


                        //判断是否跨天
                        // 如果跨天，startEndTimeOne必然是23:59，PirTimeSectionTwo必然是00:00，并且PirTimeSectionTwo的结束时间小于startEndTimeOne开始时间，则认为跨天
                        if(startEndTimeTwo[0][0]==0 && startEndTimeTwo[0][1]==0 && startEndTimeOne[1][0]==23 && startEndTimeOne[1][1]==59){
                            long lStartTime = TimeUtils.getLongTimes(startEndTimeOne[0][0],startEndTimeOne[0][1],0);
                            long lEndTime = TimeUtils.getLongTimes(startEndTimeTwo[1][0],startEndTimeTwo[1][1],0);
                            isDifferentDay = lEndTime < lStartTime;
                        }


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                DetectionDurationSetActivity.actionStart(iIdrSettingView.getActivity(), pirTimeSection1.Enable,
                        startEndTimeOne[0], isDifferentDay ? startEndTimeTwo[1] : startEndTimeOne[1], pirTimeSection1.WeekMask, DETECTION_DURATION_SET_REQUEST);
            }
        }
    }




    public static String[] WEEKS = new String[] { FunSDK.TS("Sunday"), FunSDK.TS("Monday"), FunSDK.TS("Tuesday"),
            FunSDK.TS("Wednesday"), FunSDK.TS("Thursday"), FunSDK.TS("Friday"), FunSDK.TS("Saturday") };


    /**
     * 获取重复周期文案
     */
    public static String getWeeks(int weekMask) {
        int i = 0;
        boolean isEveryDay = true;
        StringBuffer sb = new StringBuffer();
        do {
            if ((weekMask & 0x01) == 0x1) {
                sb.append(WEEKS[i]);
                sb.append(" ");
            } else {
                isEveryDay = false;
            }
            i++;
        } while ((weekMask = weekMask >> 1) != 0);
        if (isEveryDay && sb.length() > 0 && i == SDKCONST.NET_N_WEEKS) {
            return FunSDK.TS("every_day");
        }
        if (StringUtils.isStringNULL(sb.toString())) {
            return FunSDK.TS("Never");
        }
        return sb.toString();
    }


    /**
     * 保存报警时间段配置
     */
    public void savePirTimeSection(int[] startTime,int[] endTime,boolean enable,int weekMaskOne,String times){
        if (mAlarmInfoBean != null && mAlarmInfoBean.PirTimeSection != null) {
            AlarmInfoBean.PirTimeSections.PirTimeSection pirTimeSection1 = mAlarmInfoBean.PirTimeSection.PirTimeSectionOne;
            if (pirTimeSection1 != null) {

                long lStartTime = TimeUtils.getLongTimes(startTime[0],startTime[1],0);
                long lEndTime = TimeUtils.getLongTimes(endTime[0],endTime[1],0);
                boolean isNextDay = lEndTime < lStartTime;
                AlarmInfoBean.PirTimeSections.PirTimeSection pirTimeSection2 = mAlarmInfoBean.PirTimeSection.PirTimeSectionTwo;
                if(isNextDay){
                    pirTimeSection1.Enable = enable;
                    pirTimeSection1.WeekMask = weekMaskOne;
                    if(pirTimeSection2!=null){
                        pirTimeSection2.Enable = enable;

                        List<Boolean> selectedList = new ArrayList<>();
                        for (int i = 0; i < 7; ++i) {
                            selectedList.add(((weekMaskOne >> i) & 0x01) == 1);
                        }


                        List<Boolean> selectedList2 = new ArrayList<>();
                        for (int i = 0; i < 7; ++i) {
                            if(i==0){
                                selectedList2.add(selectedList.get(6));
                            } else {
                                selectedList2.add(selectedList.get(i-1));
                            }
                        }

                        int nextDayWeekMask = 0;
                        for (int i = 0; i < selectedList2.size(); i++) {
                            if (selectedList2.get(i)) {
                                nextDayWeekMask |= (int) Math.pow(2, i);
                            }
                        }
                        pirTimeSection2.WeekMask = nextDayWeekMask;
                    }
                    String[] time = times.split("-");
                    if (time.length == 2) {
                        pirTimeSection1.StartTime = time[0];
                        pirTimeSection1.EndTime = "23:59";
                        if(pirTimeSection2!=null){
                            pirTimeSection2.StartTime = "00:00";
                            pirTimeSection2.EndTime = time[1];
                        }
                    }
                } else {
                    pirTimeSection1.Enable = enable;
                    pirTimeSection1.WeekMask = weekMaskOne;
                    String[] time = times.split("-");
                    if (time.length == 2) {
                        pirTimeSection1.StartTime = time[0];
                        pirTimeSection1.EndTime = time[1];
                    }
                    if(pirTimeSection2!=null){
                        pirTimeSection2.Enable = false;
                        pirTimeSection2.StartTime = "00:00";
                        pirTimeSection2.EndTime = "23:59";
                    }
                }
            }

        }
        savePirAlarmConfig();
    }


    /**
     * 保存灵敏度配置
     */
    public void saveSensitive(int value){
        if (mAlarmInfoBean != null) {
            mAlarmInfoBean.PirSensitive = value;
        }
        savePirAlarmConfig();
    }


    /**
     * 获取PIR报警设置
     */
    private void getPIRAlarm(){
        DevConfigInfo mainConfigInfo = DevConfigInfo.create(new DeviceManager.OnDevManagerListener() {
            @Override
            public void onSuccess(String devId, int msgId, Object result) {

                iIdrSettingView.onHideWaitDialog();
                if (result instanceof AlarmInfoBean){
                    iIdrSettingView.onHideWaitDialog();
                    mAlarmInfoBean = (AlarmInfoBean) result;
                    iIdrSettingView.updatePirView(mAlarmInfoBean);

                    checkSupportPirSensitiveDevAbility();
                    checkSupportPirTimeSectionDevAbility();
                    checkSupportPEAInHumanPedDevAbility();

                }

            }

            @Override
            public void onFailed(String devId, int msgId, String s1, int errorId) {
                iIdrSettingView.onHideWaitDialog();
                getDataFail();
            }
        });
        mainConfigInfo.setJsonName(JsonConfig.ALARM_PIR);
        mainConfigInfo.setChnId(0);
        mDevConfigManager.getDevConfig(mainConfigInfo);
    }

    /**
     * 检查是否支持人形侦测
     */
    public void checkSupportPEAInHumanPedDevAbility(){
        DeviceManager.getInstance().getDevAllAbility(getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, SystemFunctionBean result) {
                if (result != null && result.AlarmFunction!=null) {
                    if(result.AlarmFunction.PEAInHumanPed){
                        updateHumanDetect();
                        return;
                    }
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

            }
        });

    }


    /**
     * 检查是否支持灵敏度设置
     */
    public void checkSupportPirSensitiveDevAbility(){
        DeviceManager.getInstance().getDevAllAbility(getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, SystemFunctionBean result) {
                if (result != null && result.OtherFunction!=null) {
                    if(result.OtherFunction.SupportPirSensitive){
                        iIdrSettingView.showPirSensitiveView(mAlarmInfoBean);
                    }
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

            }
        });

    }



    /**
     * 判断是否支持报警时间段
     */
    public void checkSupportPirTimeSectionDevAbility(){
        DeviceManager.getInstance().getDevAllAbility(getDevId(), new DeviceManager.OnDevManagerListener<SystemFunctionBean>() {
            /**
             * 成功回调
             * @param devId         设备类型
             * @param operationType 操作类型
             */
            @Override
            public void onSuccess(String devId, int operationType, SystemFunctionBean result) {
                if (result != null && result.OtherFunction!=null) {
                    if(result.OtherFunction.SupportPirTimeSection){
                        iIdrSettingView.showPirTimeSection(mAlarmInfoBean);
                    }
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

            }
        });

    }



}
