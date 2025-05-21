package demo.xm.com.xmfunsdkdemo.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import demo.xm.com.xmfunsdkdemo.utils.dp

/**
 * 顶部拖动控件
 */
class BottomSheetDragView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var clDialogRoot: ConstraintLayout? = null
    private var dialog: BottomSheetDialog? = null
    private var lastY = 0f // 用于存储上一次触摸的 Y 位置
    private var totalScrollHeight = 0f // 用于存储滚动的总高度

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = ev.rawY
                totalScrollHeight = 0f
            }

            MotionEvent.ACTION_MOVE -> {
                val currentY = ev.rawY // 获取当前触摸位置
                val dy = currentY - lastY // 计算移动距离
                totalScrollHeight += dy// 更新总滚动高度
                lastY = currentY // 更新最后的 Y 位置
                Log.e("tag1", "滚动高度 $totalScrollHeight")
                if (totalScrollHeight > 0) {
                    clDialogRoot?.translationY = totalScrollHeight
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                post {
                    Log.e("tag1", "滚动高度 $totalScrollHeight")
                    if (totalScrollHeight > 100.dp) {
                        dialog?.dismiss()
                    } else {
                        clDialogRoot?.translationY = 0f
                    }
                    totalScrollHeight = 0f
                }
            }
        }
        return true
    }

    fun setBottomSheetBehavior(
        dialog: BottomSheetDialog,
        clDialogRoot: ConstraintLayout?
    ) {
        this.dialog = dialog
        this.clDialogRoot = clDialogRoot
    }
}