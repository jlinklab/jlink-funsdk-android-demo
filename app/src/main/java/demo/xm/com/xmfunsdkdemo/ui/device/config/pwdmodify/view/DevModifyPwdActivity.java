package demo.xm.com.xmfunsdkdemo.ui.device.config.pwdmodify.view;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.xm.ui.widget.XMEditText;

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
    private Button btnChangeUserNameOk;
    private XMEditText etOldPwd;
    private XMEditText etNewPwd;

    private XMEditText etOldUserName;
    private XMEditText etNewUserName;

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
                presenter.modifyDevPwd(etOldPwd.getEditText(), etNewPwd.getEditText());
            }
        });

        btnChangeUserNameOk = findViewById(R.id.btn_modify_username_submit);
        etOldUserName = findViewById(R.id.et_modify_username_old_psw);
        etNewUserName = findViewById(R.id.et_modify_username_new_psw);
        btnChangeUserNameOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWaitDialog();
                presenter.modifyDevUserName(etOldUserName.getEditText(), etNewUserName.getEditText());
            }
        });
    }

    private void initData() {
        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
        etOldPwd.setEditText(xmDevInfo.getDevPassword());
    }

    @Override
    public void onChangePwdResult(int errorId) {
        hideWaitDialog();
        Toast.makeText(this, errorId == 0 ? getString(R.string.libfunsdk_operation_success) : getString(R.string.libfunsdk_operation_failed) + ":" + errorId, Toast.LENGTH_LONG).show();
        etOldPwd.setEditText(etNewPwd.getEditText());
    }

    @Override
    public void onChangeUserNameResult(int errorId) {
        hideWaitDialog();
        Toast.makeText(this, errorId == 0 ? getString(R.string.libfunsdk_operation_success) : getString(R.string.libfunsdk_operation_failed) + ":" + errorId, Toast.LENGTH_LONG).show();
        etOldUserName.setEditText(etNewUserName.getEditText());
    }


    @Override
    public void onGetDevUserNameResult(String oldUserName) {
        etOldUserName.setEditText(oldUserName);
    }
}
