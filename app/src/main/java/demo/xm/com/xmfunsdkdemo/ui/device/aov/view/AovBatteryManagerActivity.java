package demo.xm.com.xmfunsdkdemo.ui.device.aov.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.github.mikephil.charting.components.YAxis.YAxisLabelPosition.OUTSIDE_CHART;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.lib.FunSDK;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.aov.listener.AovBatteryManagerContract;
import demo.xm.com.xmfunsdkdemo.ui.device.aov.presenter.AovBatteryManagerPresenter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.widget.MySeekBar;
/**
 * AOV设备电池管理
 */
public class AovBatteryManagerActivity extends BaseConfigActivity<AovBatteryManagerPresenter> implements
        AovBatteryManagerContract.IAovBatteryManagerView, View.OnClickListener{
    private ListSelectItem lisPowerSupplyMode;
    private TextView tvPower;
    private ImageView ivCharging;
    private TextView tvTitlePowerMode;
    private TextView tvTipPowerMode;
    private MySeekBar sbSetBatteryLevel;

    private LineChart lineChart;
    private LineChart signalChart;


    private LineDataSet lineDataSet;
    private LineDataSet signalDataSet;
    private ConstraintLayout clChart;


    private TextView tvToday;
    private TextView tvWeek;
    private TextView tvBatteryChartTip;
    private TextView tvSignalChartTip;
    private TextView tvBatteryStatistic;

    private TextView tvNum;
    private TextView tvWakeTime;
    private TextView tvPreViewTime;


    private String devId;


    @Override
    public AovBatteryManagerPresenter getPresenter() {
        return new AovBatteryManagerPresenter(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aov_battery_manager_layout);
        initView();
        initListener();
        devId = getIntent().getStringExtra("devId");
        presenter.setDevId(devId);
        if(getIntent() != null){
            presenter.setBatteryData(getIntent().getIntExtra("curBattery",-1),
                                    getIntent().getBooleanExtra("isCharging",false));
        }
        presenter.initData();


    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.releaseIDRModel();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(FunSDK.TS("TR_Setting_Battery_Management"));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        titleBar.setBottomTip(getClass().getName());

        lisPowerSupplyMode = findViewById(R.id.lisPowerSupplyMode);
        tvPower = findViewById(R.id.tvPower);
        ivCharging = findViewById(R.id.ivCharging);
        tvTitlePowerMode = findViewById(R.id.tvTitlePowerMode);
        sbSetBatteryLevel = findViewById(R.id.sbSetBatteryLevel);
        sbSetBatteryLevel.setLeftText("5%");
        sbSetBatteryLevel.setRightText("30%");
        sbSetBatteryLevel.setMax(25);
        sbSetBatteryLevel.setSeekBarIncreaseScope(5);
        sbSetBatteryLevel.setTopTipUnit("%");
        lineChart = findViewById(R.id.LineChart);
        signalChart = findViewById(R.id.signalChart);
        clChart = findViewById(R.id.clChart);
        clChart.setVisibility(VISIBLE);
        tvToday = findViewById(R.id.tvToday);
        tvWeek = findViewById(R.id.tvWeek);
        tvBatteryChartTip = findViewById(R.id.tvBatteryChartTip);
        tvSignalChartTip = findViewById(R.id.tvSignalChartTip);
        tvBatteryStatistic = findViewById(R.id.tvBatteryStatistic);
        tvToday.setOnClickListener(v -> {
            tvToday.setBackgroundResource(R.drawable.corner_white_5);
            tvWeek.setBackgroundDrawable(null);
            presenter.setIsSearchToday(true);
            presenter.initTodayChartData();
            tvNum.setText(presenter.getTodayAlarmTime()+"");
            presenter.getTodayWakePreViewTime();
        });
        tvWeek.setOnClickListener(v -> {
            tvWeek.setBackgroundResource(R.drawable.corner_white_5);
            tvToday.setBackgroundDrawable(null);
            presenter.setIsSearchToday(false);
            presenter.initOneWeekData();
            tvNum.setText(presenter.getOneWeekAlarmTime()+"");
            presenter.getOneWeekWakePreViewTime();
        });
        tvNum = findViewById(R.id.tvNum);
        tvWakeTime = findViewById(R.id.tvWakeNum);
        tvPreViewTime = findViewById(R.id.tvtvPreViewNum);

    }


    private void initListener() {
        sbSetBatteryLevel.setMySeekBarOnSeekBarChangeListener(new MySeekBar.MySeekBarOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int process = seekBar.getProgress();
                presenter.saveLowElectrMode(process);

            }
        });

    }

    private void initLineChart() {

        lineChart.setDragEnabled(false);
        //设置是否可以缩放 x和y，默认true
        lineChart.setScaleEnabled(false);
        lineChart.setTouchEnabled(false);
        //不显示高亮
        lineChart.setHighlightPerTapEnabled(false);
        //不显示description
        lineChart.getDescription().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        setX(xAxis);
        YAxis axisRight = lineChart.getAxisRight();
        axisRight.setEnabled(false); // 右侧不显示
        YAxis axis = lineChart.getAxisLeft();
        setSetLeft(axis);
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);
        setLineData(presenter.getBatteryEntry());
        LineData lineData = new LineData(lineDataSet);
        //不显示曲线点的具体数值
        lineData.setDrawValues(false);
        lineChart.setData(lineData);
        lineChart.invalidate();

    }

    private void setLineData(List<Entry> data) {

        //将数据赋给数据集,一个数据集表示一条线
        lineDataSet = new LineDataSet(data, "");
        //设置填充颜色
        lineDataSet.setFillColor(Color.parseColor("#FFA2A2"));
        lineDataSet.setDrawCircles(false);
        Drawable drawable = getDrawable(R.drawable.battery_linechart_shape);
        drawable.setAlpha(204);
        lineDataSet.setFillDrawable(drawable);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setColor(getResources().getColor(R.color.theme_color));

    }

    private void initSignalChart() {
        signalChart.setDragEnabled(false);
        //设置是否可以缩放 x和y，默认true
        signalChart.setScaleEnabled(false);
        signalChart.setTouchEnabled(false);
        //不显示高亮
        signalChart.setHighlightPerTapEnabled(false);
        //不显示description
        signalChart.getDescription().setEnabled(false);

        XAxis xAxis = signalChart.getXAxis();
        setX(xAxis);
        YAxis axisRight = signalChart.getAxisRight();
        axisRight.setEnabled(false); // 右侧不显示
        YAxis axis = signalChart.getAxisLeft();
        setSignalLeft(axis);
        setSignalDataset(presenter.getSignalEntry());
        LineData lineData = new LineData(signalDataSet);
        //不显示曲线点的具体数值
        lineData.setDrawValues(false);
        Legend legend = signalChart.getLegend();
        legend.setEnabled(false);


        signalChart.setData(lineData);
        signalChart.invalidate();

    }

    private void setSignalDataset(List<Entry> data) {
        //将数据赋给数据集,一个数据集表示一条线
        signalDataSet = new LineDataSet(data, "");
        //设置填充颜色
        signalDataSet.setFillColor(Color.parseColor("#FFA2A2"));
        signalDataSet.setDrawCircles(false);
        Drawable drawable = getDrawable(R.drawable.signal_linechart_shape);
        drawable.setAlpha(204);
        signalDataSet.setFillDrawable(drawable);
        signalDataSet.setDrawFilled(true);
        signalDataSet.setColor(Color.parseColor("#FF3478F6"));
    }

    private void setX(XAxis xAxis) {
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//值：BOTTOM,BOTH_SIDED,BOTTOM_INSIDE,TOP,TOP_INSIDE
        xAxis.setEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0); // 开始时间为当天的零点
        if(presenter.isSearchToday()){
            xAxis.setAxisMaximum(presenter.getEndCalendar().getTimeInMillis() - presenter.getToDayStCalendar().getTimeInMillis());
        }else {
            xAxis.setAxisMaximum(presenter.getEndCalendar().getTimeInMillis() - presenter.getOnWeekStCalendar().getTimeInMillis());
        }



        xAxis.setLabelCount(7, true);
        // 设置时间格式

//         手动计算标签值
        List<String> labels = new ArrayList<>();
        if (presenter.isSearchToday()) {
            for (int i = 0; i < 7; i++) {
                labels.add(0 + (i * 4) + "");
            }
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            for (int i = 0; i < 7; i++) {
                Calendar labelCalendar = (Calendar) presenter.getOnWeekStCalendar().clone();
                labelCalendar.add(Calendar.DAY_OF_YEAR, i);
                int month = labelCalendar.get(Calendar.MONTH) + 1; // 月份从0开始计数，所以要加1
                int day = labelCalendar.get(Calendar.DAY_OF_MONTH);
                labels.add(month+"."+day);
            }
        }

        xAxis.setValueFormatter(new MyXAxisValueFormatter(labels));
        // 设置 x 轴的范围为 24 小时
        xAxis.setAvoidFirstLastClipping(true);

        xAxis.setDrawGridLines(false);

    }

    private void setSetLeft(YAxis mAxis) {
        //设置坐标轴最大值：如果设置那么轴不会根据传入数据自动设置
        mAxis.setAxisMaximum(100f);
        //设置坐标轴最小值：如果设置那么轴不会根据传入数据自动设置
        mAxis.setAxisMinimum(0f);
        mAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "%";
            }
        });
        //将图表中最高值的顶部间距（占总轴范围的百分比）与轴上的最高值相比较。
        mAxis.setSpaceMax(0);
        //将图表中最低值的底部间距（占总轴范围的百分比）与轴上的最低值相比较。
        mAxis.setSpaceMin(0);
        //设置标签个数以及是否精确（false为模糊，true为精确）
        mAxis.setLabelCount(6, false);
        //设置轴标签应绘制的位置。无论是inside_chart或outside_chart。
        mAxis.setPosition(OUTSIDE_CHART);
        //如果设置为true那么下面方法设置最小间隔生效，默认为false
        mAxis.setGranularityEnabled(true);
        //设置Y轴的值之间的最小间隔。这可以用来避免价值复制当放大到一个地步，小数设置轴不再数允许区分两轴线之间的值。

        mAxis.enableGridDashedLine(10f, 10f, 20f);
    }

    private void setSignalLeft(YAxis mAxis) {
        //设置坐标轴最大值：如果设置那么轴不会根据传入数据自动设置
        mAxis.setAxisMaximum(100f);
        //设置坐标轴最小值：如果设置那么轴不会根据传入数据自动设置
        mAxis.setAxisMinimum(0f);
        mAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "%";
            }
        });
        //将图表中最高值的顶部间距（占总轴范围的百分比）与轴上的最高值相比较。
        mAxis.setSpaceMax(0);
        //将图表中最低值的底部间距（占总轴范围的百分比）与轴上的最低值相比较。
        mAxis.setSpaceMin(0);
        //设置标签个数以及是否精确（false为模糊，true为精确）
        mAxis.setLabelCount(6, false);
        //设置轴标签应绘制的位置。无论是inside_chart或outside_chart。
        mAxis.setPosition(OUTSIDE_CHART);
        //如果设置为true那么下面方法设置最小间隔生效，默认为false
        mAxis.setGranularityEnabled(true);
        //设置Y轴的值之间的最小间隔。这可以用来避免价值复制当放大到一个地步，小数设置轴不再数允许区分两轴线之间的值。

        mAxis.enableGridDashedLine(10f, 10f, 20f);
    }


    private class MyXAxisValueFormatter extends ValueFormatter {

        private SimpleDateFormat mFormat;
        private List<String> labels;
        private int index = 0;

        public MyXAxisValueFormatter(List<String> labels) {
//            if(isSearchToday){
//                mFormat = new SimpleDateFormat("HH", Locale.ENGLISH);
//            }else {
//                mFormat = new SimpleDateFormat("MM-dd", Locale.ENGLISH);
//            }
            this.labels = labels;
        }

        @Override
        public String getFormattedValue(float value) {
            if (index >= 0 && labels.size() > 0) {
                String text = labels.get(index % labels.size());
                index++;
                return text;
            }
            return "";
        }
    }


    @Override
    public void onShowWaitDialog() {
        showWaitDialog();
    }

    @Override
    public void onHideWaitDialog() {
        hideWaitDialog();
    }



    @Override
    public void onClick(View v) {

    }

    @Override
    public Activity getActivity() {
        return AovBatteryManagerActivity.this;
    }

    /**
     * 显示设备的电源状态
     * @param isCharging 是否正在充电中
     * @param curBattery 当前电池电量百分比
     */
    public void showPowerState(boolean isCharging, int curBattery){
        if (isCharging) {
            ivCharging.setVisibility(VISIBLE);
        } else {
            ivCharging.setVisibility(GONE);
        }
        tvPower.setText(curBattery + "%");
    }

    /**
     * 显示预览时长
     * @param time 时间字符串
     */
    @Override
    public void showPrePreViewTime(String time) {
        tvPreViewTime.setText(time);
    }
    /**
     * 显示唤醒时间
     * @param time 唤醒时间
     */
    @Override
    public void showWakeTime(String time) {
        tvWakeTime.setText(time);
    }
    /**
     * 显示电源模式
     * @param powerThreshold 电源阈值，用于判断电源模式
     */
    @Override
    public void showPowerMode(int powerThreshold) {
        tvTitlePowerMode.setText(FunSDK.TS("TR_Setting_Low_Power_Mode") + "(" + powerThreshold + "%)");
    }
    /**
     * 显示电池电量水平
     * @param maxElectr 电池最大电量
     * @param minElectr 电池最小电量
     * @param curElectr 当前电量
     */
    @Override
    public void showBatteryLevel(int maxElectr, int minElectr,int curElectr) {
        sbSetBatteryLevel.setRightText(maxElectr + "%");
        sbSetBatteryLevel.setLeftText(minElectr + "%");
        sbSetBatteryLevel.setMax(maxElectr - minElectr);
        sbSetBatteryLevel.setProgress(curElectr - minElectr);
        sbSetBatteryLevel.setSeekBarIncreaseScope(minElectr);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    /**
     * 显示图表数据
     * @param isSearchToday 是否搜索今天的图表数据
     */
    @Override
    public void showChartData(boolean isSearchToday) {
        initLineChart();
        initSignalChart();
        clChart.setVisibility(VISIBLE);
        tvBatteryStatistic.setVisibility(VISIBLE);
        tvSignalChartTip.setText(isSearchToday ? FunSDK.TS("sHour") : FunSDK.TS("day"));
        tvBatteryChartTip.setText(isSearchToday ? FunSDK.TS("sHour") : FunSDK.TS("day"));
    }
    /**
     * 显示报警数量
     */
    @Override
    public void showAlarmNum(String todayAlarmTimeStr){
        tvNum.setText(todayAlarmTimeStr);
    }
}

