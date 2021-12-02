package demo.xm.com.xmfunsdkdemo.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.xm.activity.base.XMBaseActivity;
import com.xm.activity.base.XMBasePresenter;

/**
 * @author hws
 * @class
 * @time 2020/9/10 16:02
 */
public class BubbleActivity extends XMBaseActivity {
    @Override
    public XMBasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("Test");
        setContentView(textView);
    }
}
