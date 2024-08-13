package demo.xm.com.xmfunsdkdemo.ui.device.config.videoconfig.view;

import static demo.xm.com.xmfunsdkdemo.ui.device.config.DevConfigState.DEV_CONFIG_VIEW_INVISIABLE;
import static demo.xm.com.xmfunsdkdemo.ui.entity.DevRecordConfig.RECORD_MODE_CONFIG_RECORD;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.basic.G;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.lib.JSONCONFIG;
import com.lib.SDKCONST;
import com.loper7.date_time_picker.DateTimePicker;
import com.utils.TimeUtils;
import com.utils.XUtils;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.dialog.EditDialog;

import java.util.HashMap;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.DevConfigState;
import demo.xm.com.xmfunsdkdemo.ui.device.config.videoconfig.listener.DevRecordSetContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.videoconfig.presenter.DevRecordSetPresenter;
import demo.xm.com.xmfunsdkdemo.ui.entity.DevRecordConfig;
import io.reactivex.annotations.Nullable;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * 录像配置界面,可改变录像方式,关掉音频,改变文件长度
 * Created by zzw on 2022-08-25
 */
public class DevRecordSetActivity extends BaseConfigActivity<DevRecordSetPresenter> implements
        DevRecordSetContract.IDevRecordSetView, SeekBar.OnSeekBarChangeListener {

    /**
     * 两个作用：1.获取：处理录像配置之主码流部分的json数据，并显示到控件上。2.保存：根据控件信息生成json格式数据以用来保存
     */
    private DevRecordConfig mMain;

    /**
     * 主码流预录时间拖动条（分），0为关闭，取值范围为[0, 30]
     */
    private SeekBar sbMainPreRecord;

    /**
     * 主码流预录时间显示
     */
    private TextView tvMainPreRecord;

    /**
     * 主码流录像打包时间（秒），取值范围为[1, 120]
     */
    private SeekBar sbMainPacketLength;

    /**
     * 主码流录像打包时间显示
     */
    private TextView tvMainPacketLength;

    /**
     * 主码流录像模式：配置录像（ConfigRecord），手动录像（ManualRecord），关闭录像（ClosedRecord）
     */
    private Spinner spMainMode;

    /**
     * 主码流录像模式选项：，0是从不录像，1是手动录像，2是始终录像
     */
    private static final Integer[] defValues = {0, 1, 2};

    /**
     * 两个作用：1.获取：处理录像配置之辅码流部分的json数据，并显示到控件上。2.保存：根据控件信息生成json格式数据以用来保存
     */
    private DevRecordConfig mSub;

    /**
     * 辅码流部分总模块，若设备没有辅码流，则该部分不可见
     */
    private LinearLayout subLay;

    /**
     * 辅码流预录时间拖动条（分），0为关闭，取值范围为[0, 30]
     */
    private SeekBar sbSubPreRecord;
    /**
     * 辅码流预录时间显示
     */
    private TextView tvSubPreRecord;
    /**
     * 辅码流录像打包时间（秒），取值范围为[1, 120]
     */
    private SeekBar sbSubPacketLength;
    /**
     * 辅码流录像打包时间显示
     */
    private TextView tvSubPacketLength;
    /**
     * 辅码流录像模式：配置录像（ConfigRecord），手动录像（ManualRecord），关闭录像（ClosedRecord）
     */
    private Spinner spSubMode;

    /**
     * 主码流录像方式选择选项
     */
    private RadioGroup rgMainRecordType;
    /**
     * 主码流录像方式选择布局
     */
    private View layoutMainRecordType;


    /**
     * 辅码流录像方式选择选项
     */
    private RadioGroup rgSubRecordType;
    /**
     * 副码流录像方式选择布局
     */
    private View layoutSubRecordType;

    /**
     * 定时录像开关
     */
    private ListSelectItem lsiEpitomeRecord;
    /**
     * 抓图间隔
     */
    private ListSelectItem lsiCaptureInterval;
    /**
     * 开始时间
     */
    private ListSelectItem lsiStartTime;
    /**
     * 结束时间
     */
    private ListSelectItem lsiEndTime;

    private boolean isEnable;
    private int interval;
    private String startTime;
    private String endTime;
    private List<String> timeSection;
    @Override
    public DevRecordSetPresenter getPresenter() {
        return new DevRecordSetPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_setup_record);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_setup_record));
        titleBar.setRightBtnResource(R.mipmap.icon_save_normal, R.mipmap.icon_save_pressed);
        titleBar.setLeftClick(this);
        titleBar.setRightIvClick(this::tryToSaveConfig);

        mMain = new DevRecordConfig(JSONCONFIG.RECORD);     //主码流部分
        mMain.setState(DevConfigState.DEV_CONFIG_UNLOAD);   //还未加载
        sbMainPreRecord = findViewById(R.id.sb_dev_set_record_main_preRecord);
        tvMainPreRecord = findViewById(R.id.tv_dev_set_record_main_preRecord);
        sbMainPacketLength = findViewById(R.id.sb_dev_set_record_main_packetLength);
        tvMainPacketLength = findViewById(R.id.tv_dev_set_record_main_packetLength);
        spMainMode = findViewById(R.id.sp_dev_set_record_main_record_mode);
        String[] recordMode = getResources().getStringArray(R.array.device_setup_record_mode_values);
        ArrayAdapter<String> adapterRecordMode = new ArrayAdapter<>(this, R.layout.right_spinner_item, recordMode);
        adapterRecordMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMainMode.setAdapter(adapterRecordMode);
        spMainMode.setTag(defValues);
        rgMainRecordType = findViewById(R.id.rg_main_record_type);
        layoutMainRecordType = findViewById(R.id.layout_main_record_type);
        spMainMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMain.setmRecordMode(position);
                if (RECORD_MODE_CONFIG_RECORD.equals(mMain.getRecordMode())) {
                    layoutMainRecordType.setVisibility(View.VISIBLE);
                } else {
                    layoutMainRecordType.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mSub = new DevRecordConfig(JSONCONFIG.EXRECORD);    //辅码流部分
        mSub.setState(DevConfigState.DEV_CONFIG_UNLOAD);    //还未加载
        sbSubPreRecord = findViewById(R.id.sb_dev_set_record_sub_preRecord);
        tvSubPreRecord = findViewById(R.id.tv_dev_set_record_sub_preRecord);
        sbSubPacketLength = findViewById(R.id.sb_dev_set_record_sub_packetLength);
        tvSubPacketLength = findViewById(R.id.tv_dev_set_record_sub_packetLength);
        subLay = findViewById(R.id.ll_sub_dev_video_setting_front);
        spSubMode = findViewById(R.id.sp_dev_set_record_sub_record_mode);
        spSubMode.setAdapter(adapterRecordMode);
        spSubMode.setTag(defValues);
        rgSubRecordType = findViewById(R.id.rg_sub_record_type);
        layoutSubRecordType = findViewById(R.id.layout_sub_record_type);
        spSubMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSub.setmRecordMode(position);
                if (RECORD_MODE_CONFIG_RECORD.equals(mSub.getRecordMode())) {
                    layoutSubRecordType.setVisibility(View.VISIBLE);
                } else {
                    layoutSubRecordType.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lsiEpitomeRecord = findViewById(R.id.lsi_epitome_record);
        lsiCaptureInterval = findViewById(R.id.lsi_capture_interval);
        lsiStartTime = findViewById(R.id.lsi_capture_start_time);
        lsiEndTime = findViewById(R.id.lsi_capture_end_time);

        lsiEpitomeRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEnable = !isEnable;
                lsiEpitomeRecord.setSwitchState(isEnable ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
            }
        });

        lsiCaptureInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XMPromptDlg.onShowEditDialog(DevRecordSetActivity.this, getString(R.string.please_input_capture_interval), String.valueOf(interval), new EditDialog.OnEditContentListener() {
                    @Override
                    public void onResult(String content) {
                        if (XUtils.isInteger(content)) {
                            interval = Integer.parseInt(content);
                            lsiCaptureInterval.setRightText(content);
                        }
                    }
                });
            }
        });

        lsiStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePicker calendarView = new DateTimePicker(DevRecordSetActivity.this);
                calendarView.setBackgroundColor(Color.WHITE);

                calendarView.setOnDateTimeChangedListener(new Function1<Long, Unit>() {
                    @Override
                    public Unit invoke(Long aLong) {
                        String time = TimeUtils.showNormalFormat(aLong);
                        startTime = time;
                        lsiStartTime.setRightText(time);
                        return null;
                    }
                });

                XMPromptDlg.onShow(DevRecordSetActivity.this, calendarView);
            }
        });

        lsiEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePicker calendarView = new DateTimePicker(DevRecordSetActivity.this);
                calendarView.setBackgroundColor(Color.WHITE);

                calendarView.setOnDateTimeChangedListener(new Function1<Long, Unit>() {
                    @Override
                    public Unit invoke(Long aLong) {
                        String time = TimeUtils.showNormalFormat(aLong);
                        endTime = time;
                        lsiEndTime.setRightText(time);
                        return null;
                    }
                });

                XMPromptDlg.onShow(DevRecordSetActivity.this, calendarView);
            }
        });
    }

    private void initData() {
        showWaitDialog();
        presenter.getRecordInfo();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onUpdateView(String result, String key, int state) {
        switch (key) {
            case JSONCONFIG.RECORD:
                mMain.setState(state);
                if (state == DevConfigState.DEV_CONFIG_VIEW_VISIABLE && mMain.onParse(result)) {
                    sbMainPreRecord.setProgress(mMain.getmPreRecord());
                    sbMainPreRecord.setOnSeekBarChangeListener(this);
                    tvMainPreRecord.setText(mMain.getmPreRecord() + getResources().getString(R.string.date_second));

                    sbMainPacketLength.setProgress(mMain.getmPacketLength());
                    sbMainPacketLength.setOnSeekBarChangeListener(this);
                    tvMainPacketLength.setText(mMain.getmPacketLength() + getResources().getString(R.string.date_minute));
                    spMainMode.setSelection(mMain.getRecordModeLevel());

                    //根据配置显示录像方式。在ConfigRecord模式下，mask[0][0](即第一天的第一个数值)是6表示是报警录像方式、为7表示全天录像方式
                    if (RECORD_MODE_CONFIG_RECORD.equals(mMain.getRecordMode())) {
                        layoutMainRecordType.setVisibility(View.VISIBLE);
                        if (mMain.getMask() != null && mMain.getMask().length > 0) {
                            int intFromHex = G.getIntFromHex(mMain.getMask()[0][0]);
                            switch (intFromHex) {
                                case 6://报警录像
                                    rgMainRecordType.check(R.id.rb_main_alarm_record);
                                    break;
                                case 7://全天录像
                                    rgMainRecordType.check(R.id.rb_main_all_day_record);
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else {
                        layoutMainRecordType.setVisibility(View.GONE);
                    }
                }
                allLoaded();
                break;
            case JSONCONFIG.EXRECORD:
                mSub.setState(state);
                if (state == DevConfigState.DEV_CONFIG_VIEW_VISIABLE && mSub.onParse(result)) {
                    sbSubPreRecord.setProgress(mSub.getmPreRecord());
                    sbSubPreRecord.setOnSeekBarChangeListener(this);
                    tvSubPreRecord.setText(mSub.getmPreRecord() + getResources().getString(R.string.date_second));

                    sbSubPacketLength.setProgress(mSub.getmPacketLength());
                    sbSubPacketLength.setOnSeekBarChangeListener(this);
                    tvSubPacketLength.setText(mSub.getmPacketLength() + getResources().getString(R.string.date_minute));
                    spSubMode.setSelection(mSub.getRecordModeLevel());
                    //根据配置显示录像方式。在ConfigRecord模式下，mask[0][0](即第一天的第一个数值)是6表示是报警录像方式、为7表示全天录像方式
                    if (RECORD_MODE_CONFIG_RECORD.equals(mSub.getRecordMode())) {
                        layoutSubRecordType.setVisibility(View.VISIBLE);
                        if (mSub.getMask() != null && mSub.getMask().length > 0) {
                            int intFromHex = G.getIntFromHex(mSub.getMask()[0][0]);
                            switch (intFromHex) {
                                case 6://报警录像
                                    rgSubRecordType.check(R.id.rb_sub_alarm_record);
                                    break;
                                case 7://全天录像
                                    rgSubRecordType.check(R.id.rb_sub_all_day_record);
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else {
                        layoutSubRecordType.setVisibility(View.GONE);
                    }
                } else { //若不支持，则不可见
                    subLay.setVisibility(View.GONE);
                }
                allLoaded();
                break;
            case "Storage.EpitomeRecord"://定时录像
                if (state == DEV_CONFIG_VIEW_INVISIABLE) {
                    findViewById(R.id.ll_epitome_record).setVisibility(View.GONE);
                }else {
                    HashMap<String, Object> resultMap = new Gson().fromJson(result, HashMap.class);
                    if (resultMap != null && resultMap.containsKey("Storage.EpitomeRecord")) {
                        List<LinkedTreeMap<String, Object>> epitomeRecordList = (List<LinkedTreeMap<String, Object>>) resultMap.get("Storage.EpitomeRecord");
                        LinkedTreeMap<String, Object> epitomeRecordMap = epitomeRecordList.get(presenter.getChnId());
                        if (epitomeRecordMap != null) {
                            isEnable = (boolean) epitomeRecordMap.get("Enable");
                            Double intervalDouble = (Double) epitomeRecordMap.get("Interval");
                            interval = intervalDouble.intValue();
                            startTime = (String) epitomeRecordMap.get("StartTime");
                            endTime = (String) epitomeRecordMap.get("EndTime");
                            timeSection = (List<String>) epitomeRecordMap.get("TimeSection");
                            lsiEpitomeRecord.setSwitchState(isEnable ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
                            lsiCaptureInterval.setRightText(interval + "");
                            lsiStartTime.setRightText(startTime);
                            lsiEndTime.setRightText(endTime);

                            findViewById(R.id.ll_epitome_record).setVisibility(View.VISIBLE);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onSaveResult(String key, int state) {
        switch (key) {
            case JSONCONFIG.RECORD:
                mMain.setState(state);
                break;
            case JSONCONFIG.EXRECORD:
                mSub.setState(state);
                break;
        }

        if (mMain.getState() != DevConfigState.DEV_CONFIG_VIEW_VISIABLE && mSub.getState() != DevConfigState.DEV_CONFIG_VIEW_VISIABLE) {
            if ((mMain.getState() == DevConfigState.DEV_CONFIG_UPDATE_SUCCESS || mSub.getState() == DevConfigState.DEV_CONFIG_UPDATE_SUCCESS)
                    && (mSub.getState() == DevConfigState.DEV_CONFIG_UPDATE_SUCCESS || mSub.getState() == DEV_CONFIG_VIEW_INVISIABLE)) {
                showToast(getResources().getString(R.string.set_dev_config_success), Toast.LENGTH_SHORT);
                finish();
            } else {
                showToast(getResources().getString(R.string.set_dev_config_failed), Toast.LENGTH_SHORT);
            }
        }
    }


    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_dev_set_record_main_preRecord:
                sbMainPreRecord.setProgress(progress);
                tvMainPreRecord.setText(progress + getResources().getString(R.string.date_second));
                break;
            case R.id.sb_dev_set_record_main_packetLength:
                sbMainPacketLength.setProgress(progress);
                tvMainPacketLength.setText(progress + getResources().getString(R.string.date_minute));
                break;
            case R.id.sb_dev_set_record_sub_preRecord:
                sbSubPreRecord.setProgress(progress);
                tvSubPreRecord.setText(progress + getResources().getString(R.string.date_second));
                break;
            case R.id.sb_dev_set_record_sub_packetLength:
                sbSubPacketLength.setProgress(progress);
                tvSubPacketLength.setText(progress + getResources().getString(R.string.date_minute));
                break;
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /**
     * 去保存配置
     */
    private void tryToSaveConfig() {
        showWaitDialog();
        mMain.setmPreRecord(sbMainPreRecord.getProgress());
        mMain.setmPacketLength(sbMainPacketLength.getProgress());
        mMain.setmRecordMode(defValues[spMainMode.getSelectedItemPosition()]);
        if (RECORD_MODE_CONFIG_RECORD.equals(mMain.getRecordMode())) {//“ConfigRecord”模式下设置全天录像或者报警录像
            if (rgMainRecordType.getCheckedRadioButtonId() == R.id.rb_main_alarm_record) { //设置报警录像配置
                for (int i = 0; i < SDKCONST.NET_N_WEEKS; ++i) {//7天循环设置
                    String[][] mask = mMain.getMask();
                    String[][] timeSection = mMain.getTimeSection();
                    mask[i][0] = G.getHexFromInt(6);//将每天的第一组数据设置成0x06
                    timeSection[i][0] = "1 00:00:00-24:00:00";//将每天的第一组数据设置成全天并启用
                }
            } else { //设置全天24小时录像配置
                for (int i = 0; i < SDKCONST.NET_N_WEEKS; ++i) {//7天循环设置
                    String[][] mask = mMain.getMask();
                    String[][] timeSection = mMain.getTimeSection();
                    mask[i][0] = G.getHexFromInt(7);//将每天的第一组数据设置成0x07
                    timeSection[i][0] = "1 00:00:00-24:00:00";//将每天的第一组数据设置成全天并启用
                }
            }
        }
        presenter.setRecordInfo(JSONCONFIG.RECORD, mMain.getSendMsg());

        if (mSub.getState() != DEV_CONFIG_VIEW_INVISIABLE) {
            mSub.setmPreRecord(sbSubPreRecord.getProgress());
            mSub.setmPacketLength(sbSubPacketLength.getProgress());
            mSub.setmRecordMode(defValues[spSubMode.getSelectedItemPosition()]);
            if (RECORD_MODE_CONFIG_RECORD.equals(mSub.getRecordMode())) {//“ConfigRecord”模式下设置全天录像或者报警录像
                if (rgSubRecordType.getCheckedRadioButtonId() == R.id.rb_sub_alarm_record) { //设置报警录像配置
                    for (int i = 0; i < SDKCONST.NET_N_WEEKS; ++i) {//7天循环设置
                        String[][] mask = mSub.getMask();
                        String[][] timeSection = mSub.getTimeSection();
                        mask[i][0] = G.getHexFromInt(6);//将每天的第一组数据设置成0x06
                        timeSection[i][0] = "1 00:00:00-24:00:00";//将每天的第一组数据设置成全天并启用
                    }
                } else { //设置全天24小时录像配置
                    for (int i = 0; i < SDKCONST.NET_N_WEEKS; ++i) {//7天循环设置
                        String[][] mask = mSub.getMask();
                        String[][] timeSection = mSub.getTimeSection();
                        mask[i][0] = G.getHexFromInt(7);//将每天的第一组数据设置成0x07
                        timeSection[i][0] = "1 00:00:00-24:00:00";//将每天的第一组数据设置成全天并启用
                    }
                }
            }
            presenter.setRecordInfo(JSONCONFIG.EXRECORD, mSub.getSendMsg());
        }

        if (findViewById(R.id.ll_epitome_record).getVisibility() == View.VISIBLE) {
            HashMap<String, Object> sendDataMap = new HashMap<>();
            HashMap<String, Object> epitomeRecordMap = new HashMap<>();
            epitomeRecordMap.put("Enable", isEnable);
            epitomeRecordMap.put("Interval", interval);
            epitomeRecordMap.put("StartTime", startTime);
            epitomeRecordMap.put("EndTime", endTime);
            epitomeRecordMap.put("TimeSection",timeSection);
            sendDataMap.put("Storage.EpitomeRecord", epitomeRecordMap);
            sendDataMap.put("SessionID", "0x08");
            sendDataMap.put("Name", "Storage.EpitomeRecord");
            presenter.setRecordInfo("Storage.EpitomeRecord", new Gson().toJson(sendDataMap));
        }
    }

    /**
     * 判断是否全部加载完毕，若是，则收起缓冲弹窗
     */
    private void allLoaded() {
        if (mMain.getState() != DevConfigState.DEV_CONFIG_UNLOAD && mSub.getState() != DevConfigState.DEV_CONFIG_UNLOAD) {
            hideWaitDialog();
        }
    }
}
