package demo.xm.com.xmfunsdkdemo.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.xm.ui.dialog.DatePickBottomDialog;
import com.xm.ui.dialog.TimePickBottomDialog;

import java.util.Calendar;
import java.util.Date;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.dialog.NumberPickDialog;
import demo.xm.com.xmfunsdkdemo.ui.widget.NumberPicker;

/**
 * Created by ccy on 2018-02-24.
 *
 * 年-月-日-时-分 选择框，在屏幕中间弹出<br/>
 * 同风格的 时-分 选择框参考 ： {@link NumberPickDialog}
 *<br/>
 * 另外还有从底部弹出式的选择器：
 * @see TimePickBottomDialog
 * @see DatePickBottomDialog
 *
 */

public class DateNumberPickDialog extends DialogFragment implements View.OnClickListener {

    protected View rootLayout;
    
    
    private String[] years, months, days, hours, minutes;
    //不是日期值，是对应数组中的位置
    private int year, month, day, hour, minute;
    private NumberPicker npYear, npMonth, npDay, npHour, npMinute;
    private TextView btnOk, btnCancel;
    private OnDatePickerListener l;
    private int seq = 0;
    private Calendar tempCalendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogFragment_style);
        if(years == null){
            initData();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            initData();
        }

        rootLayout = inflater.inflate(R.layout.date_select_dialog, container, false);
        btnOk = (TextView) rootLayout.findViewById(R.id.tv_sure);
        btnCancel = (TextView) rootLayout.findViewById(R.id.tv_cancel);
        npYear = (NumberPicker) rootLayout.findViewById(R.id.numpicker_y);
        npMonth = (NumberPicker) rootLayout.findViewById(R.id.numpicker_Mo);
        npDay = (NumberPicker) rootLayout.findViewById(R.id.numpicker_d);
        npHour = (NumberPicker) rootLayout.findViewById(R.id.numpicker_h);
        npMinute = (NumberPicker) rootLayout.findViewById(R.id.numpicker_m);

        npYear.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npYear.setMaxValue(years.length - 1);
        npYear.setMinValue(0);
        npYear.setDisplayedValues(years);
        npYear.setValue(year);

        npMonth.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npMonth.setMaxValue(months.length - 1);
        npMonth.setMinValue(0);
        npMonth.setDisplayedValues(months);
        npMonth.setValue(month);

        npDay.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npDay.setMaxValue(days.length - 1);
        npDay.setMinValue(0);
        npDay.setDisplayedValues(days);
        npDay.setValue(day);

        npHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npHour.setMaxValue(hours.length - 1);
        npHour.setMinValue(0);
        npHour.setDisplayedValues(hours);
        npHour.setValue(hour);

        npMinute.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npMinute.setMaxValue(minutes.length - 1);
        npMinute.setMinValue(0);
        npMinute.setDisplayedValues(minutes);
        npMinute.setValue(minute);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        npYear.setOnValueChangedListener(onValueChangeListener);
        npMonth.setOnValueChangedListener(onValueChangeListener);

        return super.onCreateView(inflater,container,savedInstanceState);
    }

    private NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal, EditText editText) {
//            Log.d("ccy", "change;old = " + oldVal + ";new = " + newVal + ";getval = " + picker.getValue());
            updateMaxDays(Integer.parseInt(years[npYear.getValue()]), Integer.parseInt(months[npMonth.getValue()]));
        }
    };


    /**
     * 更新天选择器。
     * 随着年、月的变化， 当月的总天数也是会变的
     */
    private void updateMaxDays(int year, int month) {
        if (tempCalendar == null) {
            tempCalendar = Calendar.getInstance();
        }
        tempCalendar.set(Calendar.YEAR, year);
        tempCalendar.set(Calendar.MONTH, month - 1);

        int currentMonthMaxDays = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); //获取该年该月的最大天数
//        Log.d("ccy","year = " + year + ";month = " + month + ";max days = " + currentMonthMaxDays);
        //新月份的总天数和之前总天数不同，重置【天】选择器
        if (currentMonthMaxDays != days.length) {
            days = new String[currentMonthMaxDays];
            for (int i = 0; i < days.length; i++) {
                days[i] = (i < 9 ? "0" : "") + (i + 1);
            }
            //该控件内部一个BUG，在二次设置setMaxValue时，若比原来的大，会抛数组越界，这里先置null
            npDay.setDisplayedValues(null);
            //setMaxValue后，若之前选择的天大于最大值，控件会自动处理（e.g.之前选中了31，然后新的最大值为30，会自动将value置为30）
            npDay.setMaxValue(days.length - 1);
            npDay.setMinValue(0);
            npDay.setDisplayedValues(days);
        }
    }

    private void initData() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int currentYear = calendar.get(Calendar.YEAR);
        years = new String[20];
        for (int i = 0; i < years.length; i++) {
            years[i] = "" + currentYear;
            currentYear++;
        }

        months = new String[12];
        for (int i = 0; i < months.length; i++) {
            months[i] = (i < 9 ? "0" : "") + (i + 1);
        }

        int currentMonthMaxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        days = new String[currentMonthMaxDays];
        for (int i = 0; i < days.length; i++) {
            days[i] = (i < 9 ? "0" : "") + (i + 1);
        }

        hours = new String[24];
        for (int i = 0; i < hours.length; i++) {
            hours[i] = (i < 10 ? "0" : "") + i;
        }

        minutes = new String[60];
        for (int i = 0; i < minutes.length; i++) {
            minutes[i] = (i < 10 ? "0" : "") + i;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sure:
                if (l != null) {
                    l.onTimePicked(years[npYear.getValue()]
                            , months[npMonth.getValue()]
                            , days[npDay.getValue()]
                            , hours[npHour.getValue()]
                            , minutes[npMinute.getValue()]
                            , seq);
                }
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }


    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }


    //用取巧的方式设置的，故若传入的值不符合对应时间值（如hour传入了>24的值)时，会显示错误或崩溃
    public void setTimes(int year,
                         @IntRange(from = 1,to = 12) int month,
                         @IntRange(from = 1,to = 31) int day,
                         @IntRange(from = 0,to = 23) int hour,
                         @IntRange(from = 0,to = 59) int minute) {
        if(years == null){
            initData();
        }
        //年减去今年(第1个位置)即数组中对应下标位置
        this.year = year - Integer.parseInt(years[0]);
        //月、日是从1开始累加的，减去1即对应数组下标位置
        this.month = month - 1;
        this.day = day - 1;
        //时、分是从0开始累加的，即它的值跟数组下标是一毛一样的，直接赋值
        this.hour = hour;
        this.minute = minute;
    }


    public void setOnDatePickerListener(OnDatePickerListener l) {
        this.l = l;
    }

    public interface OnDatePickerListener {

        void onTimePicked(String year, String month, String day, String hour, String minute, int seq);
    }
}
