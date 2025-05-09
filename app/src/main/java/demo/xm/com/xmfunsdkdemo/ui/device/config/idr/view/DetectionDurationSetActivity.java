package demo.xm.com.xmfunsdkdemo.ui.device.config.idr.view;



import static demo.xm.com.xmfunsdkdemo.ui.device.config.idr.presenter.IdrSettingPresenter.WEEKS;
import static demo.xm.com.xmfunsdkdemo.ui.device.config.idr.view.WeekSelectActivity.AT_LEAST_ONE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.StringUtils;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;


public class DetectionDurationSetActivity extends DemoBaseActivity implements View.OnClickListener {

    public static final int PIR_SE_TIME_ONE_REQUEST_CODE = 0x01;
    public static final int PIR_SE_TIME_TWO_REQUEST_CODE = 0x02;
    public static final int PIR_WEEK_ONE_REQUEST_CODE = 0x03;
    public static final int PIR_WEEK_TWO_REQUEST_CODE = 0x04;

    ListSelectItem lsiPirTimeSectionOne;
    ListSelectItem lsiPirStartEndTimeOne;
    ListSelectItem lsiPirWeekOne;

    private boolean enable;
    private int[][] startEndTimeOne = new int[2][2];
    private int weekMaskOne;

    public static void actionStart(Activity from, boolean enable,
                                   int[] startTime, int[] endTime, int weekMaskOne, int req) {
        Intent i = new Intent(from, DetectionDurationSetActivity.class);
        i.putExtra("enable", enable);
        i.putExtra("startTime", startTime);
        i.putExtra("endTime", endTime);
        i.putExtra("weekMaskOne", weekMaskOne);
        from.startActivityForResult(i, req);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_duration_set);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        try {
            enable = intent.getBooleanExtra("enable", false);
            startEndTimeOne[0] = intent.getIntArrayExtra("startTime");
            startEndTimeOne[1] = intent.getIntArrayExtra("endTime");
            weekMaskOne = intent.getIntExtra("weekMaskOne", 0);

            lsiPirTimeSectionOne.setRightImage(enable ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
            String times = String.format("%02d:%02d-%02d:%02d", startEndTimeOne[0][0], startEndTimeOne[0][1],
                    startEndTimeOne[1][0], startEndTimeOne[1][1]);
            lsiPirStartEndTimeOne.setRightText(times);
            lsiPirWeekOne.setRightText(getWeeks(weekMaskOne));
            if (lsiPirTimeSectionOne.getRightValue() == SDKCONST.Switch.Open) {
                lsiPirStartEndTimeOne.setVisibility(View.VISIBLE);
                lsiPirWeekOne.setVisibility(View.VISIBLE);
            } else {
                lsiPirStartEndTimeOne.setVisibility(View.GONE);
                lsiPirWeekOne.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        onSaveBtnClick();
        super.onBackPressed();
    }



    private void onSaveBtnClick() {
        Intent intent = new Intent();
        intent.putExtra("enable", lsiPirTimeSectionOne.getRightValue() == SDKCONST.Switch.Open);
        intent.putExtra("startTime", startEndTimeOne[0]);
        intent.putExtra("endTime", startEndTimeOne[1]);
        intent.putExtra("weekMaskOne", weekMaskOne);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initView() {
        titleBar = findViewById(R.id.xb_duration_set);
        titleBar.setTitleText(FunSDK.TS("TR_Detection_Schedule"));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                onSaveBtnClick();
            }
        });

        lsiPirTimeSectionOne = findViewById(R.id.pir_time_section_one);
        lsiPirTimeSectionOne.setTitle(FunSDK.TS("PIR_Detect_Time_Period"));
        lsiPirStartEndTimeOne = findViewById(R.id.pir_start_end_time_one);
        lsiPirWeekOne = findViewById(R.id.pir_week_one);

        lsiPirTimeSectionOne.setOnRightClick(onRightImageClickListener);
        lsiPirStartEndTimeOne.setOnRightClick(onRightImageClickListener);
        lsiPirWeekOne.setOnRightClick(onRightImageClickListener);

        lsiPirTimeSectionOne.setOnClickListener(this);
        lsiPirStartEndTimeOne.setOnClickListener(this);
        lsiPirWeekOne.setOnClickListener(this);
    }

    private ListSelectItem.OnRightImageClickListener onRightImageClickListener = new ListSelectItem.OnRightImageClickListener() {
        @Override
        public void onClick(ListSelectItem parent, View v) {
            if (parent == lsiPirTimeSectionOne) {
                if (lsiPirTimeSectionOne.getRightValue() == SDKCONST.Switch.Open) {
                    lsiPirStartEndTimeOne.setVisibility(View.VISIBLE);
                    lsiPirWeekOne.setVisibility(View.VISIBLE);
                } else {
                    lsiPirStartEndTimeOne.setVisibility(View.GONE);
                    lsiPirWeekOne.setVisibility(View.GONE);
                }
            } else if (parent == lsiPirStartEndTimeOne
                    || parent == lsiPirWeekOne) {
                DetectionDurationSetActivity.this.onClick(parent);
            }
        }
    };

    @Override
    public void onClick(View v){
        int id = v.getId();
        if (id == R.id.pir_start_end_time_one) {
            //徘徊检测报警时间设置1
            StartEndTimeSetActivity.actionStart(DetectionDurationSetActivity.this,
                    startEndTimeOne[0],
                    startEndTimeOne[1], true,PIR_SE_TIME_ONE_REQUEST_CODE);
        } else if (id == R.id.pir_week_one) {
            //起止时间1
            WeekSelectActivity.actionStart(this, weekMaskOne, AT_LEAST_ONE, PIR_WEEK_ONE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PIR_SE_TIME_ONE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    startEndTimeOne[0] = data.getIntArrayExtra("startTime");
                    startEndTimeOne[1] = data.getIntArrayExtra("endTime");
                    //may be null
                    if (startEndTimeOne[0] != null && startEndTimeOne[1] != null) {
                        String times = String.format("%02d:%02d-%02d:%02d", startEndTimeOne[0][0], startEndTimeOne[0][1],
                                startEndTimeOne[1][0], startEndTimeOne[1][1]);
                        lsiPirStartEndTimeOne.setRightText(times);
                    }
                }
                break;
            case PIR_WEEK_ONE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    weekMaskOne = data.getIntExtra("WeekMask", 0);
                    lsiPirWeekOne.setRightText(WeekSelectActivity.getWeeks(weekMaskOne));
                }
                break;
        }
    }


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

    @Override
    public XMBasePresenter getPresenter() {
        return null;
    }
}
