package demo.xm.com.xmfunsdkdemo.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lib.FunSDK;
import com.utils.XUtils;

import demo.xm.com.xmfunsdkdemo.R;


public class MySeekBar extends FrameLayout {

    private SeekBar seekBar;
    private ConstraintLayout constraintLayout;
    private TextView tvSeekTop;
    private TextView tvLeft;
    private TextView tvRight;
    private View mView;
    private boolean tipVisible = true;
    private int scope = 0; // 增加范围
    private String topTipUnit = ""; // 单位
    private boolean isScreenBreathStyle = false; // 是否显示调整屏幕亮起时间方式

    public boolean isSetPir() {
        return isSetPir;
    }

    public void setSetPir(boolean setPir) {
        isSetPir = setPir;
    }

    private boolean isSetPir = false;
    private MySeekBarOnSeekBarChangeListener mySeekBarOnSeekBarChangeListener;


    public MySeekBar(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MySeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public MySeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MySeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mView = LayoutInflater.from(context).inflate(R.layout.layout_myseekbar, this, true);
        constraintLayout = mView.findViewById(R.id.cl_seekbar_top);
        seekBar = mView.findViewById(R.id.seekbar);
        tvSeekTop = mView.findViewById(R.id.seek_top_tv);
        tvLeft = mView.findViewById(R.id.seekbar_left_tv);
        tvRight = mView.findViewById(R.id.seekbar_right_tv);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                float textWidth = tvSeekTop.getWidth();
                float startX = seekBar.getLeft();
                int max = seekBar.getMax();
                float thumb = XUtils.dp2px(context, 20);
                float average = (((float) seekBar.getWidth()) - 2 * thumb) / max;
                float pox = startX - textWidth / 2 + thumb + average * progress;
                if(!isScreenBreathStyle){
                    tvSeekTop.setText("" + (progress + scope) + topTipUnit);
                }else if(isSetPir){
                    setPIRSentivisity(progress);
                }else {
                    setScreenBreathStyle(progress);
                }

                constraintLayout.setX(pox);
                if (mySeekBarOnSeekBarChangeListener != null) {
                    mySeekBarOnSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                constraintLayout.setVisibility(tipVisible ? View.VISIBLE : GONE);
                if (mySeekBarOnSeekBarChangeListener != null) {
                    mySeekBarOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            /**
             * 10s~300s
             * @param seekBar The SeekBar in which the touch gesture began
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();

                int time = (int) (progress + 10);

                constraintLayout.setVisibility(tipVisible ? View.INVISIBLE : GONE);
                if (mySeekBarOnSeekBarChangeListener != null) {
                    mySeekBarOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }

            }
        });

    }

    /**
     * 设置屏幕亮度时间seekbar 滑动显示
     * @param progress
     */
    private void setScreenBreathStyle(int progress) {
        if (0 <= progress && progress < 10) {
            tvSeekTop.setText(30+ FunSDK.TS("s"));
        } else if (progress >= 10 && progress < 30) {
            tvSeekTop.setText(60+ FunSDK.TS("s"));
        } else if (progress >= 30 && progress < 50) {
            tvSeekTop.setText(2+ FunSDK.TS("minute"));
        } else if (progress >= 50 && progress < 70) {
            tvSeekTop.setText(3+ FunSDK.TS("minute"));
        } else if (progress >= 70 && progress <90) {
            tvSeekTop.setText(4+ FunSDK.TS("minute"));
        } else if (progress >= 90 && progress < 110) {
            tvSeekTop.setText(5+ FunSDK.TS("minute"));
        } else if (progress >= 110 && progress < 120) {
            tvSeekTop.setText(FunSDK.TS("Never"));
        }
    }

    private void setPIRSentivisity(int process){
        if(process >= 0 && process < 10){
            tvSeekTop.setText(FunSDK.TS("TR_PIR_lowest"));
        } else if (process >= 10 && process < 30) {
            tvSeekTop.setText(FunSDK.TS("TR_PIR_Lower"));;
        } else if (process >= 30 && process < 50) {
            tvSeekTop.setText(FunSDK.TS("TR_PIR_Medium"));;
        } else if (process >= 50 && process < 70) {
            tvSeekTop.setText(FunSDK.TS("TR_PIR_Higher"));;
        }else {
            tvSeekTop.setText(FunSDK.TS("TR_PIR_Hightext"));
        }
    }

    public void setMySeekBarOnSeekBarChangeListener(MySeekBarOnSeekBarChangeListener seekBarOnSeekBarChangeListener) {
        this.mySeekBarOnSeekBarChangeListener = seekBarOnSeekBarChangeListener;
    }

    public void setProgress(int i) {
        if (seekBar != null) {
            seekBar.setProgress(i);
        }

    }

    public void setMax(int i) {
        if (seekBar != null) {
            seekBar.setMax(i);
        }
    }

    public void TipVisible(boolean b) {
        this.tipVisible = b;
        if(!b){
            constraintLayout.setVisibility(GONE);
        }

    }

    public void setLeftText(String text) {
        if (tvLeft != null) {
            tvLeft.setText(text);
        }
    }

    public void setTopTipUnit(String topTipUnit) {
        this.topTipUnit = topTipUnit;
    }

    public void setRightText(String text) {
        if (tvRight != null) {
            tvRight.setText(text);
        }
    }

    /**
     * 设置息屏时间模式
     *
     */
    public void setScreenBreathStyle0(boolean isScreenBreathStyle){
        this.isScreenBreathStyle = isScreenBreathStyle;

    }

    public void setSeekBarIncreaseScope(int i){
        this.scope = i;
    }

    public interface MySeekBarOnSeekBarChangeListener {
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(SeekBar seekBar);

        void onStopTrackingTouch(SeekBar seekBar);

    }
}
