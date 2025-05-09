package demo.xm.com.xmfunsdkdemo.ui.dialog;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import com.xm.ui.dialog.DatePickBottomDialog;

import java.util.Calendar;
import java.util.Date;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.dialog.DateNumberPickDialog;
import demo.xm.com.xmfunsdkdemo.ui.dialog.NumberPickDialog;
import demo.xm.com.xmfunsdkdemo.ui.widget.NumberPicker;

/**
 * Created by ccy on 2018-03-15.
 * 底部弹出式 时分选择器
 *
 * @see DateNumberPickDialog
 * @see DatePickBottomDialog
 * @see NumberPickDialog
 */

public class TimePickBottomDialog extends DialogFragment implements View.OnClickListener {


    protected View rootLayout;
    private String[] hours, minutes;
    //不是日期值，是对应数组中的位置
    private int hour, minute;
    private NumberPicker npHour, npMinute;
    private TextView btnOk, btnCancel,topBtnOk,topBtnCancel,tvHour,tvMin;
    private LinearLayout bottomControlLayout , topControlLayout;
    private boolean isShowTopControl = false;
    private TimePickBottomDialog.OnDatePickerListener l;
    private int seq = 0;
    private boolean isNeedChangeTextColor = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(STYLE_NO_TITLE, R.style.DialogFragment_style);
        setStyle(STYLE_NO_TITLE, R.style.SimpleDialog);
        if (hours == null) {
            initData();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            initData();
        }
        rootLayout = inflater.inflate(R.layout.time_number_pick, container, false);
        btnOk = rootLayout.findViewById(R.id.tv_sure);
        btnCancel = rootLayout.findViewById(R.id.tv_cancel);
        topBtnOk = rootLayout.findViewById(R.id.topTv_sure);
        topBtnCancel = rootLayout.findViewById(R.id.topTv_cancel);
        tvHour = rootLayout.findViewById(R.id.tvHour);
        tvMin = rootLayout.findViewById(R.id.tvMin);
        topControlLayout = rootLayout.findViewById(R.id.topcontrol_ll);
        bottomControlLayout = rootLayout.findViewById(R.id.control_ll);
        topControlLayout.setVisibility(isShowTopControl ? View.VISIBLE :View.GONE);
        bottomControlLayout.setVisibility(isShowTopControl? View.GONE : View.VISIBLE);
        tvMin.setVisibility(!isShowTopControl ? View.VISIBLE: View.GONE);
        tvHour.setVisibility(!isShowTopControl ? View.VISIBLE: View.GONE);
        npHour = rootLayout.findViewById(R.id.numpicker_h);
        npMinute = rootLayout.findViewById(R.id.numpicker_m);

        npHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npHour.setMaxValue(hours.length - 1);
        npHour.setMinValue(0);
        npHour.setDisplayedValues(hours);
        npHour.setValue(hour);
        npHour.setisNeedhangeColor(isNeedChangeTextColor);

        npMinute.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npMinute.setMaxValue(minutes.length - 1);
        npMinute.setMinValue(0);
        npMinute.setDisplayedValues(minutes);
        npMinute.setValue(minute);
        npMinute.setisNeedhangeColor(isNeedChangeTextColor);
        if(isNeedChangeTextColor){
            npMinute.getmInputText().setTextColor(Color.BLACK);
            npHour.getmInputText().setTextColor(Color.BLACK);
        }
        topBtnOk.setOnClickListener(this);
        topBtnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        return rootLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        initParams();
    }

    private void initParams() {
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.BOTTOM;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
            window.setWindowAnimations(R.style.corner_popwindow_anim_style);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decorView = window.getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                decorView.setSystemUiVisibility(uiOptions);
                decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        View decorView = window.getDecorView();
                        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                        decorView.setSystemUiVisibility(uiOptions);
                    }
                });
            }
        }
        //物理键返回可以，区域外不行
        setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
    }

    private void initData() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

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
            case R.id.topTv_sure:
            case R.id.tv_sure:
                if (l != null) {
                    l.onTimePicked(null
                            , null
                            , null
                            , hours[npHour.getValue()]
                            , minutes[npMinute.getValue()]
                            , seq);
                }
                dismiss();
                break;
            case R.id.tv_cancel:
            case R.id.topTv_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }
    public void isShowTopControl(boolean isShowTopControl){
        this.isShowTopControl = isShowTopControl;
        if(topControlLayout != null){
            topControlLayout.setVisibility(isShowTopControl ? View.VISIBLE : View.GONE);
        }
        if(bottomControlLayout != null){
            bottomControlLayout.setVisibility(isShowTopControl ? View.GONE : View.VISIBLE);
        }
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }


    //用取巧的方式设置的，故若传入的值不符合对应时间值（如hour传入了>24的值)时，会显示错误或崩溃
    public void setTimes(
            @IntRange(from = 0, to = 23) int hour,
            @IntRange(from = 0, to = 59) int minute) {
        if (hours == null) {
            initData();
        }
        //时、分是从0开始累加的，即它的值跟数组下标是一毛一样的，直接赋值
        this.hour = hour;
        this.minute = minute;
    }

    public void setOnDatePickerListener(TimePickBottomDialog.OnDatePickerListener l) {
        this.l = l;
    }

    public void setChangeColor(boolean isChangeColor) {
        isNeedChangeTextColor = isChangeColor;
        if(npMinute != null){
            npMinute.setisNeedhangeColor(isChangeColor);
            npMinute.getmInputText().setTextColor(Color.BLACK);
        }
        if(npHour != null){
            npHour.setisNeedhangeColor(isChangeColor);
            npHour.getmInputText().setTextColor(Color.BLACK);


        }
    }

    public interface OnDatePickerListener {

        void onTimePicked(String year, String month, String day, String hour, String minute, int seq);
    }
}
