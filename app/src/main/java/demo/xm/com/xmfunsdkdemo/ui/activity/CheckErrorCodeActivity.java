package demo.xm.com.xmfunsdkdemo.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.EFUN_ERROR;
import com.lib.FunSDK;
import com.lib.sdk.bean.StringUtils;
import com.utils.XUtils;
import com.xm.activity.base.XMBaseActivity;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.widget.XMEditText;
import com.xm.ui.widget.XTitleBar;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;

/**
 * @author hws
 * @class
 * @time 2020/10/10 16:28
 */
public class CheckErrorCodeActivity extends DemoBaseActivity {
    @BindView(R.id.et_input_error_code)
    XMEditText etInputErrorCode;
    @BindView(R.id.tv_error_code_result)
    TextView tvErrorCodeResult;
    @Override
    public XMBasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_error_code);
        ButterKnife.bind(this);

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        titleBar.setTitleText(getString(R.string.see_error_code));
        titleBar.setBottomTip(getClass().getName());
    }

    @OnClick(R.id.btn_check)
    public void onCheckErrorCode(View view) {
        String checkErrorId = etInputErrorCode.getEditText();
        if (StringUtils.isStringNULL(checkErrorId) || !XUtils.isInteger(checkErrorId)) {
            Toast.makeText(this, R.string.input_right_error_code,Toast.LENGTH_LONG).show();
            return;
        }

        tvErrorCodeResult.setText("");

        int iCheckErrorId = Integer.parseInt(checkErrorId);


        Field[] fields =  EFUN_ERROR.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                int errorId = (int) field.get(null);
                if (Math.abs(errorId) == Math.abs(iCheckErrorId)) {
                    tvErrorCodeResult.setText(field.getName() + "(" + FunSDK.TS(field.getName()) + ")");
                    break;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
