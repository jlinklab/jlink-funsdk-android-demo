package demo.xm.com.xmfunsdkdemo.ui.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;

import com.utils.LogUtils;
import com.utils.XUtils;

import java.lang.reflect.Method;

import demo.xm.com.xmfunsdkdemo.R;

public class HelpSuspendLayout extends ConstraintLayout {

    private float downX, downY;

    private int width, height;

    private final int screenWidth, screenHeight;

    private int topLimit;

    private int bottomLimit;

    private int leftLimit;

    private int rightLimit;

    private boolean isDrag = false;

    private OnClickCallback onClickCallback;

    private GestureDetectorCompat gestureDetector;

    /**
     * 监听事件
     * 注：！！！！！
     * 因为现在监听调整为GestureDetector实现了，若要实现该监听，需要将子布局的监听给禁掉，否则会不起作用！！
     */
    public interface OnClickCallback {
        /**
         * 单击事件监听
         *
         * @param v
         */
        default void onClick(View v) {

        }

        /**
         * 双击事件监听
         *
         * @param v
         */
        default void onDoubleClick(View v) {

        }

        /**
         * 开启悬浮隐藏倒计时
         */
        default void startShowHideCountDown() {

        }

        /**
         * 关闭悬浮隐藏倒计时
         */
        default void stopShowHideCountDown() {

        }
    }

    public HelpSuspendLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        if (isHasNavigationBar(context)) {   //如果存在底部导航栏，需要减去导航栏的高度
            screenHeight = context.getResources().getDisplayMetrics().heightPixels
                    - XUtils.getStatusBarHeight(context) - getNavigationBarHeight(context);
        } else {
            screenHeight = context.getResources().getDisplayMetrics().heightPixels
                    - XUtils.getStatusBarHeight(context);
        }

        //设置默认边界值
        leftLimit = 0;
        rightLimit = screenWidth;
        topLimit = 0;
        bottomLimit = screenHeight;

        gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                if (onClickCallback != null) {
                    onClickCallback.onDoubleClick(HelpSuspendLayout.this);
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                if (onClickCallback != null) {
                    if (!isDrag) {
                        onClickCallback.onClick(HelpSuspendLayout.this);
                    }
                }
                return true;
            }

            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDown(@NonNull MotionEvent e) {
                return true;
            }
        });
    }

    public void setTopLimit(int topLimit) {
        this.topLimit = topLimit;
        // 若超过限制，则自动归位
        if ((int) (getTop() + getTranslationY()) < topLimit) {
            setTranslationY(topLimit - getTop());
        }
    }

    public void setBottomLimit(int bottomLimit) {
        this.bottomLimit = bottomLimit;
        // 若超过限制，则自动归位
        if ((int) (getBottom() + getTranslationY()) > bottomLimit) {
            setTranslationY(bottomLimit - getBottom());
        }
    }

    public void setLeftLimit(int leftLimit) {
        this.leftLimit = leftLimit;
        // 若超过限制，则自动归位
        if ((int) (getLeft() + getTranslationX()) < leftLimit) {
            setTranslationX(leftLimit - getLeft());
        }
    }

    public void setRightLimit(int rightLimit) {
        this.rightLimit = rightLimit;
        // 若超过限制，则自动归位
        if ((int) (getRight() + getTranslationX()) > rightLimit) {
            setTranslationX(rightLimit - getRight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (this.isEnabled()) {
            changeViewPosition(event);
            return gestureDetector.onTouchEvent(event);
        }
        return false;
    }

    public void setOnClickCallback(OnClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
    }

    /**
     * 改变控件位置
     *
     * @param event
     */
    public void changeViewPosition(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                downX = event.getX();
                downY = event.getY();
                if (onClickCallback != null) {
                    onClickCallback.stopShowHideCountDown();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float xDistance = event.getX() - downX;
                float yDistance = event.getY() - downY;
                if (Math.abs(xDistance) > 10 || Math.abs(yDistance) > 10) {
                    isDrag = true;
                    int l = (int) (getLeft() + xDistance + getTranslationX());
                    int r = l + width;
                    int t = (int) (getTop() + yDistance + getTranslationY());
                    int b = t + height;
                    if (l < leftLimit || r > rightLimit) {
                        xDistance = 0;
                    }

                    LogUtils.debugInfo("test", "topLimit:" + topLimit + " bottomLimit:" + bottomLimit + " leftLimit:" + leftLimit + " rightLimit:" + rightLimit + " width:" + width + " height:" + height);
                    if (t < topLimit || b > bottomLimit) {
                        yDistance = 0;
                    }
                    setTranslationX(getTranslationX() + xDistance);
                    setTranslationY(getTranslationY() + yDistance);
                }
                break;
            case MotionEvent.ACTION_UP:
                showFull(true);
                setPressed(false);
                if (onClickCallback != null) {
                    onClickCallback.startShowHideCountDown();
                }
            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
                break;
        }
    }

    /**
     * 若是true，则让布局完整显示
     * false，则让布局显示一部分
     *
     * @param isShowFull
     */
    public void showFull(boolean isShowFull) {
        // 创建属性动画，改变 "translationX" 属性
        ObjectAnimator animator;
        if (isShowFull) {
            animator = ObjectAnimator.ofFloat(this, "translationX", 0);
        } else {
            animator = ObjectAnimator.ofFloat(this, "translationX", getWidth() / 2 + getResources().getDimension(R.dimen.normal_left_right_margin));
        }

        // 设置动画的持续时间
        animator.setDuration(300);
        // 启动动画
        animator.start();
    }

    /**
     * 重置控件回到原本位置(也可仅重置横向或纵向)
     * @param isInitX 重置横坐标
     * @param isInitY 重置纵坐标
     * @param isAnimate 是否动画回到原本位置，若否，则立即回
     */
    public void resetPosition(boolean isInitX, boolean isInitY, boolean isAnimate) {
        if (isAnimate) {
            if (isInitX) {
                // 创建属性动画，改变translationX属性
                ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "translationX", 0);
                // 设置动画的持续时间
                animatorX.setDuration(300);
                // 启动动画

                animatorX.start();
            }

            if (isInitY) {
                ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "translationY", 0);
                animatorY.setDuration(300);
                animatorY.start();
            }
        } else {
            if (isInitX) {
                setTranslationX(0);
            }

            if (isInitY) {
                setTranslationY(0);
            }
        }
    }

    public static boolean isHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
}