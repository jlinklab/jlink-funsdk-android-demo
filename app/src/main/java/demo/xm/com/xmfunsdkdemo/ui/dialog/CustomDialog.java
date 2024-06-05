package demo.xm.com.xmfunsdkdemo.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import demo.xm.com.xmfunsdkdemo.R;

/**
 * Created by ouhimehime on 16/4/22.
 * ---------自定义提示框-----------
 */

public class CustomDialog extends Dialog {

        public CustomDialog(Context context) {
            super(context);
        }

        public CustomDialog(Context context, int themeResId) {
            super(context, themeResId);
        }

        protected CustomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        public static class Builder {
            private Context context;
            private String title; //标题
            private String hint;//提示消息
            private String negative_text;//消极的
            private String positive_text;//积极的
            private DialogInterface.OnClickListener negativeListener;//消极的监听
            private DialogInterface.OnClickListener positiveListener;//积极的监听

            public Builder(Context context) {
                this.context = context;
            }

            public Builder setTitle(String title) {
                if (title == null) {
                    this.title = context.getString(R.string.user_input_pwd_error);
                }
                this.title = title;
                return this;
            }

            public Builder setHint(String hint) {
                if (hint == null) {
                    this.hint = context.getString(R.string.user_input_pwd_hint);
                }
                this.hint = hint;
                return this;
            }

            public Builder setNegativeButton(String negative_text, DialogInterface.OnClickListener negativeListener) {
                if (negative_text == null) {
                    this.negative_text = context.getString(R.string.common_cancel);
                }
                this.negative_text = negative_text;
                this.negativeListener = negativeListener;

                return this;
            }

            public Builder setPositionButton(String positive_text, DialogInterface.OnClickListener positiveListener) {
                if (positive_text == null) {
                    this.positive_text = context.getString(R.string.common_confirm);
                }
                this.positive_text = positive_text;
                this.positiveListener = positiveListener;

                return this;
            }

            private TextView tv_title_custom_dialog; //标题
            private EditText tv_message_custom_dialog;//提示信息
            private Button btn_negative_custom_dialog;//消极
            private Button btn_positive_custom_dialog;//积极

            public CustomDialog create() {
                final CustomDialog dialog = new CustomDialog(context);
                //取消点击外部消失弹窗
                dialog.setCancelable(false);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_dev_monitior, null);
                //加上这一句，取消原来的标题栏，没加这句之前，发现在三星的手机上会有一条蓝色的线
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                tv_title_custom_dialog = (TextView) view.findViewById(R.id.tv_title_custom_dialog);
                tv_message_custom_dialog = (EditText) view.findViewById(R.id.tv_message_custom_dialog);
                btn_negative_custom_dialog = (Button) view.findViewById(R.id.btn_negative_custom_dialog);
                btn_positive_custom_dialog = (Button) view.findViewById(R.id.btn_positive_custom_dialog);
                tv_title_custom_dialog.setText(title);
                tv_message_custom_dialog.setHint(hint);
                tv_message_custom_dialog.setOnTouchListener(new View.OnTouchListener() {

                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (MotionEvent.ACTION_DOWN == event.getAction()) {
                            tv_message_custom_dialog.setHint("");
                            tv_message_custom_dialog.setCursorVisible(true);// 点击显示光标
                        }
                        return false;
                    }
                });
                btn_negative_custom_dialog.setText(negative_text);
                btn_positive_custom_dialog.setText(positive_text);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                btn_negative_custom_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        negativeListener.onClick(dialog, Dialog.BUTTON_NEGATIVE);
                    }
                });
                btn_positive_custom_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        positiveListener.onClick(dialog, Dialog.BUTTON_POSITIVE);
                    }
                });
                return dialog;
            }

            public String getString(){
                return tv_message_custom_dialog.getText().toString();
            }
        }

}
