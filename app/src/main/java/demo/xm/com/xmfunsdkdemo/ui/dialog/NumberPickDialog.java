package demo.xm.com.xmfunsdkdemo.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.xm.ui.widget.NumberPicker;

import demo.xm.com.xmfunsdkdemo.R;


/**
 * 时间选择弹窗
 */

public class NumberPickDialog extends DialogFragment implements View.OnClickListener {

    protected View rootLayout;
    private NumberPicker mHourNumPicker;
    private NumberPicker mMinNumPicker;
    private String[] hours, minutes;
    protected TimeSettingListener mTimeSettingListener;
    private int hour;
    private int min;
    private boolean isOpenTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogFragment_style);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootLayout = inflater.inflate(R.layout.light_time_select, null);
        mHourNumPicker = (NumberPicker) rootLayout.findViewById(R.id.numpicker_hour);
        mMinNumPicker = (NumberPicker) rootLayout.findViewById(R.id.numpicker_min);
        rootLayout.findViewById(R.id.tv_cancel).setOnClickListener(this);
        rootLayout.findViewById(R.id.tv_sure).setOnClickListener(this);
        mHourNumPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mHourNumPicker.setMaxValue(hours.length - 1);
        mHourNumPicker.setMinValue(0);
        mHourNumPicker.setDisplayedValues(hours);
        mMinNumPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mMinNumPicker.setMaxValue(minutes.length - 1);
        mMinNumPicker.setMinValue(0);
        mMinNumPicker.setDisplayedValues(minutes);
        mMinNumPicker.setValue(min);
        mHourNumPicker.setValue(hour);
        return rootLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sure: {
                if (mTimeSettingListener != null) {
                    mTimeSettingListener.setTime(mHourNumPicker.getValue(), mMinNumPicker.getValue(), isOpenTime);
                }
                dismiss();
                break;
            }
            case R.id.tv_cancel: {
                dismiss();
                break;
            }
            default:
                break;
        }
    }


    private void initData() {
        hours = new String[24];
        for (int i = 0; i < hours.length; i++) {
            if (i < 10) {
                hours[i] = "0" + i;
            } else {
                hours[i] = "" + i;
            }
        }
        minutes = new String[60];
        for (int i = 0; i < minutes.length; i++) {
            if (i < 10) {
                minutes[i] = "0" + i;
            } else {
                minutes[i] = "" + i;
            }
        }
    }

    /**
     * @param timeSettingListener
     */
    public void setTimeSettingListener(TimeSettingListener timeSettingListener) {
        mTimeSettingListener = timeSettingListener;
    }

    public void updateTime(int hour, int min, boolean isOpenTime) {
        this.hour = hour;
        this.min = min;
        this.isOpenTime = isOpenTime;
    }

    public interface TimeSettingListener {

        boolean setTime(int hour, int min, boolean isOpenTime);

    }

}
