package demo.xm.com.xmfunsdkdemo.ui.activity;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import java.util.Locale;

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

    @BindView(R.id.tv_hint)
    TextView tvHint;
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


        boolean isCn = "zh".equals(Locale.getDefault().getLanguage());
        String hintText = getString(R.string.please_check_error_code);
        SpannableString spannableString = new SpannableString(hintText);
        String goDocText = getString(R.string.visit_open_platform_documentation_center);
        int startIndex = hintText.indexOf(goDocText);
        int endIndex = startIndex + goDocText.length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                //开放平台错误码文档链接
                //https://docs.jftech.com/docs?menusId=ab0ed73834f54368be3e375075e27fb2&siderId=a5e54a6f9ab34703b0b53131f7a2b458&lang=zh
                //https://docs.jftech.com/docs?menusId=ab0ed73834f54368be3e375075e27fb2&siderId=a5e54a6f9ab34703b0b53131f7a2b458&lang=en
                openBrowser("https://docs.jftech.com/docs?menusId=ab0ed73834f54368be3e375075e27fb2&siderId=a5e54a6f9ab34703b0b53131f7a2b458&lang="
                        + (isCn ? "zh" : "en"));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true); // 不显示下划线
                ds.setColor(getResources().getColor(android.R.color.holo_blue_dark)); // 设置链接颜色
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvHint.setText(spannableString);
        tvHint.setMovementMethod(LinkMovementMethod.getInstance());
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
