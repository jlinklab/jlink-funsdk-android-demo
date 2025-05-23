package demo.xm.com.xmfunsdkdemo.utils

import android.content.res.Resources
import android.text.SpannableString
import android.text.Spanned
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.TextView
import java.io.File


fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.setInVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    ).toInt()

val Int.sp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics
    ).toInt()


fun File.getFileNoSpaceName(): String {
    return name.replace(" ", "_")
        .replace("-", "_")
        .replace(":", "_")
}

//设置密码可见性
fun EditText.setPasswordVisible(isShow: Boolean) {
    if (!isShow) {
        this.transformationMethod = PasswordTransformationMethod()
        this.setSelection(this.text.toString().length)
    } else if (isShow) {
        this.transformationMethod = null
        this.setSelection(this.text.toString().length)
    }
}
fun TextView.setColorText(text: String, color: Int, start: Int, end: Int) {
    val spannableStringCoupon = SpannableString(text)
    spannableStringCoupon.setSpan(
        ForegroundColorSpan(color),
        start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    this.text = spannableStringCoupon
}