package demo.xm.com.xmfunsdkdemo.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lib.MsgContent;
import com.xm.ui.dialog.BaseDlg;
import com.xm.ui.widget.BtnColorBK;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.utils.LanguageUtils;

public class NetworkSwitchDlg extends BaseDlg implements DialogInterface.OnShowListener {
    private Context context;
    private TextView tvContent;
    private TextView tvTitle;
    private BtnColorBK btnConfirm;
    private BtnColorBK btnCancel;

    public NetworkSwitchDlg(Context context) {
        this.context = context;
        mDlg = new android.app.AlertDialog.Builder(context).create();
        mDlg.setOnShowListener(this);
        mDlg.show();
        Window window = mDlg.getWindow();
        window.setContentView(R.layout.dlg_network_switch_tips);
        window.setBackgroundDrawableResource(R.color.transparent);
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        initView(window);
        LanguageUtils.initLanguage(window.findViewById(R.id.layoutRoot));
        mDlg.setCancelable(false);
    }

    private void initView(Window window) {
        tvContent = window.findViewById(R.id.tv_dialog_tip);
        tvTitle = window.findViewById(R.id.tv_title);

        btnConfirm = window.findViewById(R.id.btn_confirm);
        btnCancel = window.findViewById(R.id.btn_cancel);

    }

    public NetworkSwitchDlg setPositiveButton(View.OnClickListener listener) {
        if (listener != null) {
            btnConfirm.setOnClickListener(listener);
        }
        return this;
    }

    public NetworkSwitchDlg setNegativeButton(View.OnClickListener listener) {
        if (listener != null) {
            btnCancel.setOnClickListener(listener);
        }
        return this;
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        mDlg.dismiss();
    }

    /**
     * 显示对话框
     */
    public boolean isShowing() {
        if (mDlg != null) {
            return mDlg.isShowing();
        }

        return false;
    }

    public void show() {
        if (!mDlg.isShowing()) {
            mDlg.show();
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        return 0;
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {

    }
}
