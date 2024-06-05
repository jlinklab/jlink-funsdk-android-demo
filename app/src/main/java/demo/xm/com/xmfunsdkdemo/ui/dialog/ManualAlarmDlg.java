package demo.xm.com.xmfunsdkdemo.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.FunSDK;
import com.lib.MsgContent;
import com.xm.ui.dialog.BaseDlg;

import demo.xm.com.xmfunsdkdemo.R;

public class ManualAlarmDlg extends BaseDlg implements DialogInterface.OnShowListener {
    private ImageView ivManualAlarmSwitch;

    public ManualAlarmDlg(Context context) {
        mDlg = new android.app.AlertDialog.Builder(context).create();
        mDlg.setOnShowListener(this);
        mDlg.show();
        Window window = mDlg.getWindow();
        window.setContentView(R.layout.dialog_manual_alarm);
        window.setBackgroundDrawableResource(R.color.transparent);
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        ivManualAlarmSwitch = window.findViewById(R.id.iv_manual_alarm_switch);
        TextView textView = window.findViewById(R.id.tv_hint);
        textView.setText(FunSDK.TS("TR_Click_to_stop_alarm"));
    }

    public ManualAlarmDlg setCancelable(boolean cancelable) {
        mDlg.setCancelable(cancelable);
        return this;
    }

    public ManualAlarmDlg setClickStopButton(View.OnClickListener onClickListener) {
        ivManualAlarmSwitch.setOnClickListener(onClickListener);
        return this;
    }

    public void onShow() {
        mDlg.show();
    }

    public boolean isShow() {
        return mDlg.isShowing();
    }

    public void onDismiss() {
        mDlg.dismiss();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        return 0;
    }

    @Override
    public void onShow(DialogInterface dialog) {

    }
}
