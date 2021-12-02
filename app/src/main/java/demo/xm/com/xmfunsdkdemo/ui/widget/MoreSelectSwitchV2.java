package demo.xm.com.xmfunsdkdemo.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import demo.xm.com.xmfunsdkdemo.R;


/**
 * @author hws
 * @class 多种选择的开关
 * Multiple choices of switches
 * @time 2019-12-15 16:46
 */
public class MoreSelectSwitchV2 extends View implements View.OnTouchListener {
    private static final int SWITCH_DEFAULT_COUNT = 3;
    private Paint paint;
    private int switchCount = SWITCH_DEFAULT_COUNT;
    private int openColorId;
    private int closeColorId;
    private int switchState = 0;
    private int itemWidth;
    private int height;
    private int width;
    private int xPad;
    private int yPad;
    private OnMoreSelSwitchClickListener onMoreSelSwitchClickListener;
    public MoreSelectSwitchV2(Context context) {
        this(context,null);
    }

    public MoreSelectSwitchV2(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public MoreSelectSwitchV2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        TypedArray params = context.obtainStyledAttributes(attrs, R.styleable.MoreSelectSwitch);
        if (params != null) {
            switchCount = params.getInteger(R.styleable.MoreSelectSwitch_SwitchCount,SWITCH_DEFAULT_COUNT);
            openColorId = params.getResourceId(R.styleable.MoreSelectSwitch_OpenColor,
                    context.getResources().getColor(R.color.theme_color));
            closeColorId = params.getResourceId(R.styleable.MoreSelectSwitch_CloseColor,
                    context.getResources().getColor(R.color.line_color));

            params.recycle();
        }

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        setOnTouchListener(this);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.BLACK);
        canvas.drawRect(new RectF(itemWidth / 2,(height / 2) - 4,width - itemWidth / 2,(height / 2 + 4)),paint);
        canvas.drawRect(new RectF(itemWidth / 2 + 8,(height / 2) - 16,itemWidth / 2  + 12,(height / 2 + 16)),paint);
        canvas.drawRect(new RectF(width / 2 - 4,(height / 2) - 16,width / 2,(height / 2 + 16)),paint);
        canvas.drawRect(new RectF(width - itemWidth / 2 - 12,(height / 2) - 16,width - itemWidth / 2 - 8,(height / 2 + 16)),paint);
        paint.setColor(openColorId);
        int radius = itemWidth / 5;
        canvas.drawCircle(switchState * itemWidth + (itemWidth / 2), height / 2,radius,paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        xPad = getPaddingLeft() + getPaddingRight();
        yPad = getPaddingTop() + getPaddingBottom();
        height = getMeasuredHeight();
        width = ((height - yPad) * switchCount + xPad) * 2;
        itemWidth = width / switchCount;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    public int getSwitchCount() {
        return switchCount;
    }

    public void setSwitchCount(int switchCount) {
        this.switchCount = switchCount;
        width = (height - yPad) * switchCount + xPad;
        itemWidth = width / switchCount;
        if (width > 0) {
            getLayoutParams().width = width;
            requestLayout();
        }

        postInvalidate();
    }

    public int getSwitchState() {
        return switchState;
    }

    public void setSwitchState(int switchState) {
        this.switchState = switchState % switchCount;
        postInvalidate();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int beforeSwitchState = switchState;
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (switchCount >= 3) {
                switchState = (int) (motionEvent.getX() / itemWidth);
            }else {
                switchState = ++switchState % switchCount;
            }

            postInvalidate();
            if (onMoreSelSwitchClickListener != null) {
                onMoreSelSwitchClickListener.onMoreClick(this,switchCount,beforeSwitchState,switchState);
            }
        }
        return false;
    }

    public interface OnMoreSelSwitchClickListener {
        void onMoreClick(View view,int switchCount,int beforeSwitchState,int afterSwitchState);
    }

    public void setOnMoreSelSwitchClickListener(OnMoreSelSwitchClickListener listener) {
        this.onMoreSelSwitchClickListener = listener;
    }
}
