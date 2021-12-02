package demo.xm.com.xmfunsdkdemo.ui.device.config.pwdmodify.view;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.pwdmodify.listener.DevModifyPwdContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.pwdmodify.presenter.DevModifyPwdPresenter;
import io.reactivex.annotations.Nullable;

/**
 * 密码修改界面,为了保护隐私,可以更改设备的访问密码
 * Created by jiangping on 2018-10-23.
 */
public class DevModifyPwdActivity extends BaseConfigActivity<DevModifyPwdPresenter> implements DevModifyPwdContract.IDevModifyPwdView {
    private Button btnOk;
    private EditText etOldPwd;
    private EditText etNewPwd;
    @Override
    public DevModifyPwdPresenter getPresenter() {
        return new DevModifyPwdPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_change_password);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.device_setup_change_password));
        titleBar.setLeftClick(this);

        btnOk = findViewById(R.id.btn_modify_pwd_submit);
        etOldPwd = findViewById(R.id.et_modify_pwd_old_psw);
        etNewPwd = findViewById(R.id.et_modify_pwd_new_psw);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                presenter.modifyDevPwd(etOldPwd.getText().toString().trim(),etNewPwd.getText().toString().trim());
            }
        });
    }

    private void initData() {

    }

    @Override
    public void onUpdateView(String result) {
        hideWaitDialog();
        Toast.makeText(this,result,Toast.LENGTH_LONG).show();
    }
}
