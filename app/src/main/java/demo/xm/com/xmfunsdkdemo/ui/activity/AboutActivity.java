package demo.xm.com.xmfunsdkdemo.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.utils.LogUtils;
import com.utils.XUtils;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import java.util.logging.LogManager;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;

/**
 * 关于Demo的介绍,包括logo,版本,授权
 * Introduction to Demo, including logo, version, authorization
 * Created by jiangping on 2018-10-23.
 */
public class AboutActivity extends DemoBaseActivity<AboutPresenter> implements AboutContract.IAboutView {
    private TextView tvVersion;
    private ListSelectItem lsiFeedback;//反馈

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(getClass().getName());
        tvVersion = findViewById(R.id.txtVersion);
        tvVersion.setText(XUtils.getVersion(this));
        lsiFeedback = findViewById(R.id.lsi_feedback);

        lsiFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XMPromptDlg.onShow(AboutActivity.this, getString(R.string.is_sure_feedback), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.sendFeedBack(AboutActivity.this);
                    }
                },null);
            }
        });
    }

    @Override
    public AboutPresenter getPresenter() {
        return new AboutPresenter(this);
    }


    @Override
    public void onUpdateView() {

    }
}
