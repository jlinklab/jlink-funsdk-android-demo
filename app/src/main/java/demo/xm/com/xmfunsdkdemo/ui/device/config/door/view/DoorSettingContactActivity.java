package demo.xm.com.xmfunsdkdemo.ui.device.config.door.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.dialog.EditDialog;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.door.contract.DoorSettingContactContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.door.presenter.DoorSettingContactPresenter;

import static demo.xm.com.xmfunsdkdemo.ui.device.config.door.presenter.DoorSettingPasswordPresenter.LOCK_MANAGER_TYPE_CARD;
import static demo.xm.com.xmfunsdkdemo.ui.device.config.door.presenter.DoorSettingPasswordPresenter.LOCK_MANAGER_TYPE_FINGER;
import static demo.xm.com.xmfunsdkdemo.ui.device.config.door.presenter.DoorSettingPasswordPresenter.LOCK_MANAGER_TYPE_PWD;

/**
 * Door lock contact permissions
 *
 * @author hws
 * @class
 * @time 2020/8/14 15:31
 */
public class DoorSettingContactActivity extends BaseConfigActivity<DoorSettingContactPresenter> implements DoorSettingContactContract.IDoorSettingContactView {
    private ListSelectItem lsiUnlockByPwd;
    private ListSelectItem lsiUnlockByFinger;
    private ListSelectItem lsiUnlockByCard;
    /**
     * One key unlock
     */
    private Button lsiUnlockByOneKey;
    private Button btnUnlockDoor;

    @Override
    public DoorSettingContactPresenter getPresenter() {
        return new DoorSettingContactPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_setting_contact);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        lsiUnlockByPwd = findViewById(R.id.lsi_unlock_by_pwd);
        lsiUnlockByFinger = findViewById(R.id.lsi_unlock_by_finger);
        lsiUnlockByCard = findViewById(R.id.lsi_unlock_by_card);
        btnUnlockDoor = findViewById(R.id.btn_unlock_door);
        lsiUnlockByOneKey = findViewById(R.id.btn_one_key_unlock_door);
        titleBar.setTitleText(getString(R.string.contact_permissions));
    }

    private void initListener() {
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });

        titleBar.setBottomTip(getClass().getName());

        lsiUnlockByPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoorSettingContactActivity.this, DoorSettingPasswordActivity.class);
                intent.putExtra("devId", presenter.getDevId());
                intent.putExtra("managerType", LOCK_MANAGER_TYPE_PWD);
                intent.putExtra("managerList", presenter.getDoorLockAuthManager());
                startActivity(intent);
            }
        });

        lsiUnlockByFinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoorSettingContactActivity.this, DoorSettingPasswordActivity.class);
                intent.putExtra("devId", presenter.getDevId());
                intent.putExtra("managerType", LOCK_MANAGER_TYPE_FINGER);
                intent.putExtra("managerList", presenter.getDoorLockAuthManager());
                startActivity(intent);
            }
        });

        lsiUnlockByCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoorSettingContactActivity.this, DoorSettingPasswordActivity.class);
                intent.putExtra("devId", presenter.getDevId());
                intent.putExtra("managerType", LOCK_MANAGER_TYPE_CARD);
                intent.putExtra("managerList", presenter.getDoorLockAuthManager());
                startActivity(intent);
            }
        });

        btnUnlockDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XMPromptDlg.onShowEditDialog(DoorSettingContactActivity.this, getString(R.string.input_door_unlock_pwd), "", new EditDialog.OnEditContentListener() {
                    @Override
                    public void onResult(String password) {
                        showWaitDialog();
                        presenter.unlockDoor(password);
                    }
                });
            }
        });

        lsiUnlockByOneKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWaitDialog();
                presenter.unlockDoorByOneKey();
            }
        });
    }

    private void initData() {
        showWaitDialog();
        presenter.updateDoorUserInfo();
    }

    @Override
    public void onUpdateDoorUserInfoResult(boolean isSuccess) {
        hideWaitDialog();
        lsiUnlockByPwd.setEnable(isSuccess);
        lsiUnlockByFinger.setEnable(isSuccess);
        lsiUnlockByCard.setEnable(isSuccess);
    }

    @Override
    public void onUnlockDoorResult(boolean isSuccess) {
        hideWaitDialog();
        showToast(isSuccess ? getString(R.string.unlock_s) : getString(R.string.unlock_f), Toast.LENGTH_LONG);
    }

}
