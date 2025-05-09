package demo.xm.com.xmfunsdkdemo.ui.device.config.idr.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lib.FunSDK;
import com.utils.TimeUtils;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.dialog.DateNumberPickDialog;
import demo.xm.com.xmfunsdkdemo.ui.dialog.TimePickBottomDialog;

/**
 * Created by ccy on 2018-03-22.
 * 免打扰默认时间是22:00-06:00
 */

public class StartEndTimeSetActivity extends DemoBaseActivity implements TimePickBottomDialog.OnDatePickerListener {

    public static final int REQUEST_CODE = 0xf1;
    public static final int TYPE_START = 0;
    public static final int TYPE_END = 1;
    public static final int[] DEFAULT_S_TIME = {22,0};
    public static final int[] DEFAULT_E_TIME = {6,0};
    @Nullable
    private int[] startTime, endTime;

    private ListSelectItem lsiStartTime, lsiEndTime;
    private TimePickBottomDialog timePick;

    private boolean hasModified = false;
    /**
     * 支持次日
     */
    private boolean isSupportNextDay;

    /**
     * @param startTime [0]时，[1]分,允许为空
     */
    public static void actionStart(Activity from, @Nullable int[] startTime, @Nullable int[] endTime,boolean isSupportNextDay) {
        Intent i = new Intent(from, StartEndTimeSetActivity.class);
        i.putExtra("startTime", startTime);
        i.putExtra("endTime", endTime);
        i.putExtra("isSupportNextDay",isSupportNextDay);
        from.startActivityForResult(i, REQUEST_CODE);
    }

    public static void actionStart(Activity from, @Nullable int[] startTime, @Nullable int[] endTime,int req) {
        Intent i = new Intent(from, StartEndTimeSetActivity.class);
        i.putExtra("startTime", startTime);
        i.putExtra("endTime", endTime);
        from.startActivityForResult(i, req);
    }


    public static void actionStart(Activity from, @Nullable int[] startTime, @Nullable int[] endTime,boolean isSupportNextDay,int req) {
        Intent i = new Intent(from, StartEndTimeSetActivity.class);
        i.putExtra("startTime", startTime);
        i.putExtra("endTime", endTime);
        i.putExtra("isSupportNextDay",isSupportNextDay);
        from.startActivityForResult(i, req);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.idrset_no_disturb_time_set_act);
        handleIntent();
        initView();
    }

    private void initView() {
        titleBar = findViewById(R.id.dev_set_title);
        titleBar.setTitleText(FunSDK.TS("TR_Detection_Schedule"));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                saveConfig();
            }
        });
        lsiStartTime = (ListSelectItem) findViewById(R.id.start_time);
        lsiEndTime = (ListSelectItem) findViewById(R.id.end_time);

        lsiStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timePick == null) {
                    timePick = new TimePickBottomDialog();
                    timePick.setOnDatePickerListener(StartEndTimeSetActivity.this);
                }
                timePick.setSeq(TYPE_START);
                if (startTime == null){
                    timePick.setTimes(DEFAULT_S_TIME[0], DEFAULT_S_TIME[1]);
                }else {
                    timePick.setTimes(startTime[0], startTime[1]);
                }
                timePick.show(getSupportFragmentManager(), "startTime");
            }
        });

        lsiEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timePick == null) {
                    timePick = new TimePickBottomDialog();
                    timePick.setOnDatePickerListener(StartEndTimeSetActivity.this);
                }
                timePick.setSeq(TYPE_END);
                if (endTime == null){
                    timePick.setTimes(DEFAULT_E_TIME[0], DEFAULT_E_TIME[1]);
                }else {
                    timePick.setTimes(endTime[0], endTime[1]);
                }
                timePick.show(getSupportFragmentManager(), "engTime");
            }
        });

        updateUI();
    }

    private void handleIntent() {
        if (!getIntent().hasExtra("startTime")
                || !getIntent().hasExtra("endTime")) {
            finish();
            return;
        }
        startTime = getIntent().getIntArrayExtra("startTime");
        endTime = getIntent().getIntArrayExtra("endTime");
        isSupportNextDay = getIntent().getBooleanExtra("isSupportNextDay",false);
        if (null == startTime) {
            startTime = new int[2];
            startTime[0] = DEFAULT_S_TIME[0];
            startTime[1] = DEFAULT_S_TIME[1];
        }
        if (null == endTime) {
            endTime = new int[2];
            endTime[0] = DEFAULT_E_TIME[0];
            endTime[1] = DEFAULT_E_TIME[1];
        }
    }


    private void onSaveBtnClick() {
        saveConfig();
    }

    private void saveConfig() {
        Intent i = new Intent();
        long lStartTime = TimeUtils.getLongTimes(startTime[0],startTime[1],0);
        long lEndTime = TimeUtils.getLongTimes(endTime[0],endTime[1],0);
        if (lStartTime == lEndTime) {
            Toast.makeText(this, FunSDK.TS("config_failure_opentime_same"), Toast.LENGTH_LONG).show();
            return;
        }else if (!isSupportNextDay && lEndTime < lStartTime) {
            Toast.makeText(this,FunSDK.TS("End_Time_Greater_Than_Begin_Time"),Toast.LENGTH_LONG).show();
            return;
        }

        i.putExtra("startTime", startTime);
        i.putExtra("endTime", endTime);
        i.putExtra("interDay",lEndTime < lStartTime);
        setResult(RESULT_OK, i);
        finish();
    }


    @Override
    public void onTimePicked(String year, String month, String day, String hour, String minute, int seq) {
        hasModified = true;
        switch (seq) {
            case TYPE_START:
                if (startTime == null)
                    startTime = new int[2];
                startTime[0] = Integer.parseInt(hour);
                startTime[1] = Integer.parseInt(minute);
                break;
            case TYPE_END:
                if (endTime == null)
                    endTime = new int[2];
                endTime[0] = Integer.parseInt(hour);
                endTime[1] = Integer.parseInt(minute);
                break;
        }
        updateUI();
    }

    private void updateUI() {
        if (startTime == null) {
            lsiStartTime.setRightText(FunSDK.TS("No_Set"));
        } else {
            lsiStartTime.setRightText((startTime[0] < 10 ? "0" + startTime[0] : startTime[0]) +
                    ":" +
                    (startTime[1] < 10 ? "0" + startTime[1] : startTime[1]));
        }

        if (endTime == null) {
            lsiEndTime.setRightText(FunSDK.TS("No_Set"));
        } else {
            lsiEndTime.setRightText((endTime[0] < 10 ? "0" + endTime[0] : endTime[0]) +
                    ":" +
                    (endTime[1] < 10 ? "0" + endTime[1] : endTime[1]));
        }
    }

    @Override
    public void onBackPressed() {
//        if (hasModified) {
//            XMPromptDlg.onShow(this, FunSDK.TS("save_tip"), new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            }, null);
//            return;
//        }

        onSaveBtnClick();
        super.onBackPressed();
    }

    public void setSupportNextDay(boolean supportNextDay) {
        isSupportNextDay = supportNextDay;
    }

    @Override
    public XMBasePresenter getPresenter() {
        return null;
    }
}
