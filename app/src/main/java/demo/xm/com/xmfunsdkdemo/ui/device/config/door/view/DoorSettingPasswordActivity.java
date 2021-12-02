package demo.xm.com.xmfunsdkdemo.ui.device.config.door.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lib.sdk.bean.doorlock.DoorLockAuthManageBean;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.dialog.EditDialog;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.door.contract.DoorSettingPasswordContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.door.presenter.DoorSettingPasswordPresenter;

import static demo.xm.com.xmfunsdkdemo.ui.device.config.door.presenter.DoorSettingPasswordPresenter.LOCK_MANAGER_TYPE_PWD;

/**
 * @author hws
 * @class
 * @time 2020/8/17 14:15
 */
public class DoorSettingPasswordActivity extends DemoBaseActivity<DoorSettingPasswordPresenter> implements DoorSettingPasswordContract.IDoorSettingPasswordView {
    private RecyclerView rvPwdManager;
    private ContactInfoAdapter adapter;
    @Override
    public DoorSettingPasswordPresenter getPresenter() {
        return new DoorSettingPasswordPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_setting_password);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        rvPwdManager = findViewById(R.id.rv_pwd_manager);

        titleBar.setTitleText(getString(R.string.password_manager));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });

        titleBar.setBottomTip(getClass().getName());
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }

        int managerType = intent.getIntExtra("managerType",LOCK_MANAGER_TYPE_PWD);
        DoorLockAuthManageBean doorLockAuthManageBean = (DoorLockAuthManageBean) intent.getSerializableExtra("managerList");
        presenter.setUserList(managerType,doorLockAuthManageBean);
        adapter = new ContactInfoAdapter();
        rvPwdManager.setLayoutManager(new LinearLayoutManager(this));
        rvPwdManager.setAdapter(adapter);
    }

    @Override
    public void onModifyNickNameResult(boolean isSuccess) {
        adapter.notifyDataSetChanged();
        showToast(isSuccess ? getString(R.string.modify_s) : getString(R.string.modify_f), Toast.LENGTH_LONG);
    }

    @Override
    public void onDeleteLockAuthMangerResult(boolean isSuccess) {
        showToast(isSuccess ? getString(R.string.delete_s) : getString(R.string.delete_f), Toast.LENGTH_LONG);
    }


    class ContactInfoAdapter extends RecyclerView.Adapter<ContactInfoAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contact_info,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DoorLockAuthManageBean.UserListBean.UserBean userBean = presenter.getDoorUser(position);
            if (userBean != null) {
                holder.lsiContactInfo.setTitle("" + position);
                holder.lsiContactInfo.setRightText(userBean.NickName);
            }
        }

        @Override
        public int getItemCount() {
            return presenter.getDoorUsersCount();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ListSelectItem lsiContactInfo;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                lsiContactInfo = itemView.findViewById(R.id.lsi_contact_info);
                lsiContactInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XMPromptDlg.onShowEditDialog(DoorSettingPasswordActivity.this, getString(R.string.modify_name), "", new EditDialog.OnEditContentListener() {
                            @Override
                            public void onResult(String nickName) {
                                presenter.modifyNickName(getAdapterPosition(),nickName);
                            }
                        });
                    }
                });
            }
        }
    }
}
