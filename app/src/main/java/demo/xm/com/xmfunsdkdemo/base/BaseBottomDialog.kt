package demo.xm.com.xmfunsdkdemo.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import demo.xm.com.xmfunsdkdemo.R

/**
 * 底部弹出的dialog 支持手指下滑关闭
 */
open class BaseBottomDialog<VB : ViewBinding>(
    val inflaterBlock: (LayoutInflater) -> VB,
    val onDismiss: (VB?) -> Unit = {},
    val onBind: (Dialog, VB?) -> Unit
) : BottomSheetDialogFragment() {

    open var binding: VB? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        binding = inflaterBlock(layoutInflater)
        dialog.setContentView(binding!!.root)
        onBind(dialog, binding)
        initView(dialog)
        return dialog
    }

    open fun initView(dialog: Dialog) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (needBgDim()) {
            setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        } else {
            setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogNoBgDimTheme)
        }
    }

    open fun needBgDim(): Boolean {
        return true
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss.invoke(binding)
    }
}